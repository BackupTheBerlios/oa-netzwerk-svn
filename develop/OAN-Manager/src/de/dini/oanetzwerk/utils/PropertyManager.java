package de.dini.oanetzwerk.utils;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
@ManagedBean(name="propertyManager")
@ApplicationScoped
public class PropertyManager implements Serializable {
	
    private static final long serialVersionUID = 1L;

	private final static Logger logger 				= Logger.getLogger(PropertyManager.class);
	
	private final static String CATALINA_BASE		= System.getProperty("catalina.base");
	
//	private static FacesContext faces 				= FacesContext.getCurrentInstance();
	
	
	private static String webappDir 				= "/wtpwebapps";
	private static String webappDirFallback			= "/webapps";
	private static String contextPath 				= "/OAN-Manager";
	private static String contextPathFallback		= "/oanadmin";
	private static String servletContextPathFallback= "";
	
	private static Properties serviceProperties = null;
	private static Properties adminProperties = null;
	private static Properties restProperties = null;
	private static Properties oapsProperties = null;
	private static Properties mailProperties = null;
	
	public PropertyManager() {
		super();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {

		PropertyManager.readPropertyFiles();
	}	
	
	
	public static void readPropertyFiles() {
//		String context = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
//		
//		if (context != null && context.length() > 0) {		
//			servletContextPathFallback = context;
//		}
		
		// try to guess property file path		
		boolean isLoaded = loadServiceProperties(webappDir, contextPath);

		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDirFallback, contextPath);
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDir, contextPathFallback);
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDirFallback, contextPathFallback);	
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDir, servletContextPathFallback);	
		if (!isLoaded)
			isLoaded = loadServiceProperties(webappDirFallback, servletContextPathFallback);

		loadAdminProperties(webappDir, contextPath);
		loadRestClientProperties(webappDir, contextPath);
		loadOAPSClientProperties(webappDir, contextPath);
		loadMailProperties(webappDir, contextPath);
	}	

	
	
	private static boolean loadServiceProperties(final String webappDir, final String contextPath) {
		
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
	
	private static boolean loadAdminProperties(final String webappDir, final String contextPath) {
		
		try {
			adminProperties 	= HelperMethods.loadPropertiesFromFile (CATALINA_BASE + webappDir + contextPath + "/WEB-INF/admingui.xml");
			PropertyManager.webappDir		= webappDir;
			PropertyManager.contextPath 	= contextPath;
			
			logger.info("Found admingui.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/admingui.xml'");
			return true;
		} catch (Exception ex) {
			
			logger.warn("Could not fetch admingui.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/admingui.xml'");
		} 
		return false;
	}
	
	private static boolean loadRestClientProperties(final String webappDir, final String contextPath) {
		
		try {
			restProperties 	= HelperMethods.loadPropertiesFromFile (CATALINA_BASE + webappDir + contextPath + "/WEB-INF/restclientprop.xml");
			PropertyManager.webappDir		= webappDir;
			PropertyManager.contextPath 	= contextPath;
			
			logger.info("Found restclientprop.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/restclientprop.xml'");
			return true;
		} catch (Exception ex) {
			
			logger.warn("Could not fetch restclientprop.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/restclientprop.xml'");
		} 
		return false;
	}
	
	private static boolean loadOAPSClientProperties(final String webappDir, final String contextPath) {
		try {
			oapsProperties = HelperMethods.loadPropertiesFromFile(CATALINA_BASE + webappDir + contextPath +"/WEB-INF/oapsprop.xml");
			PropertyManager.webappDir = webappDir;
			PropertyManager.contextPath = contextPath;
			logger.info("Found oapsprop.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/oapsprop.xml'");
			return true;
		} catch (Exception e) {
			logger.warn("Could not find oapsprop.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/oapsprop.xml'");
		}
		return false;
	}
	
	private static boolean loadMailProperties(final String webappDir, final String contextPath) {
		try {
			mailProperties = HelperMethods.loadPropertiesFromFile(CATALINA_BASE + webappDir + contextPath +"/WEB-INF/mailsettings.xml");
			PropertyManager.webappDir = webappDir;
			PropertyManager.contextPath = contextPath;
			logger.info("Found oapsprop.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/mailsettings.xml'");
			return true;
		} catch (Exception e) {
			logger.warn("Could not find mailsettings.xml in '" + CATALINA_BASE + webappDir + contextPath + "/WEB-INF/mailsettings.xml'");
		}
		return false;
	}
	

	public static Properties getServiceProperties() {
		if (serviceProperties == null) {
			readPropertyFiles();
		}
		return serviceProperties;
	}
	public Properties getOAPSProperties() {
		if (oapsProperties == null) {
			init();
		}
		return oapsProperties;
	}
	
	public Properties getMailProperties() {
		if (mailProperties == null) {
			init();
		}
		return mailProperties;
	}

//	public void setServiceProperties(Properties serviceProperties) {
//		this.serviceProperties = serviceProperties;
//	}

	public static Properties getAdminProperties() {
		
		if (adminProperties == null) {
			readPropertyFiles();
		}
    	return adminProperties;
    }
	
	public static Properties getRestProperties() {
		
		if (restProperties == null) {
			readPropertyFiles();
		}
    	return restProperties;
    }

	public static String getWebappDir() {
    	return webappDir;
    }

	public static String getContextPath() {
    	return contextPath;
    }	
	
	public static String getWebApplicationRootDirectory() {
    	return CATALINA_BASE + webappDir + contextPath;
    }	
		
}
