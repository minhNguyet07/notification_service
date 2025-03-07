pipeline {
    agent any

    environment {
        RESOURCE_GROUP = 'MyResourceGroup' 
        APP_NAME = 'my-notification-app'
        PUBLISH_PROFILE = credentials('azure-publish-profile')
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
                    sh '''
                    echo "$PUBLISH_PROFILE" > publish-profile.txt
                    
                    az webapp deployment source config-zip \
                        --resource-group $RESOURCE_GROUP \
                        --name $APP_NAME \
                        --src target/*.jar \
                        --subscription <YOUR_AZURE_SUBSCRIPTION_ID> \
                        --publish-profile publish-profile.txt
                    '''
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
