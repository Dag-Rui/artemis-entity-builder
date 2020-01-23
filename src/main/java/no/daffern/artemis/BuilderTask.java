package no.daffern.artemis;

import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class BuilderTask extends DefaultTask {

  @Input
  private FileCollection inputDirectories;

  @Input
  private File outputFolder;

  @Input
  private List<String> componentSuperTypes = Collections.singletonList("com.artemis.Component");

  @Input
  private boolean stripComponentName = true;

  @Input
  private String initMethodName = "init";

  @TaskAction
  public void build() {

    try {
      List<ComponentInfo> infos = new ComponentCollector().collect(inputDirectories, componentSuperTypes);
      JavaFile[] outputFiles = new SourceGenerator().build(infos, this);

      for (JavaFile file : outputFiles) {
        file.writeTo(outputFolder);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public FileCollection getInputDirectories() {
    return inputDirectories;
  }

  public void setInputDirectories(FileCollection inputDirectories) {
    this.inputDirectories = inputDirectories;
  }

  public File getOutputFolder() {
    return outputFolder;
  }

  public void setOutputFolder(File outputFolder) {
    this.outputFolder = outputFolder;
  }

  public List<String> getComponentSuperTypes() {
    return componentSuperTypes;
  }

  public void setComponentSuperTypes(List<String> componentSuperTypes) {
    this.componentSuperTypes = componentSuperTypes;
  }

  public boolean isStripComponentName() {
    return stripComponentName;
  }

  public void setStripComponentName(boolean stripComponentName) {
    this.stripComponentName = stripComponentName;
  }

  public String getInitMethodName() {
    return initMethodName;
  }

  public void setInitMethodName(String initMethodName) {
    this.initMethodName = initMethodName;
  }
}
