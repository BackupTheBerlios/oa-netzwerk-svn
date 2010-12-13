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
	
	public List<BigDecimal> querySearchService(String query) throws SearchClientException {
		return querySearchService(query, null);
	}
	
    public List<BigDecimal> querySearchService(String strQuery, String strDDC) throws SearchClientException {
		
		byte[] baResponseBody = null;
		List<BigDecimal> listResultOIDs = new ArrayList<BigDecimal>();
		
		// lazy init of internal http client
		if(myHttpClient == null) myHttpClient = getHttpClient();		
		
		String strCompleteURL = "http://" + this.strSearchServiceBaseURL + "?search=";
		try {
			if(strQuery == null) strQuery = "";
		    strCompleteURL += URLEncoder.encode(strQuery,"UTF-8");
		    logger.debug ("strQuery = '" + strQuery + "'");
		    if(strDDC != null && strDDC.length() > 0) {
		    	strCompleteURL += "&ddc=" + URLEncoder.encode(strDDC,"UTF-8");
			    logger.debug ("strDDC = '" + strDDC + "'");
		    }
		    logger.debug ("complete URL = " + strCompleteURL);
		} catch (Exception ex) {
			logger.error("couldn't encode given query string '" + strQuery + "' : " + ex);
			//return listResultOIDs;
			throw new SearchClientException("Couldn't encode given query string.");
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
					throw new SearchClientException("Wrong username and/or password while querying search service.");
				} else {				
					logger.info ("HttpStatusCode: " + statusCode);
					logger.error ("A http-error occured while processing the IDs from server " + this.strSearchServiceBaseURL);
					logger.error (method.getStatusText());
					throw new SearchClientException("A http-error occured while querying search service.");
				}

				// return empty result list
				//return listResultOIDs;

			} else {			

				baResponseBody = method.getResponseBody();
				// break if result is empty
				if(baResponseBody == null) return listResultOIDs;

			}

		} catch (ConnectException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			//return listResultOIDs;
			throw new SearchClientException("An ConnectException occured while querying search service.");

			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			//return listResultOIDs;
			throw new SearchClientException("An IOException occured while querying search service.");
			
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
			//return listResultOIDs;
			throw new SearchClientException("An error decoding response body occured while querying search service.");

		}
		
		return listResultOIDs;
	}
    

    public List<BigDecimal> querySearchService2(String strQuery, String strDDC) throws SearchClientException
    {
      byte[] baResponseBody = null;
      List<BigDecimal> listResultOIDs = new ArrayList<BigDecimal>();

      if (this.myHttpClient == null) this.myHttpClient = getHttpClient();

      String strCompleteURL = "http://134.106.31.87/search/cgi-bin/metasearch.cgi?search=";
      try {
        if (strQuery == null) strQuery = "";
        strCompleteURL = strCompleteURL + URLEncoder.encode(strQuery, "UTF-8");
        logger.debug("strQuery = '" + strQuery + "'");
        if ((strDDC != null) && (strDDC.length() > 0)) {
          strCompleteURL = strCompleteURL + "&ddc=" + URLEncoder.encode(strDDC, "UTF-8");
          logger.debug("strDDC = '" + strDDC + "'");
        }
        logger.debug("complete URL = " + strCompleteURL);
      } catch (Exception ex) {
        logger.error("couldn't encode given query string '" + strQuery + "' : " + ex);

        throw new SearchClientException("Couldn't encode given query string.");
      }
      GetMethod method = new GetMethod(strCompleteURL);
      method.setDoAuthentication(true);
      try
      {
        int statusCode = this.myHttpClient.executeMethod(method);

        if (statusCode != 200)
        {
          if (statusCode == 401) {
            logger.info("HttpStatusCode: " + statusCode);
            logger.error(method.getStatusText());
            logger.error("Wrong username and/or password");
            throw new SearchClientException("Wrong username and/or password while querying search service.");
          }
          logger.info("HttpStatusCode: " + statusCode);
          logger.error("A http-error occured while processing the IDs from server " + this.strSearchServiceBaseURL);
          logger.error(method.getStatusText());
          throw new SearchClientException("A http-error occured while querying search service.");
        }

        baResponseBody = method.getResponseBody();

        if (baResponseBody == null) { List<BigDecimal> localList1 = listResultOIDs;
          return localList1; }

      }
      catch (ConnectException ex)
      {
      }
      catch (IOException ex)
      {
      }
      finally
      {
        if (method != null) method.releaseConnection();
        if (logger.isDebugEnabled()) logger.debug("Connection closed");

      }

      try
      {
        String strSearchResult = new String(baResponseBody, "UTF-8");
        String[] lines = strSearchResult.split("\n");

        for (String line : lines) {
          String[] items = line.split(";");
          String strOID = items[0];
          logger.info("splitted result line : " + Arrays.asList(items));
          try {
            BigDecimal bdOID = new BigDecimal(strOID);
            listResultOIDs.add(bdOID);
          } catch (Exception ex) {
            logger.warn("skipped oid '" + strOID + "' from search result : " + ex.getLocalizedMessage(), ex);
          }
        }
      }
      catch (UnsupportedEncodingException ueex)
      {
        logger.error("error decoding response body : " + ueex.getLocalizedMessage(), ueex);

        throw new SearchClientException("An error decoding response body occured while querying search service.");
      }

      return listResultOIDs;
    }
	
}
