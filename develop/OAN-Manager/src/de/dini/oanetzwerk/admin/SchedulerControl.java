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
		// try {
		//
		// initAndStartScheduler();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
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

				if (job == null) {
					// continue
				}

				System.out.println("info: " + job.getName());
				JobDetail jobDetail = null;
				if (job instanceof HarvesterJob) {

					jobDetail = newJob(HarvesterJob.class).withIdentity(job.getName()).build();
				} else if (job instanceof AggregatorJob) {
					jobDetail = newJob(AggregatorJob.class).withIdentity(job.getName()).build();
				} else {
					System.out.println("else");
					// jobDetail = newJob(Marker.class).withIdentity("job1",
					// "group1").build();
				}
				logger.info("Scheduling job of type " + job.getClass());

				System.out.println("Actually scheduling job with name " + job.getName());
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
		System.out.println("Preparing jobs...");

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

		System.out.println("null: " + persistedJobs == null);
		System.out.println("size " + persistedJobs.size());
		return persistedJobs;
	}

	private AbstractServiceJob getJobforSchedulingBean(SchedulingBean bean) {

		if (bean.getStatus() != null && !ServiceStatus.Finished.equals(bean.getStatus())) {
			// setup job and schedule

			AbstractServiceJob job = null;
			if (new BigDecimal(1).equals(bean.getServiceId())) {
				System.out.println("Creating HarvesterJob");
				job = new HarvesterJob();
			} else if (new BigDecimal(2).equals(bean.getServiceId())) {
				job = new AggregatorJob();
			} else {
				// job = new MarkerJob();
				System.out.println("Unknown Job " + bean.getServiceId());
			}

			job.setName(bean.getName());
			Trigger trigger = null;

			System.out.println("out: " + bean.getNonperiodicTimestamp());

			try {

				System.out.println("interval " + bean.getInterval());
				if (bean.isPeriodic()) {

					if (bean.getNonperiodicTimestamp() == null) {
						logger.warn("Could not fetch time for periodic scheduling interval! Cannot schedule service jobs!");
						return null;
					}

					if (SchedulingIntervalType.Monthly.equals(bean.getPeriodicInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a month)");
						if (bean.getPeriodicDays() > 28) {
							trigger = newTrigger().withIdentity(bean.getName()).withSchedule(cronSchedule("0 0 2 L * ?")).forJob("job")
									.build();
						} else if (bean.getPeriodicDays() <= 28) {
							trigger = newTrigger().withIdentity(bean.getName())
									.withSchedule(cronSchedule("0 0 2 " + bean.getPeriodicDays() + " * ?")).forJob("job").build();

						}
					} else if (SchedulingIntervalType.Weekly.equals(bean.getPeriodicInterval())) {
						logger.info("periodic job scheduled. (every " + bean.getPeriodicDays() + " day of a week)");
						trigger = newTrigger()
								.withIdentity(bean.getName())
								.withSchedule(
										weeklyOnDayAndHourAndMinute(getDayOfWeek(bean.getPeriodicDays()), bean.getNonperiodicTimestamp()
												.getHours(), bean.getNonperiodicTimestamp().getMinutes())).forJob("job").build();

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
					// job.setTrigger(trigger);
					System.out.println("test");
				}

				job.setTrigger(trigger);
				return job;

			} catch (ParseException e) {
				logger.warn("Could not read cron expression for job. ", e);
			}
		}
		return null;
	}

	public boolean createJob(SchedulingBean job) {
		return storeJob(job, true);
	}

	public boolean updateJob(SchedulingBean job) {
		return storeJob(job, false);
	}

	public boolean storeJob(SchedulingBean job, boolean newJob) {

		System.out.println("Storing job....");

		if (newJob) {
			long now = new Date().getTime();
			job.setName(Long.toString(now));
		}

		// save to db, REST call
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.ServiceJob);
		// rms.setStatus(RestStatusEnum.OK);

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
			if (newJob) {
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

		if (newJob) {
			// schedule
			AbstractServiceJob asj = getJobforSchedulingBean(job);
			if (job != null) {
				JobDetail jobDetail = newJob(asj.getClass()).withIdentity(job.getName())
						.usingJobData("someProp", "someValue").build();
				try {
					System.out.println("trigger name: " + asj.getTrigger().getKey());
					scheduler.scheduleJob(jobDetail, asj.getTrigger());

					return true;
				} catch (SchedulerException e) {
					logger.warn("An error occured while scheduling job.", e);
				}
			}
		}
		else {
			// update scheduled job;
			try {
				if (job != null) {

					boolean deleted = scheduler.deleteJob(new JobKey("DEFAULT." + job.getName()));
					boolean unscheduled = scheduler.unscheduleJob(new TriggerKey("DEFAULT." + job.getName()));
					System.out.println("removed old job: " + deleted  +"  old trigger: " + unscheduled);
					AbstractServiceJob asj = getJobforSchedulingBean(job);
					JobDetail jobDetail = newJob(asj.getClass()).withIdentity(job.getName(), job.getServiceId().toString())
							.usingJobData("someProp", "someValue").build();
					
					scheduler.scheduleJob(jobDetail, asj.getTrigger());
					System.out.println("rescheduled successfully");
					return true;
				}
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}

	public boolean deleteJob(String jobId, String jobName) {

		
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

	private void outputFireTimeList(OperableTrigger trigger1, Date fromDate, Date toDate) {

		// Trigger trigger = newTrigger()
		// .withIdentity("blabla")
		// .withSchedule(
		// weeklyOnDayAndHourAndMinute(getDayOfWeek(5), 18, 30)).build();

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

	public void setRestConnector(RestConnector restConnector) {
		this.restConnector = restConnector;
	}

	public RestConnector getRestConnector() {
		return restConnector;
	}

}
