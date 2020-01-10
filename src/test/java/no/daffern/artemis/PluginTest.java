package no.daffern.artemis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class PluginTest {

  @Test
  public void exists(){
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("no.daffern.artemis");

    assertTrue(project.getPluginManager().hasPlugin("no.daffern.artemis"));

    assertNotNull(project.getTasks().getByName("builder"));
  }
}
