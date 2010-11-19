package de.dini.oanetzwerk.utils;

import java.math.BigDecimal;

public class UsageDataMonth {

	private BigDecimal oid;
	private String metrics_name;
	private long counter;
	private java.util.Date relativeToDate;
	
	public UsageDataMonth(BigDecimal oid, String metrics_name, long counter, java.util.Date relativeToDate) {
		this.oid = oid;
		this.metrics_name = metrics_name;
		this.counter = counter;
		this.relativeToDate = relativeToDate;		
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
	public java.util.Date getRelativeToDate() {
		return relativeToDate;
	}
	public void setRelativeToDate(java.util.Date relativeToDate) {
		this.relativeToDate = relativeToDate;
	}
	
	
}
