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
    implementation(project(":domain:fleet"))
    implementation(project(":navigation"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":domain:passenger"))
    implementation(UiMaterial.swipe)
    implementation(OtherLib.cameraToText)
    implementation(OtherLib.cameraView)
    implementation(OtherLib.flexBox)
    implementation(OtherLib.slidetoact)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}