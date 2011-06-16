package de.dini.oanetzwerk.servicemodule.harvester;

import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.IHarvesterMonitor;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.servicemodule.Repository;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class HarvesterRMI implements IService {

	static {

		PropertyConfigurator.configureAndWatch("log4j.properties", 60 * 1000);

	}

	private static final Logger logger = Logger.getLogger(HarvesterRMI.class);

	private static final String SERVICE_NAME = "HarvesterService";

	private Harvester harvester = null;
	private Registry registry = null;

	private int updateInterval = 5000;
	private StringBuffer messages = new StringBuffer();
	private boolean working = false;

	public HarvesterRMI() {
		super();
	}

	public static void main(String[] args) {

		PropertyConfigurator.configureAndWatch("log4j.properties", 60 * 1000);

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		new HarvesterRMI().startService();
	}

	private void startService() {

		logger.info("Harvester starting due to rmi call.");
		try {
			// FileWriter writer = new FileWriter(new
			// File("/home/davidsam/Desktop/1234567.txt"));
			// writer.write("blabla");
			// writer.flush();
			// writer.close();

			IService server = new HarvesterRMI();
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

			IHarvesterMonitor service = null;
			Map<String, String> data = new HashMap<String, String>();

			synchronized (this) {

				while (working) {

					if (registry == null)
						registry = getRegistry();

					if (registry == null) {
						logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to publish RMI-Updates!");
						this.wait(updateInterval);
						continue;
					}

					if (service == null) {
						service = (IHarvesterMonitor) registry.lookup(name);
						logger.info("IHarvesterMonitor found!");
					}

					if (service != null) {
						logger.info("Publishing Updates to Harvester monitor.");
						// create harvesting settings
						data.put("progress", String.valueOf(this.getCurrentStatus()));
						data.put("messages", messages.toString());
						service.publishServiceUpdates(data);
					}
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

		// initialize harvesting settings

		if (data == null) {
			logger.warn("Could not retrieve repository information, cannot start Harvester job! Skipping...");
		}

		String repoId = data.get("repository_id");
		boolean harvestAllRepositories = false;
		try {

			int repo = Integer.parseInt(repoId);
			if (repo == 0) {
				harvestAllRepositories = true;
			}
		} catch (NumberFormatException e) {

			logger.warn("Could not retrieve repository information, cannot start Harvester job as the repository id '" + repoId
			                + "' was invalid!  Skipping...");
		}

		// fetch repository information

		updateJobStatus(data.get("job_name"), "Working");
		
		//harvest all active repositories
		if (harvestAllRepositories) {

			List<Repository> repositories = getRepositories();
			
			for (Repository repository : repositories) {
	            
				data.put("repository_id", repository.getId().toString());
				startHarvester(data);
            }
			
		} else { // harvest a single repository

			startHarvester(data);
		}


		updateJobStatus(data.get("job_name"), "Finished");
		logger.info("Harvesting job finished!");
		return true;
	}

	@Override
	public boolean stop() throws RemoteException {

		if (this.harvester == null) {
			return true;
		}

		this.harvester.setStopped(true);
		this.harvester = null;
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

	/*************************** Retrieve repository information **************************/

	private void startHarvester(Map<String, String> data) {

		// create a new instance of the harvester and set
		harvester = new Harvester();
		harvester.prepareHarvester(Integer.parseInt(data.get("repositoryId")));

		String baseUrl = "";

		// sets the type (full | update)
		String type = data.get("harvestType"); // can be 'full' or 'update'

		if (type != null && (type.equals("full") || type.equals("update"))) {
			this.harvester.setFullharvest(type.equals("full"));

			if (type.equals("update") && data.get("date") == null) {
				logger.warn("Harvester can only run in update mode if a valid date argument is specified!");
			}
		} else {
			logger.warn("Harvest type was not specified! (full or update)");
		}

		if (!harvester.isFullharvest())
			harvester.filterDate(data.get("date"));

		if (logger.isDebugEnabled()) {

			logger.debug("Data after processing the CommandLine:");
			logger.debug("oai_url: " + harvester.getRepositoryURL());
			logger.debug("test_data: " + harvester.isTestData());
			logger.debug("harvest_amount: " + harvester.getAmount());
			logger.debug("harvest_pause: " + harvester.getInterval());
		}
		
		// start the harvester
		harvester.processRepository();

		// set to working
		working = true;

		// start sending updates to RMI-listener (HarvesterMonitorService)
		publishUpdates();

	}

	public List<Repository> getRepositories() {

		List<Repository> repoList = new ArrayList<Repository>();

		Properties props = null;
		try {
			props = HelperMethods.loadPropertiesFromFile(harvester.getPropertyfile());

		} catch (Exception e) {
			logger.warn("Could not load property file '" + harvester.getPropertyfile() + "'! Skipping harvesting ...");
		}

		RestClient client = RestClient.createRestClient(props.getProperty("host"), "Repository/", props.getProperty("username"),
		                props.getProperty("password"));
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

	
	// TODO Pull UP
	private void updateJobStatus(String jobName, String status) {
		
		Properties props = null;
		try {
			props = HelperMethods.loadPropertiesFromFile(harvester.getPropertyfile());

		} catch (Exception e) {
			logger.warn("Could not load property file '" + harvester.getPropertyfile() + "'! Skipping harvesting ...");
		}

		RestClient client = RestClient.createRestClient(props.getProperty("host"), "Repository/", props.getProperty("username"),
		                props.getProperty("password"));

		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();
		rms.setKeyword(RestKeyword.ServiceJob);
		rms.addEntrySet(new RestEntrySet());

		try {

			result = RestClient.createRestClient(props.getProperty("host"), "ServiceJob/" + jobName + "/" + status, props.getProperty("username"),
			                props.getProperty("password")).sendPostRestMessage(rms);

			if (result.getStatus() != RestStatusEnum.OK) {

				logger.error("/ServiceJob response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
				return;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		logger.info("POST sent to /ServiceJob");
	}
	


}
