node {
    try
    {
        // Only keep one build
        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '25']]])
        
        // Mark the code checkout 'stage'....
        stage ('Checkout'){
            checkout scm
            sh 'git submodule update --init'
        }

        stage ('Clean'){
            withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {
                // Run the maven build
                sh "mvn clean -PWith-IDE -Dtycho.mode=maven -fn"
            }
        }

        stage ('Compile core'){
            withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {
                // Run the maven build
                sh "mvn compile"
            }
        }

        stage ('Package core'){
            withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {
                // Run the maven build
                sh "mvn package"
                step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                step([$class: 'JacocoPublisher', exclusionPattern: '**/org/overture/ast/analysis/**/*.*, **/org/overture/ast/expressions/**/*.*, **/org/overture/ast/modules/**/*.*, **/org/overture/ast/node/**/*.*,**/org/overture/ast/patterns/**/*.*, **/org/overture/ast/statements/**/*.*, **/org/overture/ast/types/**/*.*, **/org/overture/codegen/ir/**/*, **/org/overture/ide/**/*'])

                step([$class: 'TasksPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', high: 'FIXME', ignoreCase: true, low: '', normal: 'TODO', pattern: '', unHealthy: ''])
            }
        }

        stage ('Install IDE'){
            withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {
                // Run the maven build
                sh "mvn install -PWith-IDE -Pall-platforms -P!linux64 -DexternalTestsPath=$OVERTURE_EXTERNAL_TEST_ROOT -P!ui-tests -Pforce-download-externals -Pcodesigning"
                step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                step([$class: 'JacocoPublisher', exclusionPattern: '**/org/overture/ast/analysis/**/*.*, **/org/overture/ast/expressions/**/*.*, **/org/overture/ast/modules/**/*.*, **/org/overture/ast/node/**/*.*,**/org/overture/ast/patterns/**/*.*, **/org/overture/ast/statements/**/*.*, **/org/overture/ast/types/**/*.*, **/org/overture/codegen/ir/**/*, **/org/overture/ide/**/*'])

                step([$class: 'TasksPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', high: 'FIXME', ignoreCase: true, low: '', normal: 'TODO', pattern: '', unHealthy: ''])
            }
        }

	stage('Deploy') {
            def deployBranchName = env.BRANCH_NAME
            if (env.BRANCH_NAME == 'kel/sltest-delegate-merge') {

		sh "echo Detecting current version"
                version = sh(script: "mvn -s ${env.MVN_SETTINGS_PATH} -N org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -DDmaven.repo.local=.repository/ | grep -v '\\[' | grep -v -e '^\$'", returnStdout: true).trim()
                sh "echo Version: ${version}"

                sh "echo branch is now ${env.BRANCH_NAME}"

                DEST = sh script: "echo /home/jenkins/web/overture/${deployBranchName}/Build-${BUILD_NUMBER}_`date +%Y-%m-%d_%H-%M`", returnStdout: true
                REMOTE = "jenkins@overture.au.dk"

                sh "echo The remote dir will be: ${DEST}"
                sh "ssh ${REMOTE} mkdir -p ${DEST}"

	        sh "scp core/commandline/target/Overture-${version}.jar ${REMOTE}:${DEST}"
		sh "scp -r ide/product/target/products/*.zip ${REMOTE}:${DEST}"

                sh "ssh ${REMOTE} /home/jenkins/update-latest.sh web/overture/${deployBranchName}"
            }
        }


    } catch (any) {
        currentBuild.result = 'FAILURE'
        throw any //rethrow exception to prevent the build from proceeding
    } finally {

        stage('Reporting'){
            // Notify on build failure using the Email-ext plugin
            emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
                replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
                to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                        [$class: 'RequesterRecipientProvider']]))
        }
    }
}
