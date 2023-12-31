import com.google.protobuf.gradle.*
import java.io.FileInputStream
import java.util.*

plugins {
    id(Plugins.library)
    id("project-plugins")
    id(Plugins.protobuf)
}

val keyProperties = Properties()
val keyPropertiesFile = rootProject.file("local.properties")
if (keyPropertiesFile.exists()) {
    keyProperties.load(FileInputStream(keyPropertiesFile))
}
val protobuf_platform = keyProperties.getProperty("protobuf_platform", "")

android {

    defaultConfig {
        multiDexEnabled = true
        buildConfigField(
            type = "String",
            name = "VERSION_NAME",
            value = "\"${Version.versionName}\""
        )
        buildConfigField(
            type = "String",
            name = "SPLASH_KEY",
            value = "\"${Version.versionName}\""
        )
    }

    sourceSets.getByName("main") {
        proto {
            srcDir("src/main/proto")
        }
    }

    productFlavors {
        getByName("prod") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"localhost\""
            )
        }
        getByName("stage") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"stglocalhost\""
            )
            buildConfigField(
                type = "String",
                name = "VERSION_NAME",
                value = "\"${Version.versionName} Staging\""
            )
        }
        getByName("develop") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"devlocalhost\""
            )
            buildConfigField(
                type = "String",
                name = "VERSION_NAME",
                value = "\"${Version.versionName} Dev\""
            )
        }
    }
}

dependencies {

    api(Kotlin.ktx)
    api(Kotlin.coroutine_core)
    api(Kotlin.coroutines_android)

    api(UiMaterial.appcompat)
    api(UiMaterial.recyclerview)
    api(UiMaterial.material)

    api(Lifecycle.viewmodel_ktx)

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

    api("com.google.api-client:google-api-client:1.31.5") {
        exclude(group = ("org.apache.httpcomponents"))
    }

    compileOnly(Kotlin.javax_annotation)

    api(Grpc.okhttp)

    api(platform(Firebase.bom))
    api(Firebase.core)
    api(Firebase.auth_ktx)
    api(Firebase.crash_ktx)
    api(Firebase.analytics_ktx)

    testApi(OtherLib.json)
    api(OtherLib.hawk)
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