package de.dini.oanetzwerk.admin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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


@ManagedBean(name="jobList")
@RequestScoped
public class ServiceJobListBean extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ServiceJobListBean.class);

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	@ManagedProperty(value = "#{schedulerControl}")
	private SchedulerControl schedulerControl;
	
	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	

	private List<SchedulingBean> jobList;
	
	public ServiceJobListBean() {
		super();
	}


	@PostConstruct
	public void init() {
		
		// retrieve the jobs to be displayed
		initJobs();
		

		// try to fetch jobId from request parameters in case this is an update/delete request
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String jobId = request.getParameter("jid");
		
		boolean deleted = false;
		
		int jobIndex = -1;
		System.out.println("Job-ID: " + jobId);
		if (jobId != null) {
			for (SchedulingBean job : jobList) {
				if (job.getJobId() != null && jobId.equals(job.getJobId().toString())) {
					jobIndex = jobList.indexOf(job);
					deleted = schedulerControl.deleteJob(jobId, job.getName());
				}
			}
			
			FacesMessage message;
			if (deleted) {
				jobList.remove(jobIndex);
				message = new FacesMessage("Der Job wurde erfolgreich gelöscht.");
			} else {
				message = new FacesMessage("Der Job konnte nicht entfernt werden. Bitte prüfen sie die Logs.");
			}
			ctx.addMessage("1", message);
		}
		
	}
	
	
	private void initJobs() {
//		if (jobList != null && !jobList.isEmpty()) {
//			return;
//		}

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

			jobList.add(job);

		}
		System.out.println("Job List: " + jobList.size());
	}
	
	
	
	
	/********************* Getter & Setter **********************/
	
	public List<SchedulingBean> getJobList() {
		Collections.sort(jobList);
		return jobList;
	}
	
	public boolean isJobListEmpty() {
		return jobList == null || jobList.isEmpty();
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}


	public void setSchedulerControl(SchedulerControl schedulerControl) {
		this.schedulerControl = schedulerControl;
	}
		
}
