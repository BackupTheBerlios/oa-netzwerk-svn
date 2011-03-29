package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.Utils;

@ApplicationScoped
@ManagedBean(name="restconnector")
public class RestConnector implements Serializable {

	
    private static final long serialVersionUID = 1L;
	private Properties props 		= null;

	
	public RestConnector() {
	    super();
	    init();
    }
	
	
	private void init() {
		try {
			
			this.props = HelperMethods.loadPropertiesFromFileWithinWebcontainerWebapps(Utils.getDefaultContext() + "/WEB-INF/admingui.xml");
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RestClient prepareRestTransmission(String resource) {

		return RestClient.createRestClient (new File(System.getProperty ("catalina.base") + this.props.getProperty
		 ("restclientpropfile")), resource, this.props.getProperty
		 ("username"), this.props.getProperty ("password"));
	}
}
