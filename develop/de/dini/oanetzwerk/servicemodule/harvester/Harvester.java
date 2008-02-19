/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.RestXmlCodec;

/**
 * @author Michael Kühn
 * 
 * The Harvester consists of two parts: the Harvester itself and the Object Manager. The Harvester
 * makes a connection to a given repository where metadata-objects can be accessed.
 * The Object Manager handles the harvested meta-data objects and ensures the safe storage of these objects
 * in a database if necessary.
 */

public class Harvester {
	
	/**
	 * This is an ArrayList of ObjectIdentifiers where necessary information about the objects is stored @see de.dini.oanetzwerk.ObjectIdentifier
	 */
	
	private ArrayList <ObjectIdentifier> ids = null;
	
	/**
	 * The static log4j logger. All logging will be made with this nice static logger.
	 */
	
	static Logger logger = Logger.getLogger (Harvester.class);
	
	/**
	 * 
	 */
	
	private int recordno = 0;
	
	/**
	 * Properties. 
	 */
	
	private Properties props = null;

	/**
	 * 
	 */
	
	private int errorretry = 0;
	
	/**
	 * 
	 */
	
	private String metaDataFormat ="oai_dc";
	
	/**
	 * 
	 */
	
	private String repositoryURL;
	
	/**
	 * 
	 */
	
	private int repositoryID;
		
	/**
	 * Standard Constructor which initialises the log4j and loads necessary properties.
	 */
	
	public Harvester (String url, int id) {
		
		DOMConfigurator.configure ("log4j.xml");
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile ("harvesterprop.xml");
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		this.repositoryURL = url;
		this.repositoryID = id;
	}
	
	
	/**
	 * @return the repositoryURL
	 */
	
	private final String getRepositoryURL ( ) {
	
		return this.repositoryURL;
	}
	
	/**
	 * @return the repositoryID
	 */
	
	private final int getRepositoryID ( ) {
	
		return this.repositoryID;
	}

	/**
	 * The date which comes from the command line will be filtered and observed.
	 * 
	 * @param optionValue the date string which is filtered
	 * @return the proper date string when it could be extracted null otherwise
	 */
	
	private static String filterDate (String optionValue) {
		
		if (optionValue.matches ("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			
			String [ ] today = HelperMethods.today ( ).toString ( ).split ("-");
			String [ ] dateComponents = optionValue.split ("-");
			
			if ( (new Integer (dateComponents [0]) < 1970) || (new Integer (dateComponents [0]) > new Integer (today [0]))) {
				
				logger.error ("Year must be between 1970 and " + today [0] + ".\nYou asked for the year " + dateComponents [0]);
				System.err.println ("Year must be between 1970 and " + today [0] + ".\nYou asked for the year " + dateComponents [0]);
				
				System.exit (10);
				
			} else if ( (new Integer (dateComponents [1]) < 1) || (new Integer (dateComponents [1]) > 12)) {
				
				logger.error ("The year has only 12 months. You asked for month number " + dateComponents [1]);
				System.err.println ("The year has only 12 months. You asked for month number " + dateComponents [1]);
				
				System.exit (10);
				
			} else if ( (new Integer (dateComponents [2]) < 1) || (new Integer (dateComponents [2]) > 31)) {
				
				logger.error ("Months have the max of 31, 30, 29 or 28 days. You asked for day number " + dateComponents [2]);
				System.err.println ("Months have the max of 31, 30, 29 or 28 days. You asked for day number " + dateComponents [2]);
				
				System.exit (10);
				
			} else {
				
				java.sql.Date date2test = null;
				
				try {
					
					date2test = HelperMethods.extract_datestamp (optionValue);
					
				} catch (java.text.ParseException ex) {
					
					ex.printStackTrace ( );
				}
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("filtered Date: " + date2test.toString ( ));
				
				return date2test.toString ( );
			}
			
		} else {
			
			logger.error ("Date Format wrong! Must be yyyy-mm-dd and not " + optionValue);
			System.err.println ("Date Format wrong! Must be yyyy-mm-dd and not " + optionValue);
			
			System.exit (10);
		}
		
		return null;
	}

	/**
	 * The option whether a full or only an update harvest will be processed will be discovered.
	 * If update is specified the date from which on has to be set, otherwise an error occurs.
	 * 
	 * @param optionValue should be "full" for a full-harvest or "update".
	 * @param cmd should has option "updateDate" if "update" is set
	 * @return true when full harvest is discovered false when update is discovered
	 * @throws ParseException when update is specified and no date is set
	 */
	
	private static boolean filterBool (String optionValue, CommandLine cmd) throws ParseException {
		
		if (optionValue.equalsIgnoreCase ("full")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("filteredBool: " + Boolean.TRUE);
			
			return true;
			
		} else {
			
			if (cmd.hasOption ('d') || cmd.hasOption ("updateDate")) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("filteredBool: " + Boolean.FALSE);
				
				return false;
				
			} else
				throw new ParseException ("UpdateHarvest without a Date from which on the harvest shall be start isn't allowed!");
		}
	}
	
