package de.dini.oanetzwerk.servicemodule.servicewrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.RMIService;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;

public class ServiceWrapperRMI extends RMIService {

	
	private final Logger logger = Logger.getLogger(ServiceWrapperRMI.class);

	
	private static String propertyFile	= "service.properties";
	private static String serviceName 	= "";
	private static String scriptCall	= "";
	
	
	private Registry registry = null;

	private int updateInterval = 5000;
	private StringBuffer messages = new StringBuffer();
	private boolean working = false;

	public ServiceWrapperRMI() {
		super();	
	}

	public static void main(String[] args) {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new ServiceWrapperRMI().startService();
		
	}

	private void startService() {

		logger.info(" starting due to rmi call.");

		String error = fetchProperties();
		
		if (error != null) {
			
			logger.error("Could not start service due to error:\n" + error);
			return;
		}
		
		try {
			IService server = this;
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			registry.rebind(serviceName, stub);
			System.out.println(serviceName + " bound");
		} catch (Exception e) {
			System.err.println(serviceName + " could not be bound: ");
			e.printStackTrace();
		}
		

//		publishUpdates();
	}

	
	private String fetchProperties() {
		
		logger.info("Trying to fetch properties for service...");
		
		Properties properties = new Properties();
		
		try {
		
			properties.load(new FileInputStream(propertyFile));
			serviceName	= properties.getProperty("service.name");
			scriptCall 	= properties.getProperty("script.call");			
			
			if (serviceName == null || serviceName.length() == 0) {
				return "Service name has not been specified. Please check the " + propertyFile + " and supply a value for 'service.name' !";
			}
			
			if (scriptCall == null || scriptCall.length() == 0) {
				return "Script shell command has not been specified. Please check the " + propertyFile + " and supply a value for 'script.call' !";
			}
			
		} catch (FileNotFoundException e) {
			String error = "Could not find properties file !" + new File(propertyFile).getAbsolutePath() + "/" + propertyFile;
			logger.error(error, e);
			return error;
		} catch (IOException e) {
			String error = "Could not properly read properties file !" + new File(propertyFile).getAbsolutePath() + "/" + propertyFile;
			logger.error(error, e);
			return error;
		}
		
//		if (!new File(scriptCall).exists()) {
//			System.out.println(new File(scriptCall).getAbsolutePath());
//			return "The specified script '" + scriptCall + "' does not exist! "; 
//		}
		
		logger.info("service.properties found!");
		return null;
	}
	
//	private void publishUpdates() {
//
//		String name = "HarvesterMonitorService";
//
//		try {
//
//			IHarvesterMonitor service = null;
//			Map<String, String> data = new HashMap<String, String>();
//
//			synchronized (this) {
//
//				while (working) {
//
//					if (registry == null)
//						registry = getRegistry();
//
//					if (registry == null) {
//						logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to publish RMI-Updates!");
//						this.wait(updateInterval);
//						continue;
//					}
//
//					if (service == null) {
//						service = (IHarvesterMonitor) registry.lookup(name);
//						logger.info("IHarvesterMonitor found!");
//					}
//
//					if (service != null) {
//						logger.info("Publishing Updates to Harvester monitor.");
//						// create harvesting settings
//						data.put("progress", String.valueOf(this.getCurrentStatus()));
//						data.put("messages", messages.toString());
//						service.publishServiceUpdates(data);
//					}
//					this.wait(updateInterval);
//				}
//			}
//
//		} catch (RemoteException e) {
//			System.err.println("RemoteException: ");
//			e.printStackTrace();
//		} catch (NotBoundException e) {
//			System.err.println("NotBoundException: ");
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			System.err.println("InterruptedException: ");
//			e.printStackTrace();
//		}
//	}


	/********************** Contract Implementation **********************/

	@Override
	public int getCurrentRepository() throws RemoteException {

		return 0;
	}

	@Override
	public ServiceStatus getCurrentStatus() throws RemoteException {
		if (working) {
			return ServiceStatus.Busy;
		} else {
			return ServiceStatus.Started;
		}
	}
	

	public String getUpdates() throws RemoteException {
		// TODO read log file and send back last 100 lines of log
		return "";
	}
	

	@Override
	@SuppressWarnings("static-access")
	public boolean start(Map<String, String> data) throws RemoteException {

		System.out.println(serviceName + " started!");
		
		// set to working
		updateJobStatus(data.get("job_name"), "Working");
		working = true;
		
		// TODO starting and waiting for service to complete
		startService(data);


		// service finished
		updateJobStatus(data.get("job_name"), "Finished");
		logger.info("Harvesting job finished!");
		working = false;
		return true;
	}

	@Override
	public boolean stop() throws RemoteException {

		// TODO kill command


		working = false;
		return true;
	}

	@Override
	public int getUpdateInterval() throws RemoteException {
		return this.updateInterval;
	}

	@Override
	public void setUpdateInterval(int ms) throws RemoteException {
		this.updateInterval = ms;
	}

	@Override
	public boolean stopService() throws RemoteException {
		logger.info("Unbinding " + serviceName + " !");

		try {
			if (registry == null) {
				registry = getRegistry();
			}
			registry.unbind(serviceName);
		} catch (NotBoundException e) {
			logger.info(serviceName + " already unbound.");
		}
		return true;
	}

	/*************************** Retrieve repository information **************************/

	private void startService(Map<String, String> data) {

		System.out.println("start harvester method called");

		
		try {
			Process process = Runtime.getRuntime().exec(scriptCall);
			
			// get pid 
			// see http://www.coderanch.com/t/109334/Linux-UNIX/UNIX-process-ID-java-program
			InputStream is = process.getInputStream();  
			BufferedReader stdout = new BufferedReader(new InputStreamReader(is));  
			String line = stdout.readLine();  
			
			while (line != null) {  
				System.out.println(line);  
				line = stdout.readLine();  
			} 
			
			int exitValue = process.waitFor();  
			logger.info("exit value: " + exitValue);  
			
		} catch (IOException e) {
			
			logger.error("Could not start service script! ", e);
		} catch (InterruptedException e) {
			
			logger.error("Could not start service script! ", e);
		}

//		// start sending updates to RMI-listener (HarvesterMonitorService)
//		publishUpdates();

	}


	protected String getPropertyFile() {
		return "serviceprop.xml";
	}

}
