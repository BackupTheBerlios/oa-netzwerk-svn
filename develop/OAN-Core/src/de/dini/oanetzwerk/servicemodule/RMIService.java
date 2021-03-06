package de.dini.oanetzwerk.servicemodule;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;

public abstract class RMIService implements IService {

	private static final Logger logger = Logger.getLogger(RMIService.class);

//	private Properties serviceProps; 
	private Properties restclientProps;
	private String applicationPath;
	
	private String restclientPropFileName = "config.xml";
	private boolean initializationComplete = false;
	private String oanHome = System.getenv("OAN_HOME");
	
	public RMIService() {
	    super();
	    initPropertyFiles();
	    
	}
	
	private void initPropertyFiles() {
		
		logger.info("Initializing property files...");
		
		applicationPath = null;
		
		try{
		
			// retrieve property file path (application path)
			
			applicationPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			System.out.println("apppath: " + applicationPath);
			logger.debug("Retrieving application path...");
			if (applicationPath != null && applicationPath.contains(System.getProperty("file.separator"))) {
				applicationPath = applicationPath.substring(0, applicationPath.lastIndexOf(System.getProperty("file.separator")) + 1 );
				logger.info("Resolved application path: " + applicationPath);
			}
			
		} catch (AccessControlException e) {
			logger.warn("Could not determine application path, access to directory has been denied!", e);
			return;
		}	
			

		try {
			// read restclient property file
			restclientProps = HelperMethods.loadPropertiesFromFile(applicationPath + restclientPropFileName);
			
		} catch (Exception e) {
			logger.warn("Could not load property file from " + applicationPath + restclientPropFileName + "!", e);
			return;
		}	

		// try to fetch specific service properties
		if (!readSpecificServiceProperties()) {
			initializationComplete = false;
		}
		
        logger.info("Reading " + applicationPath + "log4j.xml!");
        DOMConfigurator.configureAndWatch((applicationPath == null ? "" : applicationPath) + "log4j.xml" , 60*1000 );
		
		initializationComplete = true;
		logger.info("Reading property files has been successful!");
	}
	
	// hook for custom service property initialization
	protected boolean readSpecificServiceProperties() {
		
		// no default init, but method can be overridden
		return true;
	}

	protected List<Repository> getRepositories() {

		List<Repository> repoList = new ArrayList<Repository>();

		RestClient client = HelperMethods.prepareRestTransmission(new File(getApplicationPath() + restclientPropFileName), "Repository/", restclientProps);
		String result = client.GetData();

		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return null;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			Iterator<String> it = res.getKeyIterator();
			String key = "";
			Repository repo = new Repository();

			while (it.hasNext()) {

				key = it.next();

				// if (logger.isDebugEnabled ( ))
				// logger.debug ("key: " + key + " value: " + res.getValue
				// (key));

				if (key.equalsIgnoreCase("name")) {

					repo.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("active")) {

					repo.setActive(Boolean.parseBoolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("repository_id")) {

					repo.setId(new Long(res.getValue(key)));

				} else
					// System.out.println("Key: " + key);
					continue;
			}

			if (repo.isActive()) {
				repoList.add(repo);
			}

		}
		System.out.println(repoList.size());

		return repoList;
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

		try {				
			
//			RestClient client = RestClient.createRestClient(props.getProperty("host"), "ServiceJob/" + jobName + "/" + status, props.getProperty("username"),
//			                props.getProperty("password"));
		
			RestClient client = RestClient.createRestClient(restclientProps, "ServiceJob/" + jobName + "/" + status, restclientProps.getProperty("username"),
							restclientProps.getProperty("password"));
							
			
			result = client.sendPostRestMessage(rms);

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
	

	public Properties getConfig() {
		return restclientProps;
	}
	
	public String getConfigFilename() {
		return restclientPropFileName;
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

	protected boolean isInitializationComplete() {
    	return initializationComplete;
    }
	
}
