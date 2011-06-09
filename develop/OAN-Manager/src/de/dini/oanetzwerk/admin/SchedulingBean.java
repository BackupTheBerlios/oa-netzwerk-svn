package de.dini.oanetzwerk.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SchedulingBean {

	private Integer jobId = null;
	private String name = "";
	private BigDecimal serviceId = null;
	private ServiceStatus status = ServiceStatus.Open;
	private String info = "";
	private boolean periodic = false;
	private Date nonperiodicTimestamp = null;
	private SchedulingIntervalType periodicInterval = null;
	private int periodicDays = 0;
	private boolean nonperiodicNow = false;
	

	
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
				interval.append("jeden " + periodicDays + ". des Monats um" + date + "Uhr");
			} else if (SchedulingIntervalType.Weekly.equals(periodicInterval)) {
				interval.append("jeden " + periodicDays + " um " + date + "Uhr");
			} else if (SchedulingIntervalType.Day.equals(periodicInterval)) {
				if (periodicDays == 1) {
					interval.append("täglich um ");
				}
				else {
					interval.append("alle " + periodicDays + " Tage um ");
				}
				interval.append(date + "Uhr");
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
	
	
}
