package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ValidatorTest implements JobListener {

	@BeforeClass
	public static void setup() {

		try {
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

		// Add the conf dir to the classpath
		// Chain the current thread classloader
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new File("conf").toURL() }, currentThreadClassLoader);

		// Replace the thread classloader - assumes
		// you have permissions to do so
		Thread.currentThread().setContextClassLoader(urlClassLoader);

		// This should work now!
		Thread.currentThread().getContextClassLoader().getResourceAsStream("web/WEB-INF/lib/validator-standalone.properties");
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testNewJob() {

		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "user");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Assert.assertEquals(true, true);
	}

	@Test
	public void testOANOAIPMH() {

		SgParameters params = new SgParameters();
		
		
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "200");
		params.addParam(FieldNames.JOB_OAICONTENT_SET, "driver");
		
		
//		params.addParam(FieldNames.JOB_GENERAL_USER, "user");
//		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
//		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");

		List<Integer> ruleIds = new ArrayList<Integer>();
		ruleIds.add(1);

		boolean done = false;
		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);
			done = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		Assert.assertEquals(true, done);
	}

	@Override
	public void done(int arg0, double arg1) {

		System.out.println("done");
	}

	@Override
	public void failed(int arg0, Exception arg1) {

		System.out.println("failed");
	}
}
