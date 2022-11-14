plugins {
    id(Plugins.library)
    id("project-plugins")
    id(Plugins.safeargs)
    kotlin(Plugins.parcelize)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(UiMaterial.swipe)

    compileOnly(Kotlin.javax_annotation)
    testImplementation(OtherLib.turbin)
}