plugins {
    id(Plugins.library)
    id("project-plugins")
    kotlin(Plugins.parcelize)
}


dependencies {
    implementation(project(":core"))
    implementation(platform(Firebase.bom))
    implementation(Firebase.database_ktx)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
    testImplementation(OtherLib.turbin)
}