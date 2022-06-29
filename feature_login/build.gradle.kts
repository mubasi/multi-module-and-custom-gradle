plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
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
        buildConfigField(
            type = "String",
            name = "VERSION_NAME",
            value = "\"${Version.versionName}\""
        )
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
    implementation(project(":domain_user"))
    implementation(project(":navigation"))

    testImplementation(Junit5.jupiter)
    testImplementation(Junit5.suite)
    testRuntimeOnly(Junit5.vintage_engine)

    testImplementation(Mockk.mockk)
    testImplementation(Mockk.agent_jvm)

    testImplementation(Junit.junit)
    testImplementation(Kotlin.coroutines_test)
    androidTestImplementation(Junit.android_junit)

    testImplementation(OtherLib.turbin)
    testImplementation(OtherLib.json)

}