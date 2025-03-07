pipeline {
    agent any

    environment {
        PUBLISH_PROFILE = credentials('azure-publish-profile')
        RESOURCE_GROUP = 'MyResourceGroup' 
        APP_NAME = 'my-notification-app'
        SUBSCRIPTION_ID = 'a38e3e69-0a2d-473f-b831-51a7e6299ffa'
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/minhNguyet07/notification_service.git',
                            credentialsId: 'github-pat'
                        ]]
                    ])
                }
            }
        }

        stage('Build Application') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to Azure App Service') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'azure-publish-profile', variable: 'PUBLISH_PROFILE_PATH')]) {
                        bat '''
                        az webapp deploy ^
                            --resource-group %RESOURCE_GROUP% ^
                            --name %APP_NAME% ^
                            --src-path target/notification-service-0.0.1-SNAPSHOT.jar ^
                            --type jar ^
                            --subscription %SUBSCRIPTION_ID% ^
                        '''
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "Deployment failed. Please check the logs."
        }
        success {
            echo "Deployment successful! 🎉"
        }
    }
}
