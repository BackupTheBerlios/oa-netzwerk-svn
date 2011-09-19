package de.dini.oanetzwerk.admin.scheduling.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.RepositoryOnlineStatusBean;

public class RepositoryOnlineStatusUpdateJob implements Job {
	
	private static Logger logger = Logger.getLogger(RepositoryOnlineStatusUpdateJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		JobDataMap data = context.getJobDetail().getJobDataMap();
		
		logger.info("Starting update of repositories online status");
		System.out.println("||||| Online Status Update Job |||||");
		
		//unserialize wenn vorhanden
		RepositoryOnlineStatusBean bean = (RepositoryOnlineStatusBean)data.get("repositoryOnlineStatus");
//		bean.deleteOnlineStatus();
		bean.refreshOnlineStatus();
		// serialize
		
	}
	

}
