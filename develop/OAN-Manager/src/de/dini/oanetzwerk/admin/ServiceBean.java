package de.dini.oanetzwerk.admin;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import javax.faces.context.FacesContext;

import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.StringUtils;
import de.dini.oanetzwerk.utils.Utils;

public class ServiceBean {

	private BigDecimal serviceId = new BigDecimal(0);
	private String service;
	private String localPath;
	private String localLogPath;
	private String rmiHost;
	private ServiceStatus status;
	private Date lastFullExecution;
	private long lastFullExecutionDuration = -1;
	private int newEntries = -1;

	public ServiceBean() {
		super();
	}

	public ServiceBean(String service) {
		super();
		this.service = service;
	}

	
	/******************* Getter & Setter *******************/
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRmiHost() {
		return rmiHost;
	}

	public void setRmiHost(String rmiHost) {
		this.rmiHost = rmiHost;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public String getLowerCaseName() {
		return getService().toString().toLowerCase();
	}

	public String getPrettyName() {
		return StringUtils.getPrettyNameFromCamelCase(service.toString());
	}

	public String getServiceName() {
		return service.toString() + "Service";
	}

	public boolean isLocalService() {
		return localPath != null && new File(localPath).exists();
	}

	public boolean isStarted() {
		return ServiceStatus.Started.equals(status);
	}

	public boolean isBusy() {
		return ServiceStatus.Busy.equals(status);
	}

	public String getLocalLogPath() {
		return localLogPath;
	}

	public void setLocalLogPath(String localLogPath) {
		this.localLogPath = localLogPath;
	}

	public BigDecimal getServiceId() {
		return serviceId;
	}

	public void setServiceId(BigDecimal serviceId) {
		this.serviceId = serviceId;
	}

	public Date getLastFullExecution() {
		return lastFullExecution;
	}

	public void setLastFullExecution(Date lastFullExecution) {
		this.lastFullExecution = lastFullExecution;
	}

	public int getNewEntries() {
		return newEntries;
	}

	public void setNewEntries(int newEntries) {
		this.newEntries = newEntries;
	}

	public long getLastFullExecutionDuration() {
    	return lastFullExecutionDuration;
    }

	public void setLastFullExecutionDuration(long lastFullExecutionDuration) {
    	this.lastFullExecutionDuration = lastFullExecutionDuration;
    }

	/**************** conveniance methods ****************/
	
	public String getLastExecutionPrettyFormat() {
		return Utils.localizedTimestamp(lastFullExecution, FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public String getPrettyDuration() {
		return Utils.millisToUserReadableTime(lastFullExecutionDuration, FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
}