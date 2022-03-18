A gradle plugin for generating source for an entity builder class. 

Similar to https://github.com/junkdog/artemis-odb/wiki/Fluid-Entity-Gradle, but instead uses java files to create the 
SuperMapper and EntityBuilder. This means the components does not needs its own project.

Example usage with classes TransformComponent, InterpolateComponent and TextureComponent (TransformComponent and TextureComponent has a set() method):

    EntityBuilder ship = superMapper.create()
        .transformSet(0, 0, 0)
        .interpolate()
        .textureSet(assetManager.getTextureRegion("ship"));

To set up the plugin, add the following to build.gradle:

    apply plugin: no.daffern.artemis.EntityBuilderPlugin

    compileJava.dependsOn(builder)

    ext {
        builderOutputDir = file("$buildDir/generated-sources/builder/")
    }

    builder {
        inputDirectories = sourceSets.main.java
        outputFolder = builderOutputDir
        initMethodName = "set"
    }

    buildscript {
        dependencies {
            classpath 'no.daffern.artemis:artemis-entity-builder-plugin:1.0'
        }
    }
}

And run with "gradlew entityBuilder"
