pipeline {
    agent any
    
    environment {
     
    }
    stages {
        stage('Example stage 1') {
            steps {
               echo "Stage 1 - Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
            }
        }
        stage('Example stage 2') {
            steps {
                 echo "Stage 2 - Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
            }
        }
        
        stage('Maven Build stage 3') {
            steps {
                 echo "Maven build code: ${env.BUILD_ID} on ${env.JENKINS_URL}"
                 
                 script {
                     sh "mvn clean install package -Dintegration-tests.skip=true -Dmaven.test.skip=true"
                
                 }
            }
        }
        
        stage('Example stage 4') {
            steps {
                 echo "Deploying ............."
                 echo "Deployment complete"
            }
        } 
        
    }
}