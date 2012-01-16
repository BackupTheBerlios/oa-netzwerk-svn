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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.ProcessStreamHandler;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.PropertyManager;
import de.dini.oanetzwerk.utils.StringUtils;

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
	private static List<ServiceBean> services = new ArrayList<ServiceBean>();
	
//	static {
//		
//		List<Service> services = SchedulingBean.getServices();
//
//		for (int i = 0; i < services.size(); i++) {
//			ServiceManagementBean.services.add(new ServiceBean(services.get(i)));
//        }
//	}
//	
	public ServiceManagementBean() {
		super();
	}

//	public enum Service {
//		Harvester, Aggregator, Marker, FulltextLinkFinder, LanguageDetection, Shingler, Indexer, Classifier, DuplicateCheck;
//	}
	
	@PostConstruct
	private void init() {
				
		services = connector.fetchServices();
		
		
		initializeServiceProperties();
		initializeServiceStatus();
		initializeServiceStatistics();
		
				
		// try to fetch path to a specified java binary file
		javaPath = propertyManager.getServiceProperties().getProperty("java.path");
		if (javaPath == null || javaPath.trim().length() == 0) {
			javaPath = null;
		}
	}

	private void initializeServiceProperties() {
		
		Properties props = propertyManager.getServiceProperties();
		String path;
		
		for (ServiceBean service : services) {
	        
			// resolve environemnt variables within path
			path = StringUtils.resolveSystemVariable(props.getProperty("location." + service.getLowerCaseName()));
			
			// retrieve file paths to services (harvester, aggregator and marker)
			service.setLocalPath(path);

			// retrieve rmi registry urls for each service
			service.setRmiHost(props.getProperty("java.rmiregistry.host." + service.getLowerCaseName()));
        }
	}
	
	private void initializeServiceStatus() {
	
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
	
	private void initializeServiceStatistics() {
		
		List<SchedulingBean> jobs = connector.fetchSimpleServiceStatistics();
		
		for (ServiceBean service : services) {
			for (SchedulingBean job : jobs) {
				
				if (service.getServiceId().equals(job.getServiceId())) {	
					service.setLastFullExecution(job.getNonperiodicTimestamp());
					service.setNewEntries(job.getNewEntries());
					service.setLastFullExecutionDuration(job.getDuration());
				}
            }
        }
	}

	public String startSingleService(ServiceBean service) { 
		
		boolean started = startService(service.getServiceName(), service.getLocalPath(), service.getStatus());
		
		timeout(2000);
		
		if (started) {
			service.setStatus(checkServiceStatus(service.getServiceName(), service.getRmiHost()));
		}
		
		logger.info(service.getServiceName() + " " + service.getStatus());
		
		return "services_main";
	}
	
	public String startAllServices() {
		
		for (ServiceBean service : services) {
			startService(service.getServiceName(), service.getLocalPath(), service.getStatus());
			logger.info(service.getServiceName() + " " + service.getStatus());
        }
		timeout(5000);
				
		for (ServiceBean service : services) {
			service.setStatus(checkServiceStatus(service.getServiceName(), service.getRmiHost()));
		}
		
		return "services_main";
	}	
	
	public String stopSingleService(ServiceBean service) {
		boolean stopped = stopService(service.getServiceName(), service.getStatus(), service.getRmiHost());
		
		if (stopped) {
			service.setStatus(ServiceStatus.Stopped);
		}
		logger.info(service.getServiceName() + " " + service.getStatus());
		return "services_main";
	}
	
	public String stopAllServices() {
		
		for (ServiceBean service : services) {
	        stopSingleService(service);
        }
		return "services_main";
	}	
		
	
	public boolean startService(String serviceName, String servicePath, ServiceStatus serviceStatus) {

		if (ServiceStatus.Stopped.equals(serviceStatus)) {

			try {
				
				if (servicePath == null || servicePath.length() == 0) {
					logger.warn("Local path to service could not be found! Please make sure the service.properties have been set up correctly!" );
					FacesContext.getCurrentInstance().addMessage("1", LanguageSwitcherBean.getFacesMessage(ctx, FacesMessage.SEVERITY_INFO, "services_failure_servicepropertiesnotfound", null));
					return false;
				}
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
				
				
				System.out.println("Running command: " + javaBinaryPath + " -Dfile.encoding=UTF8 -Doan.home=" + System.getenv("OAN_HOME") + " -jar -Djava.security.policy=" + System.getenv("OAN_HOME") + "/config/java.policy "
								+ " -Djava.rmi.server.codebase=file:" + servicePath + " "
								+ servicePath);
				// TODO: change directory to service directory first
//				Process process = Runtime.getRuntime().exec(
//								javaBinaryPath + " -Dfile.encoding=UTF8 -jar -Djava.security.policy=" + servicePath.substring(0, servicePath.lastIndexOf(System.getProperty("file.separator"))) + "/java.policy "
//								+ " -Djava.rmi.server.codebase=file:" + servicePath + " "
//								+ servicePath);
				
				//TODO: remove hardcoding
				String oanHome = System.getenv("OAN_HOME");
				if (oanHome == null || oanHome.isEmpty()) {
					oanHome = "/usr/local/www/clients/oan-services/";
				}
				
				Process process = Runtime.getRuntime().exec(
								javaBinaryPath + " -Dfile.encoding=UTF8 -Doan.home=" + oanHome + " -jar -Djava.security.policy=" + oanHome + "/config/java.policy "
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
				

			} catch (IOException e) {
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
	
	private void timeout(long ms) {
		synchronized (this) {
			try {
	            this.wait(ms);
            } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	}
	
	/********************* Getter & Setter ***********************/

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
		
	public void setConnector(RestConnector connector) {
    	this.connector = connector;
    }

	public List<ServiceBean> getServices() {
//		System.out.println("refreshing service status");
//		initializeServiceStatus();
    	return services;
    }

}
