/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
	 * @throws ParseException 
	 */
	
	@SuppressWarnings("static-access")
	public static void main (String [ ] args) throws ParseException {
		
		Options options = new Options ( );
		
		options.addOption ("h", false, "show help");
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("repositoryUrl")
										.withArgName ("URL")
										.withDescription ("URL of the repository which need to be harvested")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('u'));
		options.addOption (OptionBuilder.withLongOpt ("repositoryId")
										.withArgName ("ID")
										.withDescription ("Id for the repositoryURL see database for details")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('i'));
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("harvesttype")
										.withArgName ("full|update")
										.withDescription ("harvesting-type can be 'full' for a full harvest or 'update' for an updating harvest")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('t'));
		
		if (args.length > 0) {	
			
			CommandLine cmd = new GnuParser ( ).parse (options, args);
			
			if (cmd.hasOption ("h")) 				
				HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
				
			else if ((cmd.hasOption ("repositoryUrl") || cmd.hasOption ('u')) && 
					(cmd.hasOption ("repositoryId") || cmd.hasOption ('i')) &&
					(cmd.hasOption ("harvesttype") || cmd.hasOption ('t'))) {
				
				String url;
				int id;
				boolean fullharvest;
				
				if ((url = filterUrl (cmd.getOptionValue ('u'))) == null)
					url = filterUrl (cmd.getOptionValue ("repositoryUrl"));
								
				if (cmd.getOptionValue ('i') == null)
					id = filterId (cmd.getOptionValue ("repositoryId"));
				
				else
					id = filterId (cmd.getOptionValue ('i'));
				
				if (cmd.getOptionValue ('t') == null)
					fullharvest = filterBool (cmd.getOptionValue ("harvesttype"));
				
				else
					fullharvest = filterBool (cmd.getOptionValue ('t'));
				
				Harvester harvester = new Harvester ( );
				
				harvester.processIds (fullharvest);
				harvester.processRecords (url, id);
				
			} else
				HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
			
		} else
			HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
	}

	/**
	 * @param optionValue
	 * @return
	 */
	private static boolean filterBool (String optionValue) {

		if (optionValue.equalsIgnoreCase ("full"))
			return true;
		else
			return false;
	}

	/**
	 * @param optionValue
	 * @return
	 */
	
	private static int filterId (String optionValue) {
		
		int i = new Integer (optionValue);
		
		return i;
	}

	/**
	 * @param optionValue
	 */
	
	private static String filterUrl (String optionValue) {
		
		return optionValue;
	}

	/**
	 * @param id 
	 * @param url2 
	 * 
	 */
	
	private void processRecords (String url, int id) {
		
		HttpClient client;
		GetMethod method;
		
		//String url = "http://edoc.hu-berlin.de/OAI-2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=";
		
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
	 * @param fullharvest 
	 * 
	 */
	
	private void processIds (boolean fullharvest ) {
		
		ids = new ArrayList <String> ( );
		
		//TODO: implement ID-processing + id-überprüfung (entscheidung zwischen put und post request)
		
		ids.add ("oai:HUBerlin.de:10068");
		ids.add ("oai:HUBerlin.de:10018");
	}
}
