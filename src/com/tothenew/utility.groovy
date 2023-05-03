package com.tothenew

//notifier........
def notifier(){
  wrap([$class: 'BuildUser']) {
    stage('sending notification'){
        currentBuild.displayName = "#" + currentBuild.number + ", Started by ${env.BUILD_USER}" + ", from Branch: ${env.branch_name}"
        env.use=env.BUILD_USER
        sh 'echo $use'
        //notifyStarted()
     }
    }
    
     }
//git clone
def git_clone(String repo_url){ 
withCredentials([sshUserPrivateKey(credentialsId: env.credential_git, keyFileVariable: 'SSH_KEY')]) {
                    sh """
                        eval `ssh-agent`
                        ssh-add $SSH_KEY
                        ssh-keyscan github.com >> ~/.ssh/known_hosts
                        git clone $repo_url .
                    """
                }
  
}


def Gain_Access(){
      sh '`aws sts assume-role --role-arn arn:aws:iam::388606509852:role/Shared-Jenkins-Role --role-session-name nitin > op`'
      env.AWS_SECRET_ACCESS_KEY = sh(script:'cat op | grep SecretAccessKey | awk \'{print $2}\' | sed -s \'s/"//g; s/,//g\'', returnStdout: true).trim()
      env.AWS_ACCESS_KEY_ID = sh(script:'cat op | grep AccessKeyId | awk \'{print $2}\' | sed -s \'s/"//g; s/,//g\'', returnStdout: true).trim()
      env.AWS_SESSION_TOKEN = sh(script:'cat op | grep SessionToken | awk \'{print $2}\' | sed -s \'s/"//g; s/,//g\'', returnStdout: true).trim()

}
//ECR LOGIN.......
def ECR_login(String region, String ecr_repo){
 sh '''
   aws ecr get-login-password --region $region| docker login --username AWS --password-stdin $ecr_repo

 '''
}
//BUILD IMAGE
def Build_image(String image_name){
 sh'''
 sudo docker build -t $image_name .
 
 '''
}

//PUSH IMAGE TO ECR.....
def Push_image(String ecr_repo,String image_name){
sh '''
docker tag $image_name:$tag $ecr_repo/$repo_name:$tag
docker push $ecr_repo/$image_name:$tag

'''
}
//IMAGE CLEANUP
def Image_cleanup(String ecr_repo,String repo_name,String tag){
  def image_name = $ecr_repo/$repo_name
sh 'echo removing $image_name and $image_name:$tag'
  sh 'docker rmi $image_name $image_name:$tag'

}

//update helm
def Helm_update(String devops_git_url,String service_name){
 env.new_docker_image=docker_repo+":"+env.tag
  service = split

        sh """

          rm -rf *
          
          git clone -b "$devops_branch_name" "$devops_git_url"
          cd devops
          sed -E -i 's/($service_name:).*/$service_name:$tag/' 'service/values.yaml'
          git add *
          git commit -m "adding"
          git push origin kubernetes    
        """


}
