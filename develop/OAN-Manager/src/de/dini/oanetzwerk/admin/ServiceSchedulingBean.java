package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name = "scheduling")
@RequestScoped
public class ServiceSchedulingBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ServiceSchedulingBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{schedulerControl}")
	private SchedulerControl schedulerControl;
	
	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	

	private List<SchedulingBean> jobList;
	private SchedulingBean job;

	/* form */
	
	// service
//	private Map<String, Integer> services = new HashMap<String, Integer>();
	private String chosenService;
	
	// repeatedly or once only
	private String jobType;
	
	// case repeatedly
	private String intervalType = SchedulingIntervalType.Day.toString();
	private String chosenDayOfMonth;
	private String chosenDayOfWeek;
	private String chosenDay;
	private Date   chosenDate2 = new Date();
	private String chosenHour = "20:00";

	
	// case once only
	private List<Repository> repoList;
	private String chosenRepository;
	private Date   chosenDate = new Date();
	private String chosenTime = "20:00";
	private boolean startRightNow;

	// error messages	
	private String error;

	boolean updateCase;
	private String radio1;
	

	public ServiceSchedulingBean() {

	}
	
	
	@PostConstruct
	public void init() {
		
		// init job object for a new job that might be created or updated
		job = new SchedulingBean();

		
		// try to fetch jobId from request parameters in case this is an update request
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String jobId = request.getParameter("jid");
		
		if (jobId != null) {
			logger.info("Job-ID detected: " + jobId);
			updateCase = initJob(jobId);
		}
		
		// retrieve list of repositories
		initRepositories();
		
		schedulerControl.listJobs();
	}
	
	
	/********************* Validations *********************/

	public boolean validate()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		logger.info("Validating job with service= " + chosenService + " , job-type= " + jobType + " ,date= " + chosenDate);
		
		if ("null".equals(chosenService)) {
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_chooseservice", null));
			valid = false;
		}
		
		if ("null".equals(jobType)) {
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_chooseinterval", null));
			valid = false;
			return valid;
		}		
		
		
		if (JobType.OneTime.toString().equals(jobType) && !startRightNow) {
			try {
				Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(chosenDate) + " " + chosenTime);
				job.setNonperiodicTimestamp(date);

				if (System.currentTimeMillis() > date.getTime()) {
//					((UIInput) toValidate).setValid(false);

					FacesMessage message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_datenotinfuture", null);
					context.addMessage("1", message);
					valid = false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				FacesMessage message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_checkdateformat", null);
				context.addMessage("1", message);
				valid = false;
			}
		}
		
		if (JobType.Repeatedly.toString().equals(jobType)) {
			try {
				Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(chosenDate2) + " " + chosenHour);
				job.setNonperiodicTimestamp(date);

				if (System.currentTimeMillis() > date.getTime()) {
//					((UIInput) toValidate).setValid(false);

					FacesMessage message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_datenotinfuture", null);
					context.addMessage("1", message);
					valid = false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				FacesMessage message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_error_checkdateformat", null);
				context.addMessage("1", message);
				valid = false;
			}
		}
		
		return valid;
	}
	

	public String storeJob() {

		
//		System.out.println("radio1: " + radio1);
		logger.info("Validating job with service= " + chosenService + " , job-type= " + jobType + " ,date= " + chosenDate + 
				" ,time= " + chosenTime + " ,interval-type= " + intervalType );

		boolean valid = validate();
		
		System.out.println("Job valid: " + valid);
		
		if (!valid) {
			return "failed";
		}
		
		
		if (JobType.Repeatedly.toString().equals(jobType)) {
			
			job.setPeriodic(true);
			try {
				Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(chosenDate2) + " " + chosenHour);
				job.setNonperiodicTimestamp(date);
			} catch (ParseException e) {
				// this should never happen
				e.printStackTrace();
				return "failed";
			}

			job.setPeriodicInterval(SchedulingIntervalType.valueOf(intervalType));
			try {
				if (SchedulingIntervalType.Monthly.toString().equals(intervalType)) {

					job.setPeriodicDays(Integer.parseInt(chosenDayOfMonth.substring(0, chosenDayOfMonth.length() - 1)));
				} else if (SchedulingIntervalType.Weekly.toString().equals(intervalType)) {

					job.setPeriodicDays(Integer.parseInt(chosenDayOfWeek));
				} else if (SchedulingIntervalType.Day.toString().equals(intervalType)) {

					job.setPeriodicDays(Integer.parseInt(chosenDay.substring(0, chosenDay.length() - 1)));
				}
			} catch (NumberFormatException e) {
				// this should never happen
				e.printStackTrace();
				return "failure";
			}

		} else if (JobType.OneTime.toString().equals(jobType)) {
			System.out.println("now: " + startRightNow);
			if (startRightNow) {
				job.setNonperiodicNow(true);
				job.setNonperiodicTimestamp(new Date());
			} else {
				try {
					job.setNonperiodicTimestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(new SimpleDateFormat("dd.MM.yyyy").format(chosenDate) + " " + chosenTime));
				} catch (ParseException e) {
					// this should never happen
					e.printStackTrace();
					return "failure";
				}
			}
		}

		if (chosenService != null) {
			try {
				job.setServiceId(new BigDecimal(chosenService));
			} catch (NumberFormatException e) {
				// this should never happen
				e.printStackTrace();
				return "failure";
			}
		}

		if (chosenRepository != null) {
			if (chosenRepository.equals(0)) {
				job.setInfo("All");
			} else {
				job.setInfo(chosenRepository);
			}
		}
		System.out.println("date: " + job.getNonperiodicTimestamp());

		boolean stored; 
		FacesMessage msg;
		
		if (updateCase) {
			stored = schedulerControl.updateJob(job);
			msg = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_success_update", null);
		} else {
			stored = schedulerControl.createJob(job);
			msg = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "scheduling_servicejob_success_new", null);
		}
		if (stored) {
			ctx.addMessage(null, msg);
			return "success";
		}
		
		ctx.addMessage(null, LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_ERROR, "scheduling_servicejob_error_storage", null));
		
		return "failed";
	}
	
	
	/************************* Bean Initialization ***************************/
	

	private void initRepositories() {

		RepositoryBean.setRestConnector(restConnector);

		if (repoList != null && !repoList.isEmpty()) {
			return;
		}

		
		String result = restConnector.prepareRestTransmission("Repository/").GetData();
		repoList = new ArrayList<Repository>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return;
		}

		// prepare 'all' entry
		Repository repoAll = new Repository();
		repoAll.setName(LanguageSwitcherBean.getMessage(ctx, "general_all"));
		repoAll.setId(new Long(0));
		repoList.add(repoAll);

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			Repository repo = new Repository();

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("name")) {

					repo.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("url")) {

					repo.setUrl(res.getValue(key));

				} else if (key.equalsIgnoreCase("repository_id")) {

					repo.setId(new Long(res.getValue(key)));

				} else
					// System.out.println("Key: " + key);
					continue;
			}

			repoList.add(repo);

		}
		logger.info("Repositories received: " + repoList.size());

	}
	
	private boolean initJob(String jobId) {

		try {
			Integer.parseInt(jobId);
		}catch (NumberFormatException e) {
			logger.warn("Invalid job-ID '" + jobId + "'. Job-ID is required to be a numeric value!");
			return false;
		}

		String result = restConnector.prepareRestTransmission("ServiceJob/" + jobId).GetData();
		jobList = new ArrayList<SchedulingBean>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no scheduling job details at all from the server");
			return false;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			SchedulingBean job = new SchedulingBean();

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("name")) {
					job.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("status")) {
					job.setStatus(ServiceStatus.valueOf(res.getValue(key)));

				} else if (key.equalsIgnoreCase("service_id")) {
					job.setServiceId(new BigDecimal((new Long(res.getValue(key)))));

				} else if (key.equalsIgnoreCase("job_id")) {
					job.setJobId(Integer.parseInt(res.getValue(key)));

				} else if (key.equalsIgnoreCase("info")) {
					job.setInfo(res.getValue(key));

				} else if (key.equalsIgnoreCase("periodic")) {
					job.setPeriodic(Boolean.parseBoolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("nonperiodic_date")) {
					System.out.println(res.getValue(key));
					String npd = res.getValue(key);
					
					Date date = null;
					if (npd != null) {
						
						try {
							date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(npd);
						} catch (ParseException e) {
							logger.warn("Could not parse date received from server. " + npd, e);
						}
					}
					job.setNonperiodicTimestamp(date);

				} else if (key.equalsIgnoreCase("periodic_interval_type")) {
					job.setPeriodicInterval(res.getValue(key) != null ? SchedulingIntervalType.valueOf(res.getValue(key)) : null);

				} else if (key.equalsIgnoreCase("periodic_interval_days")) {
					job.setPeriodicDays(Integer.parseInt(res.getValue(key)));
				} else

					// System.out.println("Key: " + key);
					continue;
			}

