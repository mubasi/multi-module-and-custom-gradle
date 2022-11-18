plugins {
    id(Plugins.library)
    id("project-plugins")
    id(Plugins.safeargs)
    kotlin(Plugins.parcelize)
    id("jacoco")
}

//jacoco {
//    toolVersion = "0.8.7"
//    reportsDir = file("../app/build/reports")
//}

apply {
    from("../../jacoco.gradle.kts")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(UiMaterial.swipe)

    compileOnly(Kotlin.javax_annotation)
    testImplementation(OtherLib.turbin)
}