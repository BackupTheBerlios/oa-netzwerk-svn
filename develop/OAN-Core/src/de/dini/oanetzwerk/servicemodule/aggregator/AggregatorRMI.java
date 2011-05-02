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

import de.dini.oanetzwerk.servicemodule.IAggregatorMonitor;
import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.harvester.Harvester;

public class AggregatorRMI implements IService {

	private static final Logger logger = Logger.getLogger(AggregatorRMI.class);

	private static final String SERVICE_NAME = "AggregatorService";
	
	private Aggregator aggregator = null;
	private Registry registry = null;

	private int updateInterval = 10000;
	private StringBuffer messages = new StringBuffer();

	public AggregatorRMI() {
		super();
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new AggregatorRMI().startService();
	}

	private void startService() {

		try {
			// FileWriter writer = new FileWriter(new
			// File("/home/davidsam/Desktop/1234567.txt"));
			// writer.write("blabla");
			// writer.flush();
			// writer.close();

			AggregatorRMI server = new AggregatorRMI();
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			registry.rebind(SERVICE_NAME, stub);
			System.out.println(SERVICE_NAME + " bound");
		} catch (Exception e) {
			System.err.println(SERVICE_NAME + " could not be bound: ");
			e.printStackTrace();
		}

		//
		publishUpdates();
	}

	private void publishUpdates() {

		String name = "HarvesterMonitorService";

		try {

			IAggregatorMonitor service = null;
			Map<String, String> data = new HashMap<String, String>();
			
			
			synchronized (this) {

				while (true) {

					if (registry == null)
						registry = getRegistry();
					
					if (registry == null) {
						logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to publish RMI-Updates!");
						this.wait(updateInterval);
						continue;
					}

					if (service == null) {
						service = (IAggregatorMonitor) registry.lookup(name);
					}
										
					// create harvesting settings
					data.put("progress", String.valueOf(this.getCurrentStatus()));
					data.put("messages", messages.toString());
					service.publishServiceUpdates(data);

					this.wait(updateInterval);
				}
			}

		} catch (RemoteException e) {
			System.err.println("RemoteException: ");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("NotBoundException: ");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("InterruptedException: ");
			e.printStackTrace();
		}
	}

	private static Registry getRegistry() {

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

	/********************** Contract Implementation **********************/

	@Override
	public int getCurrentRepository() throws RemoteException {

		if (this.aggregator == null) {
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
			aggregator = new Aggregator();
		}
		
		
		if (data.containsKey("complete")) {
			complete = true;
		}

		// hier wird entweder die spezifische Objekt-ID übergeben
		// oder ein Auto-Durchlauf gestartet
		if (id > 0) {
			aggregator.startSingleRecord(id, time);
		} else {
			// wenn Complete true gesetzt ist, sollen alle Daten erneut bearbeitet werden, sonst nur die aktuell zu bearbeitenden
			aggregator.startAutoMode(complete);
		}

		logger.info("Aggregator started...");
		return true;
	}
	

	@Override
	public boolean stop() throws RemoteException {

		if (this.aggregator == null) {
			return true;
		}

		this.aggregator.setStopped(true);
		this.aggregator = null;
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
			registry.unbind(SERVICE_NAME);
		} catch (NotBoundException e) {
			logger.info(SERVICE_NAME + " already unbound.");
		}
		return true;
	}

}
