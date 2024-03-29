package no.daffern.artemis.gen;

import no.daffern.artemis.gen.ComponentInfo.MethodInfo;
import org.gradle.api.file.FileCollection;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.impl.JavaClassImpl;
import org.jboss.forge.roaster.model.impl.ParameterImpl;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComponentCollector {

  public List<ComponentInfo> collect(Set<File> files, List<String> superTypes) throws IOException {
    List<JavaClassImpl> components = new ArrayList<>();

    for (File file : files) {

      JavaType<?> javaType = Roaster.parse(new FileInputStream(file));

      if (javaType instanceof JavaClassImpl) {
        JavaClassImpl type = (JavaClassImpl) javaType;

        if (superTypes.contains(type.getSuperType())) {
          components.add(type);
        }
      }
    }
    return toSpec(components);
  }

  private List<ComponentInfo> toSpec(List<JavaClassImpl> types) {
    List<ComponentInfo> components = new ArrayList<>();

    for (JavaClassImpl type : types) {
      if (type.isAbstract() || !type.isPublic() || type.isInterface()) {
        continue;
      }

      if (type.hasAnnotation(BuilderIgnore.class)) {
        continue;
      }

      ComponentInfo componentInfo = new ComponentInfo(type.getQualifiedName());
      components.add(componentInfo);

      for (MethodSource<JavaClassSource> method : type.getMethods()) {
        if (method.isStatic() || method.isAbstract() || !method.isPublic()) {
          continue;
        }

        if (method.hasAnnotation(BuilderIgnore.class)) {
          continue;
        }

        MethodInfo methodInfo = componentInfo.addMethodSpec(method.getName());

        for (ParameterSource<JavaClassSource> parameter : method.getParameters()) {

          if (parameter instanceof ParameterImpl) {
            ParameterImpl impl = (ParameterImpl) parameter;
            Type paramType = impl.getType();

            List<String> genericClasses = new ArrayList<>();
            for (Object type1 : paramType.getTypeArguments()) {
              genericClasses.add(((Type) type1).getQualifiedName());
            }

            methodInfo.addParameterSpec(impl.getType().getQualifiedName(), impl.getName(), genericClasses);
          } else {
            System.err.println("Ignoring parameter of type " + parameter.getClass().getName());
          }
        }
      }
    }
    return components;
  }
}
