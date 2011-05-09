package de.dini.oanetzwerk.admin.scheduling.jobs;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;

public class RepositoryAvailabilityJob extends AbstractServiceJob {

	private static Logger logger = Logger.getLogger(RepositoryAvailabilityJob.class);

	private List<Repository> repositories;

	public RepositoryAvailabilityJob() {

	}

	public RepositoryAvailabilityJob(String harvestType, List<Repository> repositories) {
		this.repositories = repositories;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (true)
			return;
		
		System.out.println("Checking the availability of dini certified repositories...");

		
		// TODO: retrieve Rpeository Data
		
		// TODO: check Identify urls 
		
		// TODO: store results
		
	}

	public static int progress() {
		return new Random().nextInt(100);
	}
}
