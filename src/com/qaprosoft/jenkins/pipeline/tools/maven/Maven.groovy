package com.qaprosoft.jenkins.pipeline.tools.maven
import java.util.regex.Pattern
import java.util.regex.Matcher


import com.qaprosoft.jenkins.pipeline.Configuration
import com.qaprosoft.jenkins.Logger

public class Maven {
    //TODO: migreate to traits as only it is supported in pipelines
    // https://issues.jenkins-ci.org/browse/JENKINS-46145

    def MAVEN_TOOL='M3'

    public void executeMavenGoals(goals) {
        logger.debug("Maven mixing->executeMavenGoals")
        context.withMaven(
                //EXPLICIT: Only the Maven publishers explicitly configured in "withMaven(options:...)" are used.
                publisherStrategy: 'EXPLICIT',
                // Maven installation declared in the Jenkins "Global Tool Configuration"
                maven: "${MAVEN_TOOL}",
                // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                //mavenSettingsConfig: 'settings',
                //mavenLocalRepo: ".repository"
        ) {
            // Run the maven build
            buildGoals(goals)
        }
    }

    public void executeMavenGoals(goals, mavenSettingsConfig) {
        logger.info("Maven mixing->executeMavenGoals")
        context.withMaven(
                //EXPLICIT: Only the Maven publishers explicitly configured in "withMaven(options:...)" are used.
                publisherStrategy: 'EXPLICIT',
                // Maven installation declared in the Jenkins "Global Tool Configuration"
                maven: "${MAVEN_TOOL}",
                // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                mavenSettingsConfig: "${mavenSettingsConfig}") {
            // Run the maven build
            buildGoals(goals)
        }
    }

    public void executeMavenGoals(goals, mavenSettingsConfig, mavenLocalRepo) {
        logger.info("Maven mixing->executeMavenGoals")
        context.withMaven(
                //EXPLICIT: Only the Maven publishers explicitly configured in "withMaven(options:...)" are used.
                publisherStrategy: 'EXPLICIT',
                // Maven installation declared in the Jenkins "Global Tool Configuration"
                maven: "${MAVEN_TOOL}",
                // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                mavenSettingsConfig: "${mavenSettingsConfig}",
                mavenLocalRepo: "${mavenLocalRepo}") {
            // Run the maven build
            buildGoals(goals)
        }
    }
    private def filterSecuredParams(goals) {
        def arrayOfParmeters = goals.split()
        def resultSpringOfParameters = ''
        for (parameter in arrayOfParmeters) {
            def resultString = ''
            if (parameter.contains("token") || parameter.contains("TOKEN")) {
                def arrayOfString = parameter.split("=")
                resultString = arrayOfString[0] + "=********"
            } else if (parameter.contains("-Dselenium_host")){
                parameter = "-Dselenium_host=http://demo:demo@demo.qaprosoft.com:4444/wd/hub"
                def pattern = "(\\-Dselenium_host=http:\\/\\/.+:)\\S+(@.+)"
                Matcher matcher = Pattern.compile(pattern).matcher(parameter)
                while (matcher.find()) {
                    resultString = matcher.group(1) + "********" + matcher.group(2)
                }
            } else {
                resultString = parameter
            }
            resultSpringOfParameters += resultString + ' '
        }
        return resultSpringOfParameters
    }

    public def buildGoals(goals) {
        if(context.env.getEnvironment().get("QPS_PIPELINE_LOG_LEVEL").equals(Logger.LogLevel.DEBUG.name())){
            goals = goals + " -e -X"
        }
        // parse goals replacing sensitive info by *******
        if (context.isUnix()) {
            def filteredGoals = filterSecuredParams(goals)
            context.sh """
                        echo "mvn -B ${filteredGoals}"
                        set +x
                        'mvn' -B ${goals}
                        set -x 
                       """
        } else {
            context.bat "mvn -B ${goals}"
        }
    }
}
