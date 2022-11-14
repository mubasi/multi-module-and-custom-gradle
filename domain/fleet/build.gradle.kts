plugins {
    id(Plugins.library)
    id("project-plugins")
}


dependencies {
    implementation(project(":core"))
    implementation(Kotlin.coroutines_android)
    implementation(platform(Firebase.bom))
    implementation(Firebase.database_ktx)

    compileOnly(Kotlin.javax_annotation)

    applyJUnitTestImplementation()
    testImplementation(OtherLib.turbin)
}