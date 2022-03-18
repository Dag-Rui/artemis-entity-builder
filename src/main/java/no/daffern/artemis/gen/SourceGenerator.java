package no.daffern.artemis.gen;

import com.squareup.javapoet.*;
import no.daffern.artemis.gen.ComponentInfo.MethodInfo;
import no.daffern.artemis.gen.ComponentInfo.ParameterInfo;
import org.jboss.forge.roaster._shade.org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.List;

public class SourceGenerator {

  private final TypeVariableName entityBuilderName;
  private final ClassName superMapperName;
  private final boolean stripComponentName;
  private final String initMethodName;
  private final String outputPackage;

  public SourceGenerator(boolean stripComponentName, String initMethodName, String outputPackage) {
    this.stripComponentName = stripComponentName;
    this.initMethodName = initMethodName;
    this.outputPackage = outputPackage;
    this.entityBuilderName = TypeVariableName.get("T", ClassName.get(outputPackage, "EntityBuilder"));
    this.superMapperName = ClassName.get(outputPackage, "SuperMapper");
  }

  public JavaFile[] build(List<ComponentInfo> componentInfos) {
    JavaFile superMapper = generateSuperMapper(componentInfos);
    JavaFile entityBuilder = generateBuilder(componentInfos, superMapper);

    return new JavaFile[]{superMapper, entityBuilder};
  }

  private JavaFile generateBuilder(List<ComponentInfo> componentInfos, JavaFile mapperFile) {
    TypeSpec.Builder typeSpec = TypeSpec.classBuilder("EntityBuilder")
        .addTypeVariable(entityBuilderName)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"all\"").build())
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

    //Entity id
    typeSpec.addMethod(MethodSpec.methodBuilder("entityId")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.INT)
        .addCode("return entityId;\n")
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
      if (stripComponentName) {
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
        if (methodInfo.getMethodName().equals(initMethodName)) {
          createMethodName = methodName;
        }

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(createMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(entityBuilderName)
            .addCode(componentInfo.getName() + " component = ")
            .addCode(mapperCode + ".create(entityId);\n");

        StringBuilder parameters = new StringBuilder();

        for (ParameterInfo parameterInfo : methodInfo.getParameterInfos()) {

          TypeName typeName = Utils.guessTypeName(parameterInfo.getQualifiedName(), parameterInfo.getQualifiedTemplateNames());
          methodSpec.addParameter(ParameterSpec.builder(typeName, parameterInfo.getVariableName())
              .build());

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

    return JavaFile.builder(outputPackage, typeSpec.build()).build();
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
        .returns(ClassName.get(outputPackage, "EntityBuilder<?>"))
        .addCode("return get(getWorld().create());\n")
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("get")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.INT, "entityId")
        .returns(ClassName.get(outputPackage, "EntityBuilder<?>"))
        .addCode("return new EntityBuilder<>(this, entityId);\n")
        .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.INT, "entityId")
        .addCode("getWorld().delete(entityId);\n")
        .build());

    return JavaFile.builder(outputPackage, typeSpec.build()).build();
  }

  private String getMapperName(String componentName) {
    return StringUtils.uncapitalize(componentName) + "Mapper";
  }

}