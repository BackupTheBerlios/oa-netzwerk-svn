package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.PropertyManager;

@ManagedBean(name="rest")
@RequestScoped
public class RESTStatusBean {

	private final static Logger logger = Logger.getLogger(RESTStatusBean.class);
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;	
	
	private boolean restServerAvailable = false;

		
	public RESTStatusBean() {
	    super();
    }
	
	@PostConstruct
	public void checkStatus() {

		String username = propertyManager.getAdminProperties().getProperty("username");
		String password = propertyManager.getAdminProperties().getProperty("password");
		String host 	= propertyManager.getRestProperties().getProperty("url");
		String port 	= propertyManager.getRestProperties().getProperty("NonSSLPort");
		String path 	= propertyManager.getRestProperties().getProperty("servletPath");
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		URI uri;
        try {
        	
        	HttpHost targetHost = new HttpHost(host, Integer.parseInt(port), "http"); 

        	httpclient.getCredentialsProvider().setCredentials(
        	        new AuthScope(targetHost.getHostName(), targetHost.getPort()), 
        	        new UsernamePasswordCredentials(username, password));

        	// Create AuthCache instance
        	AuthCache authCache = new BasicAuthCache();
        	// Generate BASIC scheme object and add it to the local auth cache
        	BasicScheme basicAuth = new BasicScheme();
        	authCache.put(targetHost, basicAuth);

        	// Add AuthCache to the execution context
        	BasicHttpContext localcontext = new BasicHttpContext();
        	localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);      
        	
	        uri = URIUtils.createURI("http", host, Integer.parseInt(port), 
	        				path + "/Repository", null, null);
	        
	        HttpGet httpget = new HttpGet(uri);
	        HttpResponse response = httpclient.execute(httpget);

	        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode())
	        {
	        	restServerAvailable = true;
	        	logger.info("REST-Server on host " + host + " and port " + port + " is available!");
	        	
	        	return;
	        } 
	        
        } catch (URISyntaxException e) {
	        e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }

        restServerAvailable = false;
    }

	public boolean isRestServerAvailable() {
    	return restServerAvailable;
    }

	public String getUrl() {
		return "http://" + propertyManager.getRestProperties().getProperty("url") + "/" + 
		propertyManager.getRestProperties().getProperty("servletPath");
	}
	
	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

}
