package de.dini.oanetzwerk.servicemodule.aggregator;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.servicemodule.IAggregatorMonitor;
import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.RMIService;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.servicemodule.harvester.Harvester;
import de.dini.oanetzwerk.servicemodule.harvester.HarvesterRMI;

public class AggregatorRMI extends RMIService {

	private static final Logger logger = Logger.getLogger(AggregatorRMI.class);

	private static final String SERVICE_NAME = "AggregatorService";
	
	private Aggregator aggregator 	= null;
	private Registry registry 		= null;

	private int updateInterval 		= 10000;
	private StringBuffer messages 	= new StringBuffer();
	private boolean working 		= false;
	
	public AggregatorRMI() {
		super();
	}

	public static void main(String[] args) {
		
		logger.info("Aggregator starting due to rmi call.");
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		new AggregatorRMI().startService();
	}

	private void startService() {

		if (!isInitializationComplete()) {
			logger.error("Initialization failed! Stopping Aggregator!");
			return;
		}
		
		
		try {
			IService server = this;
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Aggregator!");
				return;
			}

			registry.rebind(SERVICE_NAME, stub);
			logger.info(SERVICE_NAME + " ready and listening");
		} catch (Exception e) {
			logger.error(SERVICE_NAME + " could not be bound: ");
			e.printStackTrace();
		}

//		publishUpdates();
	}

//	private void publishUpdates() {
//
//		String name = "HarvesterMonitorService";
//
//		try {
//
//			IAggregatorMonitor service = null;
//			Map<String, String> data = new HashMap<String, String>();
//			
//			
//			synchronized (this) {
//
//				while (true) {
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
//						service = (IAggregatorMonitor) registry.lookup(name);
//						logger.info("IAggregatorMonitor found!");
//					}
//						
//					if (service != null) {
//						logger.info("Publishing Updates to Aggreagator monitor.");
//						// create aggregator settings
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

		if (this.aggregator == null) {
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

		// execute Server

		// create a new instance of the aggregator
		
		int id = 0;
		String time = null;
		boolean complete = false;

		// Bestimmen, ob nur eine einzelne ID übergeben wurde oder
		// der Auto-Modus genutzt werden soll
		if (data.containsKey("itemId")) {
			id = new Integer((data.get("itemId")));
		}

		time = data.get("timestamp");

		// TODO: wenn time null ist, geht PUT WORKFLOWDB nicht
		if(time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			time = sdf.format(new Date(System.currentTimeMillis()));
		}
		
		
		// im Testfall wird ein anderer Constructor aufgerufen
		if (data.containsKey("testing") && Boolean.TRUE.equals(Boolean.parseBoolean(data.get("testing")))) {
			aggregator = new Aggregator(true); }
		else {
			// Standardfall ohne Testing
			aggregator = new Aggregator(getApplicationPath());
		}
		
		
		if (data.containsKey("complete")) {
			complete = true;
		}
		
		// updating job status
		updateJobStatus(data.get("job_name"), "Working");
		
		// hier wird entweder die spezifische Objekt-ID übergeben
		// oder ein Auto-Durchlauf gestartet
		
		logger.info("Starting to process records...");
		
		working = true;
		if (id > 0) {
			aggregator.startSingleRecord(id, time);
		} else {
			// wenn Complete true gesetzt ist, sollen alle Daten erneut bearbeitet werden, sonst nur die aktuell zu bearbeitenden
			aggregator.startAutoMode(complete);
		}

		// updating job status
		updateJobStatus(data.get("job_name"), "Finished");
		logger.info("Aggregator Finished!");
		working = false;
		return true;
	}
	

	@Override
	public boolean stop() throws RemoteException {

		if (this.aggregator == null) {
			return true;
		}

		this.aggregator.setStopped(true);
		this.aggregator = null;
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

}
