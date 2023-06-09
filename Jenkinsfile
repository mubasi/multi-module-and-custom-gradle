def commitMessage() {
    sh 'git log --format=%B -n 1 HEAD > commitMessage'
    def commitMessage = readFile('commitMessage').trim()
    sh 'rm commitMessage'
    commitMessage
}

pipeline {
    agent {
        node {
            label 'slave-01'
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
                script{
                    writeFile file: 'release-note.txt', text: "${env.COMMIT_MESSAGE}"
                        sh 'ls -l release-note.txt'
                }    
                withCredentials([file(credentialsId: '73872cb6-5e1c-41e0-8b84-a630aa3580f4', variable: 'gs'),
                                 file(credentialsId: 'de65673a-ed5c-4533-b89a-fe6991c538ac', variable: 'jks'),
                                 file(credentialsId: '045c04a5-4ae8-476b-bf38-c2f03ddc9b8a', variable: 'fb'),
                                 file(credentialsId: 'f2ef7e87-c2c1-425c-9e4c-1aef88e58434', variable: 'local'),
                                 file(credentialsId: '3521ab7f-3916-4e56-a41e-c0dedd2e98e9', variable: 'sa')]) {
                sh "cp $gs google-services.json"
                sh "cp $jks officer.jks"
                sh "cp $fb firebase.json"
                sh "cp $local local.properties"
                sh "cp $sa service-account.json"
                sh "chmod 644 google-services.json officer.jks firebase.json local.properties service-account.json && cat local.properties"
                sh 'cat release-note.txt' 
                sh "gcloud auth activate-service-account --key-file service-account.json"
                sh "cp google-services.json ./app/"
                sh "chmod +x gradlew"

                }
            }
        }

        stage('Test Report and Code Review') {
            steps {
                withCredentials([string(credentialsId: '04398f9c-36e4-4161-b6b2-9098e7c26ad9', variable: 'TOKEN')]) {
                    sh 'chmod +x testing.sh'
                    sh './testing.sh $TESTING $BRANCH_NAME $BUILD_NUMBER $ANDROID_HOME $TOKEN'
                }
            }
        }

       stage('Build and Deploy Bucket') {
            environment {
                VERSION_PREFIX = '1.0'
                NAMESPACE="mall-officer"
            }
            stages {
                stage('Deploy Develop') {
                    when {
                        branch 'develop'
                    }
                    environment {
                        MULTI   = "${env.VERSION_PREFIX}-multi${env.BUILD_NUMBER}"
                        APP_ID  = "1:178128345896:android:42053fd72334ad42738e1d"
                        GROUPS  = "qa-da"
                    }
                    steps {
                        sh 'git submodule update --init --recursive'
                        sh 'chmod +x build.sh'
                        sh './build.sh $MULTI $ANDROID_HOME $APP_ID $GROUPS $BUILD_NUMBER'
                    }
                }
                stage('Deploy to staging') {
                    when {
                        branch 'staging'
                    }
                    environment {
                        ALPHA   = "${env.VERSION_PREFIX}-beta${env.BUILD_NUMBER}"
                        APP_ID  = "1:178128345896:android:42053fd72334ad42738e1d"
                        GROUPS  = "qa-da"
                    }
                    steps {
                        sh 'git submodule update --init --recursive'
                        sh 'chmod +x build.sh'
                        sh './build.sh $ALPHA $ANDROID_HOME $APP_ID $GROUPS $BUILD_NUMBER'
                    }
                }
            }
        }

    }

    post {
        success {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT",
            factDefinitions: [[name: "messages", template: "${env.COMMIT_MESSAGE}"]]
        }
        failure {
            office365ConnectorSend webhookUrl: "$TEAMS_MICROSOFT"
        }
        cleanup {
            /* clean up our workspace */
            deleteDir()
            /* clean up tmp directory */
            dir("${workspace}@tmp") {
                deleteDir()
            }
            /* clean up script directory */
            dir("${workspace}@script") {
                deleteDir()
            }
        }
    }
}
