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

public class Main {

  public static void main(String... args) throws IOException {

    String outputUrl = "build/generated-sources/entity-factory/";

    String[] urls = new String[]{"src/test/java/no/daffern/artemis/"};

    List<File> files = new ArrayList<>();

    for (String url : urls) {
      Files.find(
          Paths.get(url), 10, (file, attr) -> attr.isRegularFile())
          .forEach(path -> files.add(path.toFile()));
    }

    List<ComponentInfo> componentInfos = new ComponentCollector().collect(files, Collections.singletonList("com.artemis.Component"));

    JavaFile[] outputFiles = new SourceGenerator().build(componentInfos, new File(outputUrl), "set");

    for (JavaFile file : outputFiles){
      file.writeTo(Paths.get(outputUrl));
    }
  }
}
