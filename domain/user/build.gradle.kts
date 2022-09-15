plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
}

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
    implementation(platform(Firebase.bom))
    implementation(Firebase.auth_ktx)

    compileOnly(Kotlin.javax_annotation)

    testImplementation(Junit5.jupiter)
    testImplementation(Junit5.suite)
    testImplementation(OtherLib.turbin)
    testImplementation(Mockk.mockk)
    testImplementation(Junit.junit)
    testImplementation(Kotlin.coroutines_test)
    testRuntimeOnly(Junit5.vintage_engine)

}