/**
 * 
 */

package de.dini.oanetzwerk.servicemodule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael K&uuml;hn
 * 
 */

public class RestClient {
	
	private String servletPath = "restserver/server";
	private int port;
	private boolean nossl;
	private String url = "";
	private final String path;
	private final String username;
	private final String password;
	private Properties props = new Properties ( );
	static Logger logger = Logger.getLogger (RestClient.class);

	/**
	 * Creates a new client and initialises this client.
	 * The log4j is configured, the URL and the path are filtered and SSL, username and password are set.
	 * This Constructor is private due to our factory method which will call this constructor and return an
	 * instance of RestClient.
	 * @see RestClient#createRestClient(String incomming_url, String path, String userName, String passWord)
	 * 
	 * @param url the name of the REST server: i.e. foo.bar, localhost, 0.0.0.0. 
	 * @param path the path for the REST query
	 * @param user username for the authentication at the REST server
	 * @param pwd password for the authentication at the REST server
	 */
	
	private RestClient (String url, String path, String user, String pwd) {
		
		DOMConfigurator.configure ("log4j.xml");
		this.url = filterurl (url);
		this.nossl = true;
//		this.nossl = setSSL (url);
		this.path = filterpath (path);
		this.username = user;
		this.password = pwd;
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile ("restclientprop.xml");
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Conections might be impossible");
			
		} catch (FileNotFoundException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Conections might be impossible");
			
		} catch (IOException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Conections might be impossible");
		}
		
