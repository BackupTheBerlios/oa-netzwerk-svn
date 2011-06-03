package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.scheduling.SchedulerControl;
import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;

@ManagedBean(name = "scheduling")
@RequestScoped
public class ServiceSchedulingBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ServiceSchedulingBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
	@ManagedProperty(value = "#{schedulerControl}")
	private SchedulerControl schedulerControl;

	private List<SchedulingBean> jobList;
	private SchedulingBean job;

	private String chosenService;
	private String chosenDayOfMonth;
	private String chosenDayOfWeek;
	private String chosenDay;
	private String chosenHour;
	private boolean update;

	private String chosenRepository;
	private Date   chosenDate;
	private String chosenTime;
	private boolean startRightNow = false;

	private String intervalType = SchedulingIntervalType.Day.toString();
	private String jobType = JobType.Repeatedly.toString();

	private int intervalDay;

	private boolean changeJob;

	private boolean deactivated;
	private boolean deleted;
	private boolean stored;

	private boolean radio1;

	private Map<String, Integer> services = new HashMap<String, Integer>();
	private Map<String, Long> repoList;

	public boolean isRadio1() {
		return radio1;
	}

	public void setRadio1(boolean radio1) {
		this.radio1 = radio1;
	}

	public ServiceSchedulingBean() {

	}

	public boolean success() {
		return deactivated || deleted || stored;
	}

	@PostConstruct
	public void init() {

//		schedulerControl = SchedulerControl.getInstance();
		
		// init job object for a new job that might be created
		job = new SchedulingBean();

		// create a list of services
		// TODO should be retrieved from the DB
		services.put("Harvester", 1);
		services.put("Aggregator", 2);
		services.put("Marker", 3);

		// retrieve list of repositories
		initRepositories();

		// retrieve the jobs to be displayed
		if (true)
			return;

		if (jobList != null && !jobList.isEmpty()) {
			return;
		}

		String result = restConnector.prepareRestTransmission("ServiceJob/").GetData();
		jobList = new ArrayList<SchedulingBean>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no scheduling job details at all from the server");
			return;
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
					job.setNonperiodicTimestamp(new Date((res.getValue(key))));

				} else if (key.equalsIgnoreCase("periodic_interval_type")) {
					job.setPeriodicInterval(res.getValue(key) != null ? SchedulingIntervalType.valueOf(res.getValue(key)) : null);

				} else if (key.equalsIgnoreCase("periodic_interval_days")) {
					job.setPeriodicDays(Integer.parseInt(res.getValue(key)));
				} else

					// System.out.println("Key: " + key);
					continue;
			}

			jobList.add(job);

		}
		System.out.println("Job List: " + jobList.size());
	}

	public String storeJob() {

		System.out.println("radio1: " + radio1);
		System.out.println("date: " + chosenDate);
		System.out.println("time: " + chosenTime);
		System.out.println("jobType: " + jobType);
		System.out.println("intervalType: " + intervalType);

		if (JobType.Repeatedly.toString().equals(jobType)) {
			try {
				System.out.println("break1");
				Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " " + chosenHour);
				job.setNonperiodicTimestamp(date);
				System.out.println("break2");
			} catch (ParseException e) {
				// this should never happen
				e.printStackTrace();
				return "failure";
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
			try {
				job.setNonperiodicTimestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(new SimpleDateFormat("dd.MM.yyyy").format(chosenDate) + " " + chosenTime));
			} catch (ParseException e) {
				// this should never happen
				e.printStackTrace();
				return "failure";
			}
		}
		System.out.println("bla");
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
				job.setInfo("Repository mit ID " + chosenRepository);
			}
		}
		System.out.println("date: " + job.getNonperiodicTimestamp());

		boolean stored = schedulerControl.storeJob(job);

		if (stored)
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "info.success_stored", null));
		
		return "success";
	}

	public void initRepositories() {

		RepositoryBean.setRestConnector(restConnector);

		if (repoList != null && !repoList.isEmpty()) {
			return;
		}

		String result = restConnector.prepareRestTransmission("Repository/").GetData();
		repoList = new HashMap<String, Long>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return;
		}

		repoList.put("Alle", new Long(0));

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

			repoList.put(repo.getName(), repo.getId());

		}
		System.out.println(repoList.size());

	}

	/********************* + Validations *********************/

	public void validateDate(FacesContext context, UIComponent toValidate, Object value) {
		System.out.println("VALIDATING");

		// TODO
		if (true)
			return;

		String chosenDate = (String) value;

		Date date = null;
		try {
			date = new SimpleDateFormat("dd.MM.yyyy").parse(chosenDate);
		} catch (ParseException e) {
			((UIInput) toValidate).setValid(false);

			FacesMessage message = new FacesMessage("Bitte geben sie das Datum in einem gültigen Format an. (tt.mm.jjjj)");
			context.addMessage(toValidate.getClientId(context), message);
			return;
		}

		if (System.currentTimeMillis() > date.getTime()) {
			((UIInput) toValidate).setValid(false);

			FacesMessage message = new FacesMessage("Das gewählte Datum muss in der Zukunft liegen.");
			context.addMessage(toValidate.getClientId(context), message);
		}

	}

	// public List<String> getIntervalTypes() {
	//
	// List<String> intervalTypes = new ArrayList<String>();
	//
	// intervalTypes.add("monthly");
	// intervalTypes.add("weekly");
	// intervalTypes.add("day");
	//
	// return intervalTypes;
	// }

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

	public Map<String, Integer> getServices() {

		return services;
	}

	public List<String> getHours() {

		List<String> hours = new ArrayList<String>();

		hours.add("0:00");
		hours.add("0:30");
		hours.add("1:00");
		hours.add("1:30");
		hours.add("2:00");
		hours.add("2:30");
		hours.add("3:00");
		hours.add("3:30");
		hours.add("4:00");
		hours.add("4:30");
		hours.add("5:00");
		hours.add("5:30");
		hours.add("6:00");
		hours.add("6:30");
		hours.add("7:00");
		hours.add("7:30");
		hours.add("8:00");
		hours.add("8:30");
		hours.add("9:00");
		hours.add("9:30");
		hours.add("10:00");
		hours.add("10:30");
		hours.add("11:00");
		hours.add("11:30");
		hours.add("12:00");
		hours.add("12:30");
		hours.add("13:00");
		hours.add("13:30");
		hours.add("14:00");
		hours.add("14:30");
		hours.add("15:00");
		hours.add("15:30");
		hours.add("16:00");
		hours.add("16:30");
		hours.add("17:00");
		hours.add("17:30");
		hours.add("18:00");
		hours.add("18:30");
		hours.add("19:00");
		hours.add("19:30");
		hours.add("20:00");
		hours.add("20:30");
		hours.add("21:00");
		hours.add("21:30");
		hours.add("22:00");
		hours.add("22:30");
		hours.add("23:00");
		hours.add("23:30");

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

	public int getIntervalDay() {
		return intervalDay;
	}

	public void setIntervalDay(int intervalDay) {
		this.intervalDay = intervalDay;
	}

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

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public void setChangeJob(boolean s) {
		this.changeJob = s;
	}

	public boolean getChangeJob() {
		return this.changeJob;
	}

	public List<SchedulingBean> getJobList() {
		return jobList;
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

//	public void setSchedulerControl(SchedulerControl schedulerControl) {
//    	this.schedulerControl = schedulerControl;
//    }

	public Map<String, Long> getRepositories() {
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

	public void setSchedulerControl(SchedulerControl schedulerControl) {
    	this.schedulerControl = schedulerControl;
    }
	
	
	
}
