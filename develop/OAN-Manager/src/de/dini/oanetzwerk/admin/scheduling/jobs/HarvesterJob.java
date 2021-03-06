package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

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

public class HarvesterJob extends AbstractServiceJob {

	private static Logger logger = Logger.getLogger(HarvesterJob.class);
	

	public HarvesterJob() {

	}


	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println("Harvester job called");

		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		
		
		// initiate harvesting via RMI
		try {

			String name = "HarvesterService";

			String host = PropertyManager.getServiceProperties().getProperty("java.rmiregistry.host.harvester");
			
			Registry registry;
			if (host == null || "".equals(host)) {
				registry = RMIRegistryHelper.getRegistry();
				System.out.println("host is empty...");
			} else {
				registry = RMIRegistryHelper.getRegistry(host);
			}

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Harvester!");
				return;
			}
			logger.info("Inititating Harvester job with name '" + jobData.getString("job_name") + "'...");

			IService service = (IService) registry.lookup(name);

			// create harvesting settings
			Map<String, String> data = new HashMap<String, String>();

			data.put("repository_id", jobData.getString("repository_id"));
			data.put("job_name", jobData.getString("job_name"));
			// TODO: get harvest type from gui
			data.put("harvestType", "full");
			
			
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