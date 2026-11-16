pipeline {
    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    tools {
        maven 'maven-3'   // à déclarer dans Manage Jenkins > Tools
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }
        stage('Tests') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker compose -p ecommerce up --build -d'
            }
        }
    }

    post {
        success { echo 'Build OK' }
        failure { echo 'Build KO' }
    }
}