package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.JobActionsAPI;
import gr.uoa.di.validator.api.JobRunner;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validator.standalone.APIFactory;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;


@ManagedBean(name="validator")
public class ValidatorControlBean implements JobListener {

	
	private String baseUrl;
	
	
	public ValidatorControlBean() {
	}

	public String validate() {
	
		System.out.println("Validation process about to start...");
		System.out.println("Url to validate: " + baseUrl);
		
		
//		SgParameters params = new SgParameters();
//		params.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
//		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
//		params.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);

		SgParameters params = new SgParameters(); 
		params.addParam(FieldNames.JOB_GENERAL_USER, "testuser");
		params.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		params.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		params.addParam(FieldNames.JOB_OAICONTENT_RANDOM, "false");
		params.addParam(FieldNames.JOB_OAICONTENT_RECORDS, "200");
		params.addParam(FieldNames.JOB_OAICONTENT_SET, "driver");

		
		
		// Rule definition
		
		List<Integer> ruleIds = new ArrayList<Integer>();
		ruleIds.add(1);

		JobActionsAPI jobActionsAPI = APIFactory.getJobActionsAPI();

		try {
	        IJob job1 = jobActionsAPI.addNewJob(params, ruleIds);
	        
	        job1.addListener(this);
	        System.out.println(job1.toString());
	        System.out.println("OUT: " + job1.getJob().checkParams(params));
	        
	        JobRunner jobRunner = new JobRunner();
	        jobRunner.startJob(job1);

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
