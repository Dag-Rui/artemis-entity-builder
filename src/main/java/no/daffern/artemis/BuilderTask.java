package no.daffern.artemis;

import com.squareup.javapoet.JavaFile;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import no.daffern.artemis.gen.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BuilderTask extends DefaultTask {

  /**
   * Where to look for components.
   */
  @Input
  private FileCollection inputDirectories;

  /**
   * The parent component types to look for.
   */
  @Input
  private List<String> componentBaseTypes = Arrays.asList("com.artemis.Component", "com.artemis.PooledComponent");

  /**
   * Where the put the generated SuperMapper and EntityBuilder.
   */
  @Input
  private File outputFolder;

  /**
   * The package name for the generated SuperMapper and EntityBuilder.
   */
  @Input
  private String outputPackage = "no.daffern.artemis";

  /**
   * If a component is named "SpriteComponent", "Component" will be removed in the EntityBuilder methods.
   */
  @Input
  private boolean stripComponentName = true;

  /**
   * The method name on a component which will be the initializing method in the EntityBuilder.
   * For example a SpriteComponent with method init(Sprite sprite) will look like this:
   * entityBuilder.sprite(sprite)
   */
  @Input
  private String initMethodName = "init";

  @TaskAction
  public void build() {

    try {
      List<ComponentInfo> infos = new ComponentCollector().collect(inputDirectories, componentBaseTypes);
      JavaFile[] outputFiles = new SourceGenerator(this).build(infos);

      for (JavaFile javaFile : outputFiles) {
        Path filePath = Utils.packageToPath(outputFolder.toPath(), javaFile.packageName);
        Files.createDirectories(filePath);

        File file = filePath.resolve(javaFile.typeSpec.name + ".java").toFile();
        FileWriter fileWriter = new FileWriter(file);
        javaFile.writeTo(fileWriter);
        fileWriter.flush();
        fileWriter.close();
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

  public String getOutputPackage() {
    return outputPackage;
  }

  public void setOutputPackage(String outputPackage) {
    this.outputPackage = outputPackage;
  }

  public List<String> getComponentBaseTypes() {
    return componentBaseTypes;
  }

  public void setComponentBaseTypes(List<String> componentBaseTypes) {
    this.componentBaseTypes = componentBaseTypes;
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
