package no.daffern.artemis;


import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BuilderPlugin implements Plugin<Project> {

  public void apply(Project project) {
    BuilderExtension props = project.getExtensions()
        .create("builder", BuilderExtension.class);

    project.task("builder").doLast(task -> {

      List<File> inputFiles = new ArrayList<>();
      props.getInputFolders().forEach(path -> inputFiles.add(new File(path)));

      try {
        List<ComponentInfo> infos = new ComponentCollector().collect(inputFiles, props.getComponentSuperTypes());
        JavaFile[] outputFiles = new SourceGenerator().build(infos, new File(props.getOutputFolder()), props.getCreateMethodPrefix());

        for (JavaFile file : outputFiles) {
          file.writeTo(Paths.get(props.getOutputFolder()));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    });
  }
}