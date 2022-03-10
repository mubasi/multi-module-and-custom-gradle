// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url="https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(MainGradle.kotlin)
        classpath(MainGradle.gradle)
        classpath(MainGradle.jacoco_gradle)
        classpath(MainGradle.sonarqube_gradle)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven(url="https://plugins.gradle.org/m2/")
    }
}


tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}