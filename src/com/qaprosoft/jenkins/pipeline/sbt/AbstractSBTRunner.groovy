package com.qaprosoft.jenkins.pipeline.sbt

import com.qaprosoft.jenkins.pipeline.Configuration
import groovy.transform.InheritConstructors

import java.util.Date
import java.text.SimpleDateFormat
import com.qaprosoft.jenkins.pipeline.AbstractRunner


@InheritConstructors
public abstract class AbstarctSBTRunnner extends AbstractRunner{

    protected def date = new Date()
    protected def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
    protected String curDate = sdf.format(date)
    protected String randomCompareArchiveName = "loadTestingReports" + curDate + ".zip"

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