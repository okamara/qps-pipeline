package com.qaprosoft.jenkins.pipeline.sbt

import com.qaprosoft.jenkins.pipeline.Configuration
import java.util.Date
import java.text.SimpleDateFormat

trait AbstarctSBTRunnner {

    public def date = new Date()
    public def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
    public String curDate = sdf.format(date)
    public String randomCompareArchiveName = "loadTestingReports" + curDate + ".zip"

    void clean() {
        context.stage('Wipe out Workspace') {
            context.deleteDir()
        }
    }

    void publishResultsInSlack(String jobToPublish) {
        def publishInSlack = Configuration.get("publishInSlack").toString().toBoolean()
        if (publishInSlack) {
            context.build job: jobToPublish, wait: false
        }
    }

}