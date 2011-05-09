package de.dini.oanetzwerk.admin;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SchedulingBean {

	private Integer jobId = null;
	private String name = null;
	private BigDecimal serviceId = null;
	private ServiceStatus status = ServiceStatus.Open;
	private String info = null;
	private boolean periodic = false;
	private Date nonperiodicTimestamp = null;
	private SchedulingIntervalType periodicInterval = null;
	private int periodicDays = -1;

	
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

		String interval = periodic ? "regelmäßig\n" : "einmalig\n";
		
		if (periodic) {
			if (SchedulingIntervalType.Monthly.equals(periodicInterval)) {
				interval = interval + "jeden " + periodicDays + ". des Monats um" + nonperiodicTimestamp.getHours() + ":" + nonperiodicTimestamp.getMinutes() + "Uhr";;
			} else if (SchedulingIntervalType.Weekly.equals(periodicInterval)) {
				interval = interval + "jeden " + periodicDays + " um " + nonperiodicTimestamp.getHours() + ":" + nonperiodicTimestamp.getMinutes() + "Uhr";;
			} else if (SchedulingIntervalType.Day.equals(periodicInterval)) {
				if (periodicDays == 1) {
					interval = interval + "täglich um " + nonperiodicTimestamp.getHours() + ":" + nonperiodicTimestamp.getMinutes() + "Uhr";
				}
				interval = interval + "alle " + periodicDays + " Tage";
			}
		} else {
			
			interval = new SimpleDateFormat("dd.MM.yy hh:mm").format(nonperiodicTimestamp);
		}
		return interval;
    }
}
