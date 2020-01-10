package no.daffern.artemis;

import java.util.Collections;
import java.util.List;

public class BuilderExtension {

  private List<String> inputFolders;
  private String outputFolder = "build/generated-sources/no/daffern/artemis/";

  private String createMethodPrefix = "set";
  private List<String> componentSuperTypes = Collections.singletonList("com.artemis.Component");


  public List<String> getInputFolders() {
    return inputFolders;
  }

  public void setInputFolders(List<String> inputFolders) {
    this.inputFolders = inputFolders;
  }

  public String getOutputFolder() {
    return outputFolder;
  }

  public void setOutputFolder(String outputFolder) {
    this.outputFolder = outputFolder;
  }

  public String getCreateMethodPrefix() {
    return createMethodPrefix;
  }

  public void setCreateMethodPrefix(String createMethodPrefix) {
    this.createMethodPrefix = createMethodPrefix;
  }

  public List<String> getComponentSuperTypes() {
    return componentSuperTypes;
  }

  public void setComponentSuperTypes(List<String> componentSuperTypes) {
    this.componentSuperTypes = componentSuperTypes;
  }
}
