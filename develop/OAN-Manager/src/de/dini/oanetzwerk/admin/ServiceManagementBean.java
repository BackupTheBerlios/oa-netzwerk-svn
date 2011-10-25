package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
		
	private boolean rmiRegistryNotStarted = false;	
	private String javaPath = null;

	@ManagedProperty(value="#{restConnector}")
	private RestConnector connector;	
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;

	FacesContext ctx = FacesContext.getCurrentInstance();
	List<ServiceBean> services = new ArrayList<ServiceBean>();
	
	public ServiceManagementBean() {
		super();
	}

	public enum Service {
		Harvester, Aggregator, Marker, Shingler, Indexer, LanguageDetector, Classifier, DuplicateScanner;
	}
	
	@PostConstruct
	private void init() {
		
		services.add(new ServiceBean(Service.Harvester));
		services.add(new ServiceBean(Service.Aggregator));
		services.add(new ServiceBean(Service.Marker));
		services.add(new ServiceBean(Service.Shingler));
		services.add(new ServiceBean(Service.Indexer));
		services.add(new ServiceBean(Service.LanguageDetector));
		services.add(new ServiceBean(Service.Classifier));
		services.add(new ServiceBean(Service.DuplicateScanner));
		
		initializeServiceProperties();
		initializeServiceStatus();
		
				
		// try to fetch path to a specified java binary file
		javaPath = propertyManager.getServiceProperties().getProperty("java.path");
		if (javaPath == null || javaPath.trim().length() == 0) {
			javaPath = null;
		}
	}

	public void initializeServiceProperties() {
		
		Properties props = propertyManager.getServiceProperties();
		
		for (ServiceBean service : services) {
	        
			// retrieve file paths to services (harvester, aggregator and marker)
			service.setLocalPath(props.getProperty("location." + service.getLowerCaseName()));

			// retrieve rmi registry urls for each service
			service.setRmiHost(props.getProperty("java.rmiregistry.host." + service.getLowerCaseName()));
        }
	}
	
	public void initializeServiceStatus() {
	
		for (ServiceBean service : services) {

			if (rmiRegistryNotStarted) {
	        	return;
	        }

			service.setStatus(checkServiceStatus(service.getServiceName(), service.getRmiHost()));
        }
	}
	
	private ServiceStatus checkServiceStatus(String serviceName, String rmiHost) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		Registry registry;
		
		if (rmiHost == null || "".equals(rmiHost)) {
			registry = RMIRegistryHelper.getRegistry();
		} else {
			registry = RMIRegistryHelper.getRegistry(rmiHost);
		}

		try {
			IService rmiService = (IService) registry.lookup(serviceName);

			if (rmiService != null) {
				System.out.println(serviceName + " running.");
				try {
					return rmiService.getCurrentStatus();
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

	public String startService(ServiceBean service) {
		
		boolean started = startService(service.getServiceName(), service.getLocalPath(), service.getStatus());
		
		if (started) {
			service.setStatus(checkServiceStatus(service.getServiceName(), service.getRmiHost()));
		}
		logger.info(service.getServiceName() + " " + service.getStatus());
		return "services_main";
	}
	
	public String stopService(ServiceBean service) {
		boolean stopped = stopService(service.getServiceName(), service.getStatus(), service.getRmiHost());
		
		if (stopped) {
			service.setStatus(ServiceStatus.Stopped);
		}
		logger.info(service.getServiceName() + " " + service.getStatus());
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
				Process process = Runtime.getRuntime().exec(
								javaBinaryPath + " -Dfile.encoding=UTF8 -jar -Djava.security.policy=" + servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))) + "/java.policy "
								+ " -Djava.rmi.server.codebase=file:" + servicePath + " "
								+ servicePath);
				
//				Process process = Runtime.getRuntime().exec(servicePath);

				/* handling the streams to prevent blocks and deadlocks according to the javadocs. 
				 * see: http://download.oracle.com/javase/6/docs/api/java/lang/Process.html */
				ProcessStreamHandler inputStream =
				new ProcessStreamHandler(process.getInputStream(),"INPUT");
				ProcessStreamHandler errorStream =
				new ProcessStreamHandler(process.getErrorStream(),"ERROR");

				/* start the stream threads */
				inputStream.start();
				errorStream.start();
				
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
					logger.info(serviceName + " stopped.");
				} else {
					logger.warn(serviceName + " is not running! Cannot stop service!");
				}

				return true;
				
			} catch (ConnectException e) {
				logger.warn("Could not connect to RMI registry, please make sure the registry is running");
				e.printStackTrace();
				
				return true;
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

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
		
	public void setConnector(RestConnector connector) {
    	this.connector = connector;
    }

	public List<ServiceBean> getServices() {
    	return services;
    }

}
