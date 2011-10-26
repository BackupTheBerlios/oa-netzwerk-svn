package de.dini.oanetzwerk.admin;

import java.io.File;

import de.dini.oanetzwerk.admin.ServiceManagementBean.Service;
import de.dini.oanetzwerk.servicemodule.ServiceStatus;
import de.dini.oanetzwerk.utils.StringUtils;

public class ServiceBean {

	
	private Service service;
	private String localPath;
	private String rmiHost;
	private ServiceStatus status;
	
	
	
	public ServiceBean(Service service) {
	    super();
	    this.service = service;
    }
	
	
	public Service getService() {
    	return service;
    }
	public void setService(Service service) {
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
	
}
