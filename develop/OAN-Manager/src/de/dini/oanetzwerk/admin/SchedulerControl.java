package de.dini.oanetzwerk.admin;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.DateBuilder.tomorrowAt;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Marker;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.AggregatorJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.HarvesterJob;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.Utils;

@ManagedBean(name = "schedulerControl")
@ApplicationScoped
public class SchedulerControl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SchedulerControl.class);

	private Properties restProperties;
	private Scheduler scheduler;

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;

	// private static SchedulerControl instance = null;

	public SchedulerControl() {
		super();
//		try {
//
//			initAndStartScheduler();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	// public static synchronized SchedulerControl getInstance() {
	// if (instance == null) {
	// instance = new SchedulerControl();
	// }
	// return instance;
	// }

	@PostConstruct
	public void initAndStartScheduler() {

		System.out.println("SchedulerControl initiated!");

		try {

			this.restProperties = HelperMethods.loadPropertiesFromFileWithinWebcontainerWebapps(Utils.getDefaultContext()
					+ "/WEB-INF/admingui.xml");


		SchedulerFactory factory = new StdSchedulerFactory();

		scheduler = factory.getScheduler();
		scheduler.start();

		// schedule jobs

		List<AbstractServiceJob> jobsToSchedule = loadServiceJobsFromDB();

		for (AbstractServiceJob job : jobsToSchedule) {

			JobDetail jobDetail = null; 
			if (job instanceof HarvesterJob) {
				
				jobDetail = newJob(HarvesterJob.class).withIdentity("job1", "group1").build();
			} else if (job instanceof AggregatorJob) {
				jobDetail = newJob(AggregatorJob.class).withIdentity("job1", "group1").build();
			} else {
				System.out.println("else");
//				jobDetail = newJob(Marker.class).withIdentity("job1", "group1").build();
			}
			logger.info("Scheduling job of type " + job.getClass());

			scheduler.scheduleJob(jobDetail, job.getTrigger());
		}
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load jobs from DB
	 * 
	 * @return
	 */
	private List<AbstractServiceJob> loadServiceJobsFromDB() {
		System.out.println("Scheduling job...");

		List<AbstractServiceJob> persistedJobs = new ArrayList<AbstractServiceJob>();

		// db request
		String result = restConnector.prepareRestTransmission("ServiceJob/").GetData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return persistedJobs;
		}

		for (RestEntrySet res : rms.getListEntrySets()) {

			SchedulingBean bean = new SchedulingBean();
			Iterator<String> it = res.getKeyIterator();
			String key = "";

			while (it.hasNext()) {

				key = it.next();

				// if (logger.isDebugEnabled ( ))
				// logger.debug ("key: " + key + " value: " + res.getValue
				// (key));

				if (key.equalsIgnoreCase("name")) {
					bean.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("service_id")) {
					bean.setServiceId(new BigDecimal(res.getValue(key)));

				} else if (key.equalsIgnoreCase("status")) {
					bean.setStatus(ServiceStatus.valueOf(res.getValue(key)));

				} else if (key.equalsIgnoreCase("info")) {
					bean.setInfo(res.getValue(key));

				} else if (key.equalsIgnoreCase("nonperiodic_date")) {
					try {
						bean.setNonperiodicTimestamp(new Date(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(res.getValue(key)).getTime()));
					} catch (ParseException ex) {

						logger.error(ex.getLocalizedMessage());
						ex.printStackTrace();
					}

				} else if (key.equalsIgnoreCase("periodic")) {
					bean.setPeriodic(new Boolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_days")) {
					bean.setPeriodicDays(new Integer(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval")) {
					bean.setPeriodicInterval(SchedulingIntervalType.valueOf(res.getValue(key)));

				} else
					logger.info("Unknown Key: " + key);
				continue;
			}
			AbstractServiceJob job = getJobforSchedulingBean(bean);
			if (job != null) {
				persistedJobs.add(job);
			}
		}

		System.out.println("null: " + persistedJobs == null);
		System.out.println("size " + persistedJobs.size());
		return persistedJobs;
	}

	public boolean storeJob(SchedulingBean job) {

		// save to db, REST call
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.ServiceJob);
		rms.setStatus(RestStatusEnum.OK);

		res = new RestEntrySet();
		
		boolean jobIdSpecified = false;
		
		if (job.getJobId() != null && job.getJobId() > 0) {
			jobIdSpecified = true;
			res.addEntry("job_id", job.getJobId().toString());
		}

		res.addEntry("name", job.getName());
		res.addEntry("status", job.getStatus().toString());
		res.addEntry("info", job.getInfo());
		res.addEntry("service_id", job.getServiceId().toString());
		res.addEntry("periodic", Boolean.toString(job.isPeriodic()));
		res.addEntry("nonperiodic_date", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(job.getNonperiodicTimestamp()));
		res.addEntry("periodic_interval", job.getPeriodicInterval() != null ? job.getPeriodicInterval().toString() : null);
		res.addEntry("periodic_days", Integer.toString(job.getPeriodicDays()));

		System.out.println("interval: " + job.getPeriodicInterval());
		System.out.println("days: " + job.getPeriodicDays());
		rms.addEntrySet(res);

		try {
			result = restConnector.prepareRestTransmission("ServiceJob/" + (jobIdSpecified ? job.getJobId().toString() : ""))
			                .sendPutRestMessage(rms);

			if (rms.getStatus() != RestStatusEnum.OK) {

				logger.error("/ServiceJob response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		logger.info("PUT sent to /ServiceJob");
		
		// schedule 
		
		AbstractServiceJob asj = getJobforSchedulingBean(job);
		if (job != null) {
			JobDetail jobDetail = newJob(asj.getClass()).withIdentity("job1", "group1")
			.usingJobData("someProp", "someValue").build();
			try {
				scheduler.scheduleJob(jobDetail, asj.getTrigger());
				
				return true;
			} catch (SchedulerException e) {
				logger.warn("An error occured while scheduling job.", e);
			}
		}
		return false;
	}

	public void deleteJob(String jobId, String jobName) {

		// unschedule job

		JobKey key = new JobKey(jobName);
		try {
			if (scheduler.checkExists(key)) {

				scheduler.deleteJob(key);
			}
		} catch (SchedulerException e) {

			logger.warn("Could not unschedule job with name " + jobName, e);
		}

		// delete from db

		String result = restConnector.prepareRestTransmission("ServiceJob/" + jobId).DeleteData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms.getStatus() != RestStatusEnum.OK) {

			logger.error("/ServiceJob/" + jobId + " response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
		}

	}

	private AbstractServiceJob getJobforSchedulingBean(SchedulingBean bean) {

		if (bean.getStatus() != null && !ServiceStatus.Finished.equals(bean.getStatus())) {
			// setup job and schedule

			AbstractServiceJob job = null;
			if (new Integer(1).equals(bean.getServiceId())) {
				job = new HarvesterJob();
			} else if (new Integer(2).equals(bean.getServiceId())) {
				job = new AggregatorJob();
			} else {
				// job = new MarkerJob();
			}

			Trigger trigger = null;

			try {

				if (bean.isPeriodic()) {

					if (bean.getNonperiodicTimestamp() == null) {
						logger.warn("Could not fetch time for periodic scheduling interval! Cannot schedule service jobs!");
					}

					if (SchedulingIntervalType.Monthly.equals(bean.getInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a month)");
						if (bean.getPeriodicDays() > 28) {
							trigger = newTrigger().withIdentity("job").withSchedule(cronSchedule("0 0 2 L * ?")).forJob("job").build();
						} else if (bean.getPeriodicDays() <= 28) {
							trigger = newTrigger().withIdentity("job")
									.withSchedule(cronSchedule("0 0 2 " + bean.getPeriodicDays() + " * ?")).forJob("job").build();

						}
					} else if (SchedulingIntervalType.Weekly.equals(bean.getInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a week)");
						trigger = newTrigger()
								.withIdentity("job")
								.withSchedule(
										weeklyOnDayAndHourAndMinute(getDayOfWeek(bean.getPeriodicDays()), bean.getNonperiodicTimestamp()
												.getHours(), bean.getNonperiodicTimestamp().getMinutes())).forJob("job").build();

					} else if (SchedulingIntervalType.Day.equals(bean.getInterval())) {

						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " days)");
						trigger = newTrigger()
								.withIdentity("trigger3", "group1")
								.startAt(
										tomorrowAt(bean.getNonperiodicTimestamp().getHours(), bean.getNonperiodicTimestamp().getMinutes(),
												0)) // 15:00:00 tomorrow
								.withSchedule(calendarIntervalSchedule().withIntervalInDays(bean.getPeriodicDays())).build();
					}

				} else {

					logger.info("Non-periodic job scheduled.");
					trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(bean.getNonperiodicTimestamp())
							.forJob("job1", "group1").build();
				}

				return job;

			} catch (ParseException e) {
				logger.warn("Could not read cron expression for job. ", e);
			}
		}
		return null;
	}

	private int getDayOfWeek(int day) {

		switch (day) {
		case 1:
			return DateBuilder.MONDAY;
		case 2:
			return DateBuilder.TUESDAY;
		case 3:
			return DateBuilder.WEDNESDAY;
		case 4:
			return DateBuilder.THURSDAY;
		case 5:
			return DateBuilder.FRIDAY;
		case 6:
			return DateBuilder.SATURDAY;
		case 7:
			return DateBuilder.SUNDAY;
		default:
			return DateBuilder.MONDAY;
		}
	}

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public RestConnector getRestConnector() {
    	return restConnector;
    }

	
}
