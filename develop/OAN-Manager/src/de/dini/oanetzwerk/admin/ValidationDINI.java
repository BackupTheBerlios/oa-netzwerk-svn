package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.IJob;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;
import gr.uoa.di.validatorweb.actions.rulesets.RuleSet;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.myfaces.custom.emailvalidator.EmailValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.dini.oanetzwerk.utils.PropertyManager;


@ManagedBean(name = "validation")
@RequestScoped 
public class ValidationDINI implements Serializable, JobListener {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ValidationDINI.class);
	private static String defaultDiniRuleSetName = "DINI 2010";
	private static final String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private static final String emailRegex = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$"; 
		
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	@ManagedProperty(value = "#{emailer}")
	private Emailer emailer;
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	private String validationId = null;
	private String baseUrl = "http://";
	private String selectedDiniRuleset;
	private String selectedNumOfRecords = "100";
	private boolean randomRecords;
	private int score;
	private boolean adminEmail = false;
	private String email;
	
	private List<RuleSet> ruleSets = new ArrayList<RuleSet>();
	private List<SgParameters> diniRules;
	private List<Repository> repoList;
	private List<ValidationBean> validationList;
	private List<ValidatorTask> validationResults;
	
	@PostConstruct
	private void initValidationBean(){
		
		String path = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
		
		path = path.substring(path.lastIndexOf('/') + 1);
		
		if ("validation_dini.xhtml".equals(path)) {
			
			// fetch repositories
			repoList = restConnector.fetchRepositories();		
	
			Repository dummy = new Repository();
			dummy.setName(LanguageSwitcherBean.getMessage(ctx, "general_choose"));
			dummy.setOaiUrl("http://");
			repoList.add(0, dummy);
			
			
			// fetch dini rule sets 
			try {
	
				Validator validator = APIStandalone.getValidator();
				List<RuleSet> rulesets = validator.getAllRuleSets();
	
				RuleSet diniRuleSet = null;
				for (RuleSet ruleset : rulesets) {
					if (ruleset.getName().startsWith("DINI ")) {
						ruleSets.add(ruleset);
					}
				}
	
			} catch (ValidatorException e) {
				logger.warn("Could not fetch DINI rule sets. ", e);
			}
		} else if ("validation_dini_results.xhtml".equals(path)) {
			
			validationResults = fetchValidationResults();
		}
				
	}
	
	// used by validation_dini.xhtml
	
	public List<SgParameters> getDiniRules() {
		
		if (diniRules != null) {
			return diniRules;
		}
		
		String diniRuleSetName = defaultDiniRuleSetName;
		
		try {
			Validator validator = APIStandalone.getValidator();
			List<RuleSet> rulesets = validator.getAllRuleSets();
			
			RuleSet diniRuleSet = null;
			for (RuleSet ruleset : rulesets) {
				if (diniRuleSetName.equals(ruleset.getName())) {
					diniRuleSet = ruleset;
				}
			}
			
			List<String> diniRuleIds = validator.getRuleSetRuleIds(diniRuleSet.getId());			
			List<SgParameters> rules = new ArrayList<SgParameters>();

			SgParameters rule;
			
			for (String id : diniRuleIds) {

				System.out.println(id);
				rule = validator.getRule(id);
				rule.addParam("check", "true");
				rules.add(rule);
			}
			
			diniRules = rules;
			return diniRules;
		
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
		return new ArrayList<SgParameters>();
	}
	
	// used by validation_dini.xhtml
	
	public boolean validateForm()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		logger.info("Validating validation-job for OAI-Url= " + baseUrl + " , chosen DINI Certificate= " + selectedDiniRuleset );
		
		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(baseUrl);

		if (!matcher.matches() || baseUrl.length() < 10) {
			context.addMessage("1", new FacesMessage("Please specify a valid oai-pmh base url or select a repository from the dropdown list. "));
			//context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_chooseservice", null));
			valid = false;
		}
		
		try {
			// make sure a numeric value has been supplied
			int numberOfRecords = Integer.parseInt(selectedNumOfRecords);
			
			// if the value specified is too large or a negative value, just check all records by setting 0
			if (numberOfRecords < 0 || numberOfRecords > Integer.MAX_VALUE) {
				selectedNumOfRecords = "0";
			}
			
		} catch (NumberFormatException e) {
			valid = false;
		}
		
		// validate e-mail address 
		if (adminEmail) {
			// fetch admin email
			String parameter = "?verb=Identify";

			HttpClient htc = new HttpClient();
			GetMethod method = new GetMethod(baseUrl + parameter);
			method.setFollowRedirects(true);
			InputStream response = null;
			try {
				htc.executeMethod(method);
				response = method.getResponseBodyAsStream();
				DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = domFactory.newDocumentBuilder();
				Document doc = builder.parse(response);
				XPath xpath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xpath.compile("//adminEmail");
				Object result = expr.evaluate(doc, XPathConstants.NODESET);

				NodeList nodes = (NodeList) result;
				System.out.println(nodes.getLength());
				
				for (int i = 0; i < nodes.getLength(); i++) {
					email = nodes.item(i).getTextContent();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (email != null && email.length() > 0) {
			
			Pattern p = Pattern.compile(emailRegex);
	        Matcher m = p.matcher(email);

			if (!m.matches()) {
				context.addMessage("1", new FacesMessage("The given email-adress is invalid, please specify a valid email!"));
				valid = false;
			}
		}
		
		return valid;
	}
	
	// used by validation_dini.xhtml
	
	public String submitValidation() {
		
		boolean done = false;
		boolean valid = validateForm();
		
		if(!valid) {
			return "";
		}
		for (SgParameters rule: diniRules) {
			
			System.out.println(rule.getParamByName("check"));
		}
		
		// construct usage validation job
		
		SgParameters oaiUsageParams = new SgParameters();		
		oaiUsageParams.addParam(FieldNames.JOB_GENERAL_USER, "");
		oaiUsageParams.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		oaiUsageParams.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);
		
		List<Integer> uvRules = new ArrayList<Integer>();
		for (int i = 0; i < 14; i++) {
			if ("true".equals(diniRules.get(i).getParamByName("check"))) {
				uvRules.add(i+1);
			}
		}		
		
		// construct content validation job
		
		SgParameters oaiContentParams = new SgParameters();
		oaiContentParams.addParam(FieldNames.JOB_GENERAL_USER, email);
		oaiContentParams.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_RANDOM, Boolean.toString(randomRecords));
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_RECORDS, selectedNumOfRecords);

		List<Integer> cvRules = new ArrayList<Integer>();
		for (int i = 14; i < 29; i++) {
			if ("true".equals(diniRules.get(i).getParamByName("check"))) {
				cvRules.add(i+1);
			}
		}

		try {

			Validator val = APIStandalone.getValidator();

			IJob uvJob = val.addNewJob(oaiUsageParams, uvRules);
			uvJob.addListener(this);
			val.startJob(uvJob);
			
			IJob cvJob = val.addNewJob(oaiContentParams, cvRules);
			cvJob.addListener(this);
			val.startJob(cvJob);
			
			logger.info("usage validation job-id: " + uvJob.getJob().getId() + "   content validation job-id: " + cvJob.getJob().getId());
			done = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "validation_dini_overview";
	}
	
	// used by validation_dini_overview.xhtml
	
	public List<ValidationBean> getValidations(){
		
		if (validationList != null && validationList.size() > 0) {
			return validationList;
		}
		
		
		validationList = new ArrayList<ValidationBean>(); //Liste von Validations wird generiert

		List<Job> jobs = this.getJobs();
		
//		System.out.println("vorher");
//		for (Job job : jobs) {
//	        
//			System.out.println(job.getId());
//			System.out.println(job.getRepo()); 
//        }
		
		Collections.sort(jobs, new JobComparator());
		
//		System.out.println("nachher");
//		for (Job job : jobs) {
//	        
//			System.out.println(job.getId());
//			System.out.println(job.getRepo()); 
//        }
		
		String date = "";
		
		int i = 0;

		Job job, job2;
		for (int j = 0; j < jobs.size()-1; j++) {
			
			job = jobs.get(j);
			job2 = jobs.get(j+1);
			
			ValidationBean vali = new ValidationBean();
			
			// check if the jobs are related in terms of a full dini analysis (usage and content validation)
			if (job.getRepo().equals(job2.getRepo()) && job.getRuleset() != null 
					&& job2.getRuleset() != null && job.getRuleset().equals(job2.getRuleset())
					&& job.getType().equals("OAI Usage Validation") && job2.getType().equals("OAI Content Validation")
					&& (Integer.parseInt(job.getId()) + 1 == Integer.parseInt(job2.getId())) ) {
				
				// case: related jobs, combine 2 jobs to be shown as one
		
				System.out.println("started: " + job.getStarted());
				System.out.println("started2: " + job2.getStarted());
	
				// Setzen der Dauer
				vali.setDuration(job2.getDuration());
				System.out.println("duration1: " + job.getDuration());
				System.out.println("duration2: " + job2.getDuration());
				
				// Setzen des Status
				vali.setState(job.getStatus().equals("finished") && job2.getStatus().equals("finished") 
						? LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "validation_status_finished", null).getSummary() 
						: LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "validation_status_pending", null).getSummary());
	
				j++;
				
			} else { // not related
				vali.setDuration(job.getDuration());
				vali.setState(job.getStatus());
			}
			
			// set job-id 
			String id = job.getId();
			vali.setId(Integer.parseInt(job.getId()));
			
			List<Entry> e = this.getJobSummary(id);

			// Setzen des Datums
			date = job.getStarted().substring(0, 10);
			vali.setDate(date);

			// Setzen der OaiUrl
			
			vali.setOaiUrl(job.getRepo() != null ? job.getRepo().replace("\n", "") : "");
			
			// Aufnehmen in die Liste der Validations
			validationList.add(vali);
			
		}

		logger.debug("NUmber of validation-jobs to be shown: " + validationList.size()); 

		return validationList;
	}
	
	// used by validation_dini_results.xhtml

	public List<ValidatorTask> fetchValidationResults(){

		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		validationId = request.getParameter("vid");

		logger.info("Validation job-ID received: " + validationId);
		int id;
		if (validationId == null || validationId.length() == 0) {
			ctx.addMessage("1", new FacesMessage("Could not find the requested validation results!"));
			return new ArrayList<ValidatorTask>();
		}
		
		Validator val = APIStandalone.getValidator();
		
		try {
			Job job = val.getJob(validationId);
			
			if (job != null) {
				baseUrl = job.getRepo();
				score = job.getScore() != null && job.getScore().length() > 0 ? Integer.parseInt(job.getScore()) : 0;
			} 
                
		} catch (ValidatorException e) {
			logger.warn("Could not find a job for id " + validationId, e);
			ctx.addMessage("1", new FacesMessage("The job-id " + validationId + " is not valid!"));
			return new ArrayList<ValidatorTask>();
		}

		
		List<Entry> list = this.getJobSummary(validationId);

		List<ValidatorTask> tasks = new ArrayList<ValidatorTask>();
		
		boolean generalFailure = list == null || list.isEmpty();
		
		if (!generalFailure) {
			// entries from usage validation job
			for (Entry entry : list) {
	
				tasks.add(new ValidatorTask(entry, false));
			}
		}
		
		// fetch related job tasks from content validation
		String relatedJobId = null;
		try {
			relatedJobId = Integer.toString((Integer.parseInt(validationId) + 1));
			List<Entry> relatedJob = this.getJobSummary(relatedJobId);

			generalFailure = relatedJob == null || relatedJob.isEmpty();
			
			if (!generalFailure) {
				// entries from content validation job
				for (Entry entry : relatedJob) {
					tasks.add(new ValidatorTask(entry, true));
				}
			}
			
			// abort processing, general oaipmh failure occured 
			if (generalFailure) {
				ctx.addMessage("1", new FacesMessage(LanguageSwitcherBean.getMessage(ctx, "validation_failure_oaipmh")));
				return null;
			}
			
			Job job = val.getJob(Integer.toString(Integer.parseInt(validationId) + 1));
			
			if (job != null) {
				float score2 = (float) (job.getScore() != null && job.getScore().length() > 0 ? Integer.parseInt(job.getScore()) : 0);
				score = Math.round( ((float)score / 3.0f)  + (score2 / 3.0f * 2.0f) );
			} 
			
		} catch (NumberFormatException e) {
			logger.warn("Could not fetch related job with generated id.  (" + validationId + " + 1)", e);
		} catch (ValidatorException e) {
			logger.warn("Could not fetch related job with generated id.  (" + validationId + " + 1)", e);
		}
		
		return tasks;
	}
	
	
	//Liefert zum Entry mit der entsprechenden JobId Informationen
	public List<Entry> getJobSummary(String jobId) {

		try {
			Validator val = APIStandalone.getValidator();
			List<Entry> entries = val.getJobSummary(jobId);
			
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
			Validator val = APIStandalone.getValidator();
			List<Job> jobs = val.getJobsOfUser("sdavid");
			
			if (jobs == null)
			{
				logger.warn("No validation jobs are available!");
			} 
			return jobs;
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving list of validation jobs! ", e);
		}
		return null;
	}
	
	

	public void sendInfoMail(int jobId, String recipient) {
	
		// send email
		String resultsUrl = "https://localhost:8443/oanadmin/pages/validation_dini_results.xhtml?vid=" + jobId;
		System.out.println("url: " + resultsUrl);
		String subject = "OA-Netzwerk Validator - Ergebnisse";
		String message = "Die Validierung des Repositories ist beendet. Die Ergebnisse können sie unter " +
				resultsUrl + " einsehen.";
		
		boolean success = emailer.sendValidatorInfoMail(recipient, subject, message);
		if (success) {
		logger.info("The validation results for job " + jobId + " have been successfully sent via email to " + recipient + " !");
		} else {
			logger.warn("The validation results for job " + jobId + " could not be sent via email to " + recipient); 
		}
	}

	public String getValidationId() {
		return validationId;
	}

	public void setValidationId(String validationId) {
		this.validationId = validationId;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}	
	
	public List<RuleSet> getRuleSets() {
		return ruleSets;
	}

	public String getSelectedDiniRuleset() {
		return selectedDiniRuleset;
	}

	public void setSelectedDiniRuleset(String selectedDiniRuleset) {
		this.selectedDiniRuleset = selectedDiniRuleset;
	}
	
	public String getSelectedNumOfRecords() {
		return selectedNumOfRecords;
	}

	public void setSelectedNumOfRecords(String selectedNumOfRecords) {
		this.selectedNumOfRecords = selectedNumOfRecords;
	}

	public boolean isRandomRecords() {
		return randomRecords;
	}

	public void setRandomRecords(boolean randomRecords) {
		this.randomRecords = randomRecords;
	}
	
	public boolean isAdminEmail() {
    	return adminEmail;
    }

	public void setAdminEmail(boolean adminEmail) {
    	this.adminEmail = adminEmail;
    }

	public String getEmail() {
    	return email;
    }

	public void setEmail(String email) {
    	this.email = email;
    }

	public void setDiniRules(List<SgParameters> diniRules) {
		this.diniRules = diniRules;
	}

	public List<Repository> getRepositories() {
		return repoList;
	}
	
	public String getScore() {
    	return Integer.toString(score);
    }

	public List<ValidatorTask> getValidationResults() {
		return validationResults;
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public void setEmailer(Emailer emailer) {
    	this.emailer = emailer;
    }

	public void setPropertyManager(PropertyManager propertyManager) {
    	this.propertyManager = propertyManager;
    }

	@Override
	public void done(int arg0, double arg1) {
		logger.info("job finished! (" + arg0 + ")");
		
		String recipient = "";
		
		try {
			Validator val = APIStandalone.getValidator();
			Job job = val.getJob(Integer.toString(arg0));
			
			if (job == null)
			{
				logger.warn("Could not fetch job with id " + arg0 + " ! Skipping email notification!");
				return;
			} 
			
			recipient = job.getUser();
			
			System.out.println(job.getType());
			// send info mail
			sendInfoMail(arg0, recipient);
			
		} catch (ValidatorException e) {
			logger.warn("An error occured while retrieving list of validation jobs! ", e);
			return;
		}
		
	}

	@Override
	public void failed(int arg0, Exception arg1) {
		logger.warn("job failed! (" + arg0 + ")", arg1);
	}
	
}
