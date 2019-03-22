package com.company.jenkins.pipeline

import static com.qaprosoft.Utils.*
import com.qaprosoft.jenkins.pipeline.Configuration
import com.qaprosoft.jenkins.pipeline.maven.QARunner

import com.company.jenkins.jobdsl.factory.pipeline.CompanyTestJobFactory

import com.qaprosoft.jenkins.jobdsl.factory.view.ListViewFactory
import com.qaprosoft.jenkins.jobdsl.factory.pipeline.TestJobFactory
import com.qaprosoft.jenkins.jobdsl.factory.pipeline.CronJobFactory

import com.qaprosoft.jenkins.pipeline.maven.Maven
import com.qaprosoft.jenkins.pipeline.maven.sonar.Sonar
import groovy.transform.InheritConstructors

@InheritConstructors
@Mixin([Maven, Sonar])
class CompanyRunner extends QARunner {

	def overriddenFactories = [
		"com.qaprosoft.jenkins.jobdsl.factory.pipeline.TestJobFactory":"com.company.jenkins.jobdsl.factory.pipeline.CompanyTestJobFactory",
		"com.qaprosoft.jenkins.jobdsl.factory.pipeline.CronJobFactory":"com.company.jenkins.jobdsl.factory.pipeline.CompanyCronJobFactory"
	]


	public CompanyRunner(context) {
		super(context)
	}

	public void build() {
		context.println("CompanyRunner->build")
		super.build()
	}

	@Override
	public void onPush() {
		//override library and runner class for all created jobs
		pipelineLibrary = "Company-Pipeline"
		runnerClass = "com.company.jenkins.pipeline.CompanyRunner"

		super.onPush()
	}

	protected void runJob() {
		context.println("CompanyRunner->runJob")
		super.runJob()
	}

	protected void prepareForAndroid() {
		super.prepareForAndroid()
		context.println("Company->prepareForAndroid")
	}

	protected void prepareForiOS() {
		super.prepareForiOS()
		context.println("CompanyRunner->prepareForiOS")
	}

	protected void prepareForMobile() {
		super.prepareForMobile()
		context.println("CompanyRunner->prepareForMobile")
	}

	protected void registerObject(name, object) {
		// context.println("company object scanner: " + name + "; class: " + object.clazz)
		if (overriddenFactories.containsKey(object.clazz)) {
			context.println("overriding ${object.clazz} by ${overriddenFactories.get(object.clazz)}")
			object.setClass(overriddenFactories.get(object.clazz))
		}
		super.registerObject(name, object)
	}

	@Override
	protected def sendCustomizedEmail(){
		//any functionality to send customized emails etc, for example attaching *.har artifacts if any:
		// context.emailext attachmentsPattern: '**/artifacts/*.har', body: body, subject: subject, to: emailList
	}
}
