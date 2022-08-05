// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://jitpack.io")
        maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")

    }
    dependencies {
        classpath(MainGradle.kotlin)
        classpath(MainGradle.gradle)
        classpath(MainGradle.jacoco_gradle)
        classpath(MainGradle.sonarqube_gradle)
        classpath(MainGradle.protobuf)
        classpath(MainGradle.safe_args)
        classpath(MainGradle.google_service)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://plugins.gradle.org/m2/")
        maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")
        maven("https://jitpack.io")
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}