package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.PropertyManager;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name="restConnector")
@ApplicationScoped
public class RestConnector implements Serializable {

    private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(RestConnector.class);
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	public RestConnector() {
	    super();
	    System.out.println("RestConnector constructor");
    }
	

	
	public RestClient prepareRestTransmission(String resource) {

		Properties props = propertyManager.getAdminProperties();
		
		if (props == null) {
			logger.warn("Property file has not been initialized correctly, cannot prepare REST transmission!");
		}
		
		System.out.println("restclientpropfile: " + System.getProperty ("catalina.base") + propertyManager.getWebappDir() + propertyManager.getContextPath() + props.getProperty
		 ("restclientpropfile"));
		return RestClient.createRestClient (new File(System.getProperty ("catalina.base") + propertyManager.getWebappDir() + propertyManager.getContextPath() + props.getProperty
		 ("restclientpropfile")), resource, props.getProperty
		 ("username"), props.getProperty("password"));
	}


	public void setPropertyManager(PropertyManager propertyManager) {
    	this.propertyManager = propertyManager;
    	System.out.println("RestConnector propertyManager setter");
    }	
}
