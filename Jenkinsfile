node {
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    checkout scm


gitlabCommitStatus(name: 'overturetool') {
    // some block
}


    stage ('Clean'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn clean"
    }}

    stage ('Compile'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn compile"
    }}

    stage ('Package'){
    withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn package"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
				step([$class: 'JacocoPublisher', exclusionPattern: '**/org/overture/ast/analysis/**/*.*, **/org/overture/ast/expressions/**/*.*, **/org/overture/ast/modules/**/*.*, **/org/overture/ast/node/**/*.*,**/org/overture/ast/patterns/**/*.*, **/org/overture/ast/statements/**/*.*, **/org/overture/ast/types/**/*.*, **/org/overture/codegen/ir/**/*, **/org/overture/ide/**/*'])

step([$class: 'TasksPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', high: 'FIXME', ignoreCase: true, low: '', normal: 'TODO', pattern: '', unHealthy: ''])
    }}


	stage('Report generation'){
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
}
