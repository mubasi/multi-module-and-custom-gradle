plugins {
    id(Plugins.library)
    id("project-plugins")
}


dependencies {
    implementation(project(":core"))
    implementation(project(":navigation"))
}