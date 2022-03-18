package no.daff.test;

import com.squareup.javapoet.JavaFile;
import no.daffern.artemis.gen.ComponentCollector;
import no.daffern.artemis.gen.ComponentInfo;
import no.daffern.artemis.gen.SourceGenerator;
import no.daffern.artemis.gen.Utils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Testing {

  @Test
  public void generateTestFiles() throws IOException {

    HashSet<File> files = new HashSet<>();
    files.add(new File("src\\test\\java\\no\\daff\\test\\TestComponent.java"));

    List<String> superTypes = Arrays.asList("com.artemis.Component", "com.artemis.PooledComponent");

    File outputFolder = new File("source out");

    List<ComponentInfo> infos = new ComponentCollector().collect(files, superTypes);
    JavaFile[] outputFiles =
        new SourceGenerator(true, "set", "no.daffern.artemis").build(infos);

    for (JavaFile javaFile : outputFiles) {
      Path filePath = Utils.packageToPath(outputFolder.toPath(), javaFile.packageName);
      Files.createDirectories(filePath);

      File file = filePath.resolve(javaFile.typeSpec.name + ".java").toFile();
      FileWriter fileWriter = new FileWriter(file);
      javaFile.writeTo(fileWriter);
      fileWriter.flush();
      fileWriter.close();
    }
  }
}
