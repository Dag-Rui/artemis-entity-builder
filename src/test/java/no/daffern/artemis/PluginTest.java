package no.daffern.artemis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Ignore;
import org.junit.Test;

public class PluginTest {

  @Test
  public void exists() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("no.daffern.artemis");

    assertTrue(project.getPluginManager().hasPlugin("no.daffern.artemis"));

    assertNotNull(project.getTasks().getByName("builder"));
  }

  @Ignore
  @Test
  public void generate() throws IOException {
    String outputUrl = "build/generated-sources/entity-factory/";

    FileCollection files = ImmutableFileCollection
        .of(new File("src/test/no/daffern/artemis/dummy"));

    List<ComponentInfo> componentInfos = new ComponentCollector()
        .collect(files, Collections.singletonList("com.artemis.Component"));

    JavaFile[] outputFiles = new SourceGenerator().build(componentInfos, true);

    for (JavaFile file : outputFiles) {
      file.writeTo(Paths.get(outputUrl));
    }
  }
}
