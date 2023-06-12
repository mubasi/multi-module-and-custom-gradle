plugins {
    id(Plugins.library)
    id("project-plugins")
    id(Plugins.safeargs)
    kotlin(Plugins.parcelize)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:location"))

    implementation(OtherLib.tableView)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}