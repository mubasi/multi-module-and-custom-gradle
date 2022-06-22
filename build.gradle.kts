// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")

    }
    dependencies {
        classpath(MainGradle.kotlin)
        classpath(MainGradle.gradle)
        classpath(MainGradle.jacoco_gradle)
        classpath(MainGradle.sonarqube_gradle)
        classpath(MainGradle.protobuf)
        classpath(MainGradle.safe_args)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://plugins.gradle.org/m2/")
        maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}