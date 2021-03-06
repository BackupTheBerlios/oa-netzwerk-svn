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
public class MarkerJob  extends AbstractServiceJob {

		private static Logger logger = Logger.getLogger(MarkerJob.class);

		public MarkerJob() {}

		
		public void execute(JobExecutionContext context) throws JobExecutionException {

			logger.info("Marker job called");

			JobDataMap jobData = context.getJobDetail().getJobDataMap();
			String repoId = jobData.getString("repository_id");

			// initiate marker service via RMI
			try {

				String name = "MarkerService";
				String host = PropertyManager.getServiceProperties().getProperty("java.rmiregistry.host.marker");
				
				Registry registry = RMIRegistryHelper.getRegistry(host);

				if (registry == null) {
					logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start RMI-Marker!");
					return;
				}

				IService service = (IService) registry.lookup(name);

				// create harvesting settings
				Map<String, String> data = new HashMap<String, String>();

				data.put("repositoryId", repoId);
				data.put("job_name", jobData.getString("job_name"));


				boolean started = service.start(data);

				logger.info("Marker finished!");

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

