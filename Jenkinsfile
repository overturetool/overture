node {
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    checkout scm

    // Mark the code build
    stage ('Validate'){

    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: '/var/lib/jenkins/internal-resources/settings.xml') {

        // Run the maven build
        // sh "mvn validate"
    }}


    stage ('Clean'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: '/var/lib/jenkins/internal-resources/settings.xml') {

        // Run the maven build
        sh "mvn clean"
    }}

    stage ('Compile'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: '/var/lib/jenkins/internal-resources/settings.xml') {

        // Run the maven build
        sh "mvn compile"
    }}

    stage ('Package'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: '/var/lib/jenkins/internal-resources/settings.xml') {

        // Run the maven build
        sh "mvn package"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    }}

    step([
            $class            : 'GitHubCommitStatusSetter',
            errorHandlers     : [[$class: 'ShallowAnyErrorHandler']],
            statusResultSource: [
                    $class : 'ConditionalStatusResultSource',
                    results: [
                            [$class: 'BetterThanOrEqualBuildResult', result: 'SUCCESS', state: 'SUCCESS', message: currentBuild.description],
                            [$class: 'BetterThanOrEqualBuildResult', result: 'FAILURE', state: 'FAILURE', message: currentBuild.description],
                            [$class: 'AnyBuildResult', state: 'FAILURE', message: 'Loophole']
                    ]
            ]
    ])


}
