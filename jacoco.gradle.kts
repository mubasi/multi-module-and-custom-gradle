configurations.create("jacoco")
configurations.create("jacocoRuntime")

val filter = mutableSetOf(
    "**/R.class",
    "**/R$*.class",
    "**/R$*.*",
    "**/R$*.*",
    "**/R$.*",
    "**/R*.*",
    "**/R$.stylable",
    "**/R.stylable",
    "**/R.*",
    "**/*Activity.*",
    "**/Fragment*.*",
    "**/*Cache.*",
    "**/BuildConfig.*",
    "**/*Manifest.*",
    "android/**/*",
    "androidx/**/*",
    "**/*Builder.*",
    "**/utils/**/*",
    "**/api/*",
    "**/di/*",
    "**/res/**/*"
)

val unitTestReport = task<JacocoReport>("jacocoStageUnitTestReport") {
    dependsOn("testStageDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports for Debug build"
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    sourceDirectories.setFrom(
        fileTree("${project.buildDir}") {
            include(
                "src/main/java/",
                "src/main/kotlin/"
            )
        }
    )
    classDirectories.setFrom(
        fileTree(project.buildDir) {
            include(
                "**/tmp/kotlin-classes/stageDebug/**"
            )
            exclude(filter)
        }
    )

    executionData.setFrom(
        fileTree(project.buildDir) {
            include(
                "jacoco/testStageDebugUnitTest.exec"
            )
        }
    )
}

task("jacocoTestReport") {
    dependsOn("testStageDebugUnitTest")
    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "report",
                "classname" to "org.jacoco.ant.ReportTask",
                "classpath" to "jacocoAnt",
                "classpath" to configurations.getByName("jacocoAnt").asPath
            )
        }
        ant.withGroovyBuilder {
            "report"{
                "executiondata"{
                    ant.withGroovyBuilder {
                        "file"("file" to "${buildDir.path}/jacoco/testStageDebugUnitTest.exec")
                    }
                }
                "structure"("name" to "Example") {
                    "classfiles"{
                        "fileset"("dir" to "${project.buildDir}/tmp/kotlin-classes/stageDebug")
                    }
                    "sourcefiles"{
                        "fileset"("dir" to "src/main/kotlin")
                    }
                }
            }
        }
    }
}

val offlineInstrumentedOutputDir = "$buildDir.path/intermediates/classes-instrumented/debug"
gradle.taskGraph.whenReady {
    if (this.hasTask(instrument)) {
        tasks.withType(Test::class.java) {
            doFirst {
                systemProperty(
                    "jacoco-agent.destfile",
                    "${buildDir.path}/jacoco/testStageDebugUnitTest.exec"
                )
                classpath =
                    files(offlineInstrumentedOutputDir) + classpath + configurations.getByName("jacocoRuntime")
            }
        }
    }
}

val instrument = task("instrument") {
    dependsOn("compileStageDebugUnitTestSources")
    doLast {
        println("Instrumenting classes")
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "instrument",
                "classname" to "org.jacoco.ant.InstrumentTask",
                "classpath" to configurations.getByName("jacocoAnt").asPath
            )
        }
        ant.withGroovyBuilder {
            "instrument"("destdir" to "offlineInstrumentedOutputDir") {
                "fileset"("dir" to "$buildDir.path/tmp/kotlin-classes/stageDebug")
            }
        }
    }
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
        finalizedBy(unitTestReport)
    }
}