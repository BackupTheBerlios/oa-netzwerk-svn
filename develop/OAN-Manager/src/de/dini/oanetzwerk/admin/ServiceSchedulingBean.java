package de.dini.oanetzwerk.admin;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

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

	private String intervalType;
	private int intervalDay;

	private boolean changeJob;

	private boolean deactivated;
	private boolean deleted;
	private boolean stored;

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
					job.setPeriodicInterval(res.getValue(key));

				} else if (key.equalsIgnoreCase("periodic_interval_days")) {
					job.setPeriodicDays(Integer.parseInt(res.getValue(key)));
				} else

					// System.out.println("Key: " + key);
					continue;
			}

			jobList.add(job);

		}
		System.out.println(jobList.size());
	}
	

	public String storeRepository() {

		

		// REST call
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.Repository);
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
		res.addEntry("periodic_interval_type", job.getPeriodicInterval());
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

	public enum SchedulingIntervalType {
		Monthly, Weekly, Day;
	}
	
	public List<String> getIntervalTypes() {

		List<String> intervalTypes = new ArrayList<String>();
		
		intervalTypes.add("monthly");
		intervalTypes.add("weekly");
		intervalTypes.add("day");
		
		return intervalTypes;
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
