plugins {
    id(Plugins.application)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    kotlin(Plugins.parcelize)
    id(Plugins.crashlytics)
    id(Plugins.gms)
    jacoco
    id(Plugins.sonarqube)
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
        applicationId = "id.bluebird.vsm.pangkalan"
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk
        versionCode = Version.versionCode
        versionName = Version.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(type = "String", name = "BASE_URL", value = "\"http://localhost\"")
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    /** used for unit-test run with Junit5 */
    tasks.withType<Test> {
        useJUnitPlatform()
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
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            isTestCoverageEnabled = true
            isShrinkResources = false
        }
    }
    sourceSets.getByName("test") {
        kotlin.srcDir(project(":feature:queue_fleet").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:fleet").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:passenger").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:user").file("src/test/kotlin"))
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
            applicationIdSuffix = ".stage"
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
    implementation(project(":feature:queue_passenger"))
    implementation(project(":feature:queue_fleet"))
    implementation(project(":feature:select_location"))
    implementation(project(":feature:login"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:user_management"))
    implementation(project(":domain:user"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:passenger"))
    implementation(project(":domain:location"))
    implementation(project(":navigation"))
    implementation(project(":feature:monitoring"))

    testImplementation(Junit5.jupiter)
    testImplementation(Junit5.suite)
    testImplementation(OtherLib.turbin)
    testImplementation(Mockk.mockk)
    testImplementation(Junit.junit)
    testImplementation(Kotlin.coroutines_test)
    testRuntimeOnly(Junit5.vintage_engine)}

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
