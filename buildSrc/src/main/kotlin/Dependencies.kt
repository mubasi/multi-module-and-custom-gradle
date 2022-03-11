const val kotlin_version = "1.5.21"

object MainGradle {
    val gradle by lazy { "com.android.tools.build:gradle:4.2.2" }
    val protobuf by lazy { "com.google.protobuf:protobuf-gradle-plugin:0.8.10" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version" }
    val google_service by lazy { "com.google.gms:google-services:4.3.3" }
    val firebase by lazy { "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta02" }
    val safe_args by lazy { "androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5" }
    val firebase_analytics by lazy { "com.google.firebase:firebase-crashlytics-gradle:2.8.1" }
    val exifinterface by lazy { "androidx.exifinterface:exifinterface:1.3.3" }
    val jacoco_gradle by lazy { "org.jacoco:org.jacoco.core:0.8.7" }
    val sonarqube_gradle by lazy { "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3" }
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
    val parcelize by lazy { "plugin.parcelize" }
    val android_extensions by lazy { "android-extensions" }
    val kapt by lazy { "kapt" }
    val protobuf by lazy { "com.google.protobuf" }
    val safeargs by lazy { "androidx.navigation.safeargs.kotlin" }
    val gms by lazy { "com.google.gms.google-services" }
    val crashlytics by lazy { "com.google.firebase.crashlytics" }
    val sonarqube by lazy { "org.sonarqube" }
}

object Grpc {
    private const val grpc_version = "1.24.2"
    private const val protobuf_version = "3.9.1"

    val okhttp by lazy { "io.grpc:grpc-okhttp:1.32.2" }
    val protobuf_lite by lazy { "io.grpc:grpc-protobuf-lite:$grpc_version" }
    val stub by lazy { "io.grpc:grpc-stub:$grpc_version" }
    val android by lazy { "io.grpc:grpc-android:$grpc_version" }
    val java by lazy { "io.grpc:protoc-gen-grpc-kotlin:$grpc_version" }
    val protobuf_artifact by lazy { "com.google.protobuf:protoc:3.9.1" }
    val java_artifact by lazy { "io.grpc:protoc-gen-grpc-kotlin:1.37.0" }
    val get_javalite_arifact by lazy { "com.google.protobuf:protoc-gen-javalite:3.0.0" }
    val gen_artifact by lazy { "io.grpc:protoc-gen-grpc-kotlin:$grpc_version" }
    val pb_java by lazy { "com.google.protobuf:protobuf-kotlin:$protobuf_version" }
    val pb_java_utils by lazy { "com.google.protobuf:protobuf-kotlin-util:$protobuf_version" }
    val pb_google_apis by lazy { "com.google.api.grpc:googleapis-common-protos:0.0.3" }
}

object Kotlin {
    private const val coroutines_version = "1.6.0"

    val javax_annotation by lazy { "javax.annotation:javax.annotation-api:1.3.2" }
    val std_reflect by lazy { "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version" }
    val std_jdk7 by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version" }
    val std by lazy { "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version" }
    val coroutines_android by lazy {
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
    }
    val coroutine_core by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version" }
    val ktx by lazy { "androidx.core:core-ktx:1.6.0" }
}

object UiMaterial {
    private const val nav_version = "2.3.5"

    val appcompat by lazy { "androidx.appcompat:appcompat:1.4.1" }
    val material by lazy { "com.google.android.material:material:1.3.0" }
    val recyclerview by lazy { "androidx.recyclerview:recyclerview:1.2.1" }
    val viewpager2 by  lazy { "androidx.viewpager2:viewpager2:1.0.0"}
    val fragment by  lazy { "androidx.fragment:fragment-ktx:1.3.5"}
}

object Koin {
    private const val koin_version = "2.0.1"

    val scope by lazy { "org.koin:koin-androidx-scope:$koin_version" }
    val viewmodel by lazy { "org.koin:koin-androidx-viewmodel:$koin_version" }
    val ext by lazy { "org.koin:koin-androidx-ext:$koin_version" }
    val logging_interceptor by lazy { "com.squareup.okhttp3:logging-interceptor:4.2.0" }
}

object Firebase {
    private const val firebase_version = "19.0.0"
    val bom by lazy { "com.google.firebase:firebase-bom:29.0.3" }
    val core by lazy { "com.google.firebase:firebase-core" }
    val crash_ktx by lazy { "com.google.firebase:firebase-crashlytics-ktx" }
    val analytics_ktx by lazy { "com.google.firebase:firebase-analytics-ktx" }
    val auth_ktx by lazy { "com.google.firebase:firebase-auth-ktx" }
    val database_ktx by lazy { "com.google.firebase:firebase-database-ktx" }
    val ml by lazy { "com.google.firebase:firebase-ml-vision:24.0.3" }
}

object Lifecycle {
    private const val lifecycle_version = "2.4.1"
    val extensions by lazy { "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"}
    val viewmodel_ktx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"}
    val runtime by lazy { "androidx.lifecycle:lifecycle-runtime:$lifecycle_version"}
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

object Junit{
    val junit by lazy { "junit:junit:4.13"}
    val android_junit by lazy { "androidx.test.ext:junit:1.1.2"}
    val espresso_core by lazy { "androidx.test.espresso:espresso-core:3.3.0"}
    val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2"}
    val core by lazy { "androidx.arch.core:core-testing:2.1.0"}
}

object Junit5 {
    private const val Junit5_Version = "5.8.0"

    val jupiter by lazy { "org.junit.jupiter:junit-jupiter:$Junit5_Version"}
    val vintage_engine by lazy { "org.junit.vintage:junit-vintage-engine:$Junit5_Version"}
    val suite by lazy { "org.junit.platform:junit-platform-suite:1.8.0"}
}

object Mockk{
    private const val version = "1.12.3"
    val mockk by lazy {  "io.mockk:mockk:$version"}
    val agent_jvm by lazy {  "io.mockk:mockk-agent-jvm:$version"}
    val android by lazy {  "io.mockk:mockk-android:$version"}
}

object OtherLib {
    val multiDex by lazy { "androidx.multidex:multidex:2.0.1"}
    val hawk by lazy { "com.orhanobut:hawk:2.0.1"}
    val timber by lazy { "com.jakewharton.timber:timber:4.7.1"}
    val anko by lazy { "org.jetbrains.anko:anko:0.10.8"}
}