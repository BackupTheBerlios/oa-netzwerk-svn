package de.dini.oanetzwerk.admin.scheduling;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.DateBuilder.evenHourDate;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import de.dini.oanetzwerk.admin.RestConnector;
import de.dini.oanetzwerk.admin.SchedulingBean;
import de.dini.oanetzwerk.admin.SchedulingBean.SchedulingIntervalType;
import de.dini.oanetzwerk.admin.SchedulingBean.ServiceStatus;
import de.dini.oanetzwerk.admin.scheduling.AbstractServiceJob.ProcessingType;
import de.dini.oanetzwerk.admin.scheduling.jobs.AggregatorJob;
import de.dini.oanetzwerk.admin.scheduling.jobs.HarvesterJob;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.Utils;

@ManagedBean(name="schedulerControl")
@ApplicationScoped
public class SchedulerControl implements Serializable {

    private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SchedulerControl.class);
	
	private Properties restProperties;
	
	@ManagedProperty(value = "#{restConnector}")
	private RestConnector restConnector;
	
//	private static SchedulerControl instance = null;
	
	public SchedulerControl() {
	    super();
	    try {
	        
	    	initAndStartScheduler();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


//	public static synchronized SchedulerControl getInstance() {
//		if (instance == null) {
//			instance = new SchedulerControl();
//		}
//		return instance;
//	}
	
	public void initAndStartScheduler() throws SchedulerException {

		System.out.println("SchedulerControl initiated!");
		
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
		System.out.println("Scheduling job...");
		
		List<AbstractServiceJob> persistedJobs = new ArrayList<AbstractServiceJob>();
		
		// db request
		String result = restConnector.prepareRestTransmission("ServiceJob/").GetData();
		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		if (rms == null || rms.getListEntrySets().isEmpty()) {

			logger.error("received no Repository Details at all from the server");
			return null;
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

				}  else
					logger.info("Unknown Key: " + key);
					continue;
			}
			
			if (bean.getStatus() != null && !ServiceStatus.Finished.equals(bean.getStatus())) {
				// setup job and schedule
				

				
				AbstractServiceJob job;
				if (new Integer(1).equals(bean.getServiceId())) {
					job = new HarvesterJob();
				} else if (new Integer(2).equals(bean.getServiceId())) {
					job = new AggregatorJob();
				} else {
//					job = new MarkerJob();
				}

				if (bean.isPeriodic()) {
					
					if (bean.getNonperiodicTimestamp() == null) {
						logger.warn("Could not fetch time for periodic scheduling interval! Cannot schedule service jobs!");
					}
					if (SchedulingIntervalType.Monthly.equals(bean.getInterval())) {
						if (bean.getPeriodicDays() > 28) {
							 Trigger trigger = newTrigger()
							    .withIdentity("job") // because group is not specified, "trigger8" will be in the default group
							    .withSchedule(cronSchedule("0 0 2 L * ?"))
							    .forJob("job")
							    .build();
						} else if (bean.getPeriodicDays() <= 28) {
							 Trigger trigger = newTrigger()
							    .withIdentity("job") // because group is not specified, "trigger8" will be in the default group
							    .withSchedule(cronSchedule("0 0 2 " + bean.getPeriodicDays() + " * ?"))
							    .forJob("job")
							    .build();
						
					} else if (SchedulingIntervalType.Weekly.equals(bean.getInterval())) {
						
						  Trigger trigger = newTrigger()
						    .withIdentity("job")
						    .withSchedule(weeklyOnDayAndHourAndMinute(DateBuilder.WEDNESDAY, 10, 42))
						    .forJob("job")
						    .build();
						
					} else if (SchedulingIntervalType.Day.equals(bean.getInterval())) {
						 Trigger trigger = newTrigger()
						    .withIdentity("job") // because group is not specified, "trigger8" will be in the default group
						    .startAt(evenHourDate(null)) // get the next even-hour (minutes and seconds zero ("00:00"))
						    .withSchedule(simpleSchedule()
						        .withIntervalInHours(2)
						        .repeatForever())
						    // note that in this example, 'forJob(..)' is not called 
						    //  - which is valid if the trigger is passed to the scheduler along with the job  
						    .build();

					}
					
				} else {
					job.setTrigger(new SimpleTrigger("job", bean.getNonperiodicTimestamp()));
				}
			}
			
			persistedJobs.add(bean);
		}
		
		// fill persistedJobs
		
		HarvesterJob job1 = new HarvesterJob();
		job1.setServiceName("harvester");
		job1.setRepositoryId(4);
		job1.setProcessingType(ProcessingType.SINGLE);
		job1.setAdditionalInfo("Bla bla");
		
		persistedJobs.add(job1);
		return persistedJobs;
		
		
	}
	
	private Trigger getTriggerForJob(AbstractServiceJob job) {
		
		
		return new SimpleTrigger("test", new Date());

//		Trigger trigger = TriggerUtils.makeImmediateTrigger("harvesterTrigger", , repeatInterval)makeSecondlyTrigger();
//		trigger.setStartTime(TriggerUtils.getEvenSecondDate(new Date())); // start on the next even hour
//		trigger.setName("harvesterTrigger");
//		
//		return trigger;
	}


	public void setRestConnector(RestConnector restConnector) {
    	this.restConnector = restConnector;
    }
	
}
