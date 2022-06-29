import java.io.FileInputStream
import java.util.*

plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
}

val keyProperties = Properties()
val keyPropertiesFile = rootProject.file("local.properties")
if (keyPropertiesFile.exists()) {
    keyProperties.load(FileInputStream(keyPropertiesFile))
}


android {
    compileSdk = Version.compileSdk

    defaultConfig {
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk
        multiDexEnabled = true
        buildConfigField(
            type = "String",
            name = "VERSION_NAME",
            value = "\"${Version.versionName}\""
        )
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
        debug {
            isMinifyEnabled = false
        }
    }

    flavorDimensions.add("env")

    productFlavors {
        register("prod") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "${keyProperties["server_base_url_production"]}"
            )
            buildConfigField(
                type = "String",
                name = "FIREBASE_URL",
                value = "${keyProperties["firebase_url_secondary_production"]}"
            )
            dimension = "env"
        }
        register("stage") {
            dimension = "env"
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "${keyProperties["server_base_url_staging"]}"
            )
            buildConfigField(
                type = "String",
                name = "FIREBASE_URL",
                value = "${keyProperties["firebase_url_secondary_staging"]}"
            )
        }
        register("develop") {
            dimension = "env"
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "${keyProperties["server_base_url_dev"]}"
            )
            buildConfigField(
                type = "String",
                name = "FIREBASE_URL",
                value = "${keyProperties["firebase_url_secondary_dev"]}"
            )
        }
    }

    buildFeatures { dataBinding = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    api(Kotlin.ktx)
    api(Kotlin.coroutine_core)
    api(Kotlin.coroutines_android)

    api(UiMaterial.appcompat)
    api(UiMaterial.recyclerview)
    api(UiMaterial.material)

    api(Navigation.ktx)
    api(Navigation.ui_ktx)
    api(Navigation.fragment)

    api(Koin.core)
    api(Koin.android)

    testApi(Mockk.mockk)
    testApi(Mockk.agent_jvm)

    compileOnly(Kotlin.javax_annotation)

    api(Grpc.okhttp)

    testApi(Junit5.jupiter)
    testApi(Junit5.suite)
    testRuntimeOnly(Junit5.vintage_engine)

    testApi(Junit.junit)
    testApi(Kotlin.coroutines_test)
    androidTestApi(Junit.android_junit)
    androidTestApi(Junit.espresso_core)

    testApi(OtherLib.turbin)
    testApi(OtherLib.json)
    api(OtherLib.hawk)
}