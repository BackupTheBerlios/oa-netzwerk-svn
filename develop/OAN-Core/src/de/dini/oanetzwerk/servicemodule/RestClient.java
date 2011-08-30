package de.dini.oanetzwerk.servicemodule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
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
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * The RestClient provides a simple connection to the REST server. All important configurations will be stored 
 * in the config file restclientprop.xml. For more informations about restclientprop.xml see the skeleton of this file.
 * For creating an instance of the RestClient use one of the static createRestClient methods.
 * To send data you can use either Get-, Post-, Put- or DeleteData or sendGet-, sendPost-, sendPut- or sendDeleteRestMessage.
 * The first ones don't encode or decode any data, the second ones encode and decode all requestest.
 * 
 * @author Michael K&uuml;hn
 */

public class RestClient {
	
	private static final String DEFAULT_HOST = "oanet.cms.hu-berlin.de";

	private static final String DEFAULT_REST_SERVLET_PATH = "rest/server";

	/**
	 * The path to the servlet within the servlet container. (Specified in the web.xml)
	 */
	
	private String servletPath = DEFAULT_REST_SERVLET_PATH;
	
	/**
	 * The servlet container's port.
	 */
	
	private int port;
	
	/**
	 * Usage of SSl or not (if true, SSL is NOT used)
	 */
	
	private boolean nossl;
	
	/**
	 * The server name (i.e. localhost, oanet.cms.hu-berlin.de, foo.bar.net)
	 */
	
	private String qualifiedServerName = "";
	
	/**
	 * The query which will be sent to the server. (i.e. Object/1/1/ )
	 */
	
	private String queryPath;
	
	/**
	 * The username! what else? This will be used for authenticating on the server.
	 */
	
	private final String username;
	
	/**
	 * This is the secret password (don't tell anybody!) Used together with the username for authentication.
	 */
	
	private final String password;
	
	/**
	 * If you like to, you can store all necessary things in a property file. 
	 */
	
	private Properties props = new Properties ( );
	
	/**
	 * This our static log4j logger.
	 */
	
	private static Logger logger = Logger.getLogger (RestClient.class);
	
	/**
	 * Creates a new client and initializes this client.
	 * The log4j is configured, the path is filtered and SSL, username and password are set.
	 * This Constructor is private due to our factory method which will call this constructor and return an
	 * instance of RestClient.
	 * @see RestClient#createRestClient(String path, String userName, String passWord)
	 * 
	 * @param path the path for the REST query
	 * @param user username for the authentication at the REST server
	 * @param pwd password for the authentication at the REST server
	 */
	
	private RestClient (String path, String user, String pwd) {
		
		this ("", path, user, pwd);
		
		if (this.props != null) {
			
			this.qualifiedServerName = new String (this.props.getProperty ("serverURL", "localhost"));
			
		} else {
			
			logger.warn ("no ServerURL found. Trying localhost!");
		}
	}
	
	/**
	 * Creates a new client and initializes this client.
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
		
		this.qualifiedServerName = filterurl (url);
		this.nossl = true;
		this.nossl = setSSL (url);
		this.queryPath = filterpath (path);
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
			
			if (this.props == null) {
				
				this.port = 443;
				
			} else {
				
				this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
				this.port = new Integer (this.props.getProperty ("SSLPort", "443"));
				System.setProperty ("javax.net.ssl.trustStore", this.props.getProperty ("trustStore"));
				System.setProperty ("javax.net.ssl.keyStorePassword", this.props.getProperty ("keystorepassword"));
			}
			
		} else {
			
			if (this.props == null) {
				
				this.port = 80;
				
			} else {
				
				this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
				this.port = new Integer (this.props.getProperty ("NonSSLPort", "80"));
			}
		}
	}
	
	/**
	 * @param propFile
	 * @param restQueryPath
	 * @param user
	 * @param pwd
	 */
	
