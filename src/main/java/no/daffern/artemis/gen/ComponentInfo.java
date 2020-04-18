package no.daffern.artemis.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentInfo {

  private String qualifiedName;
  private List<MethodInfo> methodInfos = new ArrayList<>();

  public ComponentInfo(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public MethodInfo addMethodSpec(String methodName) {
    MethodInfo methodInfo = new MethodInfo(methodName);
    methodInfos.add(methodInfo);
    return methodInfo;
  }

  public String getQualifiedName() {
    return qualifiedName;
  }

  public String getName() {
    return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
  }

  public String getPackage() {
    return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
  }

  public List<MethodInfo> getMethodInfos() {
    return methodInfos;
  }

  public static class MethodInfo {

    private String methodName;
    private List<ParameterInfo> parameterInfos = new ArrayList<>();

    public MethodInfo(String methodName) {
      this.methodName = methodName;
    }

    public ParameterInfo addParameterSpec(String qualifiedName, String variableName) {
      ParameterInfo parameterInfo = new ParameterInfo(qualifiedName, variableName);
      parameterInfos.add(parameterInfo);
      return parameterInfo;
    }

    public ParameterInfo addParameterSpec(String qualifiedName, String variableName, List<String> templateNames) {
      ParameterInfo parameterInfo = new ParameterInfo(qualifiedName, variableName, templateNames);
      parameterInfos.add(parameterInfo);
      return parameterInfo;
    }

    public String getMethodName() {
      return methodName;
    }

    public List<ParameterInfo> getParameterInfos() {
      return parameterInfos;
    }
  }

  public static class ParameterInfo {

    private String qualifiedName;
    private String variableName;
    private List<String> qualifiedTemplateNames;

    public ParameterInfo(String qualifiedName, String variableName) {
      this.qualifiedName = qualifiedName;
      this.variableName = variableName;
    }

    public ParameterInfo(String qualifiedName, String variableName, List<String> qualifiedTemplateNames) {
      this.qualifiedName = qualifiedName;
      this.variableName = variableName;
      this.qualifiedTemplateNames = qualifiedTemplateNames;
    }

    public String getQualifiedName() {
      return qualifiedName;
    }

    public String getVariableName() {
      return variableName;
    }

    public List<String> getQualifiedTemplateNames() {
      return qualifiedTemplateNames;
    }
  }

}
