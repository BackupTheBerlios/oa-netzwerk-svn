package de.dini.oanetzwerk.servicemodule;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.utils.HelperMethods;

public abstract class RMIService implements IService {

	private static final Logger logger = Logger.getLogger(RMIService.class);

	private Properties serviceProps; 
	private Properties restclientProps;
	private String applicationPath;
	
	private String restclientPropFileName = "restclientprop.xml";
	
	
	public RMIService() {
	    super();
	    initPropertyFiles();
	}
	
	private void initPropertyFiles() {
		
		
		applicationPath = null;
		try {
			
			// retrieve property file path (application path)
			applicationPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

			logger.debug("Retrieving application path: " + applicationPath);
			if (applicationPath != null && applicationPath.contains(System.getProperty("file.separator"))) {
				System.out.println(applicationPath);
				
				applicationPath = applicationPath.substring(0, applicationPath.lastIndexOf(System.getProperty("file.separator")) + 1 );
				System.out.println("app path: " + applicationPath);
			}
			
			// read service property file
			serviceProps = HelperMethods.loadPropertiesFromFile(applicationPath + getPropertyFile());
			
			// read restclient property file
			restclientProps = HelperMethods.loadPropertiesFromFile(applicationPath + restclientPropFileName);
			
			logger.info("Reading property files successful!");
			
        } catch (Exception e) {
			logger.warn("Could not load property file '" + getPropertyFile() + "' or 'restclientprop.xml' from path " + applicationPath + "! Skipping harvesting ...");
        }
	}

	protected static Registry getRegistry() {

		try {

			// try to obtain an already started registry
			return LocateRegistry.getRegistry();

		} catch (RemoteException e) {

			logger.warn("Could not retrieve RMI-Registry! Starting a registry ...");

			// create registry if we failed to obtain an existing one
			try {

				return LocateRegistry.createRegistry(1099);

			} catch (RemoteException e2) {
				logger.error("Failed to create own RMI-Registry!");
				e2.printStackTrace();
			}
		}
		return null;
	}
	
	protected void updateJobStatus(String jobName, String status) {
		
		logger.info("Updating job status to '" + status + "' for job with name " + jobName);
		

		RestMessage rms;
		RestMessage result = null;
		rms = new RestMessage();
		rms.setKeyword(RestKeyword.ServiceJob);
		rms.addEntrySet(new RestEntrySet());

		System.out.println("props == null : " + serviceProps == null);
		try {


			
			System.out.println("username: " + serviceProps.getProperty("username"));
			
			System.out.println("bla");
			
			
//			RestClient client = RestClient.createRestClient(props.getProperty("host"), "ServiceJob/" + jobName + "/" + status, props.getProperty("username"),
//			                props.getProperty("password"));
		
			RestClient client = RestClient.createRestClient(new File(applicationPath + "restclientprop.xml"), "ServiceJob/" + jobName + "/" + status, serviceProps.getProperty("username"),
			                serviceProps.getProperty("password"));
							
			System.out.println("client null : " + client == null);
			
			result = client.sendPostRestMessage(rms);
			System.out.println("result null : " + result == null);

			if (result.getStatus() != RestStatusEnum.OK) {

				logger.error("/ServiceJob response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
				return;
			}
		} catch (UnsupportedEncodingException e) {

			logger.warn("Could not update status of service job! ", e);
			return;
		}

		logger.info("POST sent to /ServiceJob");
	}
	
	protected abstract String getPropertyFile();

	public Properties getServiceProps() {
    	return serviceProps;
    }

	public void setServiceProps(Properties serviceProps) {
    	this.serviceProps = serviceProps;
    }

	public Properties getRestclientProps() {
    	return restclientProps;
    }

	public void setRestclientProps(Properties restclientProps) {
    	this.restclientProps = restclientProps;
    }

	protected String getApplicationPath() {
    	return applicationPath;
    }
		
}
