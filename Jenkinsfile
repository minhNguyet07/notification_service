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
    }
}
