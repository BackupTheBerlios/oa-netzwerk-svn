package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.JobActionsAPI;
import gr.uoa.di.validator.api.JobRunner;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validator.standalone.APIFactory;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;

import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;

@ManagedBean(name = "validator")
public class ValidatorControlBean implements JobListener {

	private String baseUrl;
	private String numOfRecords;
	private Boolean randomRecordSelection = false;
	private String oaiContentSet;
	private String jobType;

	// TODO: automatically detect logged in user
	private String user = "sdavid";

	public ValidatorControlBean() {
	}

	public String validate() {

		System.out.println("Validation process about to start...");
		System.out.println("Url to validate: " + baseUrl);
		System.out.println("job type: " + jobType);
		System.out.println("# records: " + numOfRecords);
		System.out.println("random mode: " + randomRecordSelection);
		System.out.println("selected set: " + oaiContentSet);

		SgParameters params = new SgParameters();

		// params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		// params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		// params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);

		// general job definition
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "200");
		params.addParam(FieldNames.JOB_OAICONTENT_SET, "driver");

		// rule definition
		List<Integer> ruleIds = new ArrayList<Integer>();
		ruleIds.add(1);

		try {

			Validator val = APIStandalone.getValidator();

			IJob job = val.addNewJob(params, ruleIds);
			job.addListener(this);
			val.startJob(job);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "validator_status";
	}

//	public void connectToMySQL() {
//
//		Connection conn;
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			String url = "jdbc:mysql:///localhost:3306/oan_validator";
//
//			Properties props = new Properties();
//			props.put("user", "oan_user");
//			props.put("password", "oan123");
//			props.put("charSet", "UTF-8");
//			conn = DriverManager.getConnection(url, props);
//
//			MultipleStatementConnection stmtconn = new MultipleStatementConnection(conn);
//
//			// System.out.println("Leere Tabelle "+methodName+" vor dem neu befüllen");
//			String sql = "SELECT * FROM jobs";
//			PreparedStatement statement = conn.prepareStatement(sql);
//			stmtconn.loadStatement(statement);
//			QueryResult qs = stmtconn.execute();
//			
//			stmtconn.commit();
//
//			System.out.println("Result: " + qs.getResultSet().next());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
//	}
	
	public List<Entry> getJobSummary() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Entry> entries = val.getJobSummary("387");
			
			if (entries == null)
			{
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

			return entries;
		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Job> getJobs() {

		try {
			Validator val = APIStandalone.getValidator();
			List<Job> jobs = val.getJobsOfUser("testuser");
			
			if (jobs == null)
			{
				System.out.println("No jobs available!");
			} else {
				System.out.println("jobs received!");
				
				for (Job job: jobs) {
	                
					System.out.println("Job-ID: " + job.getId());
					System.out.println("Dauer: " + job.getDuration());
					System.out.println("Score: " + job.getScore());
					System.out.println("Status: " + job.getStatus());
					System.out.println("Typ: " + job.getType());
					
					System.out.println("\n\n");
					
                }
				
			}
			return jobs;
		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void done(int arg0, double arg1) {
		System.out.println("done");

	}

	@Override
	public void failed(int arg0, Exception arg1) {
		System.out.println("failed");

	}

	/*********************** Getter & Setter *********************/

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
