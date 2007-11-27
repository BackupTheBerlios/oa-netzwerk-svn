/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;


/**
 * @author Michael Kühn
 *
 */

public class Harvester {
	
	private List <String> ids;
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {
		
		Harvester harvester = new Harvester ( );
		
		harvester.processIds ( );
		harvester.processRecords ( );	
	}

	/**
	 * 
	 */
	
	private void processRecords ( ) {
		
		HttpClient client;
		GetMethod method;
		String url = "http://edoc.hu-berlin.de/OAI-2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=";
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			client = new HttpClient ( );
			method = new GetMethod (url + ids.get (i));
			
			try {
				
				int statuscode = client.executeMethod (method);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					//meckern
				}
								
				deliverResult2DB (HelperMethods.stream2String (method.getResponseBodyAsStream ( )), ids.get (i));
				
			} catch (HttpException ex) {
				
				ex.printStackTrace ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
				
			} finally {
				
				method.releaseConnection ( );
			}
		}
		
		client = null;
	}

	/**
	 * @param data
	 * @param string
	 */
	
	private void deliverResult2DB (String data, String id) {
		
		RestClient restclient = RestClient.createRestClient ("localhost", id, "", "");
		restclient.PutData (data);
	}

	/**
	 * 
	 */
	
	private void processIds ( ) {
		
		ids = new ArrayList <String> ( );
		
		ids.add ("oai:HUBerlin.de:10068");
		ids.add ("oai:HUBerlin.de:10018");
	}
}
