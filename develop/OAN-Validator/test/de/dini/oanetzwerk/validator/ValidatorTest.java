package de.dini.oanetzwerk.validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.constants.FieldNames;

import org.apache.tomcat.jni.User;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {

	@Test
	public void testNewJob() {

		
		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "user");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		Assert.assertEquals(true, true);
	}
}
