import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.RestConnector;
import de.dini.oanetzwerk.admin.SchedulingBean;
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
		return jobList;
	}
	
	public boolean isJobListEmpty() {
		return jobList == null || jobList.isEmpty();
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}
	
}
