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
import de.dini.oanetzwerk.servicemodule.ProcessStreamHandler;
import de.dini.oanetzwerk.servicemodule.RMIService;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;

public class ServiceWrapperRMI extends RMIService {

	
	private static final Logger logger = Logger.getLogger(ServiceWrapperRMI.class);

	
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
		
		logger.info("Service starting due to rmi call.");

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new ServiceWrapperRMI().startService();
		
	}

	private void startService() {

		if (!isInitializationComplete()) {
			logger.error("Initialization failed! Stopping Service!");
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

	@Override
	protected boolean readSpecificServiceProperties() {
		
		logger.info("Reading specific service properties ...");
		
		Properties properties = new Properties();
		
		try {
		
			properties.load(new FileInputStream(getApplicationPath() + propertyFile));
			serviceName	= properties.getProperty("service.name");
			scriptCall 	= properties.getProperty("script.call");			
			
			if (serviceName == null || serviceName.length() == 0) {
				logger.warn("Service name has not been specified. Please check the " + propertyFile + " and supply a value for 'service.name' !");
			}
			
			if (scriptCall == null || scriptCall.length() == 0) {
				logger.warn("Script shell command has not been specified. Please check the " + propertyFile + " and supply a value for 'script.call' !");
			}
			
		} catch (FileNotFoundException e) {
			String error = "Could not find properties file !" + new File(propertyFile).getAbsolutePath() + "/" + propertyFile;
			logger.error(error, e);
			return false;
		} catch (IOException e) {
			String error = "Could not properly read properties file !" + new File(propertyFile).getAbsolutePath() + "/" + propertyFile;
			logger.error(error, e);
			return false;
		}
		
		logger.info("service.properties found!");
		return true;
	}
	

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
			UnicastRemoteObject.unexportObject(this, true);
			
			logger.info(serviceName + " stopped successfully");
		} catch (NotBoundException e) {
			logger.info(serviceName + " already unbound.");
		}
		return true;
	}

	/*************************** Retrieve repository information **************************/

	private void startService(Map<String, String> data) {

		System.out.println("start " + serviceName  + " method called");

		
		try {
			logger.info("app path1 : " + getApplicationPath() + scriptCall);
			Process process = Runtime.getRuntime().exec(scriptCall.split(" "), null, new File(getApplicationPath()));
			
			/* handling the streams to prevent blocks and deadlocks according to the javadocs. 
			 * see: http://download.oracle.com/javase/6/docs/api/java/lang/Process.html */
			ProcessStreamHandler inputStream =
			new ProcessStreamHandler(process.getInputStream(),"INPUT");
			ProcessStreamHandler errorStream =
			new ProcessStreamHandler(process.getErrorStream(),"ERROR");

			// start the stream threads 
			inputStream.start();
			errorStream.start();
			
			// wait for process to finish
			// see http://www.coderanch.com/t/109334/Linux-UNIX/UNIX-process-ID-java-program			
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
		return propertyFile;
	}
	
}
