package de.dini.oanetzwerk.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import java.net.URLEncoder;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * This class wraps a HttpClient that connects to the search service. It offers convenient query-methods
 * that return lists of OIDs as result for search parameters of various formats. It will be used to as a
 * reusable module to integrate the search service into user frontends.
 * 
 * @author malitzro
 *
 */
public class SearchClient {

	private static Logger logger = Logger.getLogger (SearchClient.class);;
	private HttpClient myHttpClient = null;
	
	private String strSearchServiceBaseURL;
	private String strUsername;
	private String strPassword;
	
	public SearchClient(Properties props) {

		this.strSearchServiceBaseURL = props.getProperty("url");
		this.strUsername = props.getProperty("username");
		this.strPassword = props.getProperty("password");

	}
	
	/**
	 * Prepares the connection to the search server.
	 *   
	 * @return the initialised {@link HttpClient}
	 */
	
	private HttpClient getHttpClient() {
		
		HttpClient newclient = new HttpClient ( );
		
		newclient.getParams ( ).setAuthenticationPreemptive (true);
		Credentials defaultcreds = new UsernamePasswordCredentials (this.strUsername, this.strPassword);
		
		//newclient.getState ( ).setCredentials (new AuthScope (this.strSearchServiceBaseURL  + "?search=bar", this.strPort, "OA-Netzwerk"), defaultcreds);
		newclient.getState ( ).setCredentials (AuthScope.ANY, defaultcreds);
		
		//newclient.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		newclient.getParams ( ).setParameter ("http.protocol.content-charset", "iso-8859-1");
		//newclient.getParams ( ).setParameter ("http.authentication.credential-provider", "");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("URL to connect to: " + this.strSearchServiceBaseURL);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("connection prepared");
				
		return newclient;
	}
	
	public List<BigDecimal> querySearchService(String query) {
		
		byte[] baResponseBody = null;
		List<BigDecimal> listResultOIDs = new ArrayList<BigDecimal>();
		
		// lazy init of internal http client
		if(myHttpClient == null) myHttpClient = getHttpClient();		
		
		// TODO: prepare query
		String strCompleteURL = "http://" + this.strSearchServiceBaseURL + "?search=";
		try {
			if(query == null) query = "";
		    strCompleteURL += URLEncoder.encode(query,"UTF-8");
		    logger.debug ("complete URL = " + strCompleteURL);
		} catch (Exception ex) {
			logger.error("couldn't encode given query string '" + query + "' : " + ex);
			return listResultOIDs;
		}
		GetMethod method = new GetMethod(strCompleteURL);		
		method.setDoAuthentication( true );
		
		////////////////////////////////
		// communication part
		////////////////////////////////
		
		try {
			
			// send query
			int statusCode = myHttpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {

				if (statusCode == HttpStatus.SC_UNAUTHORIZED) {				
					logger.info ("HttpStatusCode: " + statusCode);
					logger.error (method.getStatusText());
					logger.error ("Wrong username and/or password");				
				} else {				
					logger.info ("HttpStatusCode: " + statusCode);
					logger.error ("A http-error occured while processing the IDs from server " + this.strSearchServiceBaseURL);
					logger.error (method.getStatusText());
				}

				// return empty result list
				return listResultOIDs;

			} else {			

				baResponseBody = method.getResponseBody();
				// break if result is empty
				if(baResponseBody == null) return listResultOIDs;

			}

		} catch (ConnectException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			return listResultOIDs;
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			return listResultOIDs;
			
		} finally {
			
			if (method != null)	method.releaseConnection();
			if (logger.isDebugEnabled()) logger.debug ("Connection closed");
			
		}
		
		/////////////////////////////////////////////
		// parse http result to create list of OIDs
		/////////////////////////////////////////////
		
		try {
			
			String strSearchResult = new String(baResponseBody, "UTF-8");	
			String[] lines = strSearchResult.split("\n");
			
			for(String line : lines) {				
				String[] items = line.split(";");
				String strOID = items[0]; // the first semicolon separated item should be the OID
				logger.info("splitted result line : " + Arrays.asList(items));
				try {
				    BigDecimal bdOID = new BigDecimal(strOID);
				    listResultOIDs.add(bdOID);
				} catch(Exception ex) {
					logger.warn("skipped oid '" + strOID + "' from search result : " + ex.getLocalizedMessage(), ex);
				}
			}
			
		} catch (UnsupportedEncodingException ueex) {
			
			logger.error ("error decoding response body : " + ueex.getLocalizedMessage ( ), ueex);
			return listResultOIDs;
			
		}
		
		return listResultOIDs;
	}
	
	
	
}
