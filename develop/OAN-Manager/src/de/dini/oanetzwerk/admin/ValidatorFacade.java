package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;
import gr.uoa.di.validatorweb.actions.rulesets.RuleSet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@ManagedBean(name="validatorFacade")
@ApplicationScoped
public class ValidatorFacade {

	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_USER = "dini_user";

	private static Logger logger = Logger.getLogger(ValidationDINI.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	private Validator validator;
	
	
	
	public ValidatorFacade() {
	    super();
	    // TODO Auto-generated constructor stub
    }
	
	
	@PostConstruct
	public void init() {
		
		validator = APIStandalone.getValidator();
	}
	
	
	//Liefert zum Entry mit der entsprechenden JobId Informationen
	public List<Entry> getJobSummary(String jobId) {

		try {
			List<Entry> entries = validator.getJobSummary(jobId);
			System.out.println("entries: " + entries.size());
			if (entries == null)
			{
				logger.info("No summary available for job-id " + jobId + "!" );
			}

			return entries;
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving job summary for job with id " + jobId + "! ", e);
		}
		return null;
	}
	
	//Liefert alle Jobs, die entsprechend angefordert wurden (SQL-Not.)
	public List<Job> getJobs() {
		try {
			List<Job> jobs = validator.getJobsOfUser(DEFAULT_USER);
			
			if (jobs == null)
			{
				logger.warn("Jobs could not be found!");
			} 
			return jobs;
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving list of validation jobs! ", e);
		}
		return null;
	}
	
	public Job getJob(String id) {
		try {
			;
			Job job = validator.getJob(id);
			
			if (job == null)
			{
				logger.warn("Job not found with id " + id + "!");
			} 
			return job;
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving validation job with id " + id + "! ", e);
		}
		return null;
	}
	
	public SgParameters getRule(String ruleId) {
		try {
			return validator.getRule(ruleId);
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving validator rule with id " + ruleId + "! ", e);
		}
		return null;
	}
	
	public List<RuleSet> getDiniRuleSets() {

		List<RuleSet> ruleSets = new ArrayList<RuleSet>();
		// fetch dini rule sets
		try {
			List<RuleSet> rulesets = validator.getAllRuleSets();

			for (RuleSet ruleset : rulesets) {
				if (ruleset.getName().startsWith("DINI ")) {
					ruleSets.add(ruleset);
				}
			}
			return ruleSets;

		} catch (ValidatorException e) {
			logger.warn("Could not fetch DINI rule sets. ", e);
		}
		return new ArrayList<RuleSet>();
    }
}
