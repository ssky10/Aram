pipeline {
  agent any
  stages {
    stage('assembleDebug') {
      steps {
        sh 'chmod +x ./gradlew'
        sh './gradlew assembleDebug --stacktrace'
      }
    }

  }
}