//			if (job != null) {
//				
//				// 
//				
//				this.job = job;
//				
//				private String chosenService;
//				
//				// repeatedly or once only
//				private String jobType;
//				
//				// case repeatedly
//				private String intervalType = SchedulingIntervalType.Day.toString();
//				private String chosenDayOfMonth;
//				private String chosenDayOfWeek;
//				private String chosenDay;
//				private String chosenHour = "20:00";
//
//				
//				// case once only
//				private List<Repository> repoList;
//				private String chosenRepository;
//				private Date   chosenDate = new Date();
//				private String chosenTime = "20:00";
//				private boolean startRightNow;
//				
//				return true;
//			}
		}
		return false;
	}
	
	
	/******************************* Form Preparations *******************************/
	

	public List<String> getMonthlyDays() {

		List<String> days = new ArrayList<String>();

		for (int i = 1; i <= 31; i++) {
			days.add(Integer.toString(i) + ".");
		}
		return days;
	}

	public List<String> getDays() {

		List<String> days = new ArrayList<String>();

		for (int i = 1; i <= 14; i++) {
			days.add(Integer.toString(i) + ".");
		}
		return days;
	}

	public List<String> getHours() {

		List<String> hours = new ArrayList<String>();

		for (int i = 0; i < 24; i++) {
			hours.add(i + ":00");
			hours.add(i + ":15");
			hours.add(i + ":30");
			hours.add(i + ":45");
		}

		return hours;
	}

	public enum JobType {
		Repeatedly, OneTime;
	}

	public SchedulingIntervalType[] getIntervalTypes() {
		return SchedulingIntervalType.values();
	}

	public JobType[] getJobTypes() {
		return JobType.values();
	}
	

	/*********************** Getter & Setter ***********************/
	
	public void setIntervalType(String intervalType) {
		this.intervalType = intervalType;
	}

	public String getIntervalType() {
		return intervalType;
	}

	public SchedulingBean getJob() {
		return job;
	}

	public void setJob(SchedulingBean job) {
		this.job = job;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getChosenDayOfMonth() {
		return chosenDayOfMonth;
	}

	public void setChosenDayOfMonth(String chosenDayOfMonth) {
		this.chosenDayOfMonth = chosenDayOfMonth;
	}

	public String getChosenDayOfWeek() {
		return chosenDayOfWeek;
	}

	public void setChosenDayOfWeek(String chosenDayOfWeek) {
		this.chosenDayOfWeek = chosenDayOfWeek;
	}

	public String getChosenDay() {
		return chosenDay;
	}

	public void setChosenDay(String chosenDay) {
		this.chosenDay = chosenDay;
	}

	public String getChosenService() {
		return chosenService;
	}

	public void setChosenService(String chosenService) {
		this.chosenService = chosenService;
	}

	public String getChosenHour() {
		return chosenHour;
	}

	public void setChosenHour(String chosenHour) {
		this.chosenHour = chosenHour;
	}

	public Date getChosenDate() {
		return chosenDate;
	}

	public void setChosenDate(Date chosenDate) {
		this.chosenDate = chosenDate;
	}

	public String getChosenTime() {
		return chosenTime;
	}

	public void setChosenTime(String chosenTime) {
		this.chosenTime = chosenTime;
	}

	public List<SchedulingBean> getJobList() {
		return jobList;
	}
	
	public boolean isJobListEmpty() {
		return jobList == null || jobList.isEmpty();
	}

	public List<Repository> getRepositories() {
		return repoList;
	}

	public String getChosenRepository() {
		return chosenRepository;
	}

	public void setChosenRepository(String chosenRepository) {
		this.chosenRepository = chosenRepository;
	}

	public boolean isStartRightNow() {
    	return startRightNow;
    }

	public void setStartRightNow(boolean startRightNow) {
    	this.startRightNow = startRightNow;
    }

	public Date getChosenDate2() {
		return chosenDate2;
	}

	public void setChosenDate2(Date chosenDate2) {
		this.chosenDate2 = chosenDate2;
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public void setSchedulerControl(SchedulerControl schedulerControl) {
    	this.schedulerControl = schedulerControl;
    }

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isUpdateCase() {
		return updateCase;
	}
	
	public String getRadio1() {
		return radio1;
	}

	public void setRadio1(String radio1) {
		this.radio1 = radio1;
	}
	

}
