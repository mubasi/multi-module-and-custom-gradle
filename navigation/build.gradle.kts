plugins {
    id("com.android.library")
    id("project-plugins")
    id(Plugins.safeargs)
//    jacoco
}

//jacoco {
//    toolVersion = "0.8.7"
//    reportsDir = file("$buildDir/reports")
//}

//apply {
//    from("../jacoco.gradle.kts")
//}

dependencies {
    implementation(Navigation.ktx)
    implementation(Navigation.fragment)
}