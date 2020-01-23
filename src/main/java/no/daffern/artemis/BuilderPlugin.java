package no.daffern.artemis;


import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BuilderPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.getTasks().create("builder", BuilderTask.class);
  }
}