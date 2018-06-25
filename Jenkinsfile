pipeline {
    agent any 
    stages {
        stage('build') {
            steps {
                echo 'build stage'
                withMaven(maven: '3.3.9') {
                    sh 'mvn formatter:validate'
                }
            }
        }
        stage('test') {
            steps {
                withMaven(maven: '3.3.9') {
                    sh 'mvn clean test'
                }
            }
        }
    }
    post {
        success {
            echo 'success'
        }
        failure {
            script {
                def urlBase = "${env.GIT_URL}".replaceAll(/\.git/, "")
                def commitUrl = urlBase + "/commit/${env.GIT_COMMIT}"
                def commitsUrl = urlBase + "/commits/${env.GIT_BRANCH}"
                def jenkinsUrl = "${env.BUILD_URL}".replaceAll(/\/\d+\//, "")
                def commitMessage = 
                    sh(
                        script: 'git shortlog ${GIT_COMMIT}^..${GIT_COMMIT}',
                        returnStdout: true
                    ).trim()
                commitMessage = commitMessage.replaceAll(/^[^\n]*\n\s*/,"")
                emailext (
                    subject: "Source Code Issues Detected",
                    body: """<p>Source code issues detected in commit <a href='${commitUrl}'>${env.GIT_COMMIT}</a>:
                             <br/>
                             &nbsp;&nbsp;<strong>&quot;${commitMessage}&quot;</strong>.</p>
                             <p>View the issues in <a href='${commitsUrl}'>github</a>.</p>
                             <p>View the issues in <a href='${jenkinsUrl}'>jenkins</a>.</p>""",
                    mimeType: "text/html",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                )
            }
        }
    }
}
