pipeline {
    agent {
        docker {
            image 'maven:3.8.1'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
    }
}
