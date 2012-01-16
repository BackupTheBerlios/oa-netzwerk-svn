package de.dini.oanetzwerk.admin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.datascroller.ScrollerActionEvent;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

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
	

//	public int getRowCount() {
//		if (jobList != null) {
//			return jobList.size();
//		}
//		return 0;
//    }
//
//	private int rowCount;
//
//	public void setRowCount(int rowCount) {
//    	this.rowCount = rowCount;
//    }
	
   public void scrollerAction(ActionEvent event)
    {
        ScrollerActionEvent scrollerEvent = (ScrollerActionEvent) event;
        FacesContext.getCurrentInstance().getExternalContext().log(
                        "scrollerAction: facet: "
                                        + scrollerEvent.getScrollerfacet()
                                        + ", pageindex: "
                                        + scrollerEvent.getPageIndex());
    }

}
