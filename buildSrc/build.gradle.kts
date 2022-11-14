plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.6.0"
}

gradlePlugin {
    plugins {
        register("project-plugins") {
            id = "project-plugins"
            implementationClass = "ProjectModulePlugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://jitpack.io")
    maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")
}

dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("gradle-plugin", "1.6.0"))
    implementation(kotlin("stdlib"))

    implementation("com.android.tools.build:gradle:7.1.3")
    implementation("com.android.tools.build:gradle-api:7.1.3")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.1")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
    implementation("org.jacoco:org.jacoco.core:0.8.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    implementation("com.google.gms:google-services:4.3.14")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3")
}