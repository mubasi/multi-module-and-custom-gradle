plugins {
    id(Plugins.library)
    id("project-plugins")
    kotlin(Plugins.parcelize)
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
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:location"))

    implementation(OtherLib.tableView)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}