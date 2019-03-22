package com.company.jenkins.jobdsl.factory.pipeline

@Grab('org.testng:testng:6.8.8')

import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import com.qaprosoft.jenkins.jobdsl.factory.pipeline.TestJobFactory
import groovy.transform.InheritConstructors

@InheritConstructors
public class CompanyTestJobFactory extends TestJobFactory {
	
	def create() {
		_dslFactory.println("CompanyTestJobFactory->create")
		def pipelineJob = super.create()
		
/*		def xmlFile = new Parser(suitePath)
		xmlFile.setLoadClasses(false)
		
		List<XmlSuite> suiteXml = xmlFile.parseToList()
		XmlSuite currentSuite = suiteXml.get(0)

		
		pipelineJob.with {
			parameters {
				def jobType = suiteName
				if (currentSuite.getParameter("jenkinsJobType") != null) {
					jobType = currentSuite.getParameter("jenkinsJobType")
				}
				
				switch(jobType.toLowerCase()) {
					//declare for ios and android "build" scring param
					case ~/^.*android.*$/:
					case ~/^.*ios.*$/:
						stringParam('build', 'latest', "latest - use fresh build artifact from ....")
						break;
					default:
						//do nothing
						break;
				}
			}
		}
*/
	}
}