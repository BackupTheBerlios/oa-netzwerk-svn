package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.utils.RMIRegistryHelper;
import de.dini.oanetzwerk.servicemodule.IService;
import de.dini.oanetzwerk.utils.PropertyManager;

public class ScriptServiceJob extends AbstractServiceJob{

	private static Logger logger = Logger.getLogger(MarkerJob.class);

	public ScriptServiceJob() {}

	
	public void execute(JobExecutionContext context) throws JobExecutionException {


		JobDataMap jobData 	= context.getJobDetail().getJobDataMap();
		String repoId 		= jobData.getString("repository_id");
		String serviceName 	= jobData.getString("service_name"); 
		
		logger.info(serviceName + " job called");

		// initiate marker service via RMI
		try {

			String host = PropertyManager.getServiceProperties().getProperty("java.rmiregistry.host." + serviceName.toLowerCase());
			
			Registry registry = RMIRegistryHelper.getRegistry(host);

			if (registry == null) {
				logger.error("Could not obtain an existing RMI-Registry nor create one ourselves! Aborting to start " + serviceName + " !");
				return;
			}

			IService service = (IService) registry.lookup(serviceName + "Service");

			// create harvesting settings
			Map<String, String> data = new HashMap<String, String>();

			data.put("repositoryId", repoId);
			data.put("job_name", jobData.getString("job_name"));

			boolean started = service.start(data);
			System.out.println("Client: " + started);

			logger.info(serviceName + " finished!");

		} catch (RemoteException e) {
			System.err.println("RemoteException: ");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("NotBoundException: ");
			e.printStackTrace();
		} 
	}
}
