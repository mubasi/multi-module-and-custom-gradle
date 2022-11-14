plugins {
    id(Plugins.library)
    id("project-plugins")
}

dependencies {
    implementation(project(":core"))
    implementation(platform(Firebase.bom))
    implementation(Firebase.auth_ktx)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
    testImplementation(OtherLib.turbin)

}