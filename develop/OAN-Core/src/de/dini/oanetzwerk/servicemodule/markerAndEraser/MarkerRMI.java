package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.RMIService;
import de.dini.oanetzwerk.servicemodule.Repository;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.ServiceIDException;

public class MarkerRMI extends RMIService {

	private static final Logger logger = Logger.getLogger(MarkerRMI.class);

	private static final String SERVICE_NAME = "MarkerService";
	
	private MarkerAndEraser marker = null;
	private Registry registry = null;

	private int updateInterval = 10000;
	private StringBuffer messages = new StringBuffer();
	private boolean working = false;
	
	public MarkerRMI() {
		super();
	}

	public static void main(String[] args) {
		
		logger.info("Marker starting due to rmi call.");
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new MarkerRMI().startService();
	}

	private void startService() {

		if (!isInitializationComplete()) {
			logger.error("Initialization failed! Stopping Marker!");
			return;
		}
		
		
		try {
			IService server = this;
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Marker!");
				return;
			}

			registry.rebind(SERVICE_NAME, stub);
			System.out.println(SERVICE_NAME + " ready and listening");
		} catch (Exception e) {
			System.err.println(SERVICE_NAME + " could not be bound: ");
			e.printStackTrace();
		}
	}


	/********************** Contract Implementation **********************/

	@Override
	public int getCurrentRepository() throws RemoteException {

		if (this.marker == null) {
			return -1;
		}

		return -1;
	}

	@Override
	public ServiceStatus getCurrentStatus() throws RemoteException {
		if (working) {
			return ServiceStatus.Busy;
		} else {
			return ServiceStatus.Started;
		}
	}

	@Override
	@SuppressWarnings("static-access")
	public boolean start(Map<String, String> data) throws RemoteException {
		
		System.out.println("Marker started!");
		
		// execute Server
		
		if (data == null || !data.containsKey("repositoryId")) {
			logger.warn("Could not retrieve repository information, cannot start Marker job! Skipping...");
		}

		int repo;
		
		String repoId = data.get("repositoryId");
		boolean processAllRepositories = false;
		try {

			repo = Integer.parseInt(repoId);
			if (repo == 0) {
				processAllRepositories = true;
			}
		} catch (NumberFormatException e) {

			logger.warn("Could not retrieve repository information, cannot start marker job as the repository id '" + repoId
			                + "' was invalid!  Skipping...");
			return false;
		}
		
		// updating job status
		updateJobStatus(data.get("job_name"), "Working");
		working = true;
		
		
		// fetch repository information
		// process all active repositories
		if (processAllRepositories) {

			List<Repository> repositories = getRepositories(getPropertyFile());
			
			for (Repository repository : repositories) {
	        
				try {
					synchronized (this) {
						logger.info("Running marker&eraser on repository with ID " + repository.getId());
							this.wait(10000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				marker = new MarkerAndEraser(getApplicationPath(), repository.getId());
				
				// im Testfall wird eine andere Startmethode aufgerufen
				if (data.containsKey("testing") && Boolean.TRUE.equals(Boolean.parseBoolean(data.get("testing")))) {
					marker.eraseTestOnlyData();
				} else {
					// Standardfall ohne Testing
					marker.markAndErase();
				}
            }
			
		} else { // harvest a single repository
			
			marker = new MarkerAndEraser(getApplicationPath(), (long) repo);
			
			// im Testfall wird eine andere Startmethode aufgerufen
			if (data.containsKey("testing") && Boolean.TRUE.equals(Boolean.parseBoolean(data.get("testing")))) {
				marker.eraseTestOnlyData();
			} else {
				// Standardfall ohne Testing
				marker.markAndErase();
			}
		}
		
		
		
		// updating job status
		updateJobStatus(data.get("job_name"), "Finished");
		logger.info("Marker Finished!");
		working = false;
		return true;
	}
	

	@Override
	public boolean stop() throws RemoteException {
 
		if (this.marker == null) {
			return true;
		}

		this.marker.setStopped(true);
		this.marker = null;
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
		logger.info("Unbinding " + SERVICE_NAME + " ...");
		
		try {
			if (registry == null) {
				registry = getRegistry();
			}
			registry.unbind(SERVICE_NAME);
			UnicastRemoteObject.unexportObject(this, true);
			
			logger.info(SERVICE_NAME + " stopped successfully");
			
		} catch (NotBoundException e) {
			logger.info(SERVICE_NAME + " already unbound.");
		}
		return true;
	}

	
	@Override
    protected String getPropertyFile() {
		return "markerprop.xml";
    }

}
