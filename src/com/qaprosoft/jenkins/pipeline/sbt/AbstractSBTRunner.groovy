package com.qaprosoft.jenkins.pipeline.sbt

import com.qaprosoft.jenkins.pipeline.Configuration
import groovy.transform.InheritConstructors

import java.util.Date
import java.text.SimpleDateFormat
import com.qaprosoft.jenkins.pipeline.AbstractRunner

trait AbstarctSBTRunnner{

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