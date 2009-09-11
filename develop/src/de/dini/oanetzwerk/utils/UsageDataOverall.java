package de.dini.oanetzwerk.utils;

import java.math.BigDecimal;

public class UsageDataOverall {

	private BigDecimal oid;
	private String metrics_name;
	private long counter;
	private java.util.Date lastUpdate;
	
	public UsageDataOverall(BigDecimal oid, String metrics_name, long counter, java.util.Date lastUpdate) {
		this.oid = oid;
		this.metrics_name = metrics_name;
		this.counter = counter;
		this.lastUpdate = lastUpdate;		
	}

	public BigDecimal getOid() {
		return oid;
	}

	public void setOid(BigDecimal oid) {
		this.oid = oid;
	}

	public String getMetrics_name() {
		return metrics_name;
	}

	public void setMetrics_name(String metrics_name) {
		this.metrics_name = metrics_name;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public java.util.Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(java.util.Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
}
