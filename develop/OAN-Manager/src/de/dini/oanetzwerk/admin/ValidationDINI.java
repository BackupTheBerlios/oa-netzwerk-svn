package de.dini.oanetzwerk.admin;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.dini.oanetzwerk.admin.security.EncryptionUtils;
import de.dini.oanetzwerk.utils.CommonValidationUtils;
import de.dini.oanetzwerk.utils.Utils;


@ManagedBean(name = "validation")
@RequestScoped 
public class ValidationDINI implements Serializable, JobListener {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ValidationDINI.class);
	private static String defaultDiniRuleSetName = "DINI 2010";

	public final static String SESSION_KEY_NAME = "oansession";
		
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	@ManagedProperty(value = "#{emailer}")
	private Emailer emailer;
		
	@ManagedProperty(value = "#{fileStorage}")
	private FileStorage fileStorage;
	
	@ManagedProperty(value = "#{validatorFacade}")
	private ValidatorFacade validatorFacade;
	
	private String validationId = null;
	private String baseUrl = "http://";
	private String selectedDiniRuleset;
	private String selectedNumOfRecords = "100";
	private String email;
	private String captcha;
	private String webApplicationUrl = "http://oanet.cms.hu-berlin.de/oanadmin";
	private boolean adminEmail = false;
	private boolean randomRecords;
	private boolean allRecords = false;
	
	private List<RuleSet> ruleSets = new ArrayList<RuleSet>();
	private List<SgParameters> diniRules;
	private List<Repository> repoList;

	
	@PostConstruct
	public void initValidationBean() {

		webApplicationUrl = getWebApplicationUrl();
		
//		String path = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
//		path = path.substring(path.lastIndexOf('/') + 1);

		// fetch repositories
		repoList = restConnector.fetchRepositories();

		Repository dummy = new Repository();
		dummy.setName(LanguageSwitcherBean.getMessage(ctx, "general_choose"));
		dummy.setOaiUrl("http://");
		repoList.add(0, dummy);

		
		ruleSets = validatorFacade.getDiniRuleSets();
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


		if (!CommonValidationUtils.isValidUrl(baseUrl.trim())) {
			context.addMessage("1", new FacesMessage("Please specify a valid oai-pmh base url or select a repository from the dropdown list. "));
			//context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_chooseservice", null));
			valid = false;
		}
		
		try {
			// make sure a numeric value has been supplied
			int numberOfRecords = Integer.parseInt(selectedNumOfRecords);
			
			// if the value specified is too large or a negative value, just check all records by setting 0
			if (numberOfRecords < 0 || numberOfRecords > Integer.MAX_VALUE || allRecords) {
				selectedNumOfRecords = "0";
			}
			
		} catch (NumberFormatException e) {
			valid = false;
		}
		
		// check if base url is valid and check admin email if selected
		String parameter = "?verb=Identify";

		try {
			HttpClient htc = new HttpClient();
			GetMethod method = new GetMethod(baseUrl + parameter);
			method.setFollowRedirects(true);
			InputStream response = null;
			htc.executeMethod(method);
			response = method.getResponseBodyAsStream();
			
			// check if response succeeds
			if (method.getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("Status: " + method.getStatusCode());
				context.addMessage("1", new FacesMessage("Please specify a valid oai-pmh base url! The Identity-verb could not be requested under " + baseUrl + parameter));
				valid = false;
			}
			
			// validate e-mail address 
			// fetch admin email
			if (adminEmail) {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = domFactory.newDocumentBuilder();
				Document doc = builder.parse(response);
				XPath xpath = XPathFactory.newInstance().newXPath();
				XPathExpression expr = xpath.compile("//adminEmail");
				Object result = expr.evaluate(doc, XPathConstants.NODESET);

				NodeList nodes = (NodeList) result;
				System.out.println(nodes.getLength());
				
				for (int i = 0; i < nodes.getLength(); i++) {
					if (email == null || email.trim().length() == 0) {
						email = nodes.item(i).getTextContent();
					} else {
						email = email + ";" + nodes.item(i).getTextContent();
					}
				}
			}
			response.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("testyyy");
			context.addMessage("1", new FacesMessage("The specified base url seems to be invalid!"));
			valid = false;
		}
	
		
		if (email != null && email.length() > 0) {

			if (!CommonValidationUtils.isValidEmail(email)) {
				context.addMessage("1", new FacesMessage("The given email-adress is invalid, please specify a valid email!"));
				valid = false;
			}
		}
		
		if(!validCaptcha()) {
			valid = false;
			context.addMessage("1", new FacesMessage("The given captcha code is invalid, please re-enter the code!"));
		}
		
		return valid;
	}
	

    private boolean validCaptcha() {

        // Compare the CAPTCHA value with the user entered value.
        String captchaValue = (String)((HttpSession) FacesContext
                .getCurrentInstance().getExternalContext().getSession(true))
                .getAttribute(SESSION_KEY_NAME);
        
        if(captchaValue.equalsIgnoreCase(captcha)) {
            return true;
        } 
        return false;
    }
    

	// used by validation_dini.xhtml
	
	public synchronized String submitValidation() {
		
		boolean done = false;
		boolean valid = validateForm();
		
		if(!valid) {
			return "validation_dini";
		}
		
		for (SgParameters rule: diniRules) {
			
//			System.out.println(rule.getParamByName("check"));
		}
		
		List<Integer> cvRules = new ArrayList<Integer>();
		List<Integer> uvRules = new ArrayList<Integer>();
		
		SgParameters rule = null;
		for (SgParameters r : diniRules) {
			if ("OAI Usage Validation".equals(r.getParamByName(FieldNames.RULE_JOBTYPE))) {
				uvRules.add(Integer.parseInt(r.getParamByName(FieldNames.RULE_ID)));
			}
			else {
				cvRules.add(Integer.parseInt(r.getParamByName(FieldNames.RULE_ID)));
			}
		}		

		// construct usage validation job
		
		SgParameters oaiUsageParams = new SgParameters();		
		oaiUsageParams.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		oaiUsageParams.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Usage Validation");
		oaiUsageParams.addParam(FieldNames.JOB_OAIUSAGE_BASEURL, baseUrl);
		
		
		// construct content validation job
		
		SgParameters oaiContentParams = new SgParameters();
		oaiContentParams.addParam(FieldNames.JOB_GENERAL_USER, "sdavid");
		oaiContentParams.addParam(FieldNames.JOB_GENERAL_RULESET, email); // TODO: using ruleset field for email storage
		oaiContentParams.addParam(FieldNames.JOB_GENERAL_TYPE, "OAI Content Validation");
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_BASEURL, baseUrl);
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_RANDOM, Boolean.toString(randomRecords));
		oaiContentParams.addParam(FieldNames.JOB_OAICONTENT_RECORDS, selectedNumOfRecords);


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
	
	public void sendInfoMail(int jobId, List<String> recipients) {
		
		// send email
		String encryptedAndEncodedId = EncryptionUtils.encryptAndEncode(Integer.toString(jobId - 1));
		System.out.println("webapp url: " + webApplicationUrl);
		String resultsUrl = webApplicationUrl + "/pages/validation_dini_results.xhtml?vid=" + encryptedAndEncodedId;
		System.out.println("url: " + resultsUrl);
		String subject = "OA-Netzwerk Validator - Ergebnisse";
		String message = "Die Validierung des Repositories ist beendet. Die Ergebnisse können sie unter " +
				resultsUrl + " einsehen.";
		
		boolean success = emailer.sendValidatorInfoMail(recipients, subject, message);
		if (success) {
		logger.info("The validation results for job " + jobId + " have been successfully sent via email.");
		} else {
			logger.warn("The validation results for job " + jobId + " could not be sent via email !"); 
		}
	}
	
	
	/*********************** Interface Implementation ********************/
	
	@Override
	public void done(int jobId, double arg1) {
		logger.info("job finished! (" + jobId + ")");
		
		// email
		emailNotification(jobId);
	}

	@Override
	public void failed(int jobId, Exception ex) {
		logger.warn("job failed! (" + jobId + ")", ex);
		
		Map<Integer, String> errors = fileStorage.getValidatorErrors();
		
		System.out.println("err1: " + ex.getMessage());
		System.out.println("err2: " + ex.getLocalizedMessage());
		
		String errorMessage = ex.getMessage();
		
		if (errors == null) {
			errors = new HashMap<Integer, String>();
		}
		
		errors.put(jobId, errorMessage);
		System.out.println("STORING: " + jobId + "   " + errorMessage);
		fileStorage.storeValidatorInfo(errors);
		
		// email
		emailNotification(jobId);
	}

	private void emailNotification(int jobId) {

		String recipient = "";
		
		Job job = validatorFacade.getJob(Integer.toString(jobId));
		
		if (job == null)
		{
			logger.warn("Skipping email notification!");
			return;
		} 
		
		// TODO: fix this, we are using the ruleset field to store email adresses currently,
		// as we are not able to change the code of the OpenAIRE Validator
		recipient = job.getRuleset();
	
		String[] emails = null;
		
		System.out.println(job.getRuleset()); 
		if (recipient == null || recipient.trim().length() == 0) {
			logger.info("Skipped email notification, no valid email adresses found for job with id " + jobId); 
			return;
		}
		
		if (recipient.contains(";")) {
			emails = recipient.split(";");
		} else {
			emails = new String[] {recipient};
		}
		
		List<String>  validEmails = new ArrayList<String>();
		
		for (String email : emails) {
            
			if (CommonValidationUtils.isValidEmail(email)) {
				validEmails.add(email);
			}
        }
		// check if the user-field is a valid e-mail address
		if (validEmails.size() > 0) {
			System.out.println(job.getType());
			// only send emails for content job results
			if ("OAI Content Validation".equals(job.getType())) {
			
				// send info mail
				sendInfoMail(jobId, validEmails);
			}
		}
    }
	
	public String getWebApplicationUrl() {
		
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		StringBuffer buffer = new StringBuffer(context.getRequestScheme()); // scheme
		buffer.append("://")
		.append(context.getRequestServerName()) // host
		.append(":").append(context.getRequestServerPort()) // port
		.append(context.getRequestContextPath()); // context path
		
		return buffer.toString();
	}
	
	/******************** Getter & Setter *************************/

	public static String getLocalizedRuleName(String name) {
		return ValidationDINIResults.getLocalizedRuleName(name);
	}

	public static String getMainDescription(String description) {
		return ValidationDINIResults.getMainDescription(description);
	}
	
	public static String getAdditionalDescription(String description) {
		return ValidationDINIResults.getAdditionalDescription(description);
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
	
	public boolean isAllRecords() {
		return allRecords;
	}

	public void setAllRecords(boolean allRecords) {
		this.allRecords = allRecords;
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
	
	public String getCaptcha() {
    	return captcha;
    }

	public void setCaptcha(String captcha) {
    	this.captcha = captcha;
    }

	public String getSessionKeyName() {
    	return SESSION_KEY_NAME;
    }

	public void setDiniRules(List<SgParameters> diniRules) {
		this.diniRules = diniRules;
	}

	public List<Repository> getRepositories() {
		return repoList;
	}
	
	public void setFileStorage(FileStorage fileStorage) {
    	this.fileStorage = fileStorage;
    }

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public void setEmailer(Emailer emailer) {
    	this.emailer = emailer;
    }

	public void setValidatorFacade(ValidatorFacade validatorFacade) {
    	this.validatorFacade = validatorFacade;
    }
	
}
