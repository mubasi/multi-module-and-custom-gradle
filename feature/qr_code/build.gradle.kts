plugins {
    id(Plugins.library)
    id("project-plugins")

    id("jacoco")
}

apply {
    from("../../jacoco.gradle.kts")
}

dependencies {

    implementation(project(":core"))
    implementation(project(":domain:location"))
    implementation(project(":navigation"))
    implementation(OtherLib.slidetoact)

    applyJUnitTestImplementation()
}