/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;


/**
 * @author Michael Kühn
 *
 */

public class RestClient {
	
	private boolean nossl;
	private String url;
	private String path;

	private RestClient (String url, String path) {
		
		this.url = filterurl (url);
		this.nossl = setSSL (url);
		this.path = filterpath (path);
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
		
		if (url.equalsIgnoreCase ("localhost") || url.equalsIgnoreCase ("127.0.0.1"))
			return false;
		
		else
			return true;
	}

	/**
	 * @param url
	 * @return
	 */
	
	private String filterurl (String url) {

		String filteredUrl = url;
		
		return filteredUrl;
	}

	public static RestClient createRestClient (String incomming_url, String path) {
		
		RestClient restclient = new RestClient (incomming_url, path);
		
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
			
			if (statusCode != HttpStatus.SC_OK)
				;//meckern
			
			responseBody = method.getResponseBody ( );
			
		} catch (IOException ioex) {
			
			ioex.printStackTrace ( );
			
		} finally {
			
			method.releaseConnection ( );
		}
		
		return new String (responseBody);
	}

	/**
	 * @return
	 */
	
	private HttpClient prepareConnection ( ) {
		
		HttpClient newclient = new HttpClient ( );
		StringBuffer buffer = new StringBuffer ("");
		
		if (this.nossl) {
			
			buffer.append ("http://");
			
		} else {
			
			buffer.append ("https://");
		}
		
		buffer.append (this.url).append (":8080/OA-Netzwerk/").append (path);
		this.url = buffer.toString ( );
		
		return newclient;
	}

	public final String GetData ( ) {
		
		HttpClient client = prepareConnection ( );
		GetMethod method = new GetMethod (this.url);
		
		return sendrequest (client, method);
	}
	
	public final String PostData (String data) {
		
		
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
		
		return sendrequest (client, method);
	}
	
	public final String DeleteData ( ) {
		
		
		return "";
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
