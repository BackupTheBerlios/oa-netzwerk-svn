package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.PropertyManager;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name = "services")
@RequestScoped
public class ServiceManagementBean {

	private final static Logger logger = Logger.getLogger(ServiceManagementBean.class);
	
	private static final String SERVICE_NAME_HARVESTER 	= "HarvesterService";
	private static final String SERVICE_NAME_AGGREGATOR = "AggregatorService";
	private static final String SERVICE_NAME_MARKER 	= "MarkerService";

	private boolean localHarvester = false;
	private boolean localAggregator = false;
	private boolean localMarker = false;

	private ServiceStatus harvesterStatus;
	private ServiceStatus aggregatorStatus;
	private ServiceStatus markerStatus;

	private String localPathToHarvester = null;
	private String localPathToAggregator = null;
	private String localPathToMarker = null;
	
	private String rmiHostForHarvester = null;
	private String rmiHostForAggregator = null;
	private String rmiHostForMarker = null;
	
	private boolean rmiRegistryNotStarted = false;
	
	private String javaPath = null;

	@ManagedProperty(value="#{restConnector}")
	private RestConnector connector;	
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;

	FacesContext ctx = FacesContext.getCurrentInstance();
	
	public ServiceManagementBean() {
		super();
	}

	@PostConstruct
	private void init() {
		
		// retrieve file paths to services (harvester, aggregator and marker)
		localPathToHarvester = propertyManager.getServiceProperties().getProperty("location.harvester");
		localPathToAggregator = propertyManager.getServiceProperties().getProperty("location.aggregator");
		localPathToMarker = propertyManager.getServiceProperties().getProperty("location.marker");
		
		// retrieve rmi registry urls for each service
		rmiHostForHarvester = propertyManager.getServiceProperties().getProperty("java.rmiregistry.host.harvester");
		rmiHostForAggregator = propertyManager.getServiceProperties().getProperty("java.rmiregistry.host.aggregator");
		rmiHostForMarker = propertyManager.getServiceProperties().getProperty("java.rmiregistry.host.marker");
		
		
		if (localPathToHarvester != null && new File(localPathToHarvester).exists()) {

			localHarvester = true;
		}
		
		if (localPathToAggregator != null && new File(localPathToAggregator).exists()) {

			localAggregator = true;
		}
		
		if (localPathToMarker != null && new File(localPathToMarker).exists()) {

			localMarker = true;
		}

		
		harvesterStatus = checkServiceStatus("HarvesterService", rmiHostForHarvester);

		if (!rmiRegistryNotStarted) {
			aggregatorStatus = checkServiceStatus("AggregatorService", rmiHostForAggregator);
		}
		if (!rmiRegistryNotStarted) {
			markerStatus = checkServiceStatus("MarkerService", rmiHostForMarker);
		}
		
		// try to fetch path to a specified java binary file
		javaPath = propertyManager.getServiceProperties().getProperty("java.path");
		if (javaPath == null || javaPath.trim().length() == 0) {
			javaPath = null;
		}
	}

	
	
	private ServiceStatus checkServiceStatus(String serviceName, String serviceHost) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		Registry registry;
		if (serviceHost == null || "".equals(serviceHost)) {
			registry = RMIRegistryHelper.getRegistry();
		} else {
			registry = RMIRegistryHelper.getRegistry(serviceHost);
		}

