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
    implementation(platform(Firebase.bom))
    implementation(Firebase.database_ktx)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
}