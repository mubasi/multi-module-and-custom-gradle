plugins {
    id(Plugins.library)
    id("project-plugins")
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
    implementation(project(":domain:location"))
    implementation(project(":navigation"))

    applyJUnitTestImplementation()

    androidTestImplementation(Junit.android_junit)

    testImplementation(Mockk.agent_jvm)
    testImplementation(OtherLib.turbin)
    testImplementation(OtherLib.json)

}