		if (!this.nossl) {
			
			if (props == null) {
				
				this.port = 443;
				
			} else {
				
				this.servletPath = new String (this.props.getProperty ("servletPath", "restserver/server"));
				this.port = new Integer (this.props.getProperty ("SSLPort", "443"));
				System.setProperty ("javax.net.ssl.trustStore", this.props.getProperty ("trustStore"));
				System.setProperty ("javax.net.ssl.keyStorePassword", this.props.getProperty ("keystorepassword"));
			}
			
		} else {
			
			if (props == null) {
				
				this.port = 80;
				
			} else {
				
				this.servletPath = new String (this.props.getProperty ("servletPath", "restserver/server"));
				this.port = new Integer (this.props.getProperty ("NonSSLPort", "80"));
			}
		}
	}
	
	/**
	 * @param path
	 * @return the filtered path
	 */
	
	private String filterpath (String path) {
		
		//TODO: filter path!
		
		String filteredPath = path;
		
		return filteredPath;
	}

	/**
	 * Filters the given URL and decides whether SSL has to be used or not.
	 * When the URL equals "localhost" or the ip address "127.0.0.1" SSL is not required.
	 * Otherwise SSL is strongly recommended due to security reasons. In the case the data is only sent
	 * within a private network SSL could be disabled.
	 *  
	 * @param url the URL of the REST server to connect to
	 * @return true if no SSL is needed false if SSL is recommended
	 */
	
	private boolean setSSL (String url) {
		
		if (url.equalsIgnoreCase ("localhost") || url.equalsIgnoreCase ("127.0.0.1") || url.equalsIgnoreCase ("oanet")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("noSSL");
			
			return true;
		}
		
		else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("SSL");
			
			return true;
			//TODO: if SSL works reenable false!!!!
//			return false;
		}
	}

	/**
	 * @param url
	 * @return the filtered URL
	 */
	
	private String filterurl (String url) {
		
		//TODO: filter URL!
		
		String filteredUrl = url.trim ( ).replaceAll ("\\s", "");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered URL: " + filteredUrl);
		
		return filteredUrl;
	}

	/**
	 * Creates a {@link RestClient} and calls the {@link RestClient} Constructor. 
	 * @see RestClient#RestClient(String url, String path, String user, String pwd)
	 * 
	 * @param incomming_url the name of the REST server: i.e. foo.bar, localhost, 0.0.0.0.
	 * @param path the path for the REST query
	 * @return an instance of RestClient
	 */
	
	public static RestClient createRestClient (String incomming_url, String path, String userName, String passWord) {
		
		RestClient restclient = new RestClient (incomming_url, path, userName, passWord);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("new restclient created");
		
		return restclient;
	}
	
	/**
	 * Sends the assembled request to the REST server.
	 * 
	 * @param client the {@link HttpClient} Object which handles the connection to the REST server
	 * @param method the {@link HttpMethod} Object which encodes the specific method which connects to the REST server
	 * @return the response received from the REST server
	 */
	
	private String sendrequest (HttpClient client, HttpMethod method) {

		byte [ ] responseBody = null;
		int errorcounter = 0;
		boolean cont = false;
		
		try {
			
			do {
			
				int statusCode = client.executeMethod (method);
				logger.info ("HttpStatusCode: " + statusCode);
				
				if (statusCode != HttpStatus.SC_OK) {
					
					logger.error ("A http-error occured while processing the IDs from server " + url);
					logger.error (method.getStatusText ( ));
	
					if (errorcounter++ > 10) {
						
						logger.error ("We got a http-error more than 10 times during communication with server " + url + " Now we are aborting communcation and trying to process the collected datas");
						method = null;
						
						return null;
						
					} else {
						
						logger.info (errorcounter + " errors occured. Server: " + url);
						continue;
					}
				}
				
			} while (cont);
			
			responseBody = method.getResponseBody ( );

		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
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
	 * Prepares the connection to the REST server.
	 * This method sets the authentication which is needed for the REST server and decides whether http or https is used for the connection.
	 * After all the final URI is prepared and assembled.
	 *   
	 * @return the initialised {@link HttpClient}
	 */
	
	private HttpClient prepareConnection ( ) {
		
		HttpClient newclient = new HttpClient ( );
		StringBuffer buffer = new StringBuffer ("");
		
		newclient.getParams ( ).setAuthenticationPreemptive (true);
		Credentials defaultcreds = new UsernamePasswordCredentials (this.username, this.password);
		
		if (this.nossl) {
			
			newclient.getState ( ).setCredentials (new AuthScope (this.url, this.port, AuthScope.ANY_REALM), defaultcreds);
			buffer.append ("http://");
			buffer.append (this.url).append (":").append (this.port) .append ("/").append (servletPath) .append ("/") .append (path);
			
		} else {
			
			newclient.getState ( ).setCredentials (new AuthScope (this.url, this.port, AuthScope.ANY_REALM), defaultcreds);
			buffer.append ("https://");
			buffer.append (this.url).append (":").append (this.port) .append ("/").append (servletPath) .append ("/") .append (path);
		}
		
		newclient.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("URL to connect to: " + this.url);
		
		this.url = buffer.toString ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("connection prepared");
		
		buffer = null;
		
		return newclient;
	}

	/**
	 * Provides a {@link GetMethod} connection to the REST server.
	 * 
	 * @return the response received from the REST server
	 */
	
	public final String GetData ( ) {
		
		HttpClient client = prepareConnection ( );
		GetMethod method = new GetMethod (this.url);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRequest will be used");
		
		return sendrequest (client, method);
	}
	
	/**
	 * Provides a {@link PostMethod} connection to the REST server.
	 * 
	 * @param data the data which will be sent via POST method to the REST server
	 * @return the response received from the REST server
	 * @throws UnsupportedEncodingException
	 */
	
	public final String PostData (String data) throws UnsupportedEncodingException {
		
		HttpClient client = prepareConnection ( );
		PostMethod method = new PostMethod (this.url);
		
		method.setRequestEntity (new StringRequestEntity (data, "text/plain", "UTF-8"));
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("postRequest will be used");
		
		return sendrequest (client, method);
	}
	
	/**
	 * Provides a {@link PutMethod} connection to the REST server.
	 * 
	 * @param data the data which will be sent via PUT method to the REST server
	 * @return the response received from the REST server
	 * @throws UnsupportedEncodingException
	 */
	
	public final String PutData (String data) throws UnsupportedEncodingException {
		
		HttpClient client = prepareConnection ( );
		PutMethod method = new PutMethod (this.url);
		
		method.setRequestEntity (new StringRequestEntity (data, "text/plain", "UTF-8"));
				
		if (logger.isDebugEnabled ( ))
			logger.debug ("putRequest will be used");
		
		return sendrequest (client, method);
	}
	
	/**
	 * Provides a {@link DeleteMethod} connection to the REST server.
	 * 
	 * @return the response received from the REST server
	 */
	
	public final String DeleteData ( ) {
		
		HttpClient client = prepareConnection ( );
		DeleteMethod method = new DeleteMethod (this.url);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("deleteRequest will be used");
		
		return sendrequest (client, method);
	}
	
	public RestMessage sendGetRestMessage() {
		String response = GetData(); 		
		return RestXmlCodec.decodeRestMessage(response);
	}
	
	public RestMessage sendPostRestMessage(RestMessage msg) throws UnsupportedEncodingException {
		String request = RestXmlCodec.encodeRestMessage(msg);
		String response = PostData(request); 		
		return RestXmlCodec.decodeRestMessage(response);
	}
	
	public RestMessage sendPutRestMessage(RestMessage msg) throws UnsupportedEncodingException {
		String request = RestXmlCodec.encodeRestMessage(msg);
		String response = PutData(request); 		
		return RestXmlCodec.decodeRestMessage(response);
	}
	
	public RestMessage sendDeleteRestMessage() {
		String response = DeleteData(); 		
		return RestXmlCodec.decodeRestMessage(response);
	}
}
