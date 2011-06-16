package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;

public class HarvesterJob extends AbstractServiceJob {

	private static Logger logger = Logger.getLogger(HarvesterJob.class);

//	private String harvestType = "update";
//	private List<Repository> repositories;

	public HarvesterJob() {

	}

//	public HarvesterJob(String harvestType, List<Repository> repositories) {
//		this.harvestType = harvestType;
//		this.repositories = repositories;
//	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
//		if (true)
//			return;
		
		System.out.println("Harvester job called");

		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		String repoId = jobData.getString("repository_id");
		
		
		
		// initiate harvesting via RMI
		try {

			String name = "HarvesterService";
			Registry registry = RMIRegistryHelper.getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}
			logger.info("Inititating Harvester job with name '" + getName() + "'...");

			IService service = (IService) registry.lookup(name);

			// create harvesting settings
			Map<String, String> data = new HashMap<String, String>();

			data.put("repository_id", repoId);
//			data.put("harvestType", "full");
//			data.put("date", null);
//			data.put("url", null);
//			data.put("repositoryId", "4");
//			data.put("amount", "10");
//			data.put("interval", "5000");
//			data.put("testData", "true");
//			data.put("listRecords", "true");
			// data.put(key, )

			boolean started = service.start(data);
			System.out.println("Client: " + started);

			synchronized(this) {
			for (int i = 0; i < 5; i++) {
				this.wait(3000);
				System.out.println(service.getCurrentStatus());
			}
			}
			System.out.println("Harvester initiated successfully!");

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

	public static int progress() {
		return new Random().nextInt(100);
	}

//	public void getRepositories() {
//
//	}
}