import com.tothenew.utility
//object creation
def myObject = new utility()


pipeline{
    agent{
        label 'slave2'
    }
   
    environment{
      git_url="git@github.com:manisha28nagarkoti/${service-name}.git"
      docker_repo="337764066236.dkr.ecr.ap-south-1.amazonaws.com"
      devops_git_url='git@gitlab.intelligrape.net:bharti-axa/devops.git'

      image_name= "${docker_repo}/${service_name}"
      
      credential_git='jenkin-slave-git'
      env.APPLICATION="${JOB_BASE_NAME}"
      
      
    }
    
    
   
    stages{

     stage('notifier'){
       steps{
         myObject.Notifier()
       }
     }
    

     stage('Clean WorkSpace'){
      steps{
         sh 'rm -rf *'
         sh 'rm -rf .git'
       }
     }
     stage('Git CheckOut') { 
      // Get some code from a GitHub repository
       steps{
       
        
        
        myObject.Git_checkOut(git_url)
        
        
       }
  }
   stage('Gaining Access for deployment') {
    steps{
     myObject.Gaining_access_for_ecr()

    }
  }
  stage('ECR Login'){
      steps{
        myObject.Ecr_login(region,docker_repo)}
  }
stage('Build Docker Image') {
      steps{
        myObject.Build_Docker_Image(image_name)}
  }
  
stage('Push Image to ECR'){
      steps{
        myObject.Push_Image_to_ECR(docker_repo,service_name,tag,image_name)}
  }
   
stage('Image cleanup'){
     steps{
       myObject.Image_cleanup(docker_repo,image_name,tag)}
      
  }

  stage('helm update'){
    step{
      myObject.Helm_update(devops_git_url,)
    }
      
  }
 }
}


