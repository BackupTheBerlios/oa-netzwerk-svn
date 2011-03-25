package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.JobActionsAPI;
import gr.uoa.di.validator.api.JobRunner;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validator.standalone.APIFactory;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;


@ManagedBean(name="validator")
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
		System.out.println("selected set: "+ oaiContentSet);
		
		
		
		SgParameters params = new SgParameters();
		
		
//		params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
//		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
//		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);
		
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
