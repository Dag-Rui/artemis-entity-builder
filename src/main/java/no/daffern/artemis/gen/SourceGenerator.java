package no.daffern.artemis.gen;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.List;
import javax.lang.model.element.Modifier;
import no.daffern.artemis.BuilderTask;
import no.daffern.artemis.gen.ComponentInfo.MethodInfo;
import no.daffern.artemis.gen.ComponentInfo.ParameterInfo;
import org.jboss.forge.roaster._shade.org.apache.commons.lang3.StringUtils;

public class SourceGenerator {

  private static final TypeVariableName entityBuilderName = TypeVariableName.get("T", ClassName.get("no.daffern.artemis", "EntityBuilder"));
  private static final ClassName superMapperName = ClassName.get("no.daffern.artemis", "SuperMapper");

  public JavaFile[] build(List<ComponentInfo> componentInfos, BuilderTask task) {
    JavaFile superMapper = generateSuperMapper(componentInfos);
    JavaFile entityBuilder = generateBuilder(componentInfos, superMapper, task);

    return new JavaFile[]{superMapper, entityBuilder};
  }

  private JavaFile generateBuilder(List<ComponentInfo> componentInfos, JavaFile mapperFile, BuilderTask task) {
    TypeSpec.Builder typeSpec = TypeSpec.classBuilder("EntityBuilder")
        .addTypeVariable(entityBuilderName)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"unchecked\"").build())
        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(superMapperName, "mapper")
            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
            .build())
        .addField(FieldSpec.builder(TypeName.INT, "entityId")
            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
            .build())
        .addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(superMapperName, "mapper")
            .addParameter(TypeName.INT, "entityId")
            .addCode("this.mapper = mapper;\n")
            .addCode("this.entityId = entityId;\n")
            .build());

    //Entity method
    typeSpec.addMethod(MethodSpec.methodBuilder("entity")
        .addModifiers(Modifier.PUBLIC)
        .returns(ClassName.get("com.artemis", "Entity"))
        .addCode("return mapper.getEntity(entityId);\n")
        .build());

    //delete method
    typeSpec.addMethod(MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .addCode("mapper.delete(entityId);\n")
        .build());

    for (ComponentInfo componentInfo : componentInfos) {
      String mapperCode = "mapper." + getMapperName(componentInfo.getName());
      String methodName = StringUtils.uncapitalize(componentInfo.getName());
      if (task.isStripComponentName()) {
        methodName = methodName.replace("Component", "");
      }

      //Default create method
      typeSpec.addMethod(MethodSpec
          .methodBuilder(methodName)
          .addModifiers(Modifier.PUBLIC)
          .returns(entityBuilderName)
          .addCode(mapperCode + ".create(entityId);\n")
          .addCode("return (T) this;\n")
          .build());

      //Other create methods
      for (MethodInfo methodInfo : componentInfo.getMethodInfos()) {
        String createMethodName = methodName + StringUtils.capitalize(methodInfo.getMethodName());
        if (methodInfo.getMethodName().equals(task.getInitMethodName())) {
          createMethodName = methodName;
        }

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(createMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(entityBuilderName)
            .addCode(componentInfo.getName() + " component = ")
            .addCode(mapperCode + ".create(entityId);\n");

        StringBuilder parameters = new StringBuilder();

        for (ParameterInfo parameterInfo : methodInfo.getParameterInfos()) {
          methodSpec.addParameter(findTypeName(parameterInfo), parameterInfo.getVariableName());
          parameters.append(parameterInfo.getVariableName()).append(", ");
        }

        if (parameters.length() > 0) {
          methodSpec.addCode("component." + methodInfo.getMethodName())
              .addCode("(" + parameters.substring(0, parameters.length() - 2) + ");\n")
              .addCode("return (T) this;\n");
          typeSpec.addMethod(methodSpec.build());
        }
      }
      //get method
      typeSpec.addMethod(MethodSpec.methodBuilder("get" + StringUtils.capitalize(methodName))
          .addModifiers(Modifier.PUBLIC)
          .returns(ClassName.get(componentInfo.getPackage(), componentInfo.getName()))
          .addCode("return " + mapperCode + ".get(entityId);\n")
          .build());

      //has method
      typeSpec.addMethod(MethodSpec.methodBuilder("has" + StringUtils.capitalize(methodName))
          .addModifiers(Modifier.PUBLIC)
          .returns(TypeName.BOOLEAN)
          .addCode("return " + mapperCode + ".has(entityId);\n")
          .build());

      //remove method
      typeSpec.addMethod(MethodSpec.methodBuilder("remove" + StringUtils.capitalize(methodName))
          .addModifiers(Modifier.PUBLIC)
          .addCode(mapperCode + ".remove(entityId);\n")
          .build());
    }

    return JavaFile.builder("no.daffern.artemis", typeSpec.build()).build();
  }

  private JavaFile generateSuperMapper(List<ComponentInfo> componentInfos) {
    TypeSpec.Builder typeSpec = TypeSpec.classBuilder("SuperMapper")
        .superclass(ClassName.get("com.artemis", "BaseSystem"))
        .addModifiers(Modifier.PUBLIC);

    for (ComponentInfo componentInfo : componentInfos) {
      ClassName mapperName = ClassName.get("com.artemis", "ComponentMapper");
      ClassName componentName = ClassName.get(componentInfo.getPackage(), componentInfo.getName());

      TypeName componentMapper = ParameterizedTypeName.get(mapperName, componentName);

      FieldSpec fieldSpec = FieldSpec
          .builder(componentMapper, getMapperName(componentInfo.getName()))
          .addModifiers(Modifier.PUBLIC)
          .build();

      typeSpec.addField(fieldSpec);
    }

    typeSpec.addMethod(MethodSpec.methodBuilder("processSystem")
        .addModifiers(Modifier.PROTECTED)
        .addAnnotation(Override.class)
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("getEntity")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.INT, "entityId")
        .returns(ClassName.get("com.artemis", "Entity"))
        .addCode("return getWorld().getEntity(entityId);\n")
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("create")
        .addModifiers(Modifier.PUBLIC)
        .returns(ClassName.get("no.daffern.artemis", "EntityBuilder"))
        .addCode("return get(getWorld().create());\n")
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("get")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.INT, "entityId")
        .returns(ClassName.get("no.daffern.artemis", "EntityBuilder"))
        .addCode("return new EntityBuilder(this, entityId);\n")
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.INT, "entityId")
        .addCode("getWorld().delete(entityId);\n")
        .build());

    return JavaFile.builder("no.daffern.artemis", typeSpec.build()).build();
  }

  private String getMapperName(String componentName) {
    return StringUtils.uncapitalize(componentName) + "Mapper";
  }

  private TypeName findTypeName(ParameterInfo info) {
    switch (info.getQualifiedName()) {
      case "string":
        return TypeName.get(String.class);
      case "int":
        return TypeName.get(int.class);
      case "byte":
        return TypeName.get(byte.class);
      case "long":
        return TypeName.get(long.class);
      case "float":
        return TypeName.get(float.class);
      case "double":
        return TypeName.get(double.class);
      case "boolean":
        return TypeName.get(boolean.class);
      default:
        return ClassName.get(info.getPackage(), info.getName());
    }
  }
}