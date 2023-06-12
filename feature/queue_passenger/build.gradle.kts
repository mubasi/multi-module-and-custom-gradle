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
    implementation(project(":feature:select_location"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:passenger"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":navigation"))
    implementation(OtherLib.slidetoact)

    applyJUnitTestImplementation()
    
}