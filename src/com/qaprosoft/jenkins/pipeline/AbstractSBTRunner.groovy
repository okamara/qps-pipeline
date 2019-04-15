package com.qaprosoft.jenkins.pipeline

import com.qaprosoft.jenkins.pipeline.Configuration
import groovy.transform.InheritConstructors

import com.qaprosoft.jenkins.pipeline.AbstractRunner
import java.util.Date
import java.text.SimpleDateFormat


@InheritConstructors
abstract class AbstarctSBTRunnner extends AbstractRunner{

    def date = new Date()
    def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
    String curDate = sdf.format(date)
    String randomCompareArchiveName = "loadTestingReports" + curDate + ".zip"

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