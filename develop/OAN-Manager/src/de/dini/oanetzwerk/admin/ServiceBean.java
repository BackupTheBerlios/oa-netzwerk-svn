package de.dini.oanetzwerk.admin;

import java.io.File;
import java.math.BigDecimal;

//import de.dini.oanetzwerk.admin.ServiceManagementBean.Service;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.StringUtils;

public class ServiceBean {

	private BigDecimal id = new BigDecimal(0);
	private String service;
	private String localPath;
	private String localLogPath;
	private String rmiHost;
	private ServiceStatus status;
	
	
	
	
	public ServiceBean() {
	    super();
    }

	public ServiceBean(String service) {
	    super();
	    this.service = service;
    }
	
	
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
	public BigDecimal getId() {
    	return id;
    }
	public void setId(BigDecimal id) {
    	this.id = id;
    }
		
}