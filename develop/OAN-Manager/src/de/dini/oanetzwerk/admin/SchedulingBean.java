package de.dini.oanetzwerk.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

//import de.dini.oanetzwerk.admin.ServiceManagementBean.Service;
import de.dini.oanetzwerk.utils.StringUtils;
import de.dini.oanetzwerk.utils.Utils;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class SchedulingBean implements Comparable<SchedulingBean> {

	private Integer jobId 			= null;
	private String name 			= "";
	private BigDecimal serviceId 	= null;
	private ServiceStatus status 	= ServiceStatus.Open;
	private String info 			= "";
	private boolean periodic 		= false;
	private Date nonperiodicTimestamp = null;
	private SchedulingIntervalType periodicInterval = null;
	private int periodicDays 		= 0;
	private boolean nonperiodicNow 	= false;
	private int newEntries			= -1;
	private long duration			= -1;
	
//	private static List<Service> services = new ArrayList<Service>();

//	static {
//		
//		// TODO: load service names and ids dynamicall from the db
//		services.add(Service.Harvester);
//		services.add(Service.Aggregator);
//		services.add(Service.Marker);
//		services.add(Service.FulltextLinkFinder);
//		services.add(Service.LanguageDetection);
//		services.add(Service.Shingler);
//		services.add(Service.Indexer);
//		services.add(Service.Classifier);
//		services.add(Service.DuplicateCheck);
//	}
	
	public SchedulingBean() {
		super();
	}

	public enum SchedulingIntervalType {
		Monthly, Weekly, Day;
	}
	
	public enum ServiceStatus {
		Open, Working, Finished
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getServiceId() {
		return serviceId;
	}

	public void setServiceId(BigDecimal serviceId) {
		this.serviceId = serviceId;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isPeriodic() {
		return periodic;
	}

	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}

	public Date getNonperiodicTimestamp() {
		return nonperiodicTimestamp;
	}

	public void setNonperiodicTimestamp(Date nonperiodicTimestamp) {
		this.nonperiodicTimestamp = nonperiodicTimestamp;
	}

	public SchedulingIntervalType getPeriodicInterval() {
		return periodicInterval;
	}

	public void setPeriodicInterval(SchedulingIntervalType periodicInterval) {
		this.periodicInterval = periodicInterval;
	}

	public int getPeriodicDays() {
		return periodicDays;
	}

	public void setPeriodicDays(int periodicDays) {
		this.periodicDays = periodicDays;
	}

	public String getInterval() {

		StringBuffer interval = new StringBuffer(periodic ? "regelmäßig\n" : "einmalig\n");
		

		
		if (periodic) {
			String date = new SimpleDateFormat("HH:mm").format(nonperiodicTimestamp);
			
			if (SchedulingIntervalType.Monthly.equals(periodicInterval)) {
				interval.append("jeden " + periodicDays + ". des Monats um" + date + " Uhr");
			} else if (SchedulingIntervalType.Weekly.equals(periodicInterval)) {
				interval.append("jeden " + periodicDays + " um " + date + " Uhr");
			} else if (SchedulingIntervalType.Day.equals(periodicInterval)) {
				if (periodicDays == 1) {
					interval.append("täglich um ");
				}
				else {
					interval.append("alle " + periodicDays + " Tage um ");
				}
				interval.append(date + " Uhr");
			}
		} else {
			
			interval.append(new SimpleDateFormat("dd.MM.yy HH:mm").format(nonperiodicTimestamp));
		}
		return interval.toString();
    }

	public boolean isNonperiodicNow() {
    	return nonperiodicNow;
    }

	public void setNonperiodicNow(boolean nonperiodicNow) {
    	this.nonperiodicNow = nonperiodicNow;
    }

	@Override
    public int compareTo(SchedulingBean o) {
	    
		if (o == null || o.getName() == null) {
			return 1;
		}
		
		if (this == null || this.getName() == null) {
			return -1;
		}
		
		long otherName = Long.parseLong(o.getName()); 
		long thisName = Long.parseLong(this.getName());
		
		if (thisName > otherName) {
			return 1;
		} else if (otherName == thisName) {
			return 0;
		} else {
			return -1;
		}
		
    }

	public int getNewEntries() {
    	return newEntries;
    }

	public void setNewEntries(int newEntries) {
    	this.newEntries = newEntries;
    }

	public long getDuration() {
    	return duration;
    }

	public void setDuration(long duration) {
    	this.duration = duration;
    }
	
	/**************** conveniance methods ****************/
		
	public String getPrettyDuration() {
		return Utils.millisToUserReadableTime(duration, FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
}
