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
import de.dini.oanetzwerk.utils.PropertyManager;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class AggregatorJob extends AbstractServiceJob {

	private static Logger logger = Logger.getLogger(HarvesterJob.class);


	public AggregatorJob() {

	}

	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("Aggregator job called");

		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		
		// initiate harvesting via RMI
		try {

			String name = "AggregatorService";
			String host = PropertyManager.getServiceProperties().getProperty("java.rmiregistry.host.aggregator");
			
			Registry registry = RMIRegistryHelper.getRegistry(host);

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}

			logger.info("Inititating Harvester job with name '" + jobData.getString("job_name") + "'...");


			IService service = (IService) registry.lookup(name);

			// create harvesting settings
			Map<String, String> data = new HashMap<String, String>();
			data.put("job_name", jobData.getString("job_name"));
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
