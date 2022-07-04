import java.io.FileInputStream
import java.util.*

plugins {
    id(Plugins.application)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    kotlin(Plugins.parcelize)
    jacoco
    id(Plugins.sonarqube)
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
    compileSdk = Version.compileSdk
    defaultConfig {
        applicationId = "id.bluebird.mall.officer"
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk
        versionCode = Version.versionCode
        versionName = Version.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(type = "String", name = "BASE_URL", value = "\"http://localhost\"")
        vectorDrawables.useSupportLibrary = true
    }

    /** used for unit-test run with Junit5 */
    tasks.withType<Test> {
        useJUnitPlatform()
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

    sourceSets {
        val main by getting
        main.res.srcDirs("src/main/res")
    }

    flavorDimensions.add("env")

    productFlavors {
        create("prod") {
            manifestPlaceholders["appName"] = Version.appName
            dimension = "env"
        }
        create("stage") {
            dimension = "env"
            versionNameSuffix = " Staging"
            applicationIdSuffix = ".staging"
            manifestPlaceholders["appName"] = Version.appNameStaging
        }
        create("develop") {
            dimension = "env"
            versionNameSuffix = " Dev"
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appName"] = Version.appNameDev
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
    implementation(project(":core"))
    implementation(project(":feature_queue_passenger"))
    implementation(project(":feature_login"))
    implementation(project(":feature_splash"))
    implementation(project(":domain_user"))
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
        property("sonar.sources", "**/src/main/kotlin")
        property("sonar.kotlin.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths", "build/test-results/testStageDebugUnitTest")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacocoStageUnitTestReport/jacocoStageUnitTestReport.xml"
        )
    }
}
