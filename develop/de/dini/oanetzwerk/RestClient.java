/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Michael Kühn
 *
 */

public class RestClient {
	
	private boolean nossl;
	private String url;
	private String path;
	private String username;
	private String password;
	static Logger logger = Logger.getLogger (RestClient.class);

	private RestClient (String url, String path, String user, String pwd) {
		
		DOMConfigurator.configure ("log4j.xml");
		this.url = filterurl (url);
		this.nossl = setSSL (url);
		this.path = filterpath (path);
		this.username = user;
		this.password = pwd;
	}
	
	/**
	 * @param path
	 * @return
	 */
	
	private String filterpath (String path) {
		
		String filteredPath = path;
		
		return filteredPath;
	}

	/**
	 * @param url
	 * @return
	 */
	
	private boolean setSSL (String url) {
		
		if (url.equalsIgnoreCase ("localhost") || url.equalsIgnoreCase ("127.0.0.1")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("noSSL");
			
			return true;
		}
		
		else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("SSL");
			
			return false;
		}
	}

	/**
	 * @param url
	 * @return
	 */
	
	private String filterurl (String url) {

		String filteredUrl = url;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered URL: " + filteredUrl);
		
		return filteredUrl;
	}

	/**
	 * @param incomming_url
	 * @param path
	 * @return
	 */
	
	public static RestClient createRestClient (String incomming_url, String path, String userName, String passWord) {
		
		RestClient restclient = new RestClient (incomming_url, path, userName, passWord);
		
		return restclient;
	}
	
	/**
	 * @param client
	 * @param method
	 * @return
	 */
	
	private String sendrequest (HttpClient client, HttpMethod method) {

		byte [ ] responseBody = null;
		
		try {
			
			int statusCode = client.executeMethod (method);
			
			logger.info ("HttpStatusCode: " + statusCode);
			
			if (statusCode != HttpStatus.SC_OK)
				;//meckern
			
			responseBody = method.getResponseBody ( );
			
		} catch (IOException ioex) {
			
			ioex.printStackTrace ( );
			
		} finally {
			
			method.releaseConnection ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Connection closed");
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Response: " + new String (responseBody));
		
		return new String (responseBody);
	}

	/**
	 * @return
	 */
	
	private HttpClient prepareConnection ( ) {
		
		HttpClient newclient = new HttpClient ( );
		StringBuffer buffer = new StringBuffer ("");
		
		newclient.getParams ( ).setAuthenticationPreemptive (true);
		Credentials defaultcreds = new UsernamePasswordCredentials (this.username, this.password);
		newclient.getState ( ).setCredentials (new AuthScope (this.url, 8080, AuthScope.ANY_REALM), defaultcreds);
		
		if (this.nossl) {
			
			buffer.append ("http://");
			
		} else {
			
			buffer.append ("https://");
		}
		
		buffer.append (this.url).append (":8080/restserver/").append (path);
		this.url = buffer.toString ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("client created");
		
		return newclient;
	}

	public final String GetData ( ) {
		
		HttpClient client = prepareConnection ( );
		GetMethod method = new GetMethod (this.url);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRequest");
		
		return sendrequest (client, method);
	}
	
	public final String PostData (String data) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("postRequest");
		
		return "";
	}
	
	public final String PutData (String data) {
		
		HttpClient client = prepareConnection ( );
		PutMethod method = new PutMethod (this.url);
		
		try {
			
			method.setRequestEntity (new StringRequestEntity (data, "text/plain", "UTF-8"));
			
		} catch (UnsupportedEncodingException ex) {
			
			ex.printStackTrace ( );
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("putRequest");
		
		return sendrequest (client, method);
	}
	
	public final String DeleteData ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("deleteRequest");
		
		return "";
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
