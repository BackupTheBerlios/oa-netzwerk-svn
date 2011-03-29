package de.dini.oanetzwerk.admin.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob.ProcessingType;
import de.dini.oanetzwerk.admin.scheduling.jobs.HarvesterJob;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.Utils;

public class SchedulerControl {

	private Properties restProperties;
	
	public void initAndStartScheduler() throws SchedulerException {

		
		try {
			
			this.restProperties = HelperMethods.loadPropertiesFromFileWithinWebcontainerWebapps(Utils.getDefaultContext() + "/WEB-INF/admingui.xml");
			
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();
		scheduler.start();

		
		// schedule jobs
		
		List<AbstractServiceJob> jobsToSchedule = loadServiceJobsFromDB();
		
		for (AbstractServiceJob job : jobsToSchedule) {
	        
			JobDetail jobDetail = new JobDetail(job.getServiceName(), null, HarvesterJob.class);
			Trigger trigger = getTriggerForJob(job);
			
			scheduler.scheduleJob(jobDetail, trigger);
        }
				

		Date now = new Date();
		System.out.println("Quartz Log: " + now + "   even Hour:" + TriggerUtils.getEvenHourDate(new Date()));
		
		
	}
	
	
	private List<AbstractServiceJob> loadServiceJobsFromDB() {
		
		List<AbstractServiceJob> persistedJobs = new ArrayList<AbstractServiceJob>();
		
		// db request
		
		// fill persistedJobs
		
		HarvesterJob job1 = new HarvesterJob();
		job1.setServiceName("harvester");
		job1.setInterval(123);
		job1.setRepositoryId(2);
		job1.setProcessingType(ProcessingType.SINGLE);
		job1.setAdditionalInfo("Bla bla");
		
		persistedJobs.add(job1);
		return persistedJobs;
	}
	
	private Trigger getTriggerForJob(AbstractServiceJob job) {
		
		
		
		Trigger trigger = TriggerUtils.makeSecondlyTrigger();
		trigger.setStartTime(TriggerUtils.getEvenSecondDate(new Date())); // start on the next even hour
		trigger.setName("harvesterTrigger");
		
		return trigger;
	}
}
