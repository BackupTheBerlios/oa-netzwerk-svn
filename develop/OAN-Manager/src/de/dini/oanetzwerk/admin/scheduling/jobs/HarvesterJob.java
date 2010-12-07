package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.util.Random;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HarvesterJob implements Job {

	public HarvesterJob() {
	}

	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//System.out.println("HarvesterJob is executing.");
	}
	
	public static int progress()
	{
		return new Random().nextInt(100);
	}
	
}