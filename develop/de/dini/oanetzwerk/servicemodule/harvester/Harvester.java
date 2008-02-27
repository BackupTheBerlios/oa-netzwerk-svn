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
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;

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
	 * Here we store the harvested MetaDataFormat.
	 */
	
	private String metaDataFormat ="oai_dc";
	
	/**
	 * This is the Base-URL of the repository we have to connect to.
	 */
	
	private String repositoryURL;
	
	/**
	 * This is the corresponding ID to the repository we'll connect to.
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
			System.exit (1);
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			System.exit (1);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			System.exit (1);
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
					
					logger.error ("Can't extract Datestamp from parameter 'updateDate'");
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
					System.exit (10);
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
	 * ensures that the given string is a correct URL
	 * @param optionValue the repository URL
	 * @return filtered URL
	 */
	
	private static String filterUrl (String optionValue) {
		
		//TODO: proper implementation
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered URL: " + optionValue);
		
		return optionValue;
	}
	
	/**
	 * This method fetches data from the repository and processes it.
	 * Firstly the list of supported MetaDataFormats is requested. Secondly the first List of Identifiers is requested.
	 * The retrieved list is parsed and the resumption Token, if it exists, is extracted. While all Ids are checked.
	 * The already checked Id are processed and finally if there is a resumptionToken a new List of Ids is requested.
	 * Then all begins from the very beginning until there are no more Identifiers to request.
	 * 
	 * @param fullharvest specifies whether we have a full or update harvest 
	 * @param updateFrom stores the timestamp for the update harvest. 
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
				response = listIdentifiers (getRepositoryURL ( ), this.metaDataFormat, updateFrom);
			
			else
				response = listIdentifiers (getRepositoryURL ( ), this.metaDataFormat);

			while (resumptionSet) {
					
				resumptionToken = extractIdsAndGetResumptionToken (response, getRepositoryID ( ));
				processRecords ( );
				
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
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken: " + resumptionToken);
						else;
					}
					
					resumptionToken = null;
					
				} else {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("no ResumptionToken found, IdentifierList complete");
					
					else;
					
					resumptionSet = false;
				}
				
				response = null;
			} 
		
		} catch (HttpException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream listMetaDataFormats (String url) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("listMetaDataFormat");
		
		return repositoryAnswer (url, "listMetaDataFormat", "");
	}

	/**
	 * @param url
	 * @param string
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private InputStream listIdentifiers (String url, String metaDataFormat) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("ListIdentifiers with MetaDataFormat " + metaDataFormat);
		
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
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listIdentifiers with ResumptionToken and MetaDataFormat " +
						 metaDataFormat);
			
			String resumptionToken = updateFrom;
			
			return repositoryAnswer (url, "ListIdentifiers", "&resumptionToken=" + resumptionToken);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listIdentifiers with MetaDataFormat " + metaDataFormat + " from " + updateFrom);
						
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
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (responseBody);
			
			NodeList idNodeList = document.getElementsByTagName ("identifier");
			NodeList datestampNodeList = document.getElementsByTagName ("datestamp");
			
			if (this.ids == null) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("List of ObjectIdentifiers is NULL, so I create a new one");
							
				this.ids = new ArrayList <ObjectIdentifier> ( );
			}
			
			if (logger.isDebugEnabled ( )) {
				
				logger.debug ("we have " + idNodeList.getLength ( ) + " Ids to extract");
				logger.debug ("we have " + datestampNodeList.getLength ( ) + " Datestamps to extract");
			}
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("we have " + idNodeList.getLength ( ) + " ID-Nodes to process");
			
			for (int i = 0; i < idNodeList.getLength ( ); i++) {
				
				int internalOID;
				String externalOID = idNodeList.item (i).getTextContent ( );
				String datestamp = datestampNodeList.item (i).getTextContent ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("List Record No. " + recordno++ + " " + externalOID  + " " + datestamp);
				
				
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
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("now we process " + this.ids.size ( ) + "Records");
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Object No. " + i + " is processed"); 
			
			if (this.ids.get (i).getInternalOID ( ) == -1) {
				
				// when internalOID == -1 than Object is not in the database and we have to create it
				createObjectEntry (i);
				
				// after that we have to upload the new rawdata
				updateRawData (i);
				
			} else {
				
				// Object exists and we have to look if our Rawdata is newer than the database one
				if (checkRawData (i) == true) {
					
					updateHarvestedDatestamp (i);
					
				} else {
					
					// if not, upload the new rawdata
					updateRawData (i);
				}
					
				
			} //endelse
		}
		
		this.ids = null;
	}
	
	/**
	 * 
	 */
	
	private void createObjectEntry (int index) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("we have to create a new Object");
		
		String ressource = "ObjectEntry/";
		RestClient restClient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (this.ids.get (index).getDatestamp ( ));
		
		String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
		
		cal = null;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("creating Object No. " + index + ": " + this.ids.get (index).getExternalOID ( ));
		
		try {
			
			RestMessage rms = new RestMessage ( );
			
			rms.setKeyword (RestKeyword.ObjectEntry);
			rms.setStatus (RestStatusEnum.OK);
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("repository_id", Integer.toString (getRepositoryID ( )));
			res.addEntry ("repository_identifier", this.ids.get (index).getExternalOID ( ));
			res.addEntry ("repository_datestamp", datestamp);
			
			rms.addEntrySet (res);
			
			String requestxml = RestXmlCodec.encodeRestMessage (rms);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("xml: " + requestxml);
			
			String result = restClient.PutData (requestxml);
			
			rms = null;
			res = null;
			restClient = null;
			
			String value = getValueFromKey (result, "oid");
						
			int intoid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("The internalOID for " + this.ids.get (index).getExternalOID ( ) + " is " + intoid);
			
			this.ids.get (index).setInternalOID (intoid);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * @param i
	 */
	
	private boolean checkRawData (int i) {
		
		//first we need the datestamp from the database
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("We have to check the RawData, whether it is outdated or not");
			logger.debug ("observing Object No. " + i + ": " + this.ids.get (i).getExternalOID ( ));
		}
		
		String ressource = "ObjectEntry/" + this.ids.get (i).getInternalOID ( ) + "/";
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		restclient = null;
		String value = getValueFromKey (result, "repository_datestamp");
		
		try {
			
			Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (value);	
			
			if (!repositoryDate.before (this.ids.get (i).getDatestamp ( ))) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("RepositoryDate is " + repositoryDate + " and before the harvested: " + this.ids.get (i).getDatestamp ( ));
				
				this.ids.get (i).setDatestamp (repositoryDate);
				
				return true;
				
			} else {
				
				// updated harvesteddatstamp and exit
				if (logger.isDebugEnabled ( ))
					logger.debug ("RepositoryDate is " + repositoryDate + " and after the harvested: " + this.ids.get (i).getDatestamp ( ));
				
				return false;
				
			}
			
		} catch (java.text.ParseException ex) {

			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			return true;
		}		
	}
	
	/**
	 * 
	 */
	
	private void updateHarvestedDatestamp (int index) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("We must update the harvested datestamp only"); 
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (this.ids.get (index).getDatestamp ( ));
		
		String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
		
		RestMessage rms = new RestMessage ( );
		
		rms.setKeyword (RestKeyword.ObjectEntry);
		rms.setStatus (RestStatusEnum.OK);
		
		RestEntrySet res = new RestEntrySet ( );
		
		res.addEntry ("repository_id", Integer.toString (getRepositoryID ( )));
		res.addEntry ("repository_identifier", this.ids.get (index).getExternalOID ( ));
		res.addEntry ("repository_datestamp", datestamp);
		
		rms.addEntrySet (res);
		
		String requestxml = RestXmlCodec.encodeRestMessage (rms);
		
		String ressource = "ObjectEntry/" + this.ids.get (index).getInternalOID ( ) + "/";
		
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = "";
		
		try {
			
			result = restclient.PostData (requestxml);
			
		} catch (UnsupportedEncodingException ex) {
			
			ex.printStackTrace ( );
		}
		
		rms = null;
		res = null;
		restclient = null;
		
		String value = getValueFromKey (result, "oid");
		
		int intoid = new Integer (value);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("internalOID: " + intoid + " has been successfully updated");
		
	}
	
	/**
	 * @param data
	 * @param internalOID
	 * @param datestamp
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private void updateRawData (int index) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Now we're going to update the RawData");
		
		HttpClient client = new HttpClient ( );
		HttpMethod method = new GetMethod (getRepositoryURL ( ) + "?verb=GetRecord&metadataPrefix=" + this.metaDataFormat + "&identifier=" + ids.get (index).getExternalOID ( ));
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		try {
			
			int statuscode = client.executeMethod (method);
			
			logger.info ("HttpStatusCode: " + statuscode);
			
			if (statuscode != HttpStatus.SC_OK) {
				
				;
			}
			
			GregorianCalendar cal = new GregorianCalendar ( );
			cal.setTime (ids.get (index).getDatestamp ( ));
			
			String resource = "RawRecordData/" + ids.get (index).getInternalOID ( ) + "/" + cal.get (Calendar.YEAR) + "-" + cal.get (Calendar.MONTH + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH) + "/" + this.metaDataFormat + "/";
			
			RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
			restclient.PutData (new String (Base64.encodeBase64 (HelperMethods.stream2String (method.getResponseBodyAsStream ( )).getBytes ("UTF-8"))));
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("uploaded rawdata for Database Object " + ids.get (index).getInternalOID ( ));

		} catch (HttpException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
		}
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
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("We are going to have a look if the object with the externalOID " + externalOID + " in repository nr. " +
					repositoryId + " already exists");
		
		String ressource = "ObjectEntryID/" + repositoryId + "/" + externalOID + "/";
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (result);
		
		String value = getValueFromKey (result, "oid");
		
		int intoid = -1;
				
		if (value == null)
			intoid = -1;
		
		else
			intoid = new Integer (value);
		
		return intoid;
	}
	
	/**
	 * @param result
	 * @param keyword
	 * @return
	 */
	
	private String getValueFromKey (String result, String keyword) {
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		RestEntrySet res = rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		String value = null;
		
		while (it.hasNext ( )) {
							
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + res.getValue (key));
			
			if (key.equalsIgnoreCase (keyword)) {
				
				value = res.getValue (key);
				break;
			}
		}
		
		return value;
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
					//harvester.processRecords ( );
					
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
