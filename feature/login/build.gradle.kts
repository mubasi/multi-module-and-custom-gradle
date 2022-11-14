plugins {
    id(Plugins.library)
    id("project-plugins")
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

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "VERSION_NAME",
            value = "\"${Version.versionName}\""
        )
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":navigation"))

    applyJUnitTestImplementation()
    testImplementation(Mockk.agent_jvm)
    androidTestImplementation(Junit.android_junit)
    testImplementation(OtherLib.turbin)

    testImplementation(OtherLib.json)

}