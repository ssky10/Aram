pipeline {
  agent any
  stages {
    stage('assembleDebug') {
      steps {
        sh 'sudo ./gradlew assembleDebug --stacktrace'
      }
    }

  }
}