	/**
	 * ensures correct values for the repository ID.
	 * 
	 * @param optionValue a string representing a number greater or equal zero.
	 * @return an integer which represents the repository ID
	 * @throws ParseException when the repository ID is negative
	 */
	
	private static int filterId (String optionValue) throws ParseException {
		
		int i = new Integer (optionValue);
		
		if (i < 0)
			throw new ParseException ("RepositoryID must not be negative!");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered ID: " + i);
		
		return i;
	}

	/**
	 * ensures that the given string is a correct url
	 * @param optionValue the rpository-url
	 * @return filtered url
	 */
	
	private static String filterUrl (String optionValue) {
		
		//TODO: proper implementation
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered URL: " + optionValue);
		
		return optionValue;
	}
	
	/**
	 * @param url 
	 * @param fullharvest 
	 * @param updateFrom 
	 * @param repositoryId 
	 */
	
	private void processIds (boolean fullharvest, String updateFrom) {
		
		String resumptionToken = null;
		Boolean resumptionSet = true;
		InputStream response = null;
		
		try {
			
			response = listMetaDataFormats (getRepositoryURL ( ));
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("metaDataFormats found");
			
			response = null;
			 
			if (!fullharvest)
				response = listIdentifiers (getRepositoryURL ( ), "oai_dc", updateFrom);
			
			else
				response = listIdentifiers (getRepositoryURL ( ), "oai_dc");
			
			while (resumptionSet) {
				
				resumptionToken = extractIdsAndGetResumptionToken (response, getRepositoryID ( ));
				
				if (resumptionToken != null) {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("ResumptionToken found: " + resumptionToken);
					else;
					
					if (resumptionToken.equals ("")) {
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken empty, IdentifierList complete");
						else;
						
						resumptionSet = false;
						
					} else {
												
						listIdentifiers (getRepositoryURL ( ), "resumptionToken", resumptionToken);
						
						if (logger.isDebugEnabled ( )) {
							
							logger.debug ("ResumptionToken: " + resumptionToken);
							
						} else;
					}
					
					resumptionToken = null;
					
				} else {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("no ResumptionToken found, IdentifierList complete");
					
					else;
					
					resumptionSet = false;
				}
			} 
		
		} catch (HttpException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.errorretry++;
			
			if (errorretry > 5) {
				
				resumptionToken = null;
				System.exit (5);
			}
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			this.errorretry++;
			
			if (errorretry > 5)
				System.exit (5);
		}
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream listMetaDataFormats (String url) throws HttpException, IOException {
		
		return repositoryAnswer (url, "listMetaDataFormat", "");
	}

	/**
	 * @param url
	 * @param string
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream listIdentifiers (String url, String metaDataFormat) throws HttpException, IOException {
		
		return repositoryAnswer (url, "ListIdentifiers", "&metadataPrefix=" + metaDataFormat);
	}

	/**
	 * @param url
	 * @param string
	 * @param updateFrom
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream listIdentifiers (String url, String metaDataFormat, String updateFrom) throws HttpException, IOException {
		
		if (metaDataFormat.equalsIgnoreCase ("resumptionToken")) {
			
			String resumptionToken = updateFrom;
			
			return repositoryAnswer (url, "ListIdentifiers", "&resumptionToken=" + resumptionToken);
			
		} else {
			
			return repositoryAnswer (url, "ListIdentifiers", "&metadataPrefix=" + metaDataFormat + "&from=" + updateFrom);	
		}
	}

	/**
	 * @param url
	 * @param string
	 * @param string2
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream repositoryAnswer (String url, String verb,
			String parameter) throws HttpException, IOException {
		
		int statuscode = 0;
		HttpClient client = new HttpClient ( );
		GetMethod getmethod = new GetMethod (url + "?verb=" + verb + parameter);
		
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		if (logger.isDebugEnabled ( ))
			logger.debug (url + getmethod.getQueryString ( ));
		
		statuscode = client.executeMethod (getmethod);
		
		logger.info ("HttpStatusCode: " + statuscode);
		
		if (statuscode != HttpStatus.SC_OK) {
			
		}
		
		client = null;
		return getmethod.getResponseBodyAsStream ( );
	}

	/**
	 * @param responseBody
	 * @param repositoryId
	 * @return
	 */
	
	private String extractIdsAndGetResumptionToken (InputStream responseBody, int repositoryId) {
		
		String resumptionToken = null; 
		
		try {
			
			//TODO: xml-handling has to be rewritten!
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (responseBody);
			
			NodeList idNodeList = document.getElementsByTagName ("identifier");
			NodeList datestampNodeList = document.getElementsByTagName ("datestamp");
			
			if (this.ids == null)
				this.ids = new ArrayList <ObjectIdentifier> ( );
			
			if (logger.isDebugEnabled ( )) {
				
				logger.debug ("we have " + idNodeList.getLength ( ) + " Ids to extract");
				logger.debug ("we have " + datestampNodeList.getLength ( ) + " Datestamps to extract");
			}
			
			for (int i = 0; i < idNodeList.getLength ( ); i++) {
				
				int internalOID;
				String externalOID = idNodeList.item (i).getTextContent ( );
				String datestamp = datestampNodeList.item (i).getTextContent ( );
				
				if (logger.isDebugEnabled ( )) {
					
					logger.debug ("List Record No. " + recordno++ + " " + externalOID  + " " + datestamp);
				}
				
				internalOID = objectexists (repositoryId, externalOID);
				
				if (internalOID > 0) {
					
					// we found this object in the database
					
					if (logger.isDebugEnabled ( ))
						logger.debug (externalOID + " is " + internalOID + " in our database");
					
					ids.add (new ObjectIdentifier (externalOID, datestamp, internalOID));
					
				} else if (internalOID == -1) {
					
					// we found no object in the database
					
					if (logger.isDebugEnabled ( ))
						logger.debug (externalOID + " is not in the database. so we have to create a new object");
					
					ids.add (new ObjectIdentifier (externalOID, datestamp, -1));
					
				} else {
					
					logger.error ("Error with number " + internalOID + "occured while processing Object " + externalOID);
					logger.info ("Skipping object " + externalOID + " and continue with the next one");
				}
			}
			
			NodeList rsList = document.getElementsByTagName ("resumptionToken");
			
			if (rsList.getLength ( ) > 0)
				resumptionToken = rsList.item (0).getTextContent ( );
			
			rsList = null;
			factory = null;
			builder = null;
			document = null;
			
			idNodeList = null;
			datestampNodeList = null;
			
		} catch (ParserConfigurationException pacoex) {
			
			logger.error (pacoex.getLocalizedMessage ( ));
			pacoex.printStackTrace ( );
			
		} catch (SAXException sex) {
			
			logger.error (sex.getLocalizedMessage ( ));
			sex.printStackTrace ( );
			
		} catch (IOException ioex) {
			
			logger.error (ioex.getLocalizedMessage ( ));
			ioex.printStackTrace ( );
		}
		
		return resumptionToken;
	}

	/**
	 * @param url
	 * @param repositoryID 
	 */
	
	private void processRecords ( ) {
		
		HttpClient client = null;
		GetMethod method = null;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("now we process " + this.ids.size ( ) + "Records");
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Object No. " + i + " is processed"); 
			
			if (this.ids.get (i).getInternalOID ( ) == -1) {
				
				// when internalOID == -1 than Object is not in the database and we have to create it
				createObjectEntry (i);
				
			} else {
				
				// Object exists and we have to look if our Rawdata is newer than the database one
				if (checkRawdata (i) == true) {
					
					//updatedharvestedDate ( );
					
				} else {
					
					//updateRawdata ( );
				}
					
				
			} //endelse
			
			client = new HttpClient ( );
			method = new GetMethod (getRepositoryURL ( ) + "?verb=GetRecord&metadataPrefix=" + metaDataFormat + "&identifier=" + ids.get (i).getExternalOID ( ));
			client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
			
			try {
				
				int statuscode = client.executeMethod (method);
				
				logger.info ("HttpStatusCode: " + statuscode);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					;
				}
				
				uploadRawData (HelperMethods.stream2String (method.getResponseBodyAsStream ( )), ids.get (i).getInternalOID ( ), ids.get (i).getDatestamp ( ), metaDataFormat);
				
			} catch (HttpException ex) {
				
				ex.printStackTrace ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
								
			} finally {
				
				method.releaseConnection ( );
			}
			
			method = null;
			client = null;
		}
	}

	/**
	 * 
	 */
	
	private void createObjectEntry (int index) {

		String ressource = "ObjectEntry/";
		RestClient restClient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (this.ids.get (index).getDatestamp ( ));
		
		String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
		
		cal = null;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("creating Object No. " + index + ": " + this.ids.get (index).getExternalOID ( ));
		
		try {
			
			List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
			HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
			mapEntry.put ("repository_id", Integer.toString (getRepositoryID ( )));
			mapEntry.put ("repository_identifier", this.ids.get (index).getExternalOID ( ));
			mapEntry.put ("repository_datestamp", datestamp);
			listentries.add (mapEntry);
			
			String requestxml = RestXmlCodec.encodeEntrySetRequestBody (listentries);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("xml: " + requestxml);
			
			String result = restClient.PutData (requestxml);
			
			listentries = null;
			mapEntry = null;
			restClient = null;
			
			listentries = RestXmlCodec.decodeEntrySet (result);
			mapEntry = listentries.get (0);
			Iterator <String> it = mapEntry.keySet ( ).iterator ( );
			String key = "";
			String value = "";
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (key.equalsIgnoreCase ("oid")) {
					
					value = mapEntry.get (key);
					break;
				}
			}
			
			int intoid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("internalOID: " + intoid);
			
			this.ids.get (index).setInternalOID (intoid);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * @param i
	 */
	
	private boolean checkRawdata (int i) {
		
		//first we need the datastamp from the database
		if (logger.isDebugEnabled ( ))
			logger.debug ("observing Object No. " + i + ": " + this.ids.get (i).getExternalOID ( ));
		
		String ressource = "ObjectEntry/" + this.ids.get (i).getInternalOID ( ) + "/";
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		restclient = null;
		
		listentries = RestXmlCodec.decodeEntrySet (result);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		String value = "";
		
		while (it.hasNext ( )) {
							
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + mapEntry.get (key));
			
			if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				value = mapEntry.get (key);
				break;
			}
		}
		
		try {
			
			Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (value);	
			
			if (repositoryDate.before (this.ids.get (i).getDatestamp ( ))) {// || repositoryDate.equals (this.ids.get (i).getDatestamp ( ))) {
				
				if (logger.isDebugEnabled ( )) {
					
					logger.debug ("RepositoryDate is " + repositoryDate + " and before the harvested: " + this.ids.get (i).getDatestamp ( ));
				}
				
				/*listentries = new ArrayList <HashMap <String, String>> ( );
				mapEntry = new HashMap <String ,String> ( );
				
				GregorianCalendar cal = new GregorianCalendar ( );
				cal.setTime (this.ids.get (i).getDatestamp ( ));
				
				String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
				
				mapEntry.put ("repository_id", Integer.toString (getRepositoryID ( )));
				mapEntry.put ("repository_identifier", this.ids.get (i).getExternalOID ( ));
				mapEntry.put ("repository_datestamp", datestamp);
				listentries.add (mapEntry);
				
				String requestxml = RestXmlCodec.encodeEntrySetRequestBody (listentries);
				
				restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
				
				result = restclient.PostData (requestxml);
				
				listentries = null;
				mapEntry = null;
				restclient = null;
				
				listentries = RestXmlCodec.decodeEntrySet (result);
				mapEntry = listentries.get (0);
				it = mapEntry.keySet ( ).iterator ( );
				key = "";
				value = "";
				
				while (it.hasNext ( )) {
					
					key = it.next ( );
					
					if (key.equalsIgnoreCase ("oid")) {
						
						value = mapEntry.get (key);
						break;
					}
				}
				
				int intoid = new Integer (value);
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("internalOID: " + intoid);*/
				
				return true;
				
			} else {
				
				return false;
				// updated harvesteddatstamp and exit
			}
			
		} catch (java.text.ParseException ex) {

			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			return true;
		}		
	}

	/**
	 * @param data
	 * @param internalOID
	 * @param datestamp
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private void uploadRawData (String data, int internalOID, Date datestamp, String metaDataFormat) throws HttpException, IOException {
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (datestamp);
		
		String resource = "RawRecordData/" + internalOID + "/" + cal.get (Calendar.YEAR) + "-" + cal.get (Calendar.MONTH + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH) + "/" + metaDataFormat + "/";
		
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		restclient.PutData (new String (Base64.encodeBase64 (data.getBytes ("UTF-8"))));
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("uploaded rawdata for Database Object " + internalOID);
	}
	

	/**
	 * @param oid
	 * @param datestamp 
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	private int objectexists (int repositoryId, String externalOID) throws ParserConfigurationException, SAXException, IOException {
		
		String ressource = "ObjectEntryID/" + repositoryId + "/" + externalOID + "/";
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (result);
				
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );

		listentries = RestXmlCodec.decodeEntrySet (result);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		String value = "";
		int intoid = -1;
		
		while (it.hasNext ( )) {
							
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + mapEntry.get (key));
			
			if (key.equalsIgnoreCase ("oid")) {
				
				value = mapEntry.get (key);
				break;
			}
		}
		
		if (value == null)
			intoid = -1;
		
		else
			intoid = new Integer (value);
		
		return intoid;
	}
	
	/**
	 * Main class which have to be called.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	
	@SuppressWarnings("static-access")
	public static void main (String [ ] args) {
		
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
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("updateDate")
										.withArgName ("yyyy-mm-dd")
										.withDescription ("Date from which on the Records are harvested, when update-harvest-type is specified")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('d'));
		
		if (args.length > 0) {	
			
			try {
				
				CommandLine cmd = new GnuParser ( ).parse (options, args);
				
				if (cmd.hasOption ("h")) 				
					HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
					
				else if ((cmd.hasOption ("repositoryUrl") || cmd.hasOption ('u')) && 
						(cmd.hasOption ("repositoryId") || cmd.hasOption ('i')) &&
						(cmd.hasOption ("harvesttype") || cmd.hasOption ('t'))) {
					
					String url;
					int id;
					boolean fullharvest;
					String updateDate = "";
					
					if (cmd.getOptionValue ('t') == null)
						fullharvest = filterBool (cmd.getOptionValue ("harvesttype"), cmd);
					
					else
						fullharvest = filterBool (cmd.getOptionValue ('t'), cmd);
					
					if (!fullharvest) {
						
						if (cmd.getOptionValue ('d') == null)
							updateDate = filterDate (cmd.getOptionValue ("updateDate"));
						
						else
							updateDate = filterDate (cmd.getOptionValue ('d'));
					}
					
					if (cmd.getOptionValue ('u') == null)
						url = filterUrl (cmd.getOptionValue ("repositoryUrl"));
					
					else
						url = filterUrl (cmd.getOptionValue ('u'));
									
					if (cmd.getOptionValue ('i') == null)
						id = filterId (cmd.getOptionValue ("repositoryId"));
					
					else
						id = filterId (cmd.getOptionValue ('i'));
					
					// Here we go: create a new instance of the harvester
					
					Harvester harvester = new Harvester (url, id);
					
					/* 
					 * firstly we have to collect some data from the repository, which have to be processed
					 * when we finished processing all these data, we have the IDs form all records we have
					 * to collect. This is the next step: we catch the records for all the IDs and put the
					 * raw datas into the database, if they don't exist or newer than in the database.
					 */
					
					harvester.processIds (fullharvest, updateDate);
					harvester.processRecords ( );
					
			} else {
				
				HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
				System.exit (1);
			}
				
		} catch (ParseException parex) {
			
			logger.error (parex.getMessage ( ));
			System.out.println (parex.getMessage ( ));
			HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
			System.exit (1);
		}
			
		} else {
			
			HelperMethods.printhelp ("java " + Harvester.class.getCanonicalName ( ), options);
			System.exit (1);
		}
	}
}

