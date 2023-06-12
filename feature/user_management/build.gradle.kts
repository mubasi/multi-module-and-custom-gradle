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
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(UiMaterial.swipe)

    applyJUnitTestImplementation()

    compileOnly(Kotlin.javax_annotation)
    
}