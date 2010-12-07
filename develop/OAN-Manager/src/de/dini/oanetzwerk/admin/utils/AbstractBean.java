package de.dini.oanetzwerk.admin.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public abstract class AbstractBean {

	private Properties props = null;


	public AbstractBean() {

		try {
			
			this.props = HelperMethods.loadPropertiesFromFileWithinWTPWebcontainer("OAN-Manager/WEB-INF/admingui.xml");
			
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected RestClient prepareRestTransmission(String resource) {

		return RestClient.createRestClient(new File(System.getProperty("cataline.home") + this.props.getProperty("restclientpropfile")), resource,
				this.props.getProperty("username"), this.props.getProperty("password"));
		// TODO: switch back return RestClient.createRestClient (new File
		// (System.getProperty ("catalina.base") + this.props.getProperty
		// ("restclientpropfile")), resource, this.props.getProperty
		// ("username"), this.props.getProperty ("password"));
	}
}
