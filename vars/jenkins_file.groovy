import com.tothenew.utility
//object creation

//String git_url,String docker_repo,String credential_git,
def call(String credential_git,String region){
myObject = new utility()

pipeline{
    agent{
        label 'slave2'
    }
   
    environment{
      git_url="git@github.com:manisha28nagarkoti/${service-name}.git"
      docker_registry = "${docker-registry}"
      //devops_git_url='git@gitlab.intelligrape.net:bharti-axa/devops.git'
//docker_repo='"${param.docker_url}"/"${param.service-name}"-service"' 
      docker_repo= '${param.docker_registry}"/"${param.service-name}'
      
      //credential_git='jenkin-slave-git'
      //env.APPLICATION="${JOB_BASE_NAME}"
      
      
    }
    
    
   
    stages{

//      stage('notifier'){
//        steps{
 //           script{
//          myObject.Notifier()
 //           }
//        }
//      }
    

     stage('Clean WorkSpace'){
      steps{
         sh 'rm -rf *'
         sh 'rm -rf .git'
       }
     }
     stage('Git Clone') { 
      // Get some code from a GitHub repository
       steps{
       
        
        script{
        myObject.Git_clone(git_url)
        
        }
       }
    }
//    stage('Gaining Access for deployment') {
//     steps{
       //script{
//      myObject.Gaining_access()
       //}

//     }
//   }
  stage('ECR Login'){
      steps{
      script{
        myObject.Ecr_login(region,docker_registry)
        }
       } 
  }
stage('Build Docker Image') {
      steps{
      script{
        myObject.Build_Docker_Image(docker_repo)
        }
        }
  }
  
stage('Push Image to ECR'){
      steps{
      script{
        myObject.Push_Image_to_ECR(docker_repo,docker_registry)
        }
        }
  }
   
stage('Image cleanup'){
     steps{
     script{
       myObject.Image_cleanup()}
       }
      
  }

//   stage('helm update'){
//     step{
//       myObject.Helm_update(devops_git_url,service_name)
//     }
      
//   }
 }
}

}



