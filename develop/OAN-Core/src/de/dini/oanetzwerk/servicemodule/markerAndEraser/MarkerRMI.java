package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.RMIService;
import de.dini.oanetzwerk.utils.HelperMethods;

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
		
//		DOMConfigurator.configureAndWatch("log4j.xml" , 60*1000 );
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new MarkerRMI().startService();
	}

	private void startService() {

		logger.info("Marker starting due to rmi call.");
		
		try {
			IService server = this;
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Marker!");
				return;
			}

			registry.rebind(SERVICE_NAME, stub);
			System.out.println(SERVICE_NAME + " bound");
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
	public int getCurrentStatus() throws RemoteException {
		// TODO calculate progress
		return new Random().nextInt();
	}

	@Override
	@SuppressWarnings("static-access")
	public boolean start(Map<String, String> data) throws RemoteException {

		// execute Server

		// create a new instance of the aggregator
		
		int id = 0;

		if (data.containsKey("repository_id")) {
			id = new Integer((data.get("repository_id")));
		}


		
		marker = new MarkerAndEraser(getApplicationPath(), id);
		
		// updating job status
		updateJobStatus(data.get("job_name"), "Working");
		
		// im Testfall wird eine andere Startmethode aufgerufen
		if (data.containsKey("testing") && Boolean.TRUE.equals(Boolean.parseBoolean(data.get("testing")))) {
			marker.eraseTestOnlyData();
		} else {
			// Standardfall ohne Testing
			marker.markAndErase();
		}
		
		// updating job status
		updateJobStatus(data.get("job_name"), "Finished");
		logger.info("Marker Finished!");
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
		logger.info("Unbinding " + SERVICE_NAME + " !");
		
		try {
			if (registry == null) {
				registry = getRegistry();
			}
			registry.unbind(SERVICE_NAME);
		} catch (NotBoundException e) {
			logger.info(SERVICE_NAME + " already unbound.");
		}
		return true;
	}

	@Override
    protected String getPropertyFile() {
		return "aggregatorprop.xml";
    }

}
