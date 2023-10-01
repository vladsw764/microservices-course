pipeline {
    agent {
        docker {
            image 'maven:3.9.4'
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
