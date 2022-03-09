// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        jcenter()
    }
    dependencies {
        classpath(MainGradle.kotlin)
        classpath(MainGradle.gradle)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
}


tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}