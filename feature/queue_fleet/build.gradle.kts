plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    kotlin(Plugins.parcelize)
    id(Plugins.safeargs)
//    jacoco
}

//jacoco {
//    toolVersion = "0.8.7"
//    reportsDir = file("$buildDir/reports")
//}

//apply {
//    from("../jacoco.gradle.kts")
//}

android {
    compileSdk = Version.compileSdk

    defaultConfig {
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
    }

    flavorDimensions.add("env")

    productFlavors {
        register("develop") {
            dimension = "env"
        }
        register("stage") {
            dimension = "env"
        }
        register("prod") {
            dimension = "env"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:select_location"))
    implementation(project(":domain:fleet"))
    implementation(project(":navigation"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":domain:passenger"))
    implementation(Kotlin.coroutines_android)
    implementation(UiMaterial.swipe)
    implementation(OtherLib.cameraToText)
    implementation(OtherLib.cameraView)
    implementation(OtherLib.flexBox)

    compileOnly(Kotlin.javax_annotation)

    testImplementation(Junit5.jupiter)
    testImplementation(Junit5.suite)
    testImplementation(OtherLib.turbin)
    testImplementation(Mockk.mockk)
    testImplementation(Junit.junit)
    testImplementation(Kotlin.coroutines_test)
    testRuntimeOnly(Junit5.vintage_engine)
}