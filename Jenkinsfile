pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'  // Replace with your actual credentials ID
        DOCKERHUB_USERNAME = 'rihenhouli'                    // Replace with your Docker Hub username
        DOCKER_IMAGE_NAME = 'events-project'                // Replace with your desired image name
        DOCKER_IMAGE_TAG = 'latest'                         // Replace with your desired image tag if needed
        NEXUS_CREDENTIALS_ID = 'nexus-credentials'          // Replace with your actual credentials ID for Nexus
        NEXUS_REPOSITORY_URL = 'http://localhost:8081/repository/maven-releases/'  // Replace with your Nexus repository URL
        SONARQUBE_URL = 'http://localhost:9000'            // Replace with your SonarQube server URL
        SONARQUBE_CREDENTIALS_ID = 'sonarqube-credentials'  // Replace with your SonarQube credentials ID
    }
    stages {
          stage('System Date') {
            steps {
                sh 'date'
            }
        }
        stage('Checkout') {
            steps {
                script {
                    retry(1) {
                        checkout([$class: 'GitSCM',
                            branches: [[name: '*/main']],  // Adjust the branch name if necessary
                            userRemoteConfigs: [[url: 'https://github.com/rihenhouli/devops-events-exam.git']]
                        ])
                    }
                }
            }
        }
        stage('Set Permissions') {
            steps {
                sh 'chmod +x ./mvnw'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean install'
            }
        }
        stage('Unit Tests') {
            steps {
                sh './mvnw test'
            }
        }
        stage('Code Quality') {
            steps {
                script {
                    withSonarQubeEnv('sonarqube') { // Use the SonarQube environment
                        sh './mvnw sonar:sonar -Dsonar.projectKey=devops-events-exam' // Replace 'your-project-key' with your actual project key
                    }
                }
            }
        }
        stage('Prepare Version') {
            steps {
                script {
                    sh 'mvn versions:set -DnewVersion=1.0.${BUILD_NUMBER}' // Example versioning
                }
            }
        }
        stage('Upload to Nexus') {
            steps {
                script {
                    nexusArtifactUploader artifacts: [[artifactId: 'eventsProject-1.0.0',
                                                        file: 'target/eventsProject-1.0.0.jar',
                                                        groupId: 'tn.esprit',
                                                        type: 'jar',
                                                        version: "1.0.${BUILD_NUMBER}"]],
                                            credentialsId: "${NEXUS_CREDENTIALS_ID}",
                                            groupId: 'tn.esprit',
                                            nexusUrl: "${NEXUS_REPOSITORY_URL}",
                                            nexusVersion: 'nexus3',
                                            protocol: 'http',
                                            repository: 'maven-releases'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}", ".")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CREDENTIALS_ID}") {
                        docker.image("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}").push("${DOCKER_IMAGE_TAG}")
                    }
                }
            }
        }
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose up -d'
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
