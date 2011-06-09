package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@ManagedBean(name="results")
@RequestScoped
public class ValidationResultsBean {

	private Logger logger = Logger.getLogger(ValidationBean.class);
		
	private Job job;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	
	@PostConstruct
	//Initialisiere ValidationBean fuer Aufnahme neuer Datenbestaende in Datenbank und Start des Loggers
	private void initValidationBean(){
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String repoId = request.getParameter("vid");

		int id;
		if (repoId == null) {
			return;
		}

//		try {
//			id = Integer.parseInt(repoId);
//
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		try {
//			ctx.getExternalContext().redirect("/validation_overview.xhtml");
//			ctx.getExternalContext().redirect("/validation_results_content.xhtml");
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
	

		Validator val = APIStandalone.getValidator();
		
		job = null;
		try {
			job = val.getJob(repoId);
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
		
	


//		HashMap<String, String> details = getDetails();
		

//		for (String key : details.keySet()) {
//
//			System.out.println("key: " + key);
//			if (key == null) {
//				continue;
//			} else if (key.equals("oai_url")) {
//				this.oaiUrl = details.get(key);
//			} else if (key.equals("date")) {
//				this.date = details.get(key);
//			} else if (key.equals("state")) {
//				this.state = details.get(key);
//			} else if (key.equals("duration")) {
//				this.duration = details.get(key);
//			} else if (key.equals("rule")) {
//				this.rule = details.get(key);
//			} else if (key.equals("rstate")) {
//				this.rstate = details.get(key);
//			} else if (key.equals("ruleId")) {
//				this.ruleId = details.get(key);
//			} else if (key.equals("detail")) {
//				this.detail = details.get(key);
//			} 
//		}
	}


	public Job getJob() {
		return job;
	}


	public void setJob(Job job) {
		this.job = job;
	}
	
	public List<Job> getJobs() {
		
		List<Job> jobs = new ArrayList<Job>();
		jobs.add(job);

		return jobs;
	}
	
}
