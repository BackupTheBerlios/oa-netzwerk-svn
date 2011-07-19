package de.dini.oanetzwerk.admin;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.DateBuilder.tomorrowAt;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

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
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.OperableTrigger;

import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.AggregatorJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.HarvesterJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.MarkerJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.UpdateDDCCountJob;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.Utils;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name = "schedulerControl")
@ApplicationScoped
public class SchedulerControl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SchedulerControl.class);

	private Properties restProperties;
	private Scheduler scheduler;

	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;


	public SchedulerControl() {
		super();
		System.out.println("SchedulerControl constructor");
		initAndStartScheduler();
	}


	@PostConstruct
	public void initAndStartScheduler() {

		System.out.println("SchedulerControl initiated!");

		try {

			this.restProperties = HelperMethods.loadPropertiesFromFileWithinWebcontainerWebapps(Utils.getDefaultContext()
					+ "/WEB-INF/admingui.xml");

			SchedulerFactory factory = new StdSchedulerFactory();

			// init quartz scheduler
			scheduler = factory.getScheduler();
			scheduler.start();

			if (scheduler == null) {
				System.out.println("scheduler is null after creation");
			}
			
			// schedule ddc-count update job
			scheduleDDCCounterJob();
			
			// load jobs from database
			List<AbstractServiceJob> jobsToSchedule = loadJobsFromDB();
			
			// schedule jobs
			for (AbstractServiceJob job : jobsToSchedule) {
				scheduleJob(job);
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

	private boolean scheduleDDCCounterJob() {
		logger.info("Scheduling Update-Job for ddc counts.");
		
		JobDetail jobDetail = newJob(UpdateDDCCountJob.class).withIdentity("DDC-Counter")
			.build();
		
		Trigger trigger = newTrigger().withIdentity("DDC-Counter")
				.startAt(tomorrowAt(2, 0, 0))
				.withSchedule(calendarIntervalSchedule().withIntervalInDays(1)).build();
		try {
	        scheduler.scheduleJob(jobDetail, trigger);
	        logger.info("Successfully scheduled DDC-Counter");
        } catch (SchedulerException e) {
	        logger.warn("Could not schedule DDC-Counter", e);
	        return false;
        }
        return true;
	}
	
	private boolean scheduleJob(AbstractServiceJob job) {

		logger.info("Scheduling quartz-job of type " + job.getClass() + " with name " + job.getData().getString("job_name"));
	
		System.out.println("jobname: " + job.getData().getString("job_name"));
		System.out.println("goo");
		System.out.println(job.getClass());
		JobDetail jobDetail = newJob(job.getClass()).withIdentity(job.getData().getString("job_name"))
			.usingJobData(job.getData()).build();
		
//		JobDetail jobDetail = null;
//		
//		if (job instanceof HarvesterJob) {
//			jobDetail = newJob(HarvesterJob.class).withIdentity(job.getData().getString("job_name"))
//			                .usingJobData(job.getData()).build();
//		} else if (job instanceof AggregatorJob) {
//			jobDetail = newJob(AggregatorJob.class).withIdentity(job.getData().getString("job_name")).usingJobData(job.getData()).build();
//		} else if (job instanceof MarkerJob){
//			 jobDetail = newJob(MarkerJob.class).withIdentity(job.getData().getString("job_name")).usingJobData(job.getData()).build();
//		} else {
//			logger.warn("Unknown job type! " + job.getClass().getName());
//		}
		
		if (jobDetail == null) {
			System.out.println("jobdetail iis null");
		}
		
		if (job == null) {
			System.out.println("job iis null");
		}
		
		if (job.getTrigger() == null) {
			System.out.println("jobtrigger iis null");
		}
		try {
			scheduler.scheduleJob(jobDetail, job.getTrigger());

			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;

	}

	private AbstractServiceJob getJobforSchedulingBean(SchedulingBean bean) {

		if (bean.getStatus() != null && !ServiceStatus.Finished.equals(bean.getStatus())) {
			// setup job and schedule

			AbstractServiceJob job = null;
			if (new BigDecimal(1).equals(bean.getServiceId())) {
				job = new HarvesterJob();
			} else if (new BigDecimal(2).equals(bean.getServiceId())) {
				job = new AggregatorJob();
			} else {
				job = new MarkerJob();				
			}
			JobDataMap data = new JobDataMap();
			data.put("repository_id", bean.getInfo());
			data.put("job_name", bean.getName());
			job.setData(data);

			Trigger trigger = null;

			try {
				if (bean.isPeriodic()) {

					if (bean.getNonperiodicTimestamp() == null) {
						logger.warn("Could not fetch time for periodic scheduling interval! Cannot schedule service jobs!");
						return null;
					}

					if (SchedulingIntervalType.Monthly.equals(bean.getPeriodicInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a month)");
						if (bean.getPeriodicDays() > 28) {
							trigger = newTrigger().withIdentity(bean.getName()).withSchedule(cronSchedule("0 0 2 L * ?"))
									.build();
						} else if (bean.getPeriodicDays() <= 28) {
							trigger = newTrigger().withIdentity(bean.getName())
									.withSchedule(cronSchedule("0 0 2 " + bean.getPeriodicDays() + " * ?")).build();

						}
					} else if (SchedulingIntervalType.Weekly.equals(bean.getPeriodicInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a week)");
						trigger = newTrigger()
								.withIdentity(bean.getName())
								.withSchedule(
										weeklyOnDayAndHourAndMinute(getDayOfWeek(bean.getPeriodicDays()), bean.getNonperiodicTimestamp()
												.getHours(), bean.getNonperiodicTimestamp().getMinutes())).build();

					} else if (SchedulingIntervalType.Day.equals(bean.getPeriodicInterval())) {

						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " days)");
						trigger = newTrigger()
								.withIdentity(bean.getName())
								.startAt(
										tomorrowAt(bean.getNonperiodicTimestamp().getHours(), bean.getNonperiodicTimestamp().getMinutes(),
												0)) // 15:00:00 tomorrow
								.withSchedule(calendarIntervalSchedule().withIntervalInDays(bean.getPeriodicDays())).build();
					}

				} else {

					logger.info("Non-periodic job scheduled.");
					System.out.println("startAt: " + bean.getNonperiodicTimestamp());
					trigger = (SimpleTrigger) newTrigger().withIdentity(bean.getName()).startAt(bean.getNonperiodicTimestamp()).build();
				}

				job.setTrigger(trigger);
				return job;

			} catch (ParseException e) {
				logger.warn("Could not read cron expression for job. ", e);
			}
		}
		return null;
	}

	
	/*************************** Store, Update, Get and Delete Job *************************/
	

	/**
	 * Load jobs from DB
	 * 
	 * @return
	 */
	private List<AbstractServiceJob> loadJobsFromDB() {
		logger.info("Retrieving jobs from database ...");

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
					System.out.println("jobName: " + res.getValue(key));
					bean.setName(res.getValue(key));

				} else if (key.equalsIgnoreCase("service_id")) {
					bean.setServiceId(new BigDecimal(res.getValue(key)));

				} else if (key.equalsIgnoreCase("job_id")) {
					bean.setJobId(new Integer(res.getValue(key)));

				} else if (key.equalsIgnoreCase("status")) {
					bean.setStatus(ServiceStatus.valueOf(res.getValue(key)));

				} else if (key.equalsIgnoreCase("info")) {
					bean.setInfo(res.getValue(key));

				} else if (key.equalsIgnoreCase("nonperiodic_date")) {
					try {
						System.out.println("out-1: " + res.getValue(key));
						bean.setNonperiodicTimestamp(new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(res.getValue(key)).getTime()));
						System.out.println("out0: " + bean.getNonperiodicTimestamp());
					} catch (ParseException ex) {

						logger.error(ex.getLocalizedMessage());
						ex.printStackTrace();
					}

				} else if (key.equalsIgnoreCase("periodic")) {
					bean.setPeriodic(new Boolean(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval_days")) {
					bean.setPeriodicDays(new Integer(res.getValue(key)));

				} else if (key.equalsIgnoreCase("periodic_interval_type")) {
					bean.setPeriodicInterval(res.getValue(key) == null ? null : SchedulingIntervalType.valueOf(res.getValue(key)));

				} else
					logger.info("Unknown Key: " + key);
				continue;
			}
			AbstractServiceJob job = getJobforSchedulingBean(bean);
			if (job != null) {
				persistedJobs.add(job);
			}
		}

		return persistedJobs;
	}
	
	
	public boolean createJob(SchedulingBean job) {
		return storeJob(job, true);
	}

	public boolean updateJob(SchedulingBean job) {
		return storeJob(job, false);
	}

	public boolean storeJob(SchedulingBean job, boolean isNewJob) {

		logger.info("Storing job....");

		System.out.println("isnewjob: " + isNewJob);
		if (isNewJob) {
			long now = new Date().getTime();
			job.setName(Long.toString(now));
		}
		System.out.println("job name: " + job.getName());

		// save to db, REST call
		RestMessage rms = new RestMessage();;
		RestEntrySet res = new RestEntrySet();;
		RestMessage result = null;

		rms.setKeyword(RestKeyword.ServiceJob);

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
			if (isNewJob) {
				result = restConnector.prepareRestTransmission("ServiceJob/" + (jobIdSpecified ? job.getJobId().toString() : ""))
						.sendPutRestMessage(rms);
			} else {
				result = restConnector.prepareRestTransmission("ServiceJob/" + (jobIdSpecified ? job.getJobId().toString() : ""))
						.sendPostRestMessage(rms);
			}

			if (result.getStatus() != RestStatusEnum.OK) {

				logger.error("/ServiceJob response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		logger.info("PUT sent to /ServiceJob");

		if (!isNewJob && job != null) {

			try {
				boolean deleted = scheduler.deleteJob(new JobKey("DEFAULT." + job.getName()));
				boolean unscheduled = scheduler.unscheduleJob(new TriggerKey("DEFAULT." + job.getName()));
				System.out.println("removed old job: " + deleted  +"  old trigger: " + unscheduled);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
		// schedule new job / reschedule updated job

		AbstractServiceJob asj = getJobforSchedulingBean(job);
		JobDataMap data = new JobDataMap();
		data.put("repository_id", job.getInfo());
		data.put("job_name", job.getName());
		asj.setData(data);

		// schedule job
		return scheduleJob(asj);

	}

	public boolean deleteJob(String jobId, String jobName) {

		logger.info("Deleting job with name '" + jobName + "' ...");
		
		boolean deleted = false;
		
		// unschedule job
		JobKey key = new JobKey(jobName);
		try {
			if (scheduler.checkExists(key)) {

				deleted = scheduler.deleteJob(key);
				if (deleted) {
					logger.info("Job with key '" + key + "' has been removed!" + deleted);
				}				
			}
		} catch (SchedulerException e) {
			logger.warn("Could not unschedule job with name " + jobName, e);
		}

		// delete from db

		String result = restConnector.prepareRestTransmission("ServiceJob/" + jobId).DeleteData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms.getStatus() != RestStatusEnum.OK) {
			deleted = false;
			logger.error("/ServiceJob/" + jobId + " response failed: " + rms.getStatus() + "(" + rms.getStatusDescription() + ")");
		}
		
		return deleted;
	}
	

	private int getDayOfWeek(int day) {

		System.out.println("getdayofweek: mon " + DateBuilder.MONDAY);
		System.out.println("getdayofweek: sun " + DateBuilder.SUNDAY);
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

	public void listJobs() {

		System.out.println("Listing scheduled jobs...");

		// enumerate each job group
		try {

			if (scheduler == null || scheduler.getJobGroupNames() == null || scheduler.getJobGroupNames().isEmpty()) {
				logger.warn("Could not list service jobs as scheduler has not been initialized correctly. Please review earlier log messages!");
				return;
			}
		
			for (String group : scheduler.getJobGroupNames()) {
				System.out.println("Group: " + group);
				// enumerate each job in group

				for (JobKey jobKey : scheduler.getJobKeys(groupEquals(group))) {
					System.out.println("Found job identified by: " + jobKey);
					
					List<? extends Trigger> list = scheduler.getTriggersOfJob(jobKey);

					try {
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2012-05-05");
						for (Trigger trigger : list) {
							System.out.println("trigger found");
							outputFireTimeList((OperableTrigger) trigger, new Date(), date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	/*************************** Utils ***************************/ 
	
	
	private void outputFireTimeList(OperableTrigger trigger1, Date fromDate, Date toDate) {

		Trigger trigger = null;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2011-07-05 18:30");
			trigger = newTrigger().withIdentity("blabla1").startAt(tomorrowAt(15, 0, 0)) // 15:00:00
																							// tomorrow
					.withSchedule(calendarIntervalSchedule().withIntervalInDays(3)).build();
			System.out.println(trigger.getStartTime());
			// trigger.

		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("computing...");
		List fireTimeList = TriggerUtils.computeFireTimesBetween((OperableTrigger) trigger, null, fromDate, toDate);
		System.out.println("finished computing");
		for (int i = 0; i < fireTimeList.size(); i++) {
			System.out.println(fireTimeList.get(i));
		}
	}
	
	
	/************************** Getter and Setter *******************************/
	

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public RestConnector getRestConnector() {
		return restConnector;
	}

}
