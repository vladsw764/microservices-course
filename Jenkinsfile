pipeline {
    agent any

    options {
        scm {
            git {
                remote {
                    url 'https://github.com/vladsw764/microservices-course.git'
                }
                branch 'main'
            }
        }
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout scm
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
    }

    post {
        success {
            echo 'Build succeeded! Deploying or further actions can be added here.'
        }
        failure {
            echo 'Build failed. Check the console output for details.'
        }
    }
}
