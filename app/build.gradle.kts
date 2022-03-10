import java.util.Properties
import java.io.FileInputStream

plugins {
    id(Plugins.application)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    jacoco
    id("org.sonarqube")
}

val keyProperties = Properties()
val keyPropertiesFile = rootProject.file("local.properties")
if (keyPropertiesFile.exists()) {
    keyProperties.load(FileInputStream(keyPropertiesFile))
}

jacoco {
    toolVersion = "0.8.7"
    reportsDir = file("$buildDir/reports")
}

apply {
    from("../jacoco.gradle.kts")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "id.bluebird.mall.officer"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("config") {
            keyAlias = keyProperties["keyAlias"] as String
            keyPassword = keyProperties["keyPassword"] as String
            storeFile = file(keyProperties["storeFile"] as String)
            storePassword = keyProperties["storePassword"] as String
        }
    }


    buildTypes {
        /** keystore(jsk) disimpan di Digital Outlet sharepoints folder "Keys"*/
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFile(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                )
            )
            signingConfig = signingConfigs.getByName("config")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            isTestCoverageEnabled = true
            isShrinkResources = false
        }
    }

    flavorDimensions("env")

    productFlavors {
        create("prod") {
            manifestPlaceholders += mapOf("appName" to "Officer App")
            dimension = "env"
        }
        create("stage") {
            dimension = "env"
            versionNameSuffix = " Staging"
            applicationIdSuffix = ".staging"
            manifestPlaceholders["appName"] = "Officer App (Staging)"
        }
        create("develop") {
            dimension = "env"
            versionNameSuffix = " Dev"
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appName"] = "Officer App (Dev)"

        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}
dependencies {
    implementation(Navigation.ktx)
    implementation(Navigation.ui_ktx)
    implementation(Navigation.fragment)

    implementation(Koin.scope)
    implementation(Koin.viewmodel)
    implementation(Koin.ext)

    implementation(Kotlin.ktx)
    implementation(UiMaterial.appcompat)
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

sonarqube {
    properties {
        property("sonar.projectKey", "mallOfficer")
        property("sonar.projectName", "Mall Officer App")
        property("sonar.host.url", "https://ccq.bluebird.id/")
        property("sonar.login", "5f9e294ed6303429c4ecc8ecc5823d37197b9003")
        property("sonar.tests", "./src/test/java")
        property("sonar.test.inclusions", "**/*Test*/**")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "./src/main/")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths", "build/test-results/testStageDebugUnitTest")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacocoStageUnitTestReport/jacocoStageUnitTestReport.xml"
        )
    }
}


