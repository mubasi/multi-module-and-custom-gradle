import com.google.protobuf.gradle.*
import java.io.FileInputStream
import java.util.*

plugins {
    id(Plugins.library)
    id(Plugins.protobuf)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
//    jacoco
}

//apply {
//    from("../jacoco.gradle.kts")
//}

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
            sourceSets.getByName("main") {
                proto {
                    srcDir("src/main/proto")
                }
            }
        }
    }

    sourceSets.getByName("main") {
        java {
            srcDir("build/generated/source/proto/main/java")
        }
        kotlin {
            srcDir("build/generated/source/proto/main/kotlin")
        }
        proto {
            srcDir("src/main/proto")
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

    api(platform(OkHttp.bom))
    api(OkHttp.okhttp)
    api(OkHttp.interceptor)

    testApi(Mockk.mockk)
    testApi(Mockk.agent_jvm)

    compileOnly(Kotlin.javax_annotation)

    protobuf(Grpc.pb_java)
    protobuf(Grpc.pb_java_utils)
    protobuf(Grpc.pb_google_apis)

    implementation(Grpc.okhttp)
    implementation(Grpc.protobuf_lite)
    implementation(Grpc.stub)

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
        artifact = Grpc.protobuf_artifact
    }
    plugins {
        create("javalite") {
            artifact = Grpc.get_javalite_arifact
        }
        create("grpc") {
            artifact = (Grpc.gen_artifact)
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
