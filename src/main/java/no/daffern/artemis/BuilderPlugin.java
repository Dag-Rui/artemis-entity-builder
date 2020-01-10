package no.daffern.artemis;


import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.List;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;

public class BuilderPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.getTasks().create("builder", BuilderTask.class);
  }
}