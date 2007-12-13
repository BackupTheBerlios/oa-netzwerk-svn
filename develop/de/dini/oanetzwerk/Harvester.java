/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael Kühn
 *
 */

public class Harvester {
	
	private List <String> ids;
	static Logger logger = Logger.getLogger (Harvester.class);
	private Properties props;
	
	public Harvester ( ) {
		
		DOMConfigurator.configure ("log4j.xml");
		this.props = HelperMethods.loadPropertiesFromFile ("/home/mkuehn/workspace/oa-netzwerk-develop/harvesterprop.xml");
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {
		
		// Parameter: repository_id, full/update-harvest
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
		
		//TODO: Urls have to be put into a config file
		String url = "http://edoc.hu-berlin.de/OAI-2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=";
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			client = new HttpClient ( );
			method = new GetMethod (url + ids.get (i));
			client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
			
			try {
				
				int statuscode = client.executeMethod (method);
				
				logger.info ("HttpStatusCode: " + statuscode);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					;
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
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private void deliverResult2DB (String data, String header_identifier) throws HttpException, IOException {
		
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), header_identifier, this.props.getProperty ("username"), this.props.getProperty ("password"));
		restclient.PutData (data);
	}

	/**
	 * 
	 */
	
	private void processIds ( ) {
		
		ids = new ArrayList <String> ( );
		
		//TODO: implement ID-processing + id-überprüfung (entscheidung zwischen put und post request)
		
		ids.add ("oai:HUBerlin.de:10068");
		ids.add ("oai:HUBerlin.de:10018");
	}
}
