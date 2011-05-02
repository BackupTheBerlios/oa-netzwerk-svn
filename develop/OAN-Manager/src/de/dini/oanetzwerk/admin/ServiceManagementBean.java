package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.utils.PropertyManager;

@ManagedBean(name = "services")
@ApplicationScoped
public class ServiceManagementBean {

	private final static Logger logger = Logger.getLogger(ServiceManagementBean.class);

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

		// TODO: try to connect to RMI-services
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
			IService service = (IService) registry.lookup("HarvesterService");

			if (service != null) {
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

	public boolean startHarvester() {

		if (ServiceStatus.Stopped.equals(getHarvesterStatus())) {

			try {
				Runtime.getRuntime().exec("java -jar " + localPathToHarvester);

				synchronized (this) {
					this.wait(10000);
				}

				harvesterStatus = checkServiceStatus("HarvesterService");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ready");
		}
		return true;
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

}
