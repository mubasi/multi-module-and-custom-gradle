
plugins {
    id(Plugins.application)
    id("project-plugins")
//    id(Plugins.gms)
    jacoco
    id(Plugins.sonarqube)
}

jacoco {
    toolVersion = "0.8.7"
    reportsDir = file("$buildDir/reports")
}

//apply {
//    from("../jacoco_app.gradle.kts")
//}

android {
    sourceSets.getByName("test") {
    }
}
dependencies {
    implementation(project(":core"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:home"))
    implementation(project(":navigation"))

    
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
