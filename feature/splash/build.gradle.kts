plugins {
    id(Plugins.library)
    id("project-plugins")
//    jacoco
}


dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":navigation"))
}