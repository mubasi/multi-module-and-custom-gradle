import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import com.google.protobuf.gradle.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.io.FileInputStream
import java.util.*

@Suppress("UnstableApiUsage")
class ProjectModulePlugin : Plugin<Project> {

    companion object {
        private const val ENV_DIMENSION = "env"
    }

    private val keyProperties = Properties()
    private lateinit var keyPropertiesFile: File
    private lateinit var protobufPlatform: String

    override fun apply(project: Project) {
        project.initKeyProperties()
        project.applyPlugins()
        project
            .configureAndroidDefault()
            .run {
                packagingOptions {
                    resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
                    resources.excludes.add("META-INF/DEPENDENCIES")
                    resources.excludes.add("META-INF/LICENSE.md")
                    resources.excludes.add("META-INF/LICENSE-notice.md")
                }

                print(project.name)

                flavorDimensions(ENV_DIMENSION)

//            setProtocLocation()

                buildTypes {
                    getByName("release") {
                        proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
                    }
                }

                when (this) {
                    is LibraryExtension -> {
                        configureLib(project)
                    }
                    is ApplicationExtension -> {
//                    project.setProtoc()
                        configureApp(project)
                    }
                }
            }

        project.dependencies {
            kotlinDependencies()
            koinDependencies()
        }
    }

    private val Project.androidExtension: BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
            ?: error("Project $name is not Android Project")

    private fun Project.configureAndroidDefault(): BaseExtension {
        return androidExtension.apply {
            compileSdkVersion(apiLevel = Version.compileSdk)
//            ndkVersion = Version.ndk
            defaultConfig {
                minSdk = Version.minSdk
                targetSdk = Version.targetSdk
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            project.tasks.withType<Test> {
                useJUnitPlatform()
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }

            project.tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = "1.8"
            }
        }
    }

    private fun Project.initKeyProperties() {
        keyPropertiesFile = project.rootProject.file("local.properties")
        if (keyPropertiesFile.exists()) {
            keyProperties.load(FileInputStream(keyPropertiesFile))
        }
        protobufPlatform = keyProperties.getProperty("protobuf_platform", "")
    }

    private fun Project.applyPlugins() {
        apply {
            plugin(Plugins.kotlin_android)
            plugin("kotlin-kapt")
            plugin(Plugins.kotlin_parcelize)
        }
    }

    private fun LibraryExtension.configureLib(project: Project) {
        defaultConfig {
            consumerProguardFiles("consumer-rules.pro")
        }

        productFlavors {
            register("develop") {
                dimension = ENV_DIMENSION
            }
            register("stage") {
                dimension = ENV_DIMENSION
            }
            register("prod") {
                dimension = ENV_DIMENSION
            }
        }

        buildFeatures {
            dataBinding = true
        }
    }

    private fun ApplicationExtension.configureApp(project: Project) {
        project.apply {
            plugin(Plugins.kotlin_parcelize)
            plugin(Plugins.safeargs)
            plugin(Plugins.crashlytics)
        }

        defaultConfig {
            applicationId = "id.multi.module.custome"
            vectorDrawables.useSupportLibrary = true
            multiDexEnabled = true
            versionCode = Version.versionCode
            versionName = Version.versionName
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://localhost\"")
        }

        signingConfigs {
            create("pangkalan") {
                keyAlias = ""
                keyPassword = ""
                storePassword = ""
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                isDebuggable = false
                isShrinkResources = true
                signingConfig = signingConfigs.getByName("pangkalan")
            }
            getByName("debug") {
                isMinifyEnabled = false
                isDebuggable = true
                isShrinkResources = false
                isTestCoverageEnabled = true
            }
        }

        buildFeatures {
            dataBinding = true
        }

        productFlavors {
            register("develop") {
                buildConfigField("String", "BASE_URL",
                    "\"devlocalhost\"")
                buildConfigField(
                    "String",
                    "FIREBASE_URL",
                    "\"devlocalhost\""
                )
                dimension = ENV_DIMENSION
                versionName = "${Version.appName} Dev"
                applicationIdSuffix = ".dev"
                manifestPlaceholders["appName"] = Version.appNameDev
            }
            register("stage") {
                buildConfigField("String", "BASE_URL",
                    "\"stglocalhost\"")
                buildConfigField(
                    "String",
                    "FIREBASE_URL",
                    "\"stglocalhost\""
                )
                dimension = ENV_DIMENSION
                versionName = "${Version.appName} Staging"
                applicationIdSuffix = ".stage"
                manifestPlaceholders["appName"] = Version.appNameStaging
            }
            register("prod") {
                buildConfigField("String", "BASE_URL",
                    "\"localhost\"")
                buildConfigField(
                    "String",
                    "FIREBASE_URL",
                    "\"localhost\""
                )
                dimension = ENV_DIMENSION
                versionName = Version.appName
                manifestPlaceholders["appName"] = Version.appName
            }
        }
    }

//    private fun BaseExtension.setProtocLocation() {
//        sourceSets {
//            getByName("main") {
//                proto {
//                    srcDir("src/main/proto")
//                }
//                java {
//                    srcDir("build/generated/source/proto/main/javalite")
//                    srcDir("build/generated/source/proto/main/grpc")
//                }
//            }
//        }
//    }

    private fun Project.setProtoc() {
        /*protobuf {
            protoc {
                artifact = DefineDependencies.getProtocArtifact(protobufPlatform)
            }
            plugins {
                create("javalite") {
                    artifact = DefineDependencies.getJavaLite(protobufPlatform)
                }
                create("grpc") {
                    artifact = DefineDependencies.getGrpcArtifact(protobufPlatform)
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
        }*/
    }

    private fun DependencyHandlerScope.kotlinDependencies(configurationName: String = "implementation") {
        add(configurationName, Kotlin.std)
        add(configurationName, Kotlin.ktx)
        add(configurationName, Kotlin.coroutine_core)
        add(configurationName, Kotlin.coroutines_android)
    }

    private fun DependencyHandlerScope.koinDependencies(configurationName: String = "implementation") {
        add(configurationName, Koin.core)
        add(configurationName, Koin.test)
        add(configurationName, Koin.android)
        add(configurationName, Koin.junit5)
    }
}