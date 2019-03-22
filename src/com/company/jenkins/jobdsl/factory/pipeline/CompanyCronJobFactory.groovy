package com.company.jenkins.jobdsl.factory.pipeline

@Grab('org.testng:testng:6.8.8')

import org.testng.xml.Parser
import org.testng.xml.XmlSuite
import com.qaprosoft.jenkins.jobdsl.factory.pipeline.CronJobFactory
import groovy.transform.InheritConstructors

@InheritConstructors
public class CompanyCronJobFactory extends CronJobFactory {

	def create() {
		_dslFactory.println("CompanyCronJobFactory->create")
		def pipelineJob = super.create()

		/* Example of the customized scanner and actions */		

/*		def xmlFile = new Parser(suitePath)
		xmlFile.setLoadClasses(false)
		
		List<XmlSuite> suiteXml = xmlFile.parseToList()
		XmlSuite currentSuite = suiteXml.get(0)

		
		pipelineJob.with {
			def scheduling = currentSuite.getParameter("jenkinsPipelineScheduling")
			if (scheduling != null) {
				triggers { cron(scheduling) }
			}
		}
*/
	}
}