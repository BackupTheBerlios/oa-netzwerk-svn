package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
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

	private List<SchedulingBean> jobList;
	private SchedulingBean job;

	private String chosenService;
	private String chosenDayOfMonth;
	private String chosenDayOfWeek;
	private String chosenDay;
	private String chosenHour;
	private boolean update;
	private String chosenDate;
	
	private String intervalType;
	private int intervalDay;

	private boolean changeJob;

	private boolean deactivated;
	private boolean deleted;
	private boolean stored;
	
	private String test;
	
	

	public String getTest() {
    	return test;
    }

	public void setTest(String test) {
    	this.test = test;
    }

	public ServiceSchedulingBean() {

	}

	public boolean success() {
		return deactivated || deleted || stored;
	}

	@PostConstruct
	public void init() {

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
					job.setNonperiodicTimestamp(Date.valueOf(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval_type")) {
					job.setPeriodicInterval(res.getValue(key) != null ? SchedulingIntervalType.valueOf(res.getValue(key)): null);

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

		

		// REST call
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.ServiceJob);
		rms.setStatus(RestStatusEnum.OK);

		res = new RestEntrySet();

		boolean jobIdSpecified = false;
		
		if (job.getJobId() != null && job.getJobId() > 0) {
			jobIdSpecified = true;
			res.addEntry("job_id", job.getJobId().toString());
		}
			
		
		res.addEntry("name", job.getName());
		res.addEntry("status", job.getStatus().toString());
		res.addEntry("info", job.getInfo());
		res.addEntry("service_id", job.getServiceId().toString());
		res.addEntry("periodic", Boolean.toString(job.isPeriodic()));
		res.addEntry("nonperiodic_date", job.getNonperiodicTimestamp().toString());
		res.addEntry("periodic_interval_type", job.getPeriodicInterval().toString());
		res.addEntry("periodic_interval_days", Integer.toString(job.getPeriodicDays()));
			
		
		rms.addEntrySet(res);
		
		
		try {
			result = restConnector.prepareRestTransmission("ServiceJob/" + (jobIdSpecified ? job.getJobId().toString() : null)).sendPutRestMessage(rms);
			
			if (rms.getStatus() != RestStatusEnum.OK) {

				logger.error("/ServiceJob response failed: " + rms.getStatus() + "("
						+ rms.getStatusDescription() + ")");
				return "failed";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "failed";
		}
		
		logger.info("PUT sent to /ServiceJob");

		

		ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"info.success_stored", null));

		return "success";
	}


	
	public List<String> getIntervalTypes() {

		List<String> intervalTypes = new ArrayList<String>();
		
		intervalTypes.add("monthly");
		intervalTypes.add("weekly");
		intervalTypes.add("day");
		
		return intervalTypes;
	}
	
	

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
	
	public List<String> getServices() {

		List<String> services = new ArrayList<String>();
		
		services.add("Harvester");
		services.add("Aggregator");
		services.add("Marker");
		
		return services;
	}
	
	public List<String> getHours() {

		List<String> hours = new ArrayList<String>();
		
		hours.add("0:00");
		hours.add("1:00");
		hours.add("2:00");
		hours.add("3:00");
		hours.add("4:00");
		hours.add("5:00");
		hours.add("6:00");
		hours.add("7:00");
		hours.add("8:00");
		hours.add("9:00");
		hours.add("10:00");
		hours.add("11:00");
		hours.add("12:00");
		hours.add("13:00");
		hours.add("14:00");
		hours.add("15:00");
		hours.add("16:00");
		hours.add("17:00");
		hours.add("18:00");
		hours.add("19:00");
		hours.add("20:00");
		hours.add("21:00");
		hours.add("22:00");
		hours.add("23:00");
		
		return hours;
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
	
	
	

	public String getChosenDate() {
		return chosenDate;
	}

	public void setChosenDate(String chosenDate) {
		this.chosenDate = chosenDate;
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
}
