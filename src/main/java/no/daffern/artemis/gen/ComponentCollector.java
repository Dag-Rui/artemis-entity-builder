package no.daffern.artemis.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import no.daffern.artemis.gen.ComponentInfo.MethodInfo;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.impl.JavaClassImpl;
import org.jboss.forge.roaster.model.impl.ParameterImpl;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

public class ComponentCollector {

  public List<ComponentInfo> collect(List<File> files, List<String> superTypes) throws IOException {
    List<JavaClassImpl> components = new ArrayList<>();

    for (File file : files) {

      JavaType<?> javaType = Roaster.parse(file);

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
      if (type.hasAnnotation(BuilderIgnore.class)) {
        continue;
      }

      ComponentInfo componentInfo = new ComponentInfo(type.getQualifiedName());
      components.add(componentInfo);

      for (MethodSource<JavaClassSource> method : type.getMethods()) {
        if (method.hasAnnotation(BuilderIgnore.class)) {
          continue;
        }

        MethodInfo methodInfo = componentInfo.addMethodSpec(method.getName());

        for (ParameterSource<JavaClassSource> parameter : method.getParameters()) {

          if (parameter instanceof ParameterImpl) {
            ParameterImpl impl = (ParameterImpl) parameter;

            methodInfo.addParameterSpec(impl.getType().getQualifiedName(), impl.getName());
          } else {
            System.err.println("Ignoring parameter of type " + parameter.getClass().getName());
          }
        }
      }
    }
    return components;
  }
}
