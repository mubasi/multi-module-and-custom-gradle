plugins {
    id(Plugins.library)
    id("project-plugins")

    kotlin(Plugins.parcelize)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain:user"))
    implementation(project(":navigation"))

    applyJUnitTestImplementation()
    testImplementation(Mockk.agent_jvm)
    androidTestImplementation(Junit.android_junit)
    

    testImplementation(OtherLib.json)

}