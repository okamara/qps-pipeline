package com.qaprosoft.jenkins.pipeline

import groovy.json.JsonSlurperClassic
@Grab('org.testng:testng:6.8.8')
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import com.cloudbees.groovy.cps.NonCPS

import com.qaprosoft.scm.ISCM

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

public abstract class Executor {
	//pipeline context to provide access to existing pipeline methods like echo, sh etc...
	protected def context

	//list of job parameters as a map

	protected ISCM scmClient

	protected Configuration configuration = new Configuration(context)


	public Executor(context) {
		this.context = context
	}

	protected clean() {
		context.stage('Wipe out Workspace') {
			context.deleteDir()
		}
	}

	protected void printStackTrace(Exception ex) {
		context.println("exception: " + ex.getMessage())
		context.println("exception class: " + ex.getClass().getName())
		context.println("stacktrace: " + Arrays.toString(ex.getStackTrace()))
	}

	protected String getWorkspace() {
		return context.pwd()
	}

	protected String getBuildUser() {
		try {
			return context.currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
		} catch (Exception e) {
			return ""
		}
	}

	protected Object parseJSON(String path) {
		def inputFile = new File(path)
		def content = new JsonSlurperClassic().parseFile(inputFile, 'UTF-8')
		return content
	}

    protected void publishReports(String pattern, String reportName) {
        def reports = context.findFiles(glob: pattern)
        for (int i = 0; i < reports.length; i++) {
            def parentFile = new File(reports[i].path).getParentFile()
            if (parentFile == null) {
                context.println "ERROR! Parent report is null! for " + reports[i].path
                continue
            }
            def reportDir = parentFile.getPath()
            context.println "Report File Found, Publishing " + reports[i].path
            if (i > 0){
                def reportIndex = "_" + i
                reportName = reportName + reportIndex
            }
            context.publishHTML getReportParameters(reportDir, reports[i].name, reportName )
        }
    }

    protected def getReportParameters(reportDir, reportFiles, reportName) {
        def reportParameters = [allowMissing: false,
                                alwaysLinkToLastBuild: false,
                                keepAll: true,
                                reportDir: reportDir,
                                reportFiles: reportFiles,
                                reportName: reportName]
        return reportParameters
    }

    /** Detects if any changes are present in files matching patterns  */
    @NonCPS
    protected boolean isUpdated(String patterns) {
        def isUpdated = false
        def changeLogSets = context.currentBuild.rawBuild.changeSets
        changeLogSets.each { changeLogSet ->
            /* Extracts GitChangeLogs from changeLogSet */
            for (entry in changeLogSet.getItems()) {
                /* Extracts paths to changed files */
                for (path in entry.getPaths()) {
                    context.println("UPDATED: " + path.getPath())
                    Path pathObject = Paths.get(path.getPath())
                    /* Checks whether any changed file matches one of patterns */
                    for (pattern in patterns.split(",")){
                        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern)
                        /* As only match is found stop search*/
                        if (matcher.matches(pathObject)){
                            isUpdated = true
                            return
                        }
                    }
                }
            }
        }
        return isUpdated
    }

    /** Checks if current job started as rebuild */
    protected Boolean isRebuild(String jobName) {
        Boolean isRebuild = false
        /* Gets CauseActions of the job */
        context.currentBuild.rawBuild.getActions(hudson.model.CauseAction.class).each {
            action ->
                /* Search UpstreamCause among CauseActions */
                if (action.findCause(hudson.model.Cause.UpstreamCause.class) != null)
                /* If UpstreamCause exists and has the same name as current job, rebuild was called */
                    isRebuild = (jobName == action.findCause(hudson.model.Cause.UpstreamCause.class).getUpstreamProject())
        }
        return isRebuild
    }

    /** Determines BuildCause */
    protected String getBuildCause(String jobName) {
        String buildCause = null
        /* Gets CauseActions of the job */
        context.currentBuild.rawBuild.getActions(hudson.model.CauseAction.class).each {
            action ->
//                context.println "DUMP" + action.dump()
                /* Searches UpstreamCause among CauseActions and checks if it is not the same job as current(the other way it was rebuild) */
                if (action.findCause(hudson.model.Cause.UpstreamCause.class)
                        && (jobName != action.findCause(hudson.model.Cause.UpstreamCause.class).getUpstreamProject())) {
                    buildCause = "UPSTREAMTRIGGER"
                }
                /* Searches TimerTriggerCause among CauseActions */
                else if (action.findCause(hudson.triggers.TimerTrigger$TimerTriggerCause.class)) {
                    buildCause = "TIMERTRIGGER"
                }
                /* Searches GitHubPushCause among CauseActions */
                else if (action.findCause(com.cloudbees.jenkins.GitHubPushCause.class)) {
                    buildCause = "SCMPUSHTRIGGER"
                }
                else if (action.findCause(org.jenkinsci.plugins.ghprb.GhprbCause.class)) {
                    buildCause = "SCMGHPRBTRIGGER"
                }
                else {
                    buildCause = "MANUALTRIGGER"
                }

        }
        return buildCause
    }

	XmlSuite parseSuite(String path) {
		def xmlFile = new Parser(path)
		xmlFile.setLoadClasses(false)

		List<XmlSuite> suiteXml = xmlFile.parseToList()
		XmlSuite currentSuite = suiteXml.get(0)
		return currentSuite
	}

	protected def void executeMavenGoals(goals){
		if (context.isUnix()) {
			context.sh "'mvn' -B ${goals}"
		} else {
			context.bat "mvn -B ${goals}"
		}
	}
}