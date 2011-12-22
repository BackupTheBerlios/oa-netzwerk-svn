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
import de.dini.oanetzwerk.utils.StringUtils;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
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
	private List<ServiceBean> services;
	
	public ServiceJobListBean() {
		super();
	}


	@PostConstruct
	public void init() {
		
		// retrieve services for naming lookup
		services = restConnector.fetchServices();
		
		// retrieve the jobs to be displayed
		jobList = restConnector.fetchServiceJobs();
		

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
				message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "serviceJobList_delete_success", null);
			} else {
				message = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "serviceJobList_delete_fail", null);
			}
			ctx.addMessage("1", message);
		}
		
	}
	
	public String getServiceName(BigDecimal id) {
		return services.get(id.intValue() - 1).getPrettyName().toString();
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
