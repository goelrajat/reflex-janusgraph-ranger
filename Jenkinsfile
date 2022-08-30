@Library('jenkins_lib')_
pipeline {
  agent {label 'slave'}
 
  environment {
    // Define global environment variables in this section

    buildNum = currentBuild.getNumber() ;
    buildType = BRANCH_NAME.split("/").first();
    branchVersion = BRANCH_NAME.split("/").last().toUpperCase();

    ARTIFACT_SRC_JSG_RANGER_RPM = './dist/ranger-janusgraph-service'
    ARTIFACT_DEST_JSG_RANGER_RPM = 'ggn-dev-rpms/raf/ranger-janusgraph-service'
    ARCHIVE_RPM_PATH_JSG_RANGER = './dist/ranger-janusgraph-service/*.rpm'

    SLACK_CHANNEL = 'jenkins-cdap-alerts'
    CHECKSTYLE_FILE = 'target/javastyle-result.xml'
    UNIT_RESULT = 'target/surefire-reports/*.xml'
    COBERTURA_REPORT = 'target/site/cobertura/coverage.xml'
    ALLURE_REPORT = 'allure-report/'
    HTML_REPORT = 'index.html'

    SONAR_PATH = './'
  }

    stages 
    {
        stage("Define Release Version") {
            steps {
                script {
                    //Global Lib for Environment Versions Definition
                    versionDefine()
                }
            }
        }

        stage("Initialize variable") {
            steps {
                script {
                    PUSH_JAR = false;
                    
                    if( env.buildType ==~ /(release)/)
                    {
                        PUSH_JAR = true;
                    }
                }
            }
        }

        stage ("Compile, Build and Deploy Janusgraph Ranger JAR")
        {
            stages {
                stage("Compile and build Janusgraph Ranger Service") {
                    steps {
                        script {
                            echo "Running Build"

                            sh "cd ./ranger-janusgraph-service;mvn clean install -DskipTests=true -Dcheckstyle.skip=true -Drat.skip=true -Drat.ignoreErrors=true -Dmaven.test.skip=true -Dfindbugs.skip=true"
                        }
                    }
                }

                stage("RPM Build of Janusgraph Ranger Service"){
                    steps {
                        script {
                            echo "Running RPM Build for Janusgraph Ranger Service"
                            sh 'cd ./ranger-janusgraph-service && project_version=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout` && cd .. && ./build_rpm.sh $project_version ${RELEASE}'
                        } 
                    }
                }

                stage("Artifacts Push for Janusgraph Ranger Service"){
                    steps {
                        script {
                            echo ("Artifacts Push for Janusgraph Ranger Service")
                            //Global Lib for RPM Push
                            //rpm_push(<env.buildType No need to change>, <dist is default pls specify RPM file path, <artifactory target path>) ie.        
                            rpm_push(env.buildType, env.ARTIFACT_SRC_JSG_RANGER_RPM, env.ARTIFACT_DEST_JSG_RANGER_RPM)
                        }
                    }
                }

                // stage("Push Janusgraph Ranger Service JAR to Maven Artifactory") {
                //     when {
                //         expression { PUSH_JAR == true }
                //     }
                //     steps {
                //         script {
                //             echo "Pushing Janusgraph Ranger Service JAR to Maven Artifactory"

                //             sh "cd ./ranger-janusgraph-service;mvn deploy -DskipTests=true -Dcheckstyle.skip=true -Drat.skip=true -Drat.ignoreErrors=true -Dmaven.test.skip=true -Dfindbugs.skip=true;"
                //         }
                //     }
                // }
            }
        }

        stage ("Compile, Build and Deploy Tinkerpop Ranger Authorizer JAR")
        {
            stages {
                stage("Compile and build Tinkerpop Ranger Authorizer") {
                    steps {
                        script {
                            echo "Running Build"

                            sh "cd ./tinkerpop-ranger-authorizer;mvn clean install -DskipTests=true -Dcheckstyle.skip=true -Drat.skip=true -Drat.ignoreErrors=true -Dmaven.test.skip=true -Dfindbugs.skip=true"
                        }
                    }
                }

                stage("Push Tinkerpop Ranger Authorizer JAR to Maven Artifactory") {
                    when {
                        expression { PUSH_JAR == true }
                    }
                    steps {
                        script {
                            echo "Pushing Tinkerpop Ranger Authorizer JAR to Maven Artifactory"

                            sh "cd ./tinkerpop-ranger-authorizer;mvn deploy -DskipTests=true -Dcheckstyle.skip=true -Drat.skip=true -Drat.ignoreErrors=true -Dmaven.test.skip=true -Dfindbugs.skip=true;"
                        }
                    }
                }
            }
        }
    }
    
    post {
       always {
            //Global Lib for Reports publishing
            reports_alerts(env.CHECKSTYLE_FILE, env.UNIT_RESULT, env.COBERTURA_REPORT, env.ALLURE_REPORT, env.HTML_REPORT)
 
            postBuild(env.ARCHIVE_RPM_PATH_JSG_RANGER)

            //Global Lib for slack alerts
            slackalert(env.SLACK_CHANNEL)
      }
    }
}