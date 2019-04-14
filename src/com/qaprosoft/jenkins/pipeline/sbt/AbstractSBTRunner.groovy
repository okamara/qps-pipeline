package com.qaprosoft.jenkins.pipeline.sbt

import com.qaprosoft.jenkins.pipeline.Configuration
import com.qaprosoft.jenkins.pipeline.AbstractRunner
import java.util.Date
import java.text.SimpleDateFormat


public abstract class AbstarctSBTRunnner extends AbstractRunner {

    public def date = new Date()
    public def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
    public String curDate = sdf.format(date)
    public String randomCompareArchiveName = "loadTestingReports" + curDate + ".zip"

    public AbstarctSBTRunnner(context) {
        super(context)
    }

    protected void clean() {
        context.stage('Wipe out Workspace') {
            context.deleteDir()
        }
    }

    protected void publishResultsInSlack(String jobToPublish) {
        def publishInSlack = Configuration.get("publishInSlack").toString().toBoolean()
        if (publishInSlack) {
            context.build job: jobToPublish, wait: false
        }
    }



}