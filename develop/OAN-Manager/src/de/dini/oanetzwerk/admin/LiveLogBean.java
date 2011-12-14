package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.ServiceManagementBean.Service;
import de.dini.oanetzwerk.admin.utils.AbstractBean;
import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;
import de.dini.oanetzwerk.utils.LogFileTailer;
import de.dini.oanetzwerk.utils.LogFileTailerListener;
import de.dini.oanetzwerk.utils.PropertyManager;
import de.dini.oanetzwerk.utils.StringUtils;

/**
 * @author Sammy David sammy.david@cms.hu-berlin.de
 * 
 */

@ManagedBean(name = "log")
@SessionScoped
public class LiveLogBean extends AbstractBean implements Serializable, IHarvesterMonitor, LogFileTailerListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UserBean.class);
	private static final SimpleDateFormat DATE_GER = new SimpleDateFormat("dd.MM.yyyy");

	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);

	private Long id = null;
	private String update = "";
	private String updateMessage = LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "services_log_noupdates", null)
	                .getDetail();
	private boolean renderPopups = true;
	private String chosenService;
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;

	private LogFileTailer tailer;
	private StringBuffer logCache = new StringBuffer();
	private static List<ServiceBean> services = new ArrayList<ServiceBean>();
	private static Map<String, ServiceBean> serviceMap = new HashMap<String, ServiceBean>();

	static {

		List<Service> services = SchedulingBean.getServices();

		for (int i = 0; i < services.size(); i++) {
			LiveLogBean.services.add(new ServiceBean(services.get(i)));
			System.out.println("static");
			System.out.println(services.get(i).toString());
			LiveLogBean.serviceMap.put(services.get(i).toString(), LiveLogBean.services.get(i));
		    
		}
	}

	public LiveLogBean() {
		super();

		// String name = "HarvesterMonitorService";
		//
		//
		// try {
		// // FileWriter writer = new FileWriter(new
		// File("/home/davidsam/Desktop/1234567.txt"));
		// // writer.write("blabla");
		// // writer.flush();
		// // writer.close();
		//
		// IHarvesterMonitor stub = (IHarvesterMonitor)
		// UnicastRemoteObject.exportObject(this, 0);
		// Registry registry = RMIRegistryHelper.getRegistry();
		//
		// if (registry == null) {
		// logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to bind to obtain Harvester-Monitoring!");
		// return;
		// }
		//
		// registry.rebind(name, stub);
		// System.out.println(name + " bound");
		// } catch (Exception e) {
		// System.err.println(name + " could not be bound: ");
		// e.printStackTrace();
		// }
	}

	@PostConstruct
	public void init() {

		initializeServiceProperties();

		initializeMonitoring();
	}
	
	public void initializeMonitoring() {
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String service = request.getParameter("service");
		
		if (service != null && !service.isEmpty() && !service.equals(chosenService)) {
			chosenService = service;
			readLogFile();
		}
	}

	public void initializeServiceProperties() {

		Properties props = propertyManager.getServiceProperties();
		String path, logPath;

		for (ServiceBean service : services) {

			// RESOLVE SERVICE PATH
			// resolve environemnt variables within path
			path = StringUtils.resolveSystemVariable(props.getProperty("location." + service.getLowerCaseName()));

			// retrieve file paths to services (harvester, aggregator and
			// marker)
			service.setLocalPath(path);

			// RESOLVE LOG PATH FOR SERVICE
			// resolve environment variables within log path
			logPath = StringUtils.resolveSystemVariable(props.getProperty("location." + service.getLowerCaseName() + ".logfile"));

			// retrieve log file path for services
			service.setLocalLogPath(logPath);

			// retrieve rmi registry urls for each service
			service.setRmiHost(props.getProperty("java.rmiregistry.host." + service.getLowerCaseName()));
		}
	}

	public void readLogFile() {

		// stop existing log-monitoring
		if (tailer != null) {
			tailer.removeLogFileTailerListener(this);
			tailer.stopTailing();
			tailer = null;
		}
		
		System.out.println("CHOSEN: " + chosenService);
		ServiceBean service = serviceMap.get(chosenService);
		
		if (service == null) {
			// TODO: error message
			System.out.println("Could not find service for name " + chosenService);
			return;			
		}
		
		logCache = new StringBuffer();
		
		if (service.getLocalLogPath() == null || service.getLocalLogPath().isEmpty())
		{
			System.out.println("Es wurde kein lokaler Pfad zur Log-Datei des Dienstes '"+ service.getPrettyName() +"' gefunden! (siehe services.properties)");
			return;	
		}
		
		File logFile = new File(service.getLocalLogPath());
		
		if (!logFile.exists()) {
			// TODO: error message
			System.out.println("Die Log-Datei unter '"+ service.getLocalLogPath() +"' konnte nicht gefunden werden!");
			return;
		}
		
		// start log monitoring for service
		tailer = new LogFileTailer(new File(service.getLocalLogPath()), 3000, false);
		tailer.addLogFileTailerListener(this);
		tailer.start();
	}
	
	// dummy parameter as <f:propertyActionListener> does not seem to be intended to 
	// call a setter without a parameter
	public void setClearLogCache(String dummy) {
		logCache = new StringBuffer();
	}

	/**
	 * A new line has been added to the tailed log file
	 * 
	 * @param line
	 *            The new line that has been added to the tailed log file
	 */
	public void newLogFileLine(String line) {
		logCache.append(line).append("\n");
	}

	public String getUpdate() {
		return logCache.length() == 0 ? updateMessage : logCache.toString();
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public boolean isRenderPopups() {
		return renderPopups;
	}

	public void setRenderPopups(boolean renderPopups) {
		this.renderPopups = renderPopups;
		System.out.println("popups true");
	}

	@Override
	public void publishServiceUpdates(Map<String, String> updates) {

		update += updates.get("messages");

	}

	/********************* Getter & Setter ***********************/

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	public String getChosenService() {
    	return chosenService;
    }

	public void setChosenService(String chosenService) {
    	this.chosenService = chosenService;
    }

	public List<ServiceBean> getServices() {
    	return services;
    }

	public void setServices(List<ServiceBean> services) {
    	LiveLogBean.services = services;
    }

	public long getUpdateInterval() {
		
		if (tailer != null) {
			return tailer.getSampleInterval();
		}
		return 3000;
	}
	
	public void setUpdateInterval(long ms) {

		if (tailer != null) {
			
			// minimum update frequency should be 0.5 seconds
			if (ms < 1000) {
				tailer.setSampleInterval(1000);
				return;
			}
			tailer.setSampleInterval(ms);
		}
	}
}
