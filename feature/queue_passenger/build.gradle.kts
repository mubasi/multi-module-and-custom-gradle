plugins {
    id(Plugins.library)
    id("project-plugins")
//    jacoco
    id("jacoco")
}

//jacoco {
//    toolVersion = "0.8.7"
//    reportsDir = file("$buildDir/reports")
//}

apply {
    from("../../jacoco.gradle.kts")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:select_location"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:passenger"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":navigation"))
    implementation(OtherLib.slidetoact)

    applyJUnitTestImplementation()
    testImplementation(OtherLib.turbin)
}