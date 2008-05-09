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
 * The Harvester consists of two parts: the Harvester itself and the Object Manager. The Harvester
 * creates a connection to a given repository where metadata-objects can be accessed.
 * The Object Manager handles the harvested meta-data objects and ensures the safe storage of these objects
 * in a database if necessary.
 * This class needs a property-file called harvesterprop.xml where the host to connect to, the username and the matching
 * password have to be specified.
 * 
 * @author Michael K&uuml;hn
 */

public class Harvester {
	
	/**
	 * The Harvester which will process the data-import and handle the imported objects
	 */
	
	private static Harvester harvester;
	
	/**
	 * This is an ArrayList of ObjectIdentifiers where necessary information about the objects is stored
	 * @see ObjectIdentifier
	 */
	
	private ArrayList <ObjectIdentifier> ids = null;
	
	/**
	 * The static log4j logger. All logging will be made with this nice static logger.
	 */
	
	private static Logger logger = Logger.getLogger (Harvester.class);
	
	/**
	 * The counter for the Records we are processing.
	 */
	
	private int recordno = 0;
	
	/**
	 * The Harvester properties. 
	 */
	
	private Properties props = null;

	/**
	 * Here we store the harvested MetaDataFormat.
	 */
	
	private String metaDataFormat ="oai_dc";
	
	/**
	 * This is the corresponding ID to the repository we'll connect to.
	 */
	
	private int repositoryID = 0;

	/**
	 * This is the Base-URL of the repository we have to connect to.
	 */
	
	private String repositoryURL = "";
	
	/**
	 * Indicator for full (true) or update harvest (false) 
	 */
	
	private boolean fullharvest = false;
	
	/**
	 * 
	 */
	
	private String date = "";
	
	/**
	 * Amount of Objects which are requested from the repository at once.
	 */
	
	private int amount = 10;
	
	/**
	 * Milliseconds to wait between two amounts of requested data.
	 */
	
	private int interval = 5000;
	
	/**
	 * Indicator if retrieved data are just for testing purposes or not.
	 * Objects marked as testing will be deleted from the database after a short time.
	 */
	
	private boolean testData = true;
	
	
	private String propertyfile = "harvesterprop.xml";
	
	/**
	 * Standard Constructor.
	 * It does nothing beside constructing the object instance.
	 * To configure the created harvester call {@link #prepareHarvester(int)}
	 * @see #prepareHarvester(int)
	 */
	
	public Harvester ( ) {
		
	}
	
	/**
	 * This method configures the harvester.
	 * It sets the repository ID and gets all other information from the Database
	 * 
	 * @param id the Repository ID
	 * @return true when the configuration is done otherwise {@linkplain System#exit} is called
	 * @see #getRepositoryDetails(int)
	 * @see HelperMethods#loadPropertiesFromFile(String)
	 */
	
	public boolean prepareHarvester (int id) {
		
		Harvester.harvester.repositoryID = id;
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile (this.getPropertyfile ( ));
			
			getRepositoryDetails (id);
			
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
		
		return true;
	}
	
	/**
	 * This method fetches all necessary details about the repository from the database.
	 * It fetches the OAI-URL, if only testdata shall be collected, the harvest amount and the pause between the harvested amounts.
	 * 
	 * @param id the Repository ID
	 * @see RestClient
	 * @see RestXmlCodec
	 */
	
