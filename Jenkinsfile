pipeline {
    agent any

    environment {
        PUBLISH_PROFILE = credentials('azure-publish-profile')
        RESOURCE_GROUP = 'MyResourceGroup' 
        APP_NAME = 'my-notification-app'
    }

    tools {
        maven 'mvn'  // Sử dụng Maven đã cấu hình trong Jenkins
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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to Azure App Service') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'azure-publish-profile', variable: 'PUBLISH_PROFILE_PATH')]) {
                        sh '''
                        az webapp deployment source config-zip \
                            --resource-group $RESOURCE_GROUP \
                            --name $APP_NAME \
                            --src target/*.jar \
                            --subscription a38e3e69-0a2d-473f-b831-51a7e6299ffa  \
                            --publish-profile $PUBLISH_PROFILE_PATH
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
