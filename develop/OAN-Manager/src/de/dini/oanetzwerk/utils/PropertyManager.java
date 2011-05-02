package de.dini.oanetzwerk.utils;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

@ManagedBean(name="propertyManager")
@ApplicationScoped
public class PropertyManager {

	private final static Logger logger 				= Logger.getLogger(PropertyManager.class);
	
	private final static String CATALINA_BASE		= System.getProperty("catalina.base");
	
	private static FacesContext faces 				= FacesContext.getCurrentInstance();
	
	
	private static String webappDir 				= "/webapps";
	private static String webappDirFallback			= "/wtpwebapps";
	private static String contextPath 				= "/oanadmin";
	private static String contextPathFallback		= "/OAN-Manager";
	
	private Properties serviceProperties = null;
	
	public PropertyManager() {
		super();
		
	}

	@PostConstruct
	private void init() {

		String context = faces.getExternalContext().getRequestContextPath();
		
		if (context != null && context.length() > 0) {		
			contextPath = context;
		}
		
		// try to guess property file path		
		boolean isLoaded = loadServiceProperties(webappDir, contextPath);

		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDirFallback, contextPath);
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDir, contextPathFallback);
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDirFallback, contextPathFallback);		
		
		
	}	
	
	
	private boolean loadServiceProperties(final String webappDir, final String contextPath) {
		
		try {
			serviceProperties 	= HelperMethods.loadPropertiesFromPropFile (CATALINA_BASE + webappDir + contextPath + "/WEB-INF/services.properties");
			PropertyManager.webappDir		= webappDir;
			PropertyManager.contextPath 	= contextPath;
			
			logger.info("Found services.properties in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/services.properties'");
			return true;
		} catch (Exception ex) {
			
			logger.warn("Could not fetch services.properties in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/services.properties'");
		} 
		return false;
	}

	public Properties getServiceProperties() {
		if (serviceProperties == null) {
			init();
		}
		return serviceProperties;
	}

	public void setServiceProperties(Properties serviceProperties) {
		this.serviceProperties = serviceProperties;
	}
		
}
