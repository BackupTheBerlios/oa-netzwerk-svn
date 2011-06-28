
package de.dini.oanetzwerk.validator;

import gr.uoa.di.validator.api.Entry;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.ValidatorException;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.validator.utils.AbstractBean;

@ManagedBean(name = "valiList")
@SessionScoped 
public class ValidationListBean extends AbstractBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ValidationListBean.class);
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	private String validationId = null;
	private String baseUrl = null;
	
	public ValidationListBean(){
		super();
		System.out.println("Reading Validator API");
		getJobs();
		getJobSummary("387");
	}
	
	@PostConstruct
	//Initialisiere ValidationBean fuer Aufnahme neuer Datenbestaende in Datenbank und Start des Loggers
	private void initValidationBean(){
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		validationId = request.getParameter("vid");

		System.out.println("VID: " + validationId);
		int id;
		if (validationId == null) {
			return;
		}
		
		try {
			Validator val = APIStandalone.getValidator();
			Job job = val.getJob(validationId);
			
			if (job != null) {
				baseUrl = job.getRepo();
			}
                
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
	}
	
	public List<ValidationBean> getValidations(){
		
		List<ValidationBean> valiList = new ArrayList<ValidationBean>(); //Liste von Validations wird generiert

		List<Job> jobs = this.getJobs();
		String date = "";
		String oUrl = "";
		int i = 0;

		for (Job job : jobs) {
			ValidationBean vali = new ValidationBean();
			String id = job.getId();

			List<Entry> e = this.getJobSummary(id);

			vali.setId(Integer.parseInt(job.getId()));

			// Setzen der OaiUrl
			vali.setOaiUrl(job.getRepo());
			System.out.println("url:" + vali.getOaiUrl());

			// Setzen des Datums
			date = job.getStarted().substring(0, 10);
			vali.setDate(date);

			// Setzen der Dauer
			vali.setDuration(job.getDuration());

			// Setzen des Status
			vali.setState(job.getStatus());

			// Aufnehmen in die Liste der Validations
			valiList.add(vali);
		}

		System.out.println(valiList.size()); // Größe des Repositorys wird
												// ermittelt

//		if (logger.isDebugEnabled())
//			logger.debug("DBAccessNG Instance will be prepared!");
//
//		try {
//
//			DataSource datasource = (DataSource) ((Context) new InitialContext()
//					.lookup("java:comp/env")).lookup("jdbc/oanetztest");
//			Connection connection = datasource.getConnection();
//			Connection conn2 = connection;
//			System.out.println(conn2);
//
//		} catch (NamingException ex) {
//
//			// this.datasource = new BasicDataSource();
//			// this.datasource.
//			logger.error(ex.getLocalizedMessage(), ex);
//		} catch (SQLException ex) {
//
//		}
		return valiList;
	}
	

	public List<ValidatorJob> getValidationResults(){
		
//		List<ValidationBean> valiList = new ArrayList<ValidationBean>(); //Liste von Validations wird generiert
//		
//		List<Job> jobs = this.getJobs();
//		String date = "";
//		String oUrl = "";
//		int i = 0;
//
//		for (Job job : jobs) {
//			ValidationBean vali = new ValidationBean();
//			// ValidationBean vali2 = new ValidationBean();
//			String id = job.getId();
//
//			List<Entry> e = this.getJobSummary(id);
//
//			vali.setId(Integer.parseInt(job.getId()));
//
//			// Setzen der OaiUrl
//			vali.setOaiUrl(job.getRepo());
//			System.out.println("url:" + vali.getOaiUrl());
//
//			// Setzen des Datums
//			date = job.getStarted().substring(0, 10);
//			vali.setDate(date);
//
//			// Setzen der Dauer
//			vali.setDuration(job.getDuration());
//
//			// Setzen des Status
//			vali.setState(job.getStatus());
//
//			// Aufnehmen in die Liste der Validations
//			valiList.add(vali);
//		}
//	
//		System.out.println(valiList.size()); //Größe des Repositorys wird ermittelt

		List<Entry> list = this.getJobSummary(validationId);
		System.out.println("job-id: " + validationId);
		List<ValidatorJob> jobs = new ArrayList<ValidatorJob>();
		for (Entry entry : list) {
			jobs.add(new ValidatorJob(entry));
		}
		
		return jobs;
	}
	
	
	//Liefert zum Entry mit der entsprechenden JobId Informationen
	public List<Entry> getJobSummary(String jobId) {

		try {
			Validator val = APIStandalone.getValidator();
			List<Entry> entries = val.getJobSummary(jobId);
			
			if (entries == null)
			{
				System.out.println("No summary available!");
			} else {
				System.out.println("Summary received!");
				
				for (Entry entry : entries) {
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!BEGINN AB HIER(1)!!!!!!!!!!!!!!!!!!!!!");
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
	
	//Liefert alle Jobs, die entsprechend angefordert wurden (SQL-Not.)
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
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!BEGINN AB HIER(2)!!!!!!!!!!!!!!!!!!!!");
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

	public String getValidationId() {
		return validationId;
	}

	public void setValidationId(String validationId) {
		this.validationId = validationId;
	}
	
	public String getOaiUrl() {
		return baseUrl;
	}
	
}