	private RestClient (File propFile, String restQueryPath, String user, String pwd) {
		
		this.queryPath = filterpath (restQueryPath);
		this.username = user;
		this.password = pwd;
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile (propFile.getAbsolutePath ( ));
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Connections might be impossible");
			
		} catch (FileNotFoundException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Connections might be impossible");
			
		} catch (IOException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ), ex);
			logger.warn ("SSL-Connections might be impossible");
		}
		
		if (this.props == null) {
			
			this.port = 443;
			this.qualifiedServerName = DEFAULT_HOST;
			this.servletPath = "restserver/server";
			logger.warn ("No Property File found, trying default settings!");
			
		} else {
			
			this.nossl = setSSL (qualifiedServerName);
		}
		
		if (!this.nossl && this.props != null) {
			
			this.qualifiedServerName = new String (this.props.getProperty ("url", DEFAULT_HOST));
			this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
			this.port = new Integer (this.props.getProperty ("SSLPort", "443"));
			System.setProperty ("javax.net.ssl.trustStore", this.props.getProperty ("trustStore"));
			System.setProperty ("javax.net.ssl.keyStorePassword", this.props.getProperty ("keystorepassword"));
			
		} else if (this.props != null) {
			
			this.qualifiedServerName = new String (this.props.getProperty ("url", DEFAULT_HOST));
			this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
			this.port = new Integer (this.props.getProperty ("NonSSLPort", "80"));
		}
	}
	
	
	private RestClient (Properties restclientProps, String restQueryPath, String user, String pwd) {
		
		this.queryPath = filterpath (restQueryPath);
		this.username = user;
		this.password = pwd;
		
		this.props = restclientProps;

		if (this.props == null) {
			
			this.port = 443;
			this.qualifiedServerName = DEFAULT_HOST;
			this.servletPath = DEFAULT_REST_SERVLET_PATH;
			logger.warn ("No Property File found, trying default settings!");
			
		} else {
			
			this.nossl = setSSL (qualifiedServerName);
		}
		
		if (!this.nossl && this.props != null) {
			
			this.qualifiedServerName = new String (this.props.getProperty ("url", DEFAULT_HOST));
			this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
			this.port = new Integer (this.props.getProperty ("SSLPort", "443"));
			System.setProperty ("javax.net.ssl.trustStore", this.props.getProperty ("trustStore"));
			System.setProperty ("javax.net.ssl.keyStorePassword", this.props.getProperty ("keystorepassword"));
			
		} else if (this.props != null) {
			
			this.qualifiedServerName = new String (this.props.getProperty ("url", DEFAULT_HOST));
			this.servletPath = new String (this.props.getProperty ("servletPath", DEFAULT_REST_SERVLET_PATH));
			this.port = new Integer (this.props.getProperty ("NonSSLPort", "80"));
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
			
		} else {
			
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
	 * Creates a {@link RestClient} and calls the {@link RestClient} Constructor. 
	 * @see RestClient#RestClient(String url, String path, String user, String pwd)
	 * 
	 * @param path the path for the REST query
	 * @param userName
	 * @param passWord
	 * @return an instance of RestClient
	 */
	
	public static RestClient createRestClient (String path, String userName, String passWord) {
		
		RestClient restclient = new RestClient (path, userName, passWord);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("new restclient created");
		
		return restclient;
	}
	
	/**
	 * Creates a {@link RestClient} and calls the {@link RestClient} Constructor. 
	 * @see RestClient#RestClient(String url, String path, String user, String pwd)
	 * 
	 * @param restclientPropFile the Propertyfile for the restclient
	 * @param restQueryPath the path for the REST query
	 * @param userName
	 * @param passWord
	 * @return
	 */
	
	public static RestClient createRestClient (File restclientPropFile, String restQueryPath, String userName, String passWord) {
		
		RestClient restclient = new RestClient (restclientPropFile, restQueryPath, userName, passWord);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("new restclient created");
		
		return restclient;
	}
	
	
	public static RestClient createRestClient (Properties restclientProps, String restQueryPath, String userName, String passWord) {
		
		RestClient restclient = new RestClient (restclientProps, restQueryPath, userName, passWord);
		
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
				
				if (statusCode != HttpStatus.SC_OK) {
					
					if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
						
						logger.info ("HttpStatusCode: " + statusCode);
						logger.error (method.getStatusText ( ));
						logger.error ("Wrong username and/or password");
						
					} else {
						
						logger.info ("HttpStatusCode: " + statusCode);
						logger.error ("A http-error occured while processing the IDs from server " + this.qualifiedServerName);
						logger.error (method.getStatusText ( ));
						logger.error (method.getResponseBodyAsString ( ));
					}
					
					if (errorcounter++ >= 10) {
						
						logger.error ("We got a http-error more than 10 times during communication with server " + qualifiedServerName + " Now we are aborting communcation and trying to process the collected datas");
						method = null;
						
						return null;
						
					} else {
						
						logger.info (errorcounter + " errors occured. Server: " + this.qualifiedServerName);
						cont = true;
						continue;
					}
					
				} else {
					
					responseBody = method.getResponseBody ( );
					cont = false;
				}
				
			} while (cont);
		
		} catch (ConnectException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			if (method != null)
					method.releaseConnection ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Connection closed");
			
			if (responseBody == null) {
				
				logger.error ("we did not receive any data at all");
				responseBody = new byte [0];
			}
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
			
			newclient.getState ( ).setCredentials (new AuthScope (this.qualifiedServerName, this.port, AuthScope.ANY_REALM), defaultcreds);
			buffer.append ("http://");
			buffer.append (this.qualifiedServerName).append (":").append (this.port) .append ("/").append (servletPath) .append ("/") .append (queryPath);
			
		} else {
			
			newclient.getState ( ).setCredentials (new AuthScope (this.qualifiedServerName, this.port, AuthScope.ANY_REALM), defaultcreds);
			buffer.append ("https://");
			buffer.append (this.qualifiedServerName).append (":").append (this.port) .append ("/").append (servletPath) .append ("/") .append (queryPath);
		}

		
		
		// set content charset		
		newclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

		// set connection timeout
		newclient.setConnectionTimeout(900000); // set to 15 minutes
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("URL to connect to: " + this.qualifiedServerName);
		
		this.qualifiedServerName = buffer.toString ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Full Path to connect to: " + this.qualifiedServerName);
		
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
		GetMethod method = new GetMethod (this.qualifiedServerName);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRequest will be used");
		
		return this.sendrequest (client, method);
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
		PostMethod method = new PostMethod (this.qualifiedServerName);
		
		method.setRequestEntity (new StringRequestEntity (data, "text/plain", "UTF-8"));
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("postRequest will be used");
		
		return this.sendrequest (client, method);
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
		PutMethod method = new PutMethod (this.qualifiedServerName);
		
		method.setRequestEntity (new StringRequestEntity (data, "text/plain", "UTF-8"));
				
		if (logger.isDebugEnabled ( ))
			logger.debug ("putRequest will be used");
		
		return this.sendrequest (client, method);
	}
	
	/**
	 * Provides a {@link DeleteMethod} connection to the REST server.
	 * 
	 * @return the response received from the REST server
	 */
	
	public final String DeleteData ( ) {
		
		HttpClient client = prepareConnection ( );
		DeleteMethod method = new DeleteMethod (this.qualifiedServerName);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("deleteRequest will be used");
		
		return this.sendrequest (client, method);
	}
	
	/**
	 * Provides a {@link GetMethod} connection to the REST server which returns an already decoded REST message.
	 * 
	 * @return the decoded response received from the REST server
	 */
	
	public RestMessage sendGetRestMessage ( ) {
		
		String response = this.GetData ( );
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Response: " + response);
		}
				
		return RestXmlCodec.decodeRestMessage (response);
	}
	
	/**
	 * Provides a {@link PostMethod} connection to the REST server which returns an already decoded REST message.
	 * 
	 * @param msg the REST message which will be encoded and sent via POST to the REST server
	 * @return the decoded response received from the REST server
	 * @throws UnsupportedEncodingException
	 */
	
	public RestMessage sendPostRestMessage (RestMessage msg) throws UnsupportedEncodingException {
		
		String request = RestXmlCodec.encodeRestMessage (msg);
		String response = this.PostData (request);
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Request: " + request);
			logger.debug ("Response: " + response);
		}
		
		return RestXmlCodec.decodeRestMessage (response);
	}
	
	/**
	 * Provides a {@link PutMethod} connection to the REST server which returns an already decoded REST message.
	 * 
	 * @param msg the REST message which will be encoded and sent via PUT to the REST server
	 * @return the decoded response received from the REST server
	 * @throws UnsupportedEncodingException
	 */
	
	public RestMessage sendPutRestMessage (RestMessage msg) throws UnsupportedEncodingException {
		
		String request = RestXmlCodec.encodeRestMessage (msg);
		String response = this.PutData (request);
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Request: " + request);
			logger.debug ("Response: " + response);
		}
		
		return RestXmlCodec.decodeRestMessage (response);
	}
	
	/**
	 * Provides a {@link DeleteMethod} connection to the REST server which returns an already decoded REST message.
	 * 
	 * @return the decoded response received from the REST server
	 */
	
	public RestMessage sendDeleteRestMessage ( ) {
		
		String response = this.DeleteData ( );
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Response: " + response);
		}

		return RestXmlCodec.decodeRestMessage (response);
	}
}
