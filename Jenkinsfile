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
        PROJECT= "${env.SERVICE}"
        TESTING = "${env.EXECUTOR_NUMBER}-${env.BUILD_NUMBER}"
        BRANCH_NAME = "${env.BRANCH_NAME}"
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        ANDROID_HOME="${env.ANDROID_HOME}"
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
        // stage('Test Report and Code Review') {
        //     steps {
        //         withCredentials([string(credentialsId: '04398f9c-36e4-4161-b6b2-9098e7c26ad9', variable: 'TOKEN')]) {
        //             sh 'chmod +x testing.sh'
        //             sh './testing.sh $TESTING $BRANCH_NAME $BUILD_NUMBER $ANDROID_HOME $TOKEN'
        //         }
        //     }
        // }

       stage('Build and Deploy Bucket') {
            environment {
                VERSION_PREFIX = '1.0'
                NAMESPACE="mall-officer"
            }
            stages {
                stage('Deploy Multi-Stage') {
                    when {
                        branch 'multi-stage'
                    }
                    environment {
                        ALPHA   = "${env.VERSION_PREFIX}-multi${env.BUILD_NUMBER}"
                    }
                    steps {
                        sh 'chmod +x build.sh'
                        sh "./build.sh $ALPHA $BUCKET $BRANCH_NAME $BUILD_NUMBER $ANDROID_HOME $DEPLOY_BUILD_DATE"
                    }
                }
                stage('Deploy to development') {
                    when {
                        branch 'develop'
                    }
                    environment {
                        ALPHA   = "${env.VERSION_PREFIX}-alpha${env.BUILD_NUMBER}"
                    }
                    steps {
                        sh 'chmod +x build.sh'
                        sh "./build.sh $ALPHA $BUCKET $BRANCH_NAME $BUILD_NUMBER $ANDROID_HOME $DEPLOY_BUILD_DATE"
                    }
                }
                stage('Deploy to staging') {
                    when {
                        branch 'staging'
                    }
                    environment {
                        ALPHA   = "${env.VERSION_PREFIX}-beta${env.BUILD_NUMBER}"
                    }
                    steps {
                        sh 'chmod +x build.sh'
                        sh "./build.sh $ALPHA $BUCKET $BRANCH_NAME $BUILD_NUMBER $ANDROID_HOME $DEPLOY_BUILD_DATE"
                    }
                }
            }
        }

    }

    post {
        success {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT",
            factDefinitions: [[name: "debug",   template: "<a href=\"https://storage.googleapis.com/$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/debug/app-stage-debug.apk\">Dowload Debug</a>"],
                              [name: "release", template: "<a href=\"https://storage.googleapis.com/$BUCKET/$DEPLOY_BUILD_DATE/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/release/app-stage-release-unsigned.apk\">Dowload Release</a>"],
                              [name: "messages", template: "${env.COMMIT_MESSAGE}"]]
        }

        failure {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT"
        }
    }
}
