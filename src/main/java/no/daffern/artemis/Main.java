package no.daffern.artemis;

import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;

public class Main {

  public static void main(String... args) throws IOException {

    String outputUrl = "build/generated-sources/entity-factory/";

    FileCollection files = ImmutableFileCollection.of(new File("src/test/java/no/daffern/artemis/dummy"));

    List<ComponentInfo> componentInfos = new ComponentCollector()
        .collect(files, Collections.singletonList("com.artemis.Component"));

    JavaFile[] outputFiles = new SourceGenerator().build(componentInfos, true);

    for (JavaFile file : outputFiles){
      file.writeTo(Paths.get(outputUrl));
    }
  }
}