	private void getRepositoryDetails (int id) {

		RestClient restClient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), "Repository/" + id + "/", this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		String result = restClient.GetData ( );
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		RestEntrySet res = rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
							
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + res.getValue (key));
			
			if (key.equalsIgnoreCase ("oai_url")) {
				
				this.setRepositoryURL (filterUrl (res.getValue (key)));
				continue;
				
			} else if (key.equalsIgnoreCase ("test_data")) {
				
				this.setTestData (new Boolean (res.getValue (key)));
				continue;
				
			} else if (key.equalsIgnoreCase ("harvest_amount")) {
				
				filterAmount (res.getValue (key));
				continue;
				
			} else if (key.equalsIgnoreCase ("harvest_pause")) {
				
				filterInterval (res.getValue (key));
				continue;
				
			} else continue;
		}
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Data received from Database:");
			logger.debug ("oai_url: " + this.getRepositoryURL ( ));
			logger.debug ("test_data: " + this.isTestData ( ));
			logger.debug ("harvest_amount: " + this.getAmount ( ));
			logger.debug ("harvest_pause: " + this.getInterval ( ));
		}
	}
	
	/**
	 * Getter method for the harvester
	 * 
	 * @return the Harvester
	 */
	
	public static final Harvester getHarvester ( ) {
		
		if (harvester == null) {
			
			harvester = new Harvester ( );
		}
		
		return harvester;
	}
	
	/**
	 * Getter method for repositoryURL
	 * 
	 * @return the repositoryURL
	 */
	
	public final String getRepositoryURL ( ) {
	
		return this.repositoryURL;
	}
		
	/**
	 * Setter method for the RepositoryURL
	 * 
	 * @param repositoryURL the repositoryURL to set
	 */
	
	public final void setRepositoryURL (String repositoryURL) {
	
		this.repositoryURL = repositoryURL;
	}
	
	/**
	 * Getter method for repositoryID
	 * 
	 * @return the repositoryID
	 */
	
	public final int getRepositoryID ( ) {
	
		return this.repositoryID;
	}

	/**
	 * Getter method for the fullharvest
	 * 
	 * @return the fullharvest (true if full, false if update) 
	 */
	
	public final boolean isFullharvest ( ) {
	
		return this.fullharvest;
	}
	
	/**
	 * Setter method for the fullharvest
	 * 
	 * @param fullharvest the fullharvest to set (true if full, false if update)
	 */
	
	public final void setFullharvest (boolean fullharvest) {
	
		this.fullharvest = fullharvest;
	}
	
	/**
	 * Getter method for the date
	 * 
	 * @return the date
	 */
	
	public final String getDate ( ) {
	
		return this.date;
	}
	
	/**
	 * Setter method for the date
	 * 
	 * @param date the date to set
	 */
	
	public final void setDate (String date) {
	
		this.date = date;
	}
	
	/**
	 * Getter method for the amount
	 * 
	 * @return the amount
	 */
	
	public final int getAmount ( ) {
	
		return this.amount;
	}
	
	/**
	 * Setter method for the amount
	 * 
	 * @param amount the amount to set
	 */
	
	public final void setAmount (int amount) {
	
		this.amount = amount;
	}
	
	/**
	 * Getter method for the interval
	 * 
	 * @return the interval
	 */
	
	public final int getInterval ( ) {
	
		return this.interval;
	}
	
	/**
	 * Setter method for the interval
	 * 
	 * @param interval the interval to set
	 */
	
	public final void setInterval (int interval) {
	
		this.interval = interval;
	}
	
	/**
	 * Getter method for the testdata
	 * 
	 * @return the testData
	 */
	
	public final boolean isTestData ( ) {
	
		return this.testData;
	}
	
	/**
	 * Setter method for the testdata
	 * 
	 * @param testData the testData to set
	 */
	
	public final void setTestData (boolean testData) {
	
		this.testData = testData;
	}
	
	/**
	 * @return the propertyfile
	 */
	
	public final String getPropertyfile ( ) {
	
		return this.propertyfile;
	}
	
	/**
	 * @param propertyfile the propertyfile to set
	 */
	
	public final void setPropertyfile (String propertyfile) {
	
		this.propertyfile = propertyfile;
	}
	
	/**
	 * @return the props
	 */
	public final Properties getProps ( ) {
	
		return this.props;
	}

	/**
	 * This method fetches data from the repository and processes it.
	 * Firstly the list of supported MetaDataFormats is requested. Secondly the first List of Identifiers is requested.
	 * The retrieved list is parsed and the resumption Token, if it exists, is extracted. While all Ids are checked.
	 * The already checked Id are processed and finally if there is a resumptionToken a new List of Ids is requested.
	 * Then all begins from the very beginning until there are no more Identifiers to request.
	 */
	
	public void processIds ( ) {
		
		String resumptionToken = null;
		Boolean resumptionSet = true;
		InputStream response = null;
		
		try {
			
			// this will get all metadataformats, the requested repository supports 
			
			response = listMetaDataFormats (getRepositoryURL ( ));
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("metaDataFormats found");
			
			// here has to be inserted the code which decides which format is chosen for the following harvest
			response = null;
			
			// when we only want to get all new rawdata from the given data, we'll end in here 
			if (!this.isFullharvest ( ))
				response = listIdentifiers (getRepositoryURL ( ), this.metaDataFormat, this.getDate ( ));
			
			// if we do a harvest of all rawdata, this will be chosen
			else
				response = listIdentifiers (getRepositoryURL ( ), this.metaDataFormat);
			
			while (resumptionSet) { // until we have a ResumptionToken to process
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("resumptionSet = true");
				
				/* 
				 * the response of the harvested repository is taken and the IDs in it will be extracted and we'll have
				 * returned the resumption Token if it exists
				 */
				resumptionToken = extractIdsAndGetResumptionToken (response);
				
				// Now the list of objects will be processed
				processRecords ( );
				
				// if we have resumption token, we have to find out, whether it's the last entryset or not
				if (resumptionToken != null) {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("ResumptionToken found: " + resumptionToken);
					else;
					
					if (resumptionToken.equals ("")) {
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken empty, IdentifierList complete");
						
						resumptionSet = false;
						break;
						
					} else {
												
						response = listIdentifiers (getRepositoryURL ( ), "resumptionToken", resumptionToken);
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken: " + resumptionToken);
						
						continue;
					}
					
				} else {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("no ResumptionToken found, IdentifierList complete");
					
					resumptionSet = false;
					break;
				}
			} 
		
		} catch (HttpException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} finally {
			
			resumptionToken = null;
			response = null;
		}
		
		return;
	}

	/**
	 * This method encapsulates the request to list all Meta Data Formats the repository supports.
	 * 
	 * @param url the repositoryURL to connect to
	 * @return the Inputstream which contains the answer from the repository
	 * @throws IOException 
	 * @throws HttpException 
	 * @see #repositoryAnswer(String, String, String)
	 */
	
	private InputStream listMetaDataFormats (String url) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("listMetaDataFormat");
		
		return repositoryAnswer (url, "listMetaDataFormat", "");
	}
	
	/**
	 * This method encapsulates the request for a full harvest of Identifiers from the repository
	 * 
	 * @param url the OAI-URL to connect to
	 * @param metaDataFormat the Meta Data Format which shall be requested
	 * @return the Inputstream from the repository
	 * @throws IOException 
	 * @throws HttpException 
	 * @see #repositoryAnswer(String, String, String)
	 */
	
	private InputStream listIdentifiers (String url, String metaDataFormat) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("ListIdentifiers with MetaDataFormat " + metaDataFormat);
		
		return repositoryAnswer (url, "ListIdentifiers", "&metadataPrefix=" + metaDataFormat);
	}

	/**
	 * This method encapsulates the request for an update harvest of Identifiers from the repository or the ResumptionToken Request
	 * 
	 * @param url the OAI-URL to connect to
	 * @param metaDataFormat the Meta Data Format which shall be requested or "resumptionToken" for the resumptionToken use
	 * @param updateFrom the date from which on the harvest shall start or the resumptionToken for the resumptionToken use
	 * @return the Inputstream from the repository
	 * @throws IOException 
	 * @throws HttpException 
	 * @see #repositoryAnswer(String, String, String)
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
	 * This method connects to the Repository and gets information from it.
	 * This information can be the supported Meta Data Formats, the Identifiers or other.
	 * 
	 * @param url the OAI-URL to connect to
	 * @param verb the OAI-PMH-verb to use
	 * @param parameter the parameters for the request (i.e. from, set, ...)
	 * @return the Inputstream from the repository
	 * @throws IOException 
	 * @throws HttpException
	 * @see org.apache.commons.httpclient
	 */
	
	private InputStream repositoryAnswer (String url, String verb,
			String parameter) throws HttpException, IOException {
		
		InputStream inst;
		int statuscode = 0;
		HttpClient client = new HttpClient ( );
		GetMethod getmethod = new GetMethod (url + "?verb=" + verb + parameter);
		
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		if (logger.isDebugEnabled ( ))
			logger.debug (url +  "?" + getmethod.getQueryString ( ));
		
		statuscode = client.executeMethod (getmethod);
		
		logger.info ("HttpStatusCode: " + statuscode);
		
		if (statuscode != HttpStatus.SC_OK) {
			
		}
		
		inst = getmethod.getResponseBodyAsStream ( );
		
		if (inst != null)
			return inst;
		
		else
			throw new HttpException ("ResponseBody is null!");
	}

	/**
	 * This method processes the Inputstrean from the repository and extracts the Object IDs and the ResumptionToke if it exists.
	 * It stores all IDs in a list of ObjectIdentifiers together with its datestamps. The IDs which are extracted in here are
	 * only the external IDs which means the IDs used by the harvested repository. There's still no relation to IDs in our own
	 * database.
	 * This relation is made later in this method {@link #objectexists(String)} 
	 * 
	 * @param responseBody the Inputstream from the Repository
	 * @return the ResumptionToken if any exists, null otherwise
	 * @see #objectexists(String)
	 * @see ObjectIdentifier
	 * @see javax.xml.parsers
	 */
	
	private String extractIdsAndGetResumptionToken (InputStream responseBody) {
		
		String resumptionToken = null; 
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("ResponseBody: " + responseBody.toString ( ));
		
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
				
				
				internalOID = objectexists (externalOID);
				
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
	 * This is the heart of the object handler. Here all harvested objects are processed.
	 * For every object an object entry will be created if this object still not exists in our database.
	 * Next the rawdata will be collected and put into the database. 
	 * When the object already exists the object will be checked if there's some newer data. If so the newer data
	 * will be put in the database otherwise only the datestamp of the last harvest-run is set.
	 * 
	 * @see #createObjectEntry(int)
	 * @see #updateRawData(int)
	 * @see #checkRawData(int)
	 * @see #updateHarvestedDatestamp(int)
	 */
	
	private void processRecords ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("now we process " + this.ids.size ( ) + "Records");
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Object No. " + i + " is processed"); 
			
			if (this.ids.get (i).getInternalOID ( ) == -1) {
				
				// when internalOID == -1 than Object is not in the database and we have to create it
				this.createObjectEntry (i);
				
				if (logger.isDebugEnabled ( )) {
					
					logger.debug ("Amount: " + this.getAmount ( ));
				}
				
				if ((i % this.getAmount ( )) == 0) try {
					
					logger.debug ("Going to sleep for " + getInterval ( ) + " milliseconds, letting the repository recover a bit");
					Thread.sleep (getInterval ( ));
					logger.debug ("Continuing to ask the repository for some records");
					
				} catch (InterruptedException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
				// after that we have to upload the new rawdata
				updateRawData (i);
				
			} else {
				
				// Object exists and we have to look if our Rawdata is newer than the database one
				if (checkRawData (i) == true) {
					
					updateHarvestedDatestamp (i);
					
				} else {
					
					if ((i % 20) == 0) try {
						
						logger.debug ("Going to sleep for 5 seconds, letting the repository recover a bit");
						Thread.sleep (5000);
						logger.debug ("Continuing to ask the repository for some records");
						
					} catch (InterruptedException ex) {
						
						logger.error (ex.getLocalizedMessage ( ));
						ex.printStackTrace ( );
					}
					
					// if not, upload the new rawdata
					updateRawData (i);
				}
			} //endelse
		}
		
		this.ids = null;
	}
	
	/**
	 * This method created a new object in the database and retrieves the corresponding ID.
	 * For the object-data it will transmit the repository ID, the external Object Identifier (from the repository),
	 * the datestamp from the repository, the testdata status and the failure counter for this object.
	 * The retrieved internal Object Identifier is stored in the list of ObjectIdentifieres.
	 * 
	 * @see RestClient
	 * @see ObjectIdentifier
	 */
	
	private void createObjectEntry (int index) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("we have to create a new Object");
		
		String ressource = "ObjectEntry/";
		RestClient restClient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), ressource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		
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
			res.addEntry ("testdata", Boolean.toString (isTestData ( )));
			res.addEntry ("failureCounter", "0");
			
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
	 * This method checks if the rawdata we received is newer than the rawdata in the database or not.
	 * 
	 * @param i index of the current ObjectIdentifier
	 * @return true if received data is newer, false else
	 */
	
	private boolean checkRawData (int i) {
		
		//first we need the datestamp from the database
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("We have to check the RawData, whether it is outdated or not");
			logger.debug ("observing Object No. " + i + ": " + this.ids.get (i).getExternalOID ( ));
		}
		
		String ressource = "ObjectEntry/" + this.ids.get (i).getInternalOID ( ) + "/";
		RestClient restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), ressource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		restclient = null;
		String value = getValueFromKey (result, "repository_datestamp");
		
		try {
			
			Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (value);	
			
			if (!repositoryDate.before (this.ids.get (i).getDatestamp ( ))) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("RepositoryDate is " + repositoryDate + " and after the harvested: " + this.ids.get (i).getDatestamp ( ));
				
				this.ids.get (i).setDatestamp (repositoryDate);
				
				return true;
				
			} else {
				
				// updated harvesteddatstamp and exit
				if (logger.isDebugEnabled ( ))
					logger.debug ("RepositoryDate is " + repositoryDate + " and before the harvested: " + this.ids.get (i).getDatestamp ( ));
				
				return false;
				
			}
			
		} catch (java.text.ParseException ex) {

			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			return true;
		}		
	}
	
	/**
	 * This method only sets a new datestamp for the given Object.
	 * 
	 * @param index index of the current ObjectIdentifier
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
		res.addEntry ("testdata", Boolean.toString (isTestData ( )));
		res.addEntry ("failureCounter", "0");
		
		rms.addEntrySet (res);
		
		String requestxml = RestXmlCodec.encodeRestMessage (rms);
		
		String ressource = "ObjectEntry/" + this.ids.get (index).getInternalOID ( ) + "/";
		
		RestClient restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), ressource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		
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
	 * This method puts new rawdata in the Database.
	 * 
	 * @param index
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
			
			String resource = "RawRecordData/" + ids.get (index).getInternalOID ( ) + "/" + cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH) + "/" + this.metaDataFormat + "/";
			
			RestClient restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
			restclient.PutData (new String (Base64.encodeBase64 (HelperMethods.stream2String (method.getResponseBodyAsStream ( )).getBytes ("UTF-8"))));
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("uploaded rawdata for Database Object " + ids.get (index).getInternalOID ( ));
			
			resource = "WorkflowDB/";
			
			RestMessage rms = new RestMessage ( );
			
			rms.setKeyword (RestKeyword.WorkflowDB);
			rms.setStatus (RestStatusEnum.OK);
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("object_id", Integer.toString (ids.get (index).getInternalOID ( )));
			//TODO: statt 1 service_id abfragen
			res.addEntry ("service_id", "1");
			rms.addEntrySet (res);
			
			String requestxml = RestXmlCodec.encodeRestMessage (rms);
			
			restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
			
			String result = "";
			
			try {
				
				result = restclient.PutData (requestxml);
				
			} catch (UnsupportedEncodingException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
			}
			
			rms = null;
			res = null;
			restclient = null;
			
			String value = getValueFromKey (result, "workflow_id");
			
			if (value == null) {
				
				logger.error ("I can not update the RawData because an error occured. Skipping this object and continue with next.");
				return;
			}
			
			int workflowid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("workflow_id is: " + workflowid);
			
		} catch (HttpException ex) {
			
			ex.printStackTrace ( );
			logger.error (ex.getLocalizedMessage ( ));
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			logger.error (ex.getLocalizedMessage ( ));
		}
	}

	/**
	 * This method asks the database whether an Object already exists or not.
	 * 
	 * @param externalOID the external ObjectIdentifier received from the repository
	 * @return the internal Object ID if the Object already exists, -1 else
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	private int objectexists (String externalOID) throws ParserConfigurationException, SAXException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("We are going to have a look if the object with the externalOID " + externalOID + " in repository nr. " +
					this.getRepositoryID ( ) + " already exists");
		
		String ressource = "ObjectEntryID/" + this.getRepositoryID ( ) + "/" + externalOID + "/";
		
		logger.debug ("Properties: " + this.getProps ( ).getProperty ("host"));
		
		RestClient restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), ressource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		
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
	 * This method decodes a RestMessage and extracts the value for a given key. 
	 * 
	 * @param result the encoded RestMessage
	 * @param keyword the key for the value
	 * @return the extracted value
	 */
	
	private String getValueFromKey (String result, String keyword) {
		
		if (result == null || result.equalsIgnoreCase ("")) {
			
			logger.error ("received RestMessage empty, skipping, continue with next");
			return null;
		}
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms.getStatus ( ) != RestStatusEnum.OK) {
			
			logger.error ("RestError occured: " + rms.getStatusDescription ( ));
			return null;
		}
		
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
	 * This method filters the interval-option and converts it to an integer
	 * 
	 * @param optionValue the value of the interval-option
	 * @return the interval as an integer
	 */
	
	public static int filterInterval (String optionValue) {
		
		if (optionValue != null) {
		
			Harvester.harvester.setInterval (new Integer (optionValue));
		}
		
		return 0;
	}
	
	/**
	 * This method filters the amount option and converts it to an integer
	 * 
	 * @param optionValue the value of the amount-option
	 * @return the amount as an integer
	 */
	
	public static int filterAmount (String optionValue) {
		
		int amount = 1;
		
		if (optionValue != null) {
			
			amount = new Integer (optionValue);
			
			if (amount < 1)
				throw new IllegalArgumentException ("Negative amount!");
			
			harvester.setAmount (amount);
		}
		
		return amount;
	}
	
	/**
	 * The date which comes from the command line will be filtered and observed.
	 * 
	 * @param optionValue the date string which is filtered
	 * @return the proper date string when it could be extracted null otherwise
	 */
	
	public static String filterDate (String optionValue) {
		
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
				
				harvester.setDate (date2test.toString ( ));
				
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
	
	public static boolean filterBool (String optionValue, CommandLine cmd) throws ParseException {
		
		if (optionValue.equalsIgnoreCase ("full")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("full harvest: " + Boolean.TRUE);
			
			harvester.setFullharvest (true);
			
			return true;
			
		} else {
			
			if (cmd.hasOption ('d') || cmd.hasOption ("updateDate")) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("full harvest: " + Boolean.FALSE);
				
				harvester.setFullharvest (false);
				
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
	
	public static int filterId (String optionValue) throws ParseException {
		
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
	
	public static String filterUrl (String optionValue) {
		
		if (optionValue != null) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("filtered URL: " + optionValue);
			
			harvester.setRepositoryURL (optionValue);
		}
		
		return optionValue;
	}
}

/**
 * This class represents an Object in which all necessary data for the Object-Manager will be stored.
 * Every field can be accessed via setter and getter methods.
 * 
 * @author Michael K&uuml;hn
 */

class ObjectIdentifier {
	
	/**
	 * The external Object ID which is received from the repository 
	 */
	
	private String externalOID;
	
	/**
	 * The datestamp which is received from the repository 
	 */
	
	private Date datestamp;
	
	/**
	 * The matching internal Object ID which is received from our database 
	 */
	
	private int internalOID;
	
	/**
	 * The log4j logger for all logging purposes.
	 */
	
	static Logger logger = Logger.getLogger (ObjectIdentifier.class);
	
	/**
	 * This is the standard constructor for ObjectIdentifier.
	 * It stores the external Object Identifier (from the repository) the datestamp of the last change and
	 * the internal Object identifier (used within our system)
	 * These informations are necessary for the identification of a single object. It stores the relation
	 * between the internal and the external Object Identifier. The Datestamp is used to distinguish the different versions
	 * of one object harvested at different dates. 
	 * 
	 * @param externalOID the Object Identifier which is used in the harvested repository 
	 * @param datestamp the date of the object's last change
	 * @param internalOID the Object Identifier which is used in our internal system and database
	 */
	
	public ObjectIdentifier (String externalOID, String datestamp, int internalOID) {
		
		try {
			
			this.externalOID = externalOID;
			this.datestamp = new SimpleDateFormat ("yyyy-MM-dd").parse (datestamp);
			this.internalOID = internalOID;
			
		} catch (java.text.ParseException ex) {
			
			logger.error (externalOID + "has no valid Datestamp!\n" + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * Getter method for the externalOID
	 * 
	 * @return the external object ID (comes from the harvested repository)
	 */
	
	final String getExternalOID ( ) {
	
		return this.externalOID;
	}

	/**
	 * Setter method for the externalOID
	 * 
	 * @param id the external object ID to set
	 */
	
	final void setExternalOID (String id) {
	
		this.externalOID = id;
	}

	/**
	 * Getter method for the datestamp
	 * 
	 * @return the datestamp
	 */
	
	final Date getDatestamp ( ) {
		
		return this.datestamp;
	}
	
	/**
	 * Setter method for the datestamp
	 * 
	 * @param datestamp the datestamp to set
	 */
	
	final void setDatestamp (Date datestamp) {
	
		this.datestamp = datestamp;
	}

	/**
	 * Getter method for the internalOID
	 * 
	 * @return the internalOID
	 */
	
	final int getInternalOID ( ) {
	
		return this.internalOID;
	}
	
	/**
	 * Setter method for the internalOID
	 * 
	 * @param internalOID the internalOID to set
	 */
	
	final void setInternalOID (int internalOID) {
	
		this.internalOID = internalOID;
	}
}
