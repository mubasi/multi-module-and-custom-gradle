plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
//    id(Plugins.protobuf)
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
//        consumerProguardFiles("consumer-rules.pro")
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

//    sourceSets.getByName("main") {
//        proto {
//            srcDir("src/main/proto")
//        }
//    }

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
    implementation(Kotlin.coroutines_android)

    compileOnly(Kotlin.javax_annotation)

//    protobuf(Grpc.pb_java)
//    protobuf(Grpc.pb_java_utils)
//    protobuf(Grpc.pb_google_apis)

}

//sourceSets {
//    create("main") {
//        java {
//            srcDir("build/generated/source/proto/main/javalite")
//            srcDir("build/generated/source/proto/main/grpc")
//        }
//    }
//}

//
//protobuf {
//    protoc {
//        artifact = Grpc.protobuf_artifact
//    }
//    plugins {
//        create("javalite") {
//            artifact = Grpc.get_javalite_arifact
//        }
//        create("grpc") {
//            artifact = (Grpc.gen_artifact)
//        }
//    }
//    generateProtoTasks {
//        all().forEach { task ->
//            task.plugins {
//                create("javalite") {}
//                create("grpc") { // Options added to --grpc_out
//                    option("lite")
//                }
//            }
//        }
//    }
//}