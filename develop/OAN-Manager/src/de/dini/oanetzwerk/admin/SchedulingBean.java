package de.dini.oanetzwerk.admin;

import java.math.BigDecimal;
import java.sql.Date;

public class SchedulingBean {

	private Integer jobId = null;
	private String name = null;
	private BigDecimal serviceId = null;
	private ServiceStatus status = ServiceStatus.Open;
	private String info = null;
	private boolean periodic = false;
	private Date nonperiodicTimestamp = null;
	private String periodicInterval = null;
	private int periodicDays = -1;

	
	public SchedulingBean() {
		super();
	}

	
	public enum ServiceStatus {
		Open, InProgress, Done
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

	public String getPeriodicInterval() {
		return periodicInterval;
	}

	public void setPeriodicInterval(String periodicInterval) {
		this.periodicInterval = periodicInterval;
	}

	public int getPeriodicDays() {
		return periodicDays;
	}

	public void setPeriodicDays(int periodicDays) {
		this.periodicDays = periodicDays;
	}

}
