def commitMessage() {
    sh 'git log --format=%B -n 1 HEAD > commitMessage'
    def commitMessage = readFile('commitMessage').trim()
    sh 'rm commitMessage'
    commitMessage
}

pipeline {
    agent {
        node {
            label 'slave-00'
            customWorkspace "workspace/${env.BRANCH_NAME}/src/git.bluebird.id/outlet-mall_officer"
        }
    }
    environment {
        SERVICE="outlet-mall_officer"
        BUCKET="outlet-apk"
        TEAMS_MICROSOFT = credentials('786c8d07-a295-487e-83d2-1c4cbe71e2a2')
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    env.COMMIT_MESSAGE = commitMessage()
                    env.DEPLOY_BUILD_DATE = sh(returnStdout: true, script: "date +'%F'").trim()
                }
                sh 'printenv'
            }
        }
        stage('Prepare') {
            steps {
                withCredentials([file(credentialsId: 'fa022f45-5480-435e-9c46-67e24a57e285', variable: 'gs'),
                                 file(credentialsId: 'de65673a-ed5c-4533-b89a-fe6991c538ac', variable: 'jks'),
                                 file(credentialsId: 'f2ef7e87-c2c1-425c-9e4c-1aef88e58434', variable: 'local'),
                                 file(credentialsId: '3521ab7f-3916-4e56-a41e-c0dedd2e98e9', variable: 'sa')]) {
                sh "cp $jks officer.jks"
                sh "cp $local local.properties"
                sh "cp $sa service-account.json"
                sh "chmod 644 officer.jks local.properties service-account.json && cat local.properties"
                sh "gcloud auth activate-service-account --key-file service-account.json"
                sh "chmod +x gradlew"
                }
            }
        }
        stage('Jacoco Report') {
           steps {
                sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew clean"
                sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew jacocoTestReport"
           }
        }
        stage('Code review') {
            environment {
                scannerHome = tool 'sonarQubeScanner'
            }
            steps {
                withSonarQubeEnv('sonarQube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/') || env.BRANCH_NAME.startsWith('feature/') 
                    || env.BRANCH_NAME.startsWith('bugfix/') ) {
                        sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew clean assembleStage -PBUILD_NUMBER=${env.BUILD_NUMBER}"
                        sh "gsutil cp -r app/build/outputs/apk/stage/* gs://$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"
                        sh 'rm service-account.json local.properties officer.jks'
                    }
                    else if (env.BRANCH_NAME == 'develop') {
                        sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew clean assembleStage -PBUILD_NUMBER=${env.BUILD_NUMBER}"
                        sh "gsutil cp -r app/build/outputs/apk/stage/* gs://$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"
                        sh 'rm service-account.json local.properties officer.jks'
                    }
                    else if (env.BRANCH_NAME == 'staging') {
                        sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew clean assembleStage -PBUILD_NUMBER=${env.BUILD_NUMBER}"
                        sh "gsutil cp -r app/build/outputs/apk/stage/* gs://$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"
                        sh 'rm service-account.json local.properties officer.jks'
                    }
                    else if (env.BRANCH_NAME == 'test-sonar-qube') {
                        sh "ANDROID_HOME=${env.ANDROID_HOME} ./gradlew clean assembleStage -PBUILD_NUMBER=${env.BUILD_NUMBER}"
                        sh "gsutil cp -r app/build/outputs/apk/stage/* gs://$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"
                        sh 'rm service-account.json local.properties officer.jks'
                    }
                    else {
                        sh 'exit'
                    }
                }
            }
        }
    }

    post {
        success {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT",
            factDefinitions: [[name: "debug",   template: "<a href=\"https://storage.googleapis.com/$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/debug/app-stage-debug.apk\">Dowload Debug</a>"],
                              [name: "release", template: "<a href=\"https://storage.googleapis.com/$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/release/app-stage-release.apk\">Dowload Release</a>"],
                              [name: "messages", template: "${env.COMMIT_MESSAGE}"]]
        }
        failure {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT"
        }
    }
}
