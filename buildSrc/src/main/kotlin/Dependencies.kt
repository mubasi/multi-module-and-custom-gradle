private const val kotlin_version = "1.5.21"

object MainGradle {
    const val gradle = "com.android.tools.build:gradle:4.2.2"
    const val protobuf = "com.google.protobuf:protobuf-gradle-plugin:0.8.10"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    const val google_service = "com.google.gms:google-services:4.3.3"
    const val firebase = "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta02"
    const val safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5"
    val jacoco_gradle by lazy { "org.jacoco:org.jacoco.core:0.8.7" }
    val sonarqube_gradle by lazy { "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3" }
    const val firebase_analytics = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
    const val exifinterface = "androidx.exifinterface:exifinterface:1.3.3"
}

object Compose {
    private const val compose_version = "1.0.1"
    val ui by lazy { "androidx.compose.ui:ui:$compose_version" }
    val material by lazy { "androidx.compose.material:material:$compose_version" }
    val ui_tooling_preview by lazy { "androidx.compose.ui:ui-tooling-preview:$compose_version" }
    val ui_test by lazy { "androidx.compose.ui:ui-test-junit4:$compose_version" }
    val ui_tooling by lazy { "androidx.compose.ui:ui-tooling:$compose_version" }
}

object Navigation {
    private const val nav_version = "2.3.5"

    val compose by lazy { "androidx.navigation:navigation-compose:$nav_version" }
    val ktx by lazy { "androidx.navigation:navigation-fragment-ktx:$nav_version" }
    val ui_ktx by lazy { "androidx.navigation:navigation-ui-ktx:$nav_version" }
    val fragment by lazy { "androidx.navigation:navigation-fragment-ktx:$nav_version" }

}

object Plugins {
    val application by lazy { "com.android.application" }
    val android by lazy { "android" }
    val parcelize by lazy { "parcelize" }
    val android_extensions by lazy { "android-extensions" }
    val kapt by lazy { "kapt" }
    const val protobuf = "com.google.protobuf"
    const val safeargs = "androidx.navigation.safeargs.kotlin"
    const val gms = "com.google.gms.google-services"
    const val crashlytics = "com.google.firebase.crashlytics"
}

object Grpc {
    private const val grpc_version = "1.24.2"
    private const val protobuf_version = "3.9.1"

    const val okhttp = "io.grpc:grpc-okhttp:1.32.2"
    const val protobuf_lite = "io.grpc:grpc-protobuf-lite:$grpc_version"
    const val stub = "io.grpc:grpc-stub:$grpc_version"
    const val android = "io.grpc:grpc-android:$grpc_version"
    const val java = "io.grpc:protoc-gen-grpc-kotlin:$grpc_version"
    const val protobuf_artifact = "com.google.protobuf:protoc:3.9.1"
    const val java_artifact = "io.grpc:protoc-gen-grpc-kotlin:1.37.0"
    const val get_javalite_arifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
    const val gen_artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpc_version"
    const val pb_java = "com.google.protobuf:protobuf-kotlin:$protobuf_version"
    const val pb_java_utils = "com.google.protobuf:protobuf-kotlin-util:$protobuf_version"
    const val pb_google_apis = "com.google.api.grpc:googleapis-common-protos:0.0.3"
}

object Kotlin {
    private const val coroutines_version = "1.3.5"

    const val javax_annotation = "javax.annotation:javax.annotation-api:1.3.2"
    const val std_reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    const val std_jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    const val std = "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    val ktx by lazy { "androidx.core:core-ktx:1.6.0" }
    const val coroutines_android =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
    const val coroutine_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"
}

object UiMaterial {
    private const val nav_version = "2.3.5"

    const val appcompat = "androidx.appcompat:appcompat:1.4.1"
    const val material = "com.google.android.material:material:1.3.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"
    const val fragment = "androidx.fragment:fragment-ktx:1.3.5"
}

object Koin {
    private const val koin_version = "2.0.1"

    val scope by lazy { "org.koin:koin-androidx-scope:$koin_version" }
    val viewmodel by lazy { "org.koin:koin-androidx-viewmodel:$koin_version" }
    val ext by lazy { "org.koin:koin-androidx-ext:$koin_version" }
    val logging_interceptor by lazy { "com.squareup.okhttp3:logging-interceptor:4.2.0" }
}

object Jacoco {
    const val test = "org.jacoco:org.jacoco.ant:0.8.7"
}

object Firebase {
    private const val firebase_version = "19.0.0"
    const val bom = "com.google.firebase:firebase-bom:29.0.3"
    const val core = "com.google.firebase:firebase-core"
    const val crash_ktx = "com.google.firebase:firebase-crashlytics-ktx"
    const val analytics_ktx = "com.google.firebase:firebase-analytics-ktx"
    const val auth_ktx = "com.google.firebase:firebase-auth-ktx"
    const val database_ktx = "com.google.firebase:firebase-database-ktx"
    const val ml = "com.google.firebase:firebase-ml-vision:24.0.3"
}

object Lifecycle {
    private const val lifecycle_version = "2.1.0"
    const val extensions = "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"
    const val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    const val runtime = "androidx.lifecycle:lifecycle-runtime:$lifecycle_version"
}

object Room {
    private const val room_version = "2.4.2"

    val runtime by lazy { "androidx.room:room-runtime:$room_version" }
    val rx2 by lazy { "androidx.room:room-rxjava2:$room_version" }
    val rx3 by lazy { "androidx.room:room-rxjava3:$room_version" }
    val guava by lazy { "androidx.room:room-guava:$room_version" }
    val testing by lazy { "androidx.room:room-testing:$room_version" }
    val compiler by lazy { "androidx.room:room-compiler:$room_version" }
    val paging by lazy { "androidx.room:room-paging:2.5.0-alpha01" }
}

object Junit5 {
    private const val Junit5_Version = "5.8.0"

    const val jupiter = "org.junit.jupiter:junit-jupiter:$Junit5_Version"
    const val vintage_engine = "org.junit.vintage:junit-vintage-engine:$Junit5_Version"
    const val suite = "org.junit.platform:junit-platform-suite:1.8.0"
}

object TestLib {
    private const val power_mock_version = "2.0.9"

    const val junit = "junit:junit:4.13"
    const val android_junit = "androidx.test.ext:junit:1.1.2"
    const val espresso_core = "androidx.test.espresso:espresso-core:3.3.0"
    const val mockito = "org.mockito:mockito-core:3.11.2"
    const val power_mockito = "org.powermock:powermock-core:$power_mock_version"
    const val power_mockito2 = "org.powermock:powermock-api-mockito2:$power_mock_version"
    const val power_mockito_junit_rule =
        "org.powermock:powermock-module-junit4-rule:$power_mock_version"
    const val power_mockito_junit = "org.powermock:powermock-module-junit4:$power_mock_version"
    const val mockk = "io.mockk:mockk:1.12.0"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2"
    const val core = "androidx.arch.core:core-testing:2.1.0"
}

object OtherLib {
    const val multiDex = "androidx.multidex:multidex:2.0.1"
    const val hawk = "com.orhanobut:hawk:2.0.1"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val anko = "org.jetbrains.anko:anko:0.10.8"
}