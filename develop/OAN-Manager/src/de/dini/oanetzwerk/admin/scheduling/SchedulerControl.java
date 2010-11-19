package de.dini.oanetzwerk.admin.scheduling;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import de.dini.oanetzwerk.admin.scheduling.jobs.HarvesterJob;

public class SchedulerControl {

	public void initAndStartScheduler() throws SchedulerException {

		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();
		scheduler.start();

		JobDetail jobDetail = new JobDetail("harvester", null, HarvesterJob.class);

		Trigger trigger = TriggerUtils.makeSecondlyTrigger(); // fire every hour
		trigger.setStartTime(TriggerUtils.getEvenSecondDate(new Date())); // start on the next even hour
		trigger.setName("harvesterTrigger");
	
		scheduler.scheduleJob(jobDetail, trigger);

		Date now = new Date();
		System.out.println("Quartz Log: " + now + "   even Hour:" + TriggerUtils.getEvenHourDate(new Date()));
		
		
	}
}
