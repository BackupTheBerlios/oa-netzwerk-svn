package de.dini.oanetzwerk.servicemodule.harvester;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;
import de.dini.oanetzwerk.servicemodule.IService;

public class HarvesterRMI implements IService {

	private static final Logger logger = Logger.getLogger(HarvesterRMI.class);

	private Harvester harvester = null;
	private Registry registry = null;

	private int updateInterval = 10000;
	private StringBuffer messages = new StringBuffer();

	public HarvesterRMI() {
		super();
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new HarvesterRMI().startService();
	}

	private void startService() {
		String name = "HarvesterService";

		try {
			// FileWriter writer = new FileWriter(new
			// File("/home/davidsam/Desktop/1234567.txt"));
			// writer.write("blabla");
			// writer.flush();
			// writer.close();

			HarvesterRMI server = new HarvesterRMI();
			IService stub = (IService) UnicastRemoteObject.exportObject(server, 0);
			registry = getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			registry.rebind(name, stub);
			System.out.println(name + " bound");
		} catch (Exception e) {
			System.err.println(name + " could not be bound: ");
			e.printStackTrace();
		}

		//
		publishUpdates();
	}

	private void publishUpdates() {

		String name = "HarvesterMonitorService";

		try {

			IHarvesterMonitor service = null;
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
						service = (IHarvesterMonitor) registry.lookup(name);
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

		if (this.harvester == null) {
			return -1;
		}

		return this.harvester.getRepositoryID();
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

		// create a new instance of the harvester and set
//		harvester = Harvester.getHarvester();
//		harvester.prepareHarvester(Integer.parseInt(data.get("repositoryId")));
//
//		String baseUrl = "";
//
//		harvester.filterDate(data.get("date"));
//		harvester.filterAmount(data.get("amount"));
//		harvester.filterInterval(data.get("interval"));
//
//		harvester.setTestData(Boolean.getBoolean(data.get("testData")));
//		harvester.setListRecords(Boolean.getBoolean(data.get("listRecords")));
//
//
//		if (logger.isDebugEnabled()) {
//
//			logger.debug("Data after processing the CommandLine:");
//			logger.debug("oai_url: " + harvester.getRepositoryURL());
//			logger.debug("test_data: " + harvester.isTestData());
//			logger.debug("harvest_amount: " + harvester.getAmount());
//			logger.debug("harvest_pause: " + harvester.getInterval());
//		}
//
//		harvester.processRepository();

		logger.info("Harvester started...");
		return true;
	}

	@Override
	public boolean stop() throws RemoteException {

		if (this.harvester == null) {
			return true;
		}

		this.harvester.setStopped(true);
		this.harvester = null;
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

}