/**
 * @author Michael Kühn
 *
 */

class ObjectIdentifier {
	
	private String externalOID;
	private Date datestamp;
	private int internalOID;
	
	/**
	 * @param externalOID
	 * @param datestamp
	 */
	
	public ObjectIdentifier (String externalOID, String datestamp, int internalOID) {
		
		try {
			
			this.externalOID = externalOID;
			this.datestamp = new SimpleDateFormat ("yyyy-MM-dd").parse (datestamp);
			this.internalOID = internalOID;
			
		} catch (java.text.ParseException ex) {
			
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * @return the id
	 */
	
	final String getExternalOID ( ) {
	
		return this.externalOID;
	}

	/**
	 * @param id the id to set
	 */
	
	final void setExternalOID (String id) {
	
		this.externalOID = id;
	}

	/**
	 * @return the datestamp
	 */
	
	final Date getDatestamp ( ) {
		
		return this.datestamp;
	}
	
	/**
	 * @param datestamp the datestamp to set
	 */
	
	final void setDatestamp (Date datestamp) {
	
		this.datestamp = datestamp;
	}

	
	/**
	 * @return the internalOID
	 */
	final int getInternalOID ( ) {
	
		return this.internalOID;
	}

	
	/**
	 * @param internalOID the internalOID to set
	 */
	final void setInternalOID (int internalOID) {
	
		this.internalOID = internalOID;
	}
}
