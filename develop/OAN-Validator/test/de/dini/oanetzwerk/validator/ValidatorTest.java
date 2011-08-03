package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;

import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;

public class ValidatorTest implements JobListener {

	// @BeforeClass
	// public static void setup() {
	//
	// try {
	// ClassLoader currentThreadClassLoader =
	// Thread.currentThread().getContextClassLoader();
	//
	// // Add the conf dir to the classpath
	// // Chain the current thread classloader
	// URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new
	// File("conf").toURL() }, currentThreadClassLoader);
	//
	// // Replace the thread classloader - assumes
	// // you have permissions to do so
	// Thread.currentThread().setContextClassLoader(urlClassLoader);
	//
	// // This should work now!
	// Thread.currentThread().getContextClassLoader().getResourceAsStream("web/WEB-INF/lib/validator-standalone.properties");
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// }

	
	//@Test
	public void testCreateDINI2010RuleSet() {
		
		try {

			Validator val = APIStandalone.getValidator();

			val.createRuleSet("DINI-Zertifikat 2010", new String[] { "1", "2"} );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void testCreateDINI2010Rules() {
		
		try {

			Validator validator = APIStandalone.getValidator();

			
			// verb=Identify ; email mandatory, regex
			SgParameters params = new SgParameters();
			params.addParam(FieldNames.RULE_TYPE, "Regular Expression");
			params.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params.addParam(FieldNames.RULE_MANDATORY, "true");
			params.addParam(FieldNames.RULE_SUCCESS, ">0");
			params.addParam(FieldNames.RULE_WEIGHT, "1");
			params.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=adminEmail");
			params.addParam(FieldNames.RULE_NAME, "valid administrator e-mail");
			params.addParam(FieldNames.RULE_DESCRIPTION, "Die E-Mail Adresse des Administrators entspricht keinem gültigen Format für E-Mail-Adressen.");
			params.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
			
			// mandatory repositoryName
			SgParameters params2 = new SgParameters();
			params2.addParam(FieldNames.RULE_TYPE, "Field Exists");
			params2.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params2.addParam(FieldNames.RULE_MANDATORY, "true");
			params2.addParam(FieldNames.RULE_SUCCESS, ">0");
			params2.addParam(FieldNames.RULE_WEIGHT, "1");
			params2.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=repositoryName");
			params2.addParam(FieldNames.RULE_NAME, "mandatory field repositoryName");
			params2.addParam(FieldNames.RULE_DESCRIPTION, "Das Repository muss einen Namen haben.");

			
			// mandatory protocolVersion 2.0
			SgParameters params3 = new SgParameters();
			params3.addParam(FieldNames.RULE_TYPE, "Vocabulary");
			params3.addParam(FieldNames.RULE_JOBTYPE, "OAI Content Validation");
			params2.addParam(FieldNames.RULE_MANDATORY, "true");
			params2.addParam(FieldNames.RULE_SUCCESS, ">0");
			params2.addParam(FieldNames.RULE_WEIGHT, "1");
			params2.addParam(FieldNames.RULE_PROVIDER_INFORMATION, "verb=Identify, field=protocolVersion");
			params2.addParam(FieldNames.RULE_NAME, "oaipmh version 2.0");
			params2.addParam(FieldNames.RULE_DESCRIPTION, "Der OAI-PMH Standard in Version 2.0 sollte unterstützt werden.");
			params2.addParam(FieldNames.RULE_VOCABULARY_WORDS, "2.0");
			
			validator.addNewRule(params);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	@Test
	public void testNewJob() {

		// SgParameters params = new SgParameters();
		// params.addParam(FieldNames.JOB_GENERAL_USER, "user");
		// params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		// params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL,
		// "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php");
		//
		// List<Integer> ruleIds = new ArrayList<Integer>();
		// ruleIds.add(1);
		//
		boolean done = false;
		// try {
		//
		// Validator val = APIStandalone.getValidator();
		//
		// IJob job = val.addNewJob(params, ruleIds);
		// job.addListener(this);
		// val.startJob(job);
		// done = true;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();

		SgParameters params = new SgParameters();

		// params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		// params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		// params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);

		// general job definition
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php");
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "30");
		params.addParam(FieldNames.JOB_OAICONTENT_SET, "driver");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		// for (int i = 1; i < 24; i++) {

		// if (i == 24 || i == 27 || i == 30) {
		// continue;
		// }
		// ruleIds.add(i);
		// }

		// ruleIds.add(1);
		ruleIds.add(24);
		// ruleIds.add(3);

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);
			done = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
				                     // keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Assert.assertEquals(true, done);
	}

//	@Test
	public void newOaiPMHJob() {

		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php ");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 20; i < 32; i++) {
			ruleIds.add(i);
		}

		ruleIds.add(34);

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
									 // keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
//	@Test
	public void newJob() {

		SgParameters params = new SgParameters();
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, "http://ubm.opus.hbz-nrw.de/phpoai/oai2.php ");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		// for (int i = 1; i < 24; i++) {
		// ruleIds.add(i);
		// }

		ruleIds.add(24); // all the checks fail with exception, see below

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
									 // keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// @Test
	public void getJobSummary() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Entry> entries = val.getJobSummary("387");

			if (entries == null) {
				System.out.println("No summary available!");
			} else {
				System.out.println("Summary received!");

				for (Entry entry : entries) {

					System.out.println("Rule-ID: " + entry.getRuleId());
					System.out.println("Name: " + entry.getName());
					System.out.println("Description: " + entry.getDescription());
					System.out.println("Weight: " + entry.getWeight());
					System.out.println("#Successes: " + entry.getSuccesses());

					for (String error : entry.getErrors()) {
						System.out.println("err: " + error);
					}

					System.out.println("\n\n");

				}

			}

		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Test
	public void getJobs() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Job> jobs = val.getJobsOfUser("testuser");

			if (jobs == null) {
				System.out.println("No jobs available!");
			} else {
				System.out.println("jobs received!");

				for (Job job : jobs) {

					System.out.println("Job-ID: " + job.getId());
					System.out.println("Dauer: " + job.getDuration());
					System.out.println("Score: " + job.getScore());
					System.out.println("Status: " + job.getStatus());
					System.out.println("Typ: " + job.getType());

					System.out.println("\n\n");

				}

			}

		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	 @Test
	public void testOANOAIPMH() {

		SgParameters params = new SgParameters();

		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "100");
		// params.addParam(FieldNames.JOB_OAICONTENT_SET, "DRIVER");

		// params.addParam(FieldNames.JOB_GENERAL_USER, "user");
		// params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		// params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL,
		// "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");

		List<Integer> ruleIds = new ArrayList<Integer>();
		for (int i = 1; i < 20; i++) {
			ruleIds.add(i);
		}
		
		ruleIds.add(32);
		ruleIds.add(33);

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

		synchronized (this) {

			try {
				this.wait(10000000); // just for simple junit testing purpose,
				                     // keeping the application alive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Assert.assertEquals(true, done);
	}

	// @Test
	public void connectToMySQL() {

		Connection conn;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/oan_validator";

			Properties props = new Properties();
			props.put("user", "oan_user");
			props.put("password", "oan123");
			props.put("charSet", "UTF-8");
			conn = DriverManager.getConnection(url, props);

			MultipleStatementConnection stmtconn = new MultipleStatementConnection(conn);

			// System.out.println("Leere Tabelle "+methodName+" vor dem neu befüllen");
			String sql = "SELECT count(*) FROM jobs";
			PreparedStatement statement = conn.prepareStatement(sql);
			stmtconn.loadStatement(statement);
			QueryResult qs = stmtconn.execute();

			stmtconn.commit();

			ResultSet rs = qs.getResultSet();
			rs.next();
			System.out.println("Result: " + rs.getInt(1));

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
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
