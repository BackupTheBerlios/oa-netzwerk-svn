package de.dini.oanetzwerk.admin.utils;

import java.io.File;
import java.io.FileInputStream;
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
//		System.out.println("Tomcat: " + new File(System.getProperty("cataline.home")) == null);
//		System.out.println("Tomcat2: " + System.getProperty("cataline.home"));
//		System.out.println("Tomcat2: " + System.getProperty("cataline.base"));
//		System.out.println("propFilePath: " + this.props.getProperty("restclientpropfile"));
		
//		String pathToRestConfig = System.getProperty("cataline.home") + this.props.getProperty("restclientpropfile");
		String pathToRestConfig = "C:\\Dokumente und Einstellungen\\quickstx\\Desktop\\Downloads\\apache-tomcat-6.0.30\\webapps\\OAN-Manager\\WEB-INF\\restclientprop.xml";
			
		return RestClient.createRestClient(new File(pathToRestConfig), resource,
				this.props.getProperty("username"), this.props.getProperty("password"));
		// TODO: switch back return RestClient.createRestClient (new File
		// (System.getProperty ("catalina.base") + this.props.getProperty
		// ("restclientpropfile")), resource, this.props.getProperty
		// ("username"), this.props.getProperty ("password"));
	}
}
