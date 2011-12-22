package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.PropertyManager;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name="restConnector")
@ApplicationScoped
public class RestConnector implements Serializable {

    private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(RestConnector.class);
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	
	private long serviceRefreshRate = 3 * 60000; // 3 * 60000ms = 3min 
	private Date lastServiceRetrieval;
	private List<ServiceBean> services = new ArrayList<ServiceBean>();
	
	public RestConnector() {
	    super();
    }
	

	
	public List<Repository> fetchRepositories() {
		
		logger.info("Retrieving repositories from server ...");

		String result = prepareRestTransmission("Repository/").GetData();
		List<Repository> repoList = new ArrayList<Repository>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("Received no repository details at all from the server!");
			return repoList;
		}

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

				} else if (key.equalsIgnoreCase("active")) {

					repo.setActive(Boolean.parseBoolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("last_full_harvest_begin")) {

					repo.setLastFullHarvestBegin(res.getValue(key));

				} else if (key.equalsIgnoreCase("oai_url")) {
					
					repo.setOaiUrl(res.getValue(key));
					
				} else
					continue;
			}

			repoList.add(repo);
		}
		
		logger.info(repoList.size() + " repositories received from server!");
		
		return repoList;
	}
	
	// TODO: reload after a couple of minutes (cache meanwhile) and if services have changed via ServiceManager
	
	public List<ServiceBean> fetchServices() {
		

		if (lastServiceRetrieval != null && services != null && !serviceRefreshIsDue()) {
			logger.info("Using cached services ...");
			return services;
		}
		logger.info("Retrieving services from server ...");

		String result = prepareRestTransmission("Services/").GetData();
		List<ServiceBean> serviceList = new ArrayList<ServiceBean>();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("Received no service details at all from the server!");
			return serviceList;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			ServiceBean repo = new ServiceBean();

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("name")) {

					repo.setService(res.getValue(key));

				} else if (key.equalsIgnoreCase("service_id")) {

					repo.setId(new BigDecimal(res.getValue(key)));
					
				} else
					continue;
			}

			serviceList.add(repo);
		}
		
		logger.info(serviceList.size() + " services received from server!");
		
		synchronized (this) {
	        services = serviceList;
        }
		
		return services;
	}
	
	private boolean serviceRefreshIsDue(){
		
		return lastServiceRetrieval.getTime() + serviceRefreshRate >= new Date().getTime();
	}
	
	public List<SchedulingBean> fetchServiceJobs() {
		
		logger.info("Retrieving service jobs from server ...");

		List<SchedulingBean> jobList = new ArrayList<SchedulingBean>();
		String result = prepareRestTransmission("ServiceJob/").GetData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no service jobs at all from the server");
			return jobList;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			SchedulingBean bean = new SchedulingBean();
			Iterator<String> it = res.getKeyIterator();
			String key = "";

			while (it.hasNext()) {

				key = it.next();

				if (key.equalsIgnoreCase("name")) {
					logger.info("Retrieved job from DB with job-name: " + res.getValue(key));
					bean.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("service_id")) {
					bean.setServiceId(new BigDecimal(res.getValue(key)));

				} else if (key.equalsIgnoreCase("job_id")) {
					bean.setJobId(new Integer(res.getValue(key)));

				} else if (key.equalsIgnoreCase("status")) {
					bean.setStatus(ServiceStatus.valueOf(res.getValue(key)));

				} else if (key.equalsIgnoreCase("info")) {
					bean.setInfo(res.getValue(key));

				} else if (key.equalsIgnoreCase("nonperiodic_date")) {
					try {
						bean.setNonperiodicTimestamp(new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(res.getValue(key)).getTime()));
					} catch (ParseException ex) {

						logger.warn("Could not parse date received from server. " + res.getValue(key), ex);
					}

				} else if (key.equalsIgnoreCase("periodic")) {
					bean.setPeriodic(new Boolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval_days")) {
					bean.setPeriodicDays(new Integer(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval_type")) {
					bean.setPeriodicInterval(res.getValue(key) == null ? null : SchedulingIntervalType.valueOf(res.getValue(key)));

				} else
					continue;
			}
			jobList.add(bean);
		}
		logger.info(jobList.size() + " service jobs received from the server.");
		
		return jobList;
	}
	
	public RestClient prepareRestTransmission(String resource) {

		Properties props = propertyManager.getAdminProperties();
		
		if (props == null) {
			logger.warn("Property file has not been initialized correctly, cannot prepare REST transmission!");
		}
		
		System.out.println("restclientpropfile: " + System.getProperty ("catalina.base") + propertyManager.getWebappDir() + propertyManager.getContextPath() + props.getProperty
		 ("restclientpropfile"));
		return RestClient.createRestClient (new File(System.getProperty ("catalina.base") + propertyManager.getWebappDir() + propertyManager.getContextPath() + props.getProperty
		 ("restclientpropfile")), resource, props.getProperty
		 ("username"), props.getProperty("password"));
	}


	public void setPropertyManager(PropertyManager propertyManager) {
    	this.propertyManager = propertyManager;
    }

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}	
	
}
