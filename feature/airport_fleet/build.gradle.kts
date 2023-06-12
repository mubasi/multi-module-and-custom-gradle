plugins {
    id(Plugins.library)
    id("project-plugins")
    id(Plugins.safeargs)
    id("jacoco")
}

apply {
    from("../../jacoco.gradle.kts")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:select_location"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":domain:airport_assignment"))
    implementation(project(":domain:passenger"))
    implementation(project(":domain:fleet"))
    implementation(project(":navigation"))
    implementation(UiMaterial.swipe)
    implementation(OtherLib.cameraToText)
    implementation(OtherLib.cameraView)
    implementation(OtherLib.flexBox)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}