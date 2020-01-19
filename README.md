A gradle plugin for generating source for an entity builder class. 

Similar to https://github.com/junkdog/artemis-odb/wiki/Fluid-Entity-Gradle, but instead uses java source to create the 
SuperMapper and EntityBuilder. This means the components does not needs its own project.

Example usage:

    EntityBuilder ship = superMapper.create()
        .transformSet(0, 0, 0)
        .interpolate()
        .cameraFocus()
        .textureSet(assetManager.getTextureRegion("ship"));

To set up the plugin, add the following to build.gradle:

apply plugin: BuilderPlugin

ext {
    builderOutputDir = file("$buildDir/generated-sources/builder/")
}

builder{
    inputDirectories = sourceSets.main.java
    outputFolder = builderOutputDir
}

And run with "gradlew builder"
