plugins {
    id(Plugins.library)
    id("project-plugins")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":domain:location"))
    implementation(project(":domain:airport_location"))
    implementation(project(":navigation"))

    applyJUnitTestImplementation()

    androidTestImplementation(Junit.android_junit)

    testImplementation(Mockk.agent_jvm)
    testImplementation(OtherLib.turbin)
    testImplementation(OtherLib.json)

}