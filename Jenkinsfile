pipeline {
    agent any
    environment {
        PUBLISH_PROFILE = credentials('azure-publish-profile')
        RESOURCE_GROUP = 'MyResourceGroup' 
        APP_NAME = 'my-notification-app' 
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/minhNguyet07/notification_service.git'  
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests' 
            }
        }
        stage('Deploy to Azure App Service') {
            steps {
                script {
                    writeFile file: 'publish-profile.xml', text: PUBLISH_PROFILE  
                    sh '''
                    az webapp deployment source config-zip \
                    --resource-group $RESOURCE_GROUP \
                    --name $APP_NAME \
                    --src target/*.jar
                    '''
                }
            }
        }
    }
}
