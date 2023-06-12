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
    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}