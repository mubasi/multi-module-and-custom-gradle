plugins {
    id(Plugins.library)
    id("project-plugins")
}

dependencies {
    implementation(project(":core"))

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}