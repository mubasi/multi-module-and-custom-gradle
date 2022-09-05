import com.google.protobuf.gradle.*
import java.io.FileInputStream
import java.util.*

plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    id(Plugins.protobuf)
}

val keyProperties = Properties()
val keyPropertiesFile = rootProject.file("local.properties")
if (keyPropertiesFile.exists()) {
    keyProperties.load(FileInputStream(keyPropertiesFile))
}
val protobuf_platform = keyProperties.getProperty("protobuf_platform", "")

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
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    sourceSets.getByName("main") {
        proto {
            srcDir("src/main/proto")
        }
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

    api(Lifecycle.runtime_ktx)

    api(Navigation.ktx)
    api(Navigation.ui_ktx)
    api(Navigation.fragment)

    api(Koin.core)
    api(Koin.android)

    api(Grpc.protobuf_lite)
    api(Grpc.stub)

    protobuf(Grpc.pb_java)
    protobuf(Grpc.pb_java_utils)
    protobuf(Grpc.pb_google_apis)

    testApi(Mockk.mockk)
    testApi(Mockk.agent_jvm)

    api("com.google.api-client:google-api-client:1.31.5") {
        exclude(group = ("org.apache.httpcomponents"))
    }

    compileOnly(Kotlin.javax_annotation)

    api(Grpc.okhttp)

    implementation(platform(Firebase.bom))
    api(Firebase.core)
    api(Firebase.auth_ktx)
    api(Firebase.crash_ktx)
    api(Firebase.analytics_ktx)

    testApi(Junit5.jupiter)
    testApi(Junit5.suite)
    testApi(OtherLib.turbin)
    testApi(Mockk.mockk)
    testApi(Junit.junit)
    testApi(Kotlin.coroutines_test)
    testRuntimeOnly(Junit5.vintage_engine)

    testApi(OtherLib.json)
    api(OtherLib.hawk)
}

sourceSets {
    create("main") {
        java {
            srcDir("build/generated/source/proto/main/javalite")
            srcDir("build/generated/source/proto/main/grpc")
        }
    }
}


protobuf {
    protoc {
        artifact = Grpc.getProtocArtifact(protobuf_platform)
    }
    plugins {
        create("javalite") {
            artifact = Grpc.getJavaLite(protobuf_platform)
        }
        create("grpc") {
            artifact = Grpc.getGrpcArtifact(protobuf_platform)
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("javalite") {}
                create("grpc") { // Options added to --grpc_out
                    option("lite")
                }
            }
        }
    }
}