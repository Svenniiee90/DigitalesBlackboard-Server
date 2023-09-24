pipeline {
    agent any
    environment {
		dockerHome = tool 'docker'
		mavenHome = tool 'maven3.8.6'
		PATH = "$dockerHome/bin:$mavenHome/bin:$PATH"
		registry = "hub-s3.codedudes.de/dbb-server"
        registryCredential = 'dockerhubaccount'
        dockerImage = ''
	}

	stages{
		stage('Checkout') {
			steps{
				sh "mvn --version"
				sh "docker version"
				echo "PATH - $PATH"
			}
		}
		
        stage ('Maven clean') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean' 
            }
        }
        
        stage ('Maven install') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install' 
            }
        }
        
        stage('Build Docker Image') {
            steps{
                script{
                   dockerImage = docker.build registry + ":$BUILD_ID"
        }
    }
}
		

        stage('Upload to dockerhub') {
            steps{
               
               sh 'docker push hub-s3.codedudes.de/dbb-server:$BUILD_ID'
               
        }
    }


        stage('Deploy Container') {
	    steps{
                sshagent(['server3']){
                sh '''
		ssh -o StrictHostKeyChecking=no root@212.227.224.201 docker stop dbb-server
    ssh -o StrictHostKeyChecking=no root@212.227.224.201 docker rm dbb-server
		ssh -o StrictHostKeyChecking=no root@212.227.224.201 docker run -d -p 8082:8080 --name dbb-server hub-s3.codedudes.de/dbb-server:$BUILD_ID
                '''
          }
      }
  }

   }
}
