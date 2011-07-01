package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;

public class AggregatorJob extends AbstractServiceJob {

	private static Logger logger = Logger.getLogger(HarvesterJob.class);

	private String harvestType = "update";
	private List<Repository> repositories;

	public AggregatorJob() {

	}

	public AggregatorJob(String harvestType, List<Repository> repositories) {
		this.harvestType = harvestType;
		this.repositories = repositories;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("Aggregator job called");

		// initiate harvesting via RMI
		try {

			String name = "AggregatorService";
			Registry registry = RMIRegistryHelper.getRegistry();

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			IService service = (IService) registry.lookup(name);

			// create harvesting settings
			Map<String, String> data = new HashMap<String, String>();

			data.put("complete", "true");

			// data.put(key, )

			boolean started = service.start(data);
//			System.out.println("Client: " + started);

//			for (int i = 0; i < 5; i++) {
//				this.wait(3000);
//				System.out.println(service.getCurrentStatus());
//			}
			System.out.println("Aggregator finished.");

		} catch (RemoteException e) {
			System.err.println("RemoteException: ");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("NotBoundException: ");
			e.printStackTrace();
		} 

	}

	public static int progress() {
		return new Random().nextInt(100);
	}
}
