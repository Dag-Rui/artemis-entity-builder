package no.daffern.artemis;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceSet;

public class BuilderExtension {

  private SourceSet src;

  private File outputFolder;

  private String createMethodPrefix = "set";
  private List<String> componentSuperTypes = Collections.singletonList("com.artemis.Component");

  public SourceSet getSrc() {
    return src;
  }

  public void setSrc(SourceSet src) {
    this.src = src;
  }

  public File getOutputFolder() {
    return outputFolder;
  }

  public void setOutputFolder(File outputFolder) {
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
