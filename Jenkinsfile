pipeline{
    agent{
        label "admin"
    }
    environment {
        APP_NAME = "events-project"
        RELEASE = "1.0.0"
        DOCKER_USER = "rihenhouli"
        DOCKER_PASS = 'Idocker.123*'
        IMAGE_NAME = "${DOCKER_USER}" + "/" + "${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
        JENKINS_API_TOKEN = credentials("JENKINS_API_TOKEN")

    }
    stages{
        stage("Cleanup Workspace"){
            steps {
                cleanWs()
            }
        }
    }
}