package no.daffern.artemis.gen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Utils {

  public static String getName(String qualifiedName) {
    if (qualifiedName.contains(".")) {
      return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
    }
    return qualifiedName;
  }

  public static String getPackage(String qualifiedName) {
    if (qualifiedName.contains(".")) {
      return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }
    return qualifiedName;
  }

  public static TypeName guessTypeName(String qualifiedName, List<String> qualifiedParameterTypes) {
    TypeName typeName = guessTypeName(qualifiedName);

    if (qualifiedParameterTypes.isEmpty()) {
      return typeName;
    }

    if (typeName instanceof ClassName) {
      TypeName[] parameters = new TypeName[qualifiedParameterTypes.size()];
      for (int i = 0; i < qualifiedParameterTypes.size(); i++) {
        parameters[i] = guessTypeName(qualifiedParameterTypes.get(i));
      }

      return ParameterizedTypeName.get((ClassName) typeName, parameters);
    }

    throw new RuntimeException("Primitive type " + qualifiedName + " is parameterized???");
  }

  public static TypeName guessTypeName(String qualifiedName) {
    switch (qualifiedName) {
      case "string":
        return ClassNames.STRING;
      case "int":
        return TypeName.INT;
      case "short":
        return TypeName.SHORT;
      case "byte":
        return TypeName.BYTE;
      case "long":
        return TypeName.LONG;
      case "float":
        return TypeName.FLOAT;
      case "double":
        return TypeName.DOUBLE;
      case "boolean":
        return TypeName.BOOLEAN;
      case "char":
        return TypeName.CHAR;
      default:
        if (qualifiedName.endsWith("?")) {
          return ClassName.OBJECT;
        }
        return ClassName.bestGuess(qualifiedName);
    }
  }

  public static Path packageToPath(Path basePath, String packageName) throws IOException {
    Path outputDirectory = basePath;
    if (!packageName.isEmpty()) {
      for (String packageComponent : packageName.split("\\.")) {
        outputDirectory = outputDirectory.resolve(packageComponent);
      }
      Files.createDirectories(outputDirectory);
    }
    return outputDirectory;
  }

}
