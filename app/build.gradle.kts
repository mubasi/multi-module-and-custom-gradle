
plugins {
    id(Plugins.application)
    id("project-plugins")
    id(Plugins.gms)
    jacoco
    id(Plugins.sonarqube)
}

jacoco {
    toolVersion = "0.8.7"
    reportsDir = file("$buildDir/reports")
}

apply {
    from("../jacoco_app.gradle.kts")
}

android {
    sourceSets.getByName("test") {
        kotlin.srcDir(project(":feature:queue_fleet").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:passenger").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:user").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:fleet").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:location").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:airport_assignment").file("src/test/kotlin"))
        kotlin.srcDir(project(":domain:airport_location").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:select_location").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:user_management").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:queue_passenger").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:monitoring").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:qr_code").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:airport_fleet").file("src/test/kotlin"))
        kotlin.srcDir(project(":feature:queue_car_fleet").file("src/test/kotlin"))
    }
}
dependencies {
    implementation(project(":core"))
    implementation(project(":feature:queue_passenger"))
    implementation(project(":feature:queue_fleet"))
    implementation(project(":feature:select_location"))
    implementation(project(":feature:login"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:user_management"))
    implementation(project(":domain:user"))
    implementation(project(":domain:fleet"))
    implementation(project(":domain:passenger"))
    implementation(project(":domain:location"))
    implementation(project(":domain:airport_assignment"))
    implementation(project(":domain:airport_location"))
    implementation(project(":navigation"))
    implementation(project(":feature:monitoring"))
    implementation(project(":feature:qr_code"))
    implementation(project(":feature:airport_fleet"))
    implementation(project(":feature:queue_car_fleet"))

    testImplementation(OtherLib.turbin)
    applyJUnitTestImplementation()
}

sonarqube {
    properties {
        property("sonar.projectKey", "mallOfficer")
        property("sonar.projectName", "Mall Officer App")
        property("sonar.host.url", "https://ccq.bluebird.id/")
        property("sonar.login", "5f9e294ed6303429c4ecc8ecc5823d37197b9003")
        property("sonar.tests", "./src/test/java")
        property("sonar.test.inclusions", "**/*Test*/**")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "**/src/main/kotlin")
        property("sonar.kotlin.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths", "build/test-results/testStageDebugUnitTest")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacocoStageUnitTestReport/jacocoStageUnitTestReport.xml"
        )
    }
}
