package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.utils.PropertyManager;

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

	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;

	public ServiceManagementBean() {
		super();
	}

	@PostConstruct
	private void init() {

		localPathToHarvester = propertyManager.getServiceProperties().getProperty("location.harvester");

		if (localPathToHarvester != null && new File(localPathToHarvester).exists()) {

			localHarvester = true;
		}

		harvesterStatus = checkServiceStatus("HarvesterService");
		aggregatorStatus = checkServiceStatus("AggregatorService");
		markerStatus = checkServiceStatus("MarkerService");
	}

	private ServiceStatus checkServiceStatus(String serviceName) {

		Registry registry = RMIRegistryHelper.getRegistry();

		try {
			IService service = (IService) registry.lookup(serviceName);

			if (service != null) {
				System.out.println(serviceName + " running.");
				return ServiceStatus.Started;
			}
		} catch (ConnectException e) {
			logger.warn("Could not connect to RMI registry, please make sure the registry is running");
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			logger.info("RMI-Service with name " + serviceName + " is not yet registered with rmi-registry.");
		}
		return ServiceStatus.Stopped;
	}

	public boolean startHarvesterService() {
		
		boolean started = startService(SERVICE_NAME_HARVESTER, localPathToHarvester, getHarvesterStatus());
		
		if (started) {
			harvesterStatus = checkServiceStatus(SERVICE_NAME_HARVESTER);
		}
		logger.info(SERVICE_NAME_HARVESTER + " " + harvesterStatus);
		return started;
	}
	
	public boolean stopHarvesterService() {
		boolean stopped = stopService(SERVICE_NAME_HARVESTER, getHarvesterStatus());
		
		if (stopped) {
			harvesterStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_HARVESTER + " " + harvesterStatus);
		return stopped;
	}
	
	public boolean startAggregatorService() {
		
		boolean started = startService(SERVICE_NAME_AGGREGATOR, localPathToAggregator, getAggregatorStatus());
		
		if (started) {
			aggregatorStatus = checkServiceStatus(SERVICE_NAME_AGGREGATOR);
		}
		logger.info(SERVICE_NAME_AGGREGATOR + " " + aggregatorStatus);
		return started;
	}
	
	public boolean stopAggregatorService() {
		boolean stopped = stopService(SERVICE_NAME_AGGREGATOR, getAggregatorStatus());
		
		if (stopped) {
			aggregatorStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_AGGREGATOR + " " + aggregatorStatus);
		return stopped;
	}
	
	public boolean startMarkerService() {
		
		boolean started = startService(SERVICE_NAME_MARKER, localPathToMarker, getMarkerStatus());
		
		if (started) {
			markerStatus = checkServiceStatus(SERVICE_NAME_MARKER);
		}
		logger.info(SERVICE_NAME_MARKER + " " + markerStatus);
		return started;
	}
	
	public boolean stopMarkerService() {
		boolean stopped = stopService(SERVICE_NAME_MARKER, getMarkerStatus());
		
		if (stopped) {
			markerStatus = ServiceStatus.Stopped;
		}
		logger.info(SERVICE_NAME_MARKER + " " + markerStatus);
		return stopped;
	}
	
	
	public boolean startService(String serviceName, String servicePath, ServiceStatus serviceStatus) {

		if (ServiceStatus.Stopped.equals(serviceStatus)) {

			try {
				// java -jar
				// -Djava.security.policy=/home/sam/Dev/TestDirectory/services/harvester/java.policy
				// -Djava.rmi.server.codebase=file:/home/sam/Dev/TestDirectory/services/harvester/Harvester.jar
				// /home/sam/Dev/TestDirectory/services/harvester/Harvester.jar
				System.out.println(servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))));
				Runtime.getRuntime().exec(
						"java -jar -Djava.security.policy=" + servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))) + "/java.policy "
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

	public boolean stopService(String serviceName, ServiceStatus serviceStatus) {

		if (ServiceStatus.Started.equals(serviceStatus)) {

			try {

				logger.info("Stopping " + serviceName + "...");
				Registry registry = RMIRegistryHelper.getRegistry();

				IService service = (IService) registry.lookup(serviceName);

				if (service != null) {
					service.stopService();
				}

				logger.info(serviceName + " stopped.");
				return true;
				
			} catch (ConnectException e) {
				logger.warn("Could not connect to RMI registry, please make sure the registry is running");
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

	public enum ServiceStatus {

		Started, Busy, Stopped
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

}
