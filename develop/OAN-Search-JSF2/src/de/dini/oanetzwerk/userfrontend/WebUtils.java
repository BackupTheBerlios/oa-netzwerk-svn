package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;


public class WebUtils {

	private final static Logger logger 				= Logger.getLogger(WebUtils.class);
	
	private final static String CATALINA_BASE		= System.getProperty("catalina.base");
	
	private static FacesContext faces 				= FacesContext.getCurrentInstance();
	
	private static Properties basicProperties;
	private static Properties restClientProperties;
	private static Properties searchClientProperties;
	
	private static String webappDir 				= "/webapps";
	private static String webappDirFallback			= "/wtpwebapps";
	private static String contextPath 				= "/oansearch";
	private static String contextPathFallback		= "/OAN-Search-JSF2";
	
	
	static {
		
		String context = faces.getExternalContext().getRequestContextPath();
		
		if (context != null && context.length() > 0) {		
			contextPath = context;
		}
		
		// try to guess property file path		
		boolean isLoaded = loadBasicProperties(webappDir, contextPath);

		if (!isLoaded)
			isLoaded = loadBasicProperties(webappDirFallback, contextPath);
		if (!isLoaded)
			isLoaded = loadBasicProperties(webappDir, contextPathFallback);
		if (!isLoaded)
			isLoaded = loadBasicProperties(webappDirFallback, contextPathFallback);		
		
		
		loadRestClientProperties();
		loadSearchClientProperties();
	}
	
	private static boolean loadBasicProperties(final String webappDir, final String contextPath) {
		
		try {
			basicProperties 	= HelperMethods.loadPropertiesFromFile (CATALINA_BASE + webappDir + contextPath + "/WEB-INF/userfrontend_gui.xml");
			WebUtils.webappDir		= webappDir;
			WebUtils.contextPath 	= contextPath;
			
			logger.info("Found userfrontend.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/userfrontend_gui.xml'");
			return true;
		} catch (Exception ex) {
			
			logger.warn("Could not fetch userfrontend.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/userfrontend_gui.xml'");
		} 
		return false;
	}
	
	private static void loadRestClientProperties() {
		
		try {
			restClientProperties = HelperMethods.loadPropertiesFromFile (CATALINA_BASE + getWebappPath() + "/WEB-INF/restclientprop.xml");

		} catch (InvalidPropertiesFormatException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			
		} catch (FileNotFoundException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
		}
	}
	
	
	private static void loadSearchClientProperties() {
		
		try {
			searchClientProperties = HelperMethods.loadPropertiesFromFile (CATALINA_BASE + getWebappPath() + "/WEB-INF/searchclientprop.xml");

		} catch (InvalidPropertiesFormatException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			
		} catch (FileNotFoundException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
		}
	}
	
	public static RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + getWebappPath() + basicProperties.getProperty ("restclientpropfile")), resource, basicProperties.getProperty ("username"), basicProperties.getProperty ("password"));
	}
	
	public static Properties getBasicProperties() {
    	return basicProperties;
    }

	public static Properties getRestClientProperties() {
    	return restClientProperties;
    }

	public static Properties getSearchClientProperties() {
    	return searchClientProperties;
    }

	public static String getWebappPath()
	{
		return webappDir + contextPath;
	}
}
