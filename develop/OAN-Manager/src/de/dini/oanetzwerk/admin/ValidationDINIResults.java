package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import de.dini.oanetzwerk.admin.security.EncryptionUtils;

@ManagedBean(name="validationResults")
@SessionScoped
public class ValidationDINIResults {

	
	private static final String MANDATORY = "1";
	private static final String JOBTYPE_CONTENT = "OAI Content Validation";
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ValidationDINI.class);
	
	private FacesMessage invalidURLMessage = new FacesMessage("Could not find the requested validation results! The specified URL parameter seems to be invalid!");
	
	@ManagedProperty(value = "#{fileStorage}")
	private FileStorage fileStorage;
	
	@ManagedProperty(value = "#{validatorFacade}")
	private ValidatorFacade validatorFacade;
	
	private String validationId = null;
	private String baseUrl = "http://";
	private String errorMessage;
	private int score;
	private int bonus;

	private List<ValidationBean> validationList;
	private List<ValidatorTask> mandatoryTasks 	= null;
	private List<ValidatorTask> optionalTasks 	= null;
	private long validationsLastFetched 		= 0;
	private long jobsLastFetched 				= 0;
	private boolean validationFailed			= false;
	private boolean renderPopups = false;
	
	@PostConstruct
	public void init(){
		
		System.out.println("URL: " + baseUrl);
		System.out.println(((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("validationId"));
//		fetchValidationResults();		
	}
		
	
	// used by validation_dini_results.xhtml
	private void fetchValidationResults(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		if (! validationIdParamFound()) {
			return;
		}
		
		logger.info("Fetching results... validation job-ID received: " + validationId);

		// fetch job
		Job job = validatorFacade.getJob(validationId);

		if (job == null) {
			ctx.addMessage("1", new FacesMessage("The job-id " + validationId + " is not valid!"));
			return; // new ArrayList<ValidatorTask>();
		} 

		baseUrl = job.getRepo();
		
		// fetch results from usage job
		List<Entry> list = validatorFacade.getJobSummary(validationId);
		
		// fetch related job results from content validation
		String relatedJobId = Integer.toString((Integer.parseInt(validationId) + 1));
		List<Entry> relatedJob = validatorFacade.getJobSummary(relatedJobId);
		
		mandatoryTasks 	= new ArrayList<ValidatorTask>();
		optionalTasks 	= new ArrayList<ValidatorTask>();
		
		validationFailed = list == null || list.isEmpty() || relatedJob == null || relatedJob.isEmpty();
		
		// abort processing, general oaipmh failure occured 
		if (validationFailed) {
			ctx.addMessage("1", new FacesMessage(LanguageSwitcherBean.getMessage(ctx, "validation_failure_oaipmh")));
			return; // null;
		}
		
		list.addAll(relatedJob);
		
		// entries from usage validation job
		SgParameters params = null;
		boolean mandatory = false;
		boolean contentType = false;
		
		for (Entry entry : list) {
			
			//fetch rule, too

			params = validatorFacade.getRule(Integer.toString(entry.getRuleId()));	
			mandatory = MANDATORY.equals(params.getParamByName(FieldNames.RULE_MANDATORY));
			contentType = JOBTYPE_CONTENT.equals(params.getParamByName(FieldNames.JOB_GENERAL_TYPE));
			System.out.println("getMandatory: " + params.getParamByName(MANDATORY));
			
			if (mandatory) {					
				mandatoryTasks.add(new ValidatorTask(entry, params, contentType));
			} else {
				optionalTasks.add(new ValidatorTask(entry, params, contentType));
			}
		}
		
//			Job job = val.getJob(Integer.toString(Integer.parseInt(validationId) + 1));
//			
//			if (job != null) {
//				float score2 = (float) (job.getScore() != null && job.getScore().length() > 0 ? Integer.parseInt(job.getScore()) : 0);
//				score = Math.round( ((float)score / 3.0f)  + (score2 / 3.0f * 2.0f) );
//			} 
		
		float points = 0.0f;
		for (ValidatorTask task : mandatoryTasks) {
			// calculate points per rule and add it to the overall points
			points += task.getSuccessPercentage() / 100.0f * ((float)task.getEntry().getWeight()); 
			System.out.println("current points: " + points + "   perc: " + task.getSuccessPercentage());
		}
		
		score = Math.round(100.0f/38.0f * points);
		System.out.println("Score: " + score);
		
		float extraPoints = 0.0f;
		for (ValidatorTask task : optionalTasks) {
			// calculate points per rule and add it to the overall points
			extraPoints += task.getSuccessPercentage() / 100.0f * ((float)task.getEntry().getWeight()) * 2 ; // * 2; each rule should make 2 bonus points 
			System.out.println("current bonus: " + extraPoints + "   perc: " + task.getSuccessPercentage());
		}
		
		bonus = Math.round(extraPoints);
		System.out.println("Bonus: " + bonus);
		
		// read and set error message if job failed
		Map<Integer, String> errorMap = fileStorage.getValidatorErrors();
		if (errorMap != null) {
			errorMessage = errorMap.get(validationId);
			errorMessage = errorMessage == null ? errorMap.get(validationId + 1) : errorMessage;
		}

	}
	
	
	// used by validation_dini_overview.xhtml
	private List<ValidationBean> fetchJobs(){
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		System.out.println("Fetch jobs..."); 

		// only get List of the job that has been explicitly requested
		if (validationIdParamFound()) {
			return fetchJob();
		}		
		
		// get a list of all jobs
		validationList = new ArrayList<ValidationBean>(); //Liste von Validations wird generiert

		List<Job> jobs = validatorFacade.getJobs();
		Collections.sort(jobs, new JobComparator());
		
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
					&& job.getType().equals("OAI Usage Validation") && job2.getType().equals(JOBTYPE_CONTENT)
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
			
			List<Entry> e = validatorFacade.getJobSummary(id);

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
	
	private List<ValidationBean> fetchJob(){
		
		FacesContext ctx = FacesContext.getCurrentInstance();

		
		System.out.println("Fetch jobs update"); 
		validationList = new ArrayList<ValidationBean>(); //Liste von Validations wird generiert

		List<Job> jobs = new ArrayList<Job>();
		jobs.add(validatorFacade.getJob(validationId));
		jobs.add(validatorFacade.getJob(Integer.toString((Integer.parseInt(validationId) + 1))));
		
		Collections.sort(jobs, new JobComparator());
		
		String date = "";

		Job job, job2;
		
		for (int j = 0; j < jobs.size()-1; j++) {
			
			job = jobs.get(j);
			job2 = jobs.get(j+1);
			
			ValidationBean vali = new ValidationBean();
			
			// check if the jobs are related in terms of a full dini analysis (usage and content validation)
			if (job.getRepo().equals(job2.getRepo()) && job.getRuleset() != null 
					&& job2.getRuleset() != null && job.getRuleset().equals(job2.getRuleset())
					&& job.getType().equals("OAI Usage Validation") && job2.getType().equals(JOBTYPE_CONTENT)
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
			
			List<Entry> e = validatorFacade.getJobSummary(id);

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
	
	private boolean validationIdParamFound() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String encryptedAndEncodedId = request.getParameter("vid");
		
		System.out.println("retreived param: " + encryptedAndEncodedId);		
		String id = null;
		try {
		
			id = EncryptionUtils.decryptAndDecode(encryptedAndEncodedId);
			validationId = id;
		}catch (Exception e) {
			logger.warn("Could not decrypt validation-id jor job! Results could not be fetched.");
			validationId = null;
		}

		if (validationId == null || validationId.length() == 0) {
			if (!ctx.getMessageList().contains(invalidURLMessage)) {
				ctx.addMessage("1", invalidURLMessage);
			}
			return false; // new ArrayList<ValidatorTask>();
		}
		return true;
	}
	
	
	/************************* Localized Rule title, descriptions, errors *************************/ 
	
	public static String getLocalizedRuleName(String name) {
		 return getLocalizedText(name);
	}

	public static String getMainDescription(String description) {
		return getLocalizedDescriptions(description)[0];
	}
	
	public static String getAdditionalDescription(String description) {
		String[] descriptions = getLocalizedDescriptions(description);
		return (descriptions != null && descriptions.length == 2) ? descriptions[1] : ""; 
	}
	
	public static String getErrorDescription(String description) {
		String[] descriptions = getLocalizedDescriptions(description);
		return (descriptions != null && descriptions.length == 3) ? descriptions[2] : ""; 
	}
	
	private static String[] getLocalizedDescriptions(String description) {

		String localizedDescription = getLocalizedText(description);
		
		// use main description only
		String[] descriptionsPerLanguage = null;
		
		if (localizedDescription != null && localizedDescription.contains("&&&")) {
			descriptionsPerLanguage = localizedDescription.split("&&&");
		} else {
			return new String[] { localizedDescription };
		}
		
		return descriptionsPerLanguage;
		
	}
	
	private static String getLocalizedText(String text) {
		// expected String format
		// germanMainDescription &&& germanAdditionalInfo ||| englishMainDescription &&& englishAdditionalInfo
				
		String[] textForAllLanguages = null;

		if (text != null && text.contains("|||")) {
			textForAllLanguages = text.split("\\|\\|\\|");
		} else {
			textForAllLanguages = new String[] { text };
		}

		if (textForAllLanguages.length > 1) {
			if (Locale.GERMAN.equals(FacesContext.getCurrentInstance().getViewRoot().getLocale())) {
				// german
				return textForAllLanguages[0];
			} else {
				// english
				return textForAllLanguages[1];
			}
		} else {
			return text;
		}
	}
	
	public String getRuleUrl(String baseUrl, String providerInfo) {
		
		if (providerInfo == null || providerInfo.length() == 0) {
			return null;
		}
		
		String url = null;
		
		try {
			if (providerInfo.contains("verb=")) {
				return baseUrl + "?" + providerInfo.substring(providerInfo.indexOf("verb="), providerInfo.indexOf(',', providerInfo.indexOf("verb=")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**************************** Error Highlighting *****************************/
	
	
	public String getHighlightedResponse(String baseUrl, String providerInfo, String optionalIdentifier) {
		
//		System.out.println("YYY: " + baseUrl);
//		System.out.println("YYY2: " + providerInfo);
		if (providerInfo == null || providerInfo.length() == 0) {
			return null;
		}
		
		String url = null;
		
		try {
//			System.out.println("argh1");
			String tag = null;
			if (providerInfo.contains("verb=")) {
//				System.out.println("argh2");
				url = baseUrl + "?" + providerInfo.substring(providerInfo.indexOf("verb="), providerInfo.indexOf(',', providerInfo.indexOf("verb=")));
			}
			
			if (providerInfo.contains("field=")) {

				tag = providerInfo.substring(providerInfo.indexOf("field=") + 6, providerInfo.length());
				
			} else  {
				System.out.println(optionalIdentifier);
				if (optionalIdentifier == null || optionalIdentifier.length() == 0) {
					logger.warn("This should not happen, rule with field information but no additional identifier.");
				}
				url = baseUrl + "?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + optionalIdentifier;
				tag = providerInfo;
			}
			
			
			// getXMLReponse();
			String xml = getXMLResponse(url);
//			System.out.println("break1" + xml);
//			System.out.println("break2" + tag);
			// String output = decorateXML(); 
			renderPopups = false;
			return decorateXml(xml, tag);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	private String getRecordUrl(String baseUrl) {
//		
//		
//	}
	
	private String getXMLResponse(String url) {
//		System.out.println("break: " + url);
		HttpClient htc = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.setFollowRedirects(true);
		InputStream response = null;
		try {
			htc.executeMethod(method);
			
//			response = method.getResponseBodyAsStream();
//			System.out.println(method.getResponseHeader("content-type"));
//			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = domFactory.newDocumentBuilder();
//			Document doc = builder.parse(response);
			
			// response seems to be valid xml
			return method.getResponseBodyAsString();
			
//			XPath xpath = XPathFactory.newInstance().newXPath();
//			XPathExpression expr = xpath.compile("//adminEmail");
//			Object result = expr.evaluate(doc, XPathConstants.NODESET);
//
//			NodeList nodes = (NodeList) result;
//			System.out.println(nodes.getLength());
//			
//			for (int i = 0; i < nodes.getLength(); i++) {
//				if (email == null || email.trim().length() == 0) {
//					email = nodes.item(i).getTextContent();
//				} else {
//					email = email + ";" + nodes.item(i).getTextContent();
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String decorateXml(String xml, String tagToHighlight) {

		System.out.println(xml);
		long start = System.currentTimeMillis();
		xml = formatXml(xml);
		System.out.println(xml);
		xml = StringEscapeUtils.escapeHtml(xml);
		xml = xml.replaceAll("&lt;", "<b>&lt;");
		xml = xml.replaceAll("&gt;", "&gt;</b>");
		xml = xml.replaceAll("<b></b>", "<b>");
		xml = xml.replaceAll("&lt;" + tagToHighlight + "&gt;", "&lt;" + tagToHighlight + "&gt;<font color=\"#CC0000\">");
		xml = xml.replaceAll("&lt;/" + tagToHighlight + "&gt;", "</font>&lt;/" + tagToHighlight + "&gt;");
		System.out.println(System.currentTimeMillis()-start);
		xml = xml.replaceAll("\n", "<br />");
		xml = xml.replaceAll("   ", "&nbsp;&nbsp;&nbsp;");
		System.out.println("yyyyy: " + xml);
		return xml;
		
	}
	
    public String formatXml(String xml){
        try{
            Transformer serializer= SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource=new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res =  new StreamResult(new ByteArrayOutputStream());            
            serializer.transform(xmlSource, res);
            return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());
        }catch(Exception e){
            e.printStackTrace();
            return xml;
        }
    }
    
    
    /*************************** Getters & Setters ************************/

    public String getEncryptedValidationId(Integer id) {
    	return EncryptionUtils.encryptAndEncode(Integer.toString(id));
    }
    
	public List<ValidatorTask> getMandatoryValidationResults() {
		if (mandatoryTasks == null || System.currentTimeMillis()-validationsLastFetched > 5000) {
			System.out.println("FETCHING RESULTS");
			fetchValidationResults();
			validationsLastFetched = System.currentTimeMillis();
		}
		return mandatoryTasks;		
	}
	
	public List<ValidatorTask> getOptionalValidationResults() {
		if (optionalTasks == null || System.currentTimeMillis()-validationsLastFetched > 5000) {
			System.out.println("FETCHING RESULTS");
			fetchValidationResults();
			validationsLastFetched = System.currentTimeMillis();
		}
		return optionalTasks;		
	}
	
	public List<ValidationBean> getValidations() {
		if (validationList == null || validationList.isEmpty() || System.currentTimeMillis()-jobsLastFetched > 5000) {
			System.out.println("FETCHING JOBS");
			fetchJobs();
			jobsLastFetched = System.currentTimeMillis();
		}
		
		return validationList;
	}
	
	public void setFileStorage(FileStorage fileStorage) {
    	this.fileStorage = fileStorage;
    }

	public boolean isRenderPopups() {
		return renderPopups;
	}

	public void setRenderPopups(boolean renderPopups) {
		System.out.println("render popups true");
		this.renderPopups = renderPopups;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getScore() {
    	return Integer.toString(score);
    }

	public String getBonus() {
		return Integer.toString(bonus);
	}

	public boolean isValidationFailed() {
		return validationFailed;
	}

	public String getValidationId() {
		return validationId;
	}

	public void setValidationId(String validationId) {
		this.validationId = validationId;
	}

	public void setValidatorFacade(ValidatorFacade validatorFacade) {
    	this.validatorFacade = validatorFacade;
    }
}