		try {
			IService service = (IService) registry.lookup(serviceName);

			if (service != null) {
				System.out.println(serviceName + " running.");
				try {
					return service.getCurrentStatus();
				} catch (ConnectException e) {
					return ServiceStatus.Stopped;
				}
			}
		} catch (ConnectException e) {
			logger.warn("Could not connect to RMI registry, please make sure the registry is running");
			rmiRegistryNotStarted = true;
			context.addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "services_failure_rmiregistrynotfound", null));
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			logger.info("RMI-Service with name " + serviceName + " is not yet registered with rmi-registry.");
		}
		return ServiceStatus.Stopped;
	}

	public String startHarvesterService() {
		
		boolean started = startService(SERVICE_NAME_HARVESTER, localPathToHarvester, getHarvesterStatus());
		
		if (started) {
			harvesterStatus = checkServiceStatus(SERVICE_NAME_HARVESTER, rmiHostForHarvester);
		}
		logger.info(SERVICE_NAME_HARVESTER + " " + harvesterStatus);
		return "services_main";
	}
	
	public String stopHarvesterService() {
		boolean stopped = stopService(SERVICE_NAME_HARVESTER, getHarvesterStatus(), rmiHostForHarvester);
		
		if (stopped) {
			harvesterStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_HARVESTER + " " + harvesterStatus);
		return "services_main";
	}
	
	public String startAggregatorService() {
		
		boolean started = startService(SERVICE_NAME_AGGREGATOR, localPathToAggregator, getAggregatorStatus());
		
		if (started) {
			aggregatorStatus = checkServiceStatus(SERVICE_NAME_AGGREGATOR, rmiHostForAggregator);
		}
		logger.info(SERVICE_NAME_AGGREGATOR + " " + aggregatorStatus);
		return "services_main";
	}
	
	public String stopAggregatorService() {
		boolean stopped = stopService(SERVICE_NAME_AGGREGATOR, getAggregatorStatus(), rmiHostForAggregator);
		
		if (stopped) {
			aggregatorStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_AGGREGATOR + " " + aggregatorStatus);
		return "services_main";
	}
	
	public String startMarkerService() {
		
		boolean started = startService(SERVICE_NAME_MARKER, localPathToMarker, getMarkerStatus());
		
		if (started) {
			markerStatus = checkServiceStatus(SERVICE_NAME_MARKER, rmiHostForMarker);
		}
		logger.info(SERVICE_NAME_MARKER + " " + markerStatus);
		return "services_main";
	}
	
	public String stopMarkerService() {
		boolean stopped = stopService(SERVICE_NAME_MARKER, getMarkerStatus(), rmiHostForMarker);
		
		if (stopped) {
			markerStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_MARKER + " " + markerStatus);
		return "services_main";
	}
	
	
	public boolean startService(String serviceName, String servicePath, ServiceStatus serviceStatus) {

		if (ServiceStatus.Stopped.equals(serviceStatus)) {

			try {
				// java -jar
				// -Djava.security.policy=/home/sam/Dev/TestDirectory/services/harvester/java.policy
				// -Djava.rmi.server.codebase=file:/home/sam/Dev/TestDirectory/services/harvester/Harvester.jar
				// /home/sam/Dev/TestDirectory/services/harvester/Harvester.jar
				System.out.println(servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))));
				
				String javaBinaryPath = null;
				
				if (javaPath != null) {
					if (javaPath.endsWith(System.getProperty("file.separator") + "java")) {
						javaBinaryPath = javaPath;
					} else if (javaPath.endsWith(System.getProperty("file.separator"))) {
						javaBinaryPath = javaPath + "java";
					} else if (!javaPath.endsWith(System.getProperty("file.separator")) && !javaPath.endsWith("java")) {
						javaBinaryPath = javaPath + System.getProperty("file.separator") + "java";
					}
				} else {
					javaBinaryPath = "java";
				}
				
				
				if (!checkIfLocalPathToServiceIsValid(servicePath)) {
					System.out.println("Service with path '" + servicePath + "' does not exist! Failed to start " + serviceName);
				}
				
				
				System.out.println("Running command: " + javaBinaryPath + " -Dfile.encoding=UTF8 -jar -Djava.security.policy=" + servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))) + "/java.policy "
								+ " -Djava.rmi.server.codebase=file:" + servicePath + " "
								+ servicePath);
				// TODO: change directory to service directory first
				Runtime.getRuntime().exec(
								javaBinaryPath + " -Dfile.encoding=UTF8 -jar -Djava.security.policy=" + servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))) + "/java.policy "
								+ " -Djava.rmi.server.codebase=file:" + servicePath + " "
								+ servicePath);

				logger.info("Starting " + serviceName + "...");
				synchronized (this) {
					this.wait(5000);
				}

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	

	public boolean stopService(String serviceName, ServiceStatus serviceStatus, String serviceHost) {

		if (ServiceStatus.Started.equals(serviceStatus)) {

			try {

				logger.info("Stopping " + serviceName + "...");
				Registry registry = RMIRegistryHelper.getRegistry(serviceHost);

				IService service = (IService) registry.lookup(serviceName);

				if (service != null) {
					service.stopService();
				}

				logger.info(serviceName + " stopped.");
				return true;
				
			} catch (ConnectException e) {
				logger.warn("Could not connect to RMI registry, please make sure the registry is running");
				
				if (serviceName.equals(SERVICE_NAME_HARVESTER)) {
					harvesterStatus = ServiceStatus.Stopped;
				} else if (serviceName.equals(SERVICE_NAME_AGGREGATOR)) {
					aggregatorStatus = ServiceStatus.Stopped;
				} else if (serviceName.equals(SERVICE_NAME_MARKER)) {
					markerStatus = ServiceStatus.Stopped;
				}
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				logger.info("RMI-Service with name " + serviceName + " is not yet registered with rmi-registry.");
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return false;
	}

	
	private boolean checkIfLocalPathToServiceIsValid(String path) {
	
		return new File(path).exists();
	}

	/********************* Getter & Setter ***********************/

	public ServiceStatus getHarvesterStatus() {
		return harvesterStatus;
	}

	public void setHarvesterStatus(ServiceStatus harvesterStatus) {
		this.harvesterStatus = harvesterStatus;
	}

	public ServiceStatus getAggregatorStatus() {
		return aggregatorStatus;
	}

	public void setAggregatorStatus(ServiceStatus aggregatorStatus) {
		this.aggregatorStatus = aggregatorStatus;
	}

	public ServiceStatus getMarkerStatus() {
		return markerStatus;
	}

	public void setMarkerStatus(ServiceStatus markerStatus) {
		this.markerStatus = markerStatus;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
		
	public void setConnector(RestConnector connector) {
    	this.connector = connector;
    }

	public boolean isHarvesterStarted() {
		return ServiceStatus.Started.equals(harvesterStatus);
	}
	
	public boolean isAggregatorStarted() {
		return ServiceStatus.Started.equals(aggregatorStatus);
	}
	
	public boolean isMarkerStarted() {
		return ServiceStatus.Started.equals(markerStatus);
	}
	
	public boolean isHarvesterBusy() {
		return ServiceStatus.Busy.equals(harvesterStatus);
	}
	
	public boolean isAggregatorBusy() {
		return ServiceStatus.Busy.equals(aggregatorStatus);
	}
	
	public boolean isMarkerBusy() {
		return ServiceStatus.Busy.equals(markerStatus);
	}

	public boolean isLocalHarvester() {
    	return localHarvester;
    }

	public boolean isLocalAggregator() {
    	return localAggregator;
    }

	public boolean isLocalMarker() {
    	return localMarker;
    }

}
