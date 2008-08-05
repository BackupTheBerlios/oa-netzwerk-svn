/**
 *
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * The Harvester consists of two parts: the Harvester itself and the Object
 * Manager. The Harvester creates a connection to a given repository where
 * metadata-objects can be accessed. The Object Manager handles the harvested
 * meta-data objects and ensures the safe storage of these objects in a database
 * if necessary. This class needs a property-file called harvesterprop.xml where
 * the host to connect to, the username and the matching password have to be
 * specified.
 * 
 * @author Michael K&uuml;hn
 */

public class Harvester {
	
	/**
	 * The Harvester which will process the data-import and handle the imported
	 * objects
	 */
	
	private static Harvester harvester;
	
	/**
	 * This is an ArrayList of ObjectIdentifiers where necessary information
	 * about the objects is stored
	 * 
	 * @see ObjectIdentifier
	 */
	
	private ArrayList <ObjectIdentifier> ids = null;
	
	/**
	 * The static log4j logger. All logging will be made with this nice static
	 * logger.
	 */
	
	private static Logger logger = Logger.getLogger (Harvester.class);
	
	/**
	 * The Harvester properties.
	 */
	
	private Properties props = null;
	
	/**
	 * Here we store the harvested MetaDataFormat.
	 */
	
	private final String metaDataFormat = "oai_dc";
	
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
	 * Indicator if retrieved data are just for testing purposes or not. Objects
	 * marked as testing will be deleted from the database after a short time.
	 */
	
	private boolean testData = true;
	
	private String propertyfile = "harvesterprop.xml";
	
	private boolean listrecords = false;
	
	private int getRecordCounter = 1;
	
	/**
	 * Standard Constructor. It does nothing beside constructing the object
	 * instance. To configure the created harvester call
	 * {@link #prepareHarvester(int)}
	 * 
	 * @see #prepareHarvester(int)
	 */
	
	public Harvester ( ) { }
	
	/**
	 * This method configures the harvester. It sets the repository ID and gets
	 * all other information from the Database
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
			this.getRepositoryDetails (id);
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			System.exit (1);
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			System.exit (1);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			System.exit (1);
		}
		
		return true;
	}
	
	/**
	 * This method fetches all necessary details about the repository from the
	 * database. It fetches the OAI-URL, if only testdata shall be collected,
	 * the harvest amount and the pause between the harvested amounts.
	 * 
	 * @param id the Repository ID
	 * @see RestClient
	 * @see RestXmlCodec
	 */
	
	private void getRepositoryDetails (int id) {
		
		if (this.getProps ( ).containsKey ("listrecords"))
			setListRecords (new Boolean (this.getProps ( ).getProperty ("listrecords", "false")));
		
		String result = this.prepareRestTransmission ("Repository/" + id + "/").GetData ( );
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no Repository Details at all from the server");
			System.exit (1);
		}
		
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
				
			} else if (key.equalsIgnoreCase ("listrecords")) {
				
				this.setListRecords (new Boolean (res.getValue (key)));
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

		if (harvester == null)
			harvester = new Harvester ( );
		
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
	 * @param repositoryURL
	 *            the repositoryURL to set
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
	 * @param fullharvest
	 *            the fullharvest to set (true if full, false if update)
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
	 * @param amount
	 *            the amount to set
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
	 * @param listrecords
	 */
	
	public final void setListRecords (boolean listrecords) {
		
		this.listrecords = listrecords;
	}
	
	/**
	 * @return the props
	 */
	
	public final Properties getProps ( ) {

		return this.props;
	}
	
	/**
	 * @return
	 */
	
	protected ArrayList <ObjectIdentifier> getIds ( ) {

		if (this.ids == null)
			this.ids = new ArrayList <ObjectIdentifier> ( );
		
		return this.ids;
	}
	
	/**
	 * @param ids
	 */
	
	protected void setIds (ArrayList <ObjectIdentifier> ids) {

		this.ids = ids;
	}
	
	/**
	 * This method fetches data from the repository and processes it. Firstly
	 * the list of supported MetaDataFormats is requested. Secondly the first
	 * List of Identifiers is requested. The retrieved list is parsed and the
	 * resumption Token, if it exists, is extracted. While all Ids are checked.
	 * The already checked Id are processed and finally if there is a
	 * resumptionToken a new List of Ids is requested. Then all begins from the
	 * very beginning until there are no more Identifiers to request.
	 */
	
	public void processRepository ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("process Repository");
		
		Element resumptionToken = null;
		Boolean isResumptionSet = true;
		InputStream responseStream = null;
		
		try {
			
			// this will get all metadataformats, the requested repository
			// supports
			responseStream = this.listMetaDataFormats (getRepositoryURL ( ));
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("metaDataFormats found");
			
			// here has to be inserted the code which decides which format is
			// chosen for the following harvest
			responseStream = null;
			
			if (this.listrecords) {
				
				logger.info ("using listRecord-Method for retreiving data from Repository No " + getRepositoryID ( ));
				
				if (!this.isFullharvest ( ))
					responseStream = this.listRecords (this.getRepositoryURL ( ), this.metaDataFormat, this.getDate ( ));
				
				else
					responseStream = this.listRecords (this.getRepositoryURL ( ), this.metaDataFormat);
				
				while (isResumptionSet) {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("processing retrieved RecordList and retreiving Resumption Token");
					
					resumptionToken = this.processListedRecordsAndGetResumptionToken (responseStream);
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("processing extracted Records");
					
					this.processRecords ( );
					
					if (resumptionToken != null) {
						
						logger.info ("ResumptionToken received from Repository No " + this.getRepositoryID ( ));
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken: " + resumptionToken.getText ( )); else;
						
						if (resumptionToken.getTextTrim ( ).equals ("")) {
							
							if (logger.isDebugEnabled ( ))
								logger.debug ("ResumptionToken empty, Request complete");
							
							isResumptionSet = false;
							break;
							
						} else {
							
							responseStream = listRecords (getRepositoryURL ( ), "resumptionToken", resumptionToken.getTextTrim ( ));
							
							if (logger.isDebugEnabled ( ))
								logger.debug ("ResumptionToken: " + resumptionToken.getTextTrim ( ));
							
							continue;
						}
						
					} else {
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("No ResumptionToken found, Request complete");
						
						isResumptionSet = false;
						break;
					}
				}
				
			} else {
				
				logger.info ("using listIedntifiers- and getRecord-Methods for retreiving data from Repository No " + getRepositoryID ( ));
				
				// when we only want to get all new rawdata from the given data,
				// we'll end in here
				if (!this.isFullharvest ( ))
					responseStream = this.listIdentifiers (this.getRepositoryURL ( ), this.metaDataFormat, this.getDate ( ));
				
				// if we do a harvest of all rawdata, this will be chosen
				else responseStream = this.listIdentifiers (this.getRepositoryURL ( ), this.metaDataFormat);
				
				while (isResumptionSet) { // until we have a ResumptionToken to process
					
					/*
					 * the response of the harvested repository is taken and the
					 * IDs in it will be extracted and we'll have returned the
					 * resumption Token if it exists
					 */
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("extracting Ids and Resumption Token from retrieved Identifier List");
					
					resumptionToken = this.extractIdsAndGetResumptionToken (responseStream);
					
					// Now the list of objects will be processed
					if (logger.isDebugEnabled ( ))
						logger.debug ("Processing extracted Records");
					
					this.processRecords ( );
					
					// if we have resumption token, we have to find out, whether
					// it's the last entryset or not
					if (resumptionToken != null) {
						
						logger.info ("ResumptionToken received from Repository No " + this.getRepositoryID ( ));
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("ResumptionToken: " + resumptionToken.getText ( )); else ;
						
						if (resumptionToken.getTextTrim ( ).equals ("")) {
							
							if (logger.isDebugEnabled ( ))
								logger.debug ("ResumptionToken empty, IdentifierList complete");
							
							isResumptionSet = false;
							break;
							
						} else {
							
							responseStream = listIdentifiers (getRepositoryURL ( ), "resumptionToken", resumptionToken.getTextTrim ( ));
							
							if (logger.isDebugEnabled ( ))
								logger.debug ("ResumptionToken: " + resumptionToken.getTextTrim ( ));
							
							continue;
						}
						
					} else {
						
						if (logger.isDebugEnabled ( ))
							logger.debug ("No ResumptionToken found, IdentifierList complete");
						
						isResumptionSet = false;
						break;
					}
				}
			}
			
		} catch (HttpException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			resumptionToken = null;
			responseStream = null;
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("finished");
		
		return;
	}
	
	/**
	 * @param response
	 * @return the resumption token
	 */
	
	@SuppressWarnings ("unchecked")
	private Element processListedRecordsAndGetResumptionToken (
			InputStream responseBody) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("processListedRecordsAndGetResumptionToken");
		
		SAXBuilder builder = new SAXBuilder ( );
		Element resumptionToken = null;
		
		try {
			
			Document doc = builder.build (responseBody);
			Element root = doc.getRootElement ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("processing RecordElementList");
			
			this.processRecordElementList (root);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("retrieving Resumption Token");
			
			resumptionToken = searchFirstChild (root.getChildren ( ), "resumptionToken");
			doc = null;
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (JDOMException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (ParserConfigurationException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (SAXException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (java.text.ParseException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
		}
		
		builder = null;
		return resumptionToken;
	}
	
	/**
	 * @param elements
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws java.text.ParseException 
	 */
	
	@SuppressWarnings("unchecked")
	private void processRecordElementList (Element element) throws ParserConfigurationException, SAXException, IOException, java.text.ParseException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("processRecordElementList");
		
		List <Element> recordlist = searchAllChildren (element.getChildren ( ), "record");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Storing extracted records");
		
		for (Element record : recordlist) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug (element.getName ( ) + " is stored with value: " + element.getText ( ));
			
			this.storeSingleRecord (record);
		}
	}
	
	/**
	 * @param elements
	 * @param string
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	
	@SuppressWarnings("unchecked")
	private Element searchFirstChild (List <Element> elements, String name) throws ParserConfigurationException, SAXException, IOException {
		
		Element returnelement = null;
		
		for (Element element : elements) {
			
			if (element.getName ( ).equalsIgnoreCase (name)) {
				
				returnelement = element;
				break;
				
			} else {
				
				returnelement = searchFirstChild (element.getChildren ( ), name);
				
				if (returnelement != null)
					break;
			}
		}
		
		return returnelement;
	}
	
	/**
	 * @param elements
	 * @param name
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	
	@SuppressWarnings ("unchecked")
	private List <Element> searchAllChildren (List <Element> elements, String name) throws ParserConfigurationException, SAXException, IOException {
		
		List <Element> returnelements = new ArrayList <Element> ( );
		
		for (Element element : elements) {
			
			if (element.getName ( ).equalsIgnoreCase (name)) {
				
				returnelements.add (element);
				
			} else {
				
				returnelements.addAll (searchAllChildren (element.getChildren ( ), name));
			}
		}
		
		return returnelements;
	}
	
	/**
	 * @param record
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws java.text.ParseException 
	 */
	
	@SuppressWarnings ("unchecked")
	private void storeSingleRecord (Element record) throws ParserConfigurationException, SAXException, IOException, java.text.ParseException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("storeSingleRecord");
		
		Element header = searchFirstChild (record.getChildren ( ), "header");
		
		Element identifier = searchFirstChild (header.getChildren ( ), "identifier");
		String recordHeaderIdentifier = identifier.getTextTrim ( );
		Element date = searchFirstChild (header.getChildren ( ), "datestamp");
		String dateStamp = date.getTextTrim ( );
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("recordHeaderIdentifier: " + recordHeaderIdentifier);
			logger.debug ("dateStamp: " + dateStamp);
		}
		
		int oid = this.objectexists (recordHeaderIdentifier);
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("OID: " + oid);
			logger.debug ("adding new ObjectIdentifier for later Processing");
		}
		
		if (oid > 0) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("object exists, so we have to look new rawdata exists");
			
			String objectEntryResponse = prepareRestTransmission ("ObjectEntry/" + oid + "/").GetData ( );
			String objectEntryDatestamp = getValueFromKey (objectEntryResponse, "repository_datestamp");
			
			Date objectEntryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (objectEntryDatestamp);
			Date headerDate = new SimpleDateFormat ("yyyy-MM-dd").parse (dateStamp);
			
			if (objectEntryDate.before (headerDate)) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("new rawdata is available, so we'll store her");
				
				this.getIds ( ).add (new ObjectIdentifier (recordHeaderIdentifier, dateStamp, oid, record));
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("no new rawdata. We must only update the datestamps. Rawdata won't be stored");
				
				this.getIds ( ).add (new ObjectIdentifier (recordHeaderIdentifier, dateStamp, oid, null));
			}
			
			objectEntryResponse = null;
			objectEntryDatestamp = null;
			objectEntryDate = null;
			headerDate = null;
			
		} else if (oid == -1) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("object does not exits, so we have to fetch all data");
			
			this.getIds ( ).add (new ObjectIdentifier (recordHeaderIdentifier, dateStamp, oid, record));
			
		} else {
			
			logger.error ("Error with number " + oid + "occured while processing Object " + recordHeaderIdentifier);
			logger.info ("Skipping object " + recordHeaderIdentifier + " and continue with the next one");
		}
		
		recordHeaderIdentifier = null;
		dateStamp = null;
	}
	
	/**
	 * @param url
	 * @param metaDataFormat
	 * @param updateFrom
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	
	private InputStream listRecords (String url, String metaDataFormat, String updateFrom) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("listRecords");
		
		if (metaDataFormat.equalsIgnoreCase ("resumptionToken")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listRecords with ResumptionToken and MetaDataFormat " + metaDataFormat);
			
			String resumptionToken = updateFrom;
			
			return this.repositoryAnswer (url, "ListRecords", "&resumptionToken=" + resumptionToken);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listRecords with MetaDataFormat " + metaDataFormat + " from " + updateFrom);
			
			return this.repositoryAnswer (url, "ListRecords", "&metadataPrefix=" + metaDataFormat + "&from=" + updateFrom);
		}
	}
	
	/**
	 * @param url
	 * @param metaDataFormat
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	
	private InputStream listRecords (String url, String metaDataFormat) throws HttpException, IOException {

		if (logger.isDebugEnabled ( ))
			logger.debug ("listRecords");
		
		return this.repositoryAnswer (url, "ListRecords", "&metadataPrefix=" + metaDataFormat);
	}
	
	/**
	 * This method encapsulates the request to list all Meta Data Formats the
	 * repository supports.
	 * 
	 * @param url the repositoryURL to connect to
	 * @return the Inputstream which contains the answer from the repository
	 * @throws IOException
	 * @throws HttpException
	 * @see #repositoryAnswer(String, String, String)
	 */
	
	private InputStream listMetaDataFormats (String url) throws HttpException, IOException {
		
		return this.repositoryAnswer (url, "listMetaDataFormat", "");
	}
	
	/**
	 * This method encapsulates the request for a full harvest of Identifiers
	 * from the repository
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
		
		return this.repositoryAnswer (url, "ListIdentifiers", "&metadataPrefix=" + metaDataFormat);
	}
	
	/**
	 * This method encapsulates the request for an update harvest of Identifiers
	 * from the repository or the ResumptionToken Request
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
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("listIdentifiers");
		
		if (metaDataFormat.equalsIgnoreCase ("resumptionToken")) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listIdentifiers with ResumptionToken and MetaDataFormat " + metaDataFormat);
			
			String resumptionToken = updateFrom;
			
			return this.repositoryAnswer (url, "ListIdentifiers", "&resumptionToken=" + resumptionToken);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("listIdentifiers with MetaDataFormat " + metaDataFormat + " from " + updateFrom);
			
			return this.repositoryAnswer (url, "ListIdentifiers", "&metadataPrefix=" + metaDataFormat + "&from=" + updateFrom);
		}
	}
	
	/**
	 * This method connects to the Repository and gets information from it. This
	 * information can be the supported Meta Data Formats, the Identifiers or
	 * other.
	 * 
	 * @param url the OAI-URL to connect to
	 * @param verb the OAI-PMH-verb to use
	 * @param parameter the parameters for the request (i.e. from, set, ...)
	 * @return the Inputstream from the repository
	 * @throws IOException
	 * @throws HttpException
	 * @see org.apache.commons.httpclient
	 */
	
	private InputStream repositoryAnswer (String url, String verb, String parameter) throws HttpException, IOException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("repositoryAnswer");
		
		InputStream inst;
		int statuscode = 0;
		HttpClient client = new HttpClient ( );
		GetMethod getmethod = new GetMethod (url + "?verb=" + verb + parameter);
		
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		if (logger.isDebugEnabled ( ))
			logger.debug (url + "?" + getmethod.getQueryString ( ));
		
		try {
			
			statuscode = client.executeMethod (getmethod);
			
		} catch (Exception ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			System.exit (2);
		}
		
		if (statuscode != HttpStatus.SC_OK) {
			
			logger.warn ("HTTP Status Code: " + statuscode);
		}
		
		inst = getmethod.getResponseBodyAsStream ( );
		client = null;
		
		if (inst != null)
			return inst;
		
		else
			throw new HttpException ("ResponseBody is null!");
	}
	
	/**
	 * This method processes the Inputstrean from the repository and extracts
	 * the Object IDs and the ResumptionToke if it exists. It stores all IDs in
	 * a list of ObjectIdentifiers together with its datestamps. The IDs which
	 * are extracted in here are only the external IDs which means the IDs used
	 * by the harvested repository. There's still no relation to IDs in our own
	 * database. This relation is made later in this method
	 * {@link #objectexists(String)}
	 * 
	 * @param responseBody the Inputstream from the Repository
	 * @return the ResumptionToken if any exists, null otherwise
	 * @see #objectexists(String)
	 * @see ObjectIdentifier
	 * @see javax.xml.parsers
	 */
	
	@SuppressWarnings ("unchecked")
	private Element extractIdsAndGetResumptionToken (InputStream responseBody) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("extractIdsAndGetResumptionToken");
		
		Element resumptionToken = null;
		SAXBuilder builder = new SAXBuilder ( );
		
		try {
			
			Document doc = builder.build (responseBody);
			Element root = doc.getRootElement ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("processing headers");
			
			this.processHeaderElements (root);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("retrieving Resumption Token");
			
			resumptionToken = searchFirstChild (root.getChildren ( ), "resumptionToken");
			doc = null;
			
		} catch (ParserConfigurationException pacoex) {
			
			logger.error (pacoex.getLocalizedMessage ( ), pacoex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (SAXException sex) {
			
			logger.error (sex.getLocalizedMessage ( ), sex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (IOException ioex) {
			
			logger.error (ioex.getLocalizedMessage ( ), ioex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (JDOMException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
			
		} catch (java.text.ParseException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			logger.error ("Trying to process what I have, but major processing problem occured!");
		}
		
		builder = null;
		return resumptionToken;
	}
	
	/**
	 * @param children
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws java.text.ParseException
	 */
	
	@SuppressWarnings ("unchecked")
	private void processHeaderElements (Element element) throws ParserConfigurationException, SAXException, IOException, java.text.ParseException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("processHeaderElements");
		
		List <Element> headerlist = searchAllChildren (element.getChildren ( ), "header");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("storing extracted headers");
		
		for (Element header : headerlist) {
			
			this.processHeader (header);
		}
	}
	
	/**
	 * @param element
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws java.text.ParseException
	 */
	
	@SuppressWarnings ("unchecked")
	private void processHeader (Element header) throws ParserConfigurationException, SAXException, IOException, java.text.ParseException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("processHeader");
		
		String headerIdentifier = searchFirstChild (header.getChildren ( ), "identifier").getValue ( );
		String headerDatestamp = searchFirstChild (header.getChildren ( ), "datestamp").getValue ( );
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("headerIdentifier = " + headerIdentifier);
			logger.debug ("headerDatestamp = " + headerDatestamp);
		}
		
		int oid = this.objectexists (headerIdentifier);
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("oid = " + oid);
			logger.debug ("processing objects found in headers");  
		}
		
		if (oid > 0) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("object exists, so we have to look new rawdata exists");
			
			String objectEntryResponse = prepareRestTransmission ("ObjectEntry/" + oid + "/").GetData ( );
			String objectEntryDatestamp = getValueFromKey (objectEntryResponse, "repository_datestamp");
			
			Date objectEntryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (objectEntryDatestamp);
			Date headerDate = new SimpleDateFormat ("yyyy-MM-dd").parse (headerDatestamp);
			
			if (objectEntryDate.before (headerDate)) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("new rawdata is available, so we'll fetch her");
				
				this.storeSingleRecord (this.retrieveRecord (headerIdentifier));
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("no new rawdata. We must only update the datestamps");
				
				this.storeHeaderData (headerIdentifier, headerDatestamp, oid);
			}
			
			objectEntryResponse = null;
			objectEntryDatestamp = null;
			objectEntryDate = null;
			headerDate = null;
			
		} else if (oid == -1) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("object does not exits, so we have to fetch all data");
			
			this.storeSingleRecord (this.retrieveRecord (headerIdentifier));
			
		} else {
			
			logger.error ("Error with number " + oid + "occured while processing Object " + headerIdentifier);
			logger.info ("Skipping object " + headerIdentifier + " and continue with the next one");
		}
		
		headerIdentifier = null;
		headerDatestamp = null;
	}
	
	/**
	 * @param headerIdentifier
	 * @param headerDatestamp
	 * @param oid
	 */
	
	private void storeHeaderData (String headerIdentifier, String headerDatestamp, int oid) {
		
		logger.info ("got Header Data for " + headerIdentifier);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("storing header data (external id, datestamp and internal ID), but nothing more 'cause there no new data");
		
		this.getIds ( ).add (new ObjectIdentifier (headerIdentifier, headerDatestamp, oid, null));
	}
	
	/**
	 * @param headerIdentifier
	 * @return
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	
	@SuppressWarnings("unchecked")
	private Element retrieveRecord (String headerIdentifier) throws ParserConfigurationException, SAXException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("retrieveRecord");
		
		// TODO: allgemeine Methode einbauen (getRepositoryAnswer)
		HttpClient client = new HttpClient ( );
		HttpMethod method = new GetMethod (getRepositoryURL ( ) + "?verb=GetRecord&metadataPrefix=" + this.metaDataFormat + "&identifier=" + headerIdentifier);
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Amount: " + this.getAmount ( ));
			
			if ((this.getRecordCounter++ % this.getAmount ( )) == 0) {
				
				logger.debug ("Going to sleep for " + this.getInterval ( ) + " milliseconds, letting the repository recover a bit");
				Thread.sleep (this.getInterval ( ));
				logger.debug ("Continuing to ask the repository for some records");
				
				this.getRecordCounter = 1;
			}
			
			int statuscode = client.executeMethod (method);
			
			logger.info ("HTTP Status Code: " + statuscode);
			
			if (statuscode != HttpStatus.SC_OK) {
				
				logger.error ("HTTP Status Code: " + statuscode);
			}
			
			Document doc = new SAXBuilder ( ).build (method.getResponseBodyAsStream ( ));
			
			return searchFirstChild (doc.getRootElement ( ).getChildren ( ), "record");
			
		} catch (HttpException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (JDOMException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (InterruptedException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return null;
	}
	
	/**
	 * This is the heart of the object handler. Here all harvested objects are
	 * processed. For every object an object entry will be created if this
	 * object still not exists in our database. Next the rawdata will be
	 * collected and put into the database. When the object already exists the
	 * object will be checked if there's some newer data. If so the newer data
	 * will be put in the database otherwise only the datestamp of the last
	 * harvest-run is set.
	 * @throws UnsupportedEncodingException 
	 * 
	 * @see #createObjectEntry(int)
	 * @see #updateRawData(int)
	 * @see #checkRawData(int)
	 * @see #updateHarvestedDatestamp(int)
	 */
	
	protected void processRecords ( ) throws UnsupportedEncodingException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("processRecords");
		
		if (this.ids.size ( ) < 1) {
			
			logger.info ("No Records to process at all");
			
			this.ids = null;
			return;
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("now we process " + this.ids.size ( ) + " Records");
		
		if (this.isFullharvest ( ))
			this.setFullHarvestDateStamp ( );
		
		for (int objectcounter = 0; objectcounter < this.ids.size ( ); objectcounter++) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Object No. " + objectcounter + " is processed");
			
			if (this.ids.get (objectcounter).getInternalOID ( ) == -1) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("internal OID == -1, so we have to create a new Object");
				
				// when internalOID == -1 than Object is not in the database and
				// we have to create it
				this.createObjectEntry (objectcounter);
				
				// after that we have to upload the new rawdata
				this.updateRawData (objectcounter);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("Object exists, only update rawdata or timestamps");
				
				// Object exists and we have to look if our Rawdata is newer
				// than the database one
				if (this.ids.get (objectcounter).getRawData ( ) != null) {
					
					this.updateRawData (objectcounter);
					
				} else {
					
					this.updateHarvestedDatestamp (objectcounter);
				} // endelse
			} // endelse
		} // endfor
		
		this.ids = null;
	} // end processRecords
	
	/**
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	
	private void setFullHarvestDateStamp ( ) throws UnsupportedEncodingException {
		
		String result = prepareRestTransmission ("Repositories/" + this.getRepositoryID ( ) + "/harvestedtoday/").PostData ("");
		//TODO: result auswerten
	}

	/**
	 * This method created a new object in the database and retrieves the
	 * corresponding ID. For the object-data it will transmit the repository ID,
	 * the external Object Identifier (from the repository), the datestamp from
	 * the repository, the testdata status and the failure counter for this
	 * object. The retrieved internal Object Identifier is stored in the list of
	 * ObjectIdentifieres.
	 * 
	 * @see RestClient
	 * @see ObjectIdentifier
	 */
	
	protected void createObjectEntry (int index) {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("createObjectEntry");
			logger.debug ("we have to create a new Object");
			logger.debug ("creating Object No. " + index + ": " + this.ids.get (index).getExternalOID ( ));
		}
		
		try {
			
			String result = prepareRestTransmission ("ObjectEntry/").PutData (this.createObjectEntryRestMessage (index, 0));
			String value = getValueFromKey (result, "oid");
			
			int intoid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("The internalOID for " + this.ids.get (index).getExternalOID ( ) + " is " + intoid);
			
			this.ids.get (index).setInternalOID (intoid);
			
			result = null;
			value = null;
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
	}
	
	/**
	 * @param datestamp
	 * @param i
	 * @return
	 */
	
	private String createObjectEntryRestMessage (int index, int failurecounter) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("createObjectEntryRestMessage");
		
		RestMessage rms = new RestMessage ( );
		
		rms.setKeyword (RestKeyword.ObjectEntry);
		rms.setStatus (RestStatusEnum.OK);
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (this.ids.get (index).getDatestamp ( ));
		
		String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
		
		RestEntrySet res = new RestEntrySet ( );
		
		res.addEntry ("repository_id", Integer.toString (this.getRepositoryID ( )));
		res.addEntry ("repository_identifier", this.ids.get (index).getExternalOID ( ));
		res.addEntry ("repository_datestamp", datestamp);
		res.addEntry ("testdata", Boolean.toString (this.isTestData ( )));
		res.addEntry ("failureCounter", Integer.toString (failurecounter));
		res.addEntry ("peculiar", Boolean.toString (false));
		res.addEntry ("outdated", Boolean.toString (false));
		
		rms.addEntrySet (res);
		
		String requestxml = RestXmlCodec.encodeRestMessage (rms);
		
		if (logger.isDebugEnabled ( )) 
			logger.debug ("xml: " + requestxml);
		
		res = null;
		rms = null;
		
		return requestxml;
	}
	
	/**
	 * This method only sets a new datestamp for the given Object.
	 * 
	 * @param index index of the current ObjectIdentifier
	 */
	
	private void updateHarvestedDatestamp (int index) {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("updateHarvestedDatestamp");
			logger.debug ("We must update the harvested datestamp only");
		}
		
		String postObjectEntryResult = "";
		
		try {
			
			postObjectEntryResult = prepareRestTransmission ("ObjectEntry/" + this.ids.get (index).getInternalOID ( ) + "/").PostData (this.createObjectEntryRestMessage (index, 0));
			
		} catch (UnsupportedEncodingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		String value = getValueFromKey (postObjectEntryResult, "oid");
		
		int intoid = new Integer (value);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("internalOID: " + intoid + " has been successfully updated");
		
		postObjectEntryResult = null;
	}
	
	/**
	 * This method puts new RawData in the Database.
	 * 
	 * @param index
	 * @throws IOException
	 * @throws HttpException
	 */
	
	protected void updateRawData (int index) {

		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("updateRawData");
			logger.debug ("Now we're going to update the RawData");
		}
		
		ObjectIdentifier currentObject = this.getIds ( ).get (index);
		
		try {
			
			GregorianCalendar cal = new GregorianCalendar ( );
			cal.setTime (currentObject.getDatestamp ( ));
			
			// First the Keyword plus the 2 Options is generated. Something like
			// RawRecordData/1970-01-01/oai_dc
			// With this Information a connection to the RestServer is provided,
			// which have to be told what to do: PutData
			// Put transmits the received data from the Repository, which is a
			// stream, so this stream has to be converted
			// to a UTF-8-encoded string. Finally this string has to be encoded
			// with Base64. After that the utf-8-base64-string will be sent.
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("putRawRecordDataResult");
			
			String putRawRecordDataResult = prepareRestTransmission (
					"RawRecordData/" + currentObject.getInternalOID ( ) + "/"
							+ cal.get (Calendar.YEAR) + "-"
							+ (cal.get (Calendar.MONTH) + 1) + "-"
							+ cal.get (Calendar.DAY_OF_MONTH) + "/"
							+ this.metaDataFormat + "/").PutData (
									ObjectIdentifier.encodeRawData (currentObject
									.getRawDataAsString ( )));
			
			// TODO: putRawRecordDataResult auswerten und bei Fehler nicht weitermachen, sondern FailureCounter hochsetzen
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("postObjectEntryResult");
			
			String postObjectEntryResult = prepareRestTransmission (
					"ObjectEntry/" + currentObject.getInternalOID ( ) + "/")
					.PostData (createObjectEntryRestMessage (index, 0));
			
			// TODO: repsonse decodieren und auswerten (status-value,
			// description, ...)
			
			if (logger.isDebugEnabled ( )) {
				
				logger.debug ("uploaded rawdata for Database Object " + currentObject.getInternalOID ( ));
				logger.debug ("ObjectEntry with internalOID: " + currentObject.getInternalOID ( ) + " has been successfully updated");
			}
			
			// Retrieving ServiceID
			String getServicesResult = prepareRestTransmission (
					"Services/byName/Harvester/").GetData ( );
			
			RestMessage rms = RestXmlCodec.decodeRestMessage (getServicesResult);
			RestEntrySet res = rms.getListEntrySets ( ).get (0);
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			
			BigDecimal thisServiceID = new BigDecimal (0);
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key: " + key + " value: " + res.getValue (key));
				
				if (key.equalsIgnoreCase ("service_id")) {
					
					thisServiceID = new BigDecimal (res.getValue (key));
					continue;
					
				} else if (key.equalsIgnoreCase ("name")) {
					
					if (!res.getValue (key).equalsIgnoreCase ("Harvester"))
						logger.warn ("Should read Harvester and read " + res.getValue (key) + " instead!");
					
					continue;
					
				} else {
					
					logger.warn ("Unknown RestMessage key received: " + res.getValue (key));
					
					continue;
				}
			}
			
			// Updating WorkflowDB
			rms = new RestMessage ( );
			
			rms.setKeyword (RestKeyword.WorkflowDB);
			rms.setStatus (RestStatusEnum.OK);
			
			res = new RestEntrySet ( );
			
			res.addEntry ("object_id", Integer.toString (this.ids.get (index).getInternalOID ( )));
			res.addEntry ("service_id", thisServiceID.toPlainString ( ));
			rms.addEntrySet (res);
			
			String requestxml = RestXmlCodec.encodeRestMessage (rms);
			
			String putWorkflowDBResult = new String ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("putWorkflowDBResult");
			
			try {
				
				putWorkflowDBResult = prepareRestTransmission ("WorkflowDB/").PutData (requestxml);
				
			} catch (UnsupportedEncodingException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
			
			rms = null;
			res = null;
			
			String value = getValueFromKey (putWorkflowDBResult, "workflow_id");
			
			if (value == null) {
				
				logger.error ("I can not update the RawData because an error occured. Skipping this object and continue with next.");
				
				return;
			}
			
			int workflowid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("workflow_id for OID " + this.ids.get (index).getInternalOID ( ) + " is: " + workflowid);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		logger.info ("stored Data for object " + this.ids.get (index).getExternalOID ( ) + " --> " + this.ids.get (index).getInternalOID ( ));
	}
	
	/**
	 * @param string
	 * @return
	 */
	
	private RestClient prepareRestTransmission (String resource) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("prepareRestTransmission");
		
		return RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
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

		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("objectexists?");
			logger.debug ("We are going to have a look if the object with the externalOID " + externalOID + " in repository nr. " + this.getRepositoryID ( ) + " already exists");
			logger.debug ("Properties: " + this.getProps ( ).getProperty ("host"));
		}
		
		String result = this.prepareRestTransmission ("ObjectEntryID/" + this.getRepositoryID ( ) + "/" + externalOID + "/").GetData ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (result);
		
		String value = this.getValueFromKey (result, "oid");
		
		int intoid = -1;
		
		if (value == null)
			intoid = -1;
		
		else intoid = new Integer (value);
		
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
			
			if (rms.getStatus ( ) == RestStatusEnum.NO_OBJECT_FOUND_ERROR)
				logger.info ("Object does not exist, we'll care about this");
			
			else logger.error ("Rest-Error occured: " + rms.getStatusDescription ( ));
			
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
		
		if (optionValue != null)
			Harvester.harvester.setInterval (new Integer (optionValue));
		
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
			
			if ((new Integer (dateComponents [0]) < 1970) || (new Integer (dateComponents [0]) > new Integer (today [0]))) {
				
				logger.error ("Year must be between 1970 and " + today [0] + ".\nYou asked for the year " + dateComponents [0]);
				System.exit (10);
				
			} else if ((new Integer (dateComponents [1]) < 1) || (new Integer (dateComponents [1]) > 12)) {
				
				logger.error ("The year has only 12 months. You asked for month number " + dateComponents [1]);
				System.exit (10);
				
			} else if ((new Integer (dateComponents [2]) < 1) || (new Integer (dateComponents [2]) > 31)) {
				
				logger.error ("Months have the max of 31, 30, 29 or 28 days. You asked for day number " + dateComponents [2]);
				System.exit (10);
				
			} else {
				
				java.sql.Date date2test = null;
				
				try {
					
					date2test = HelperMethods.extract_datestamp (optionValue);
					
				} catch (java.text.ParseException ex) {
					
					logger.error ("Can't extract Datestamp from parameter 'updateDate'", ex);
					System.exit (10);
				}
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("filtered Date: " + date2test.toString ( ));
				
				harvester.setDate (date2test.toString ( ));
				
				return date2test.toString ( );
			}
			
		} else {
			
			logger.error ("Date Format wrong! Must be yyyy-mm-dd and not " + optionValue);
			System.exit (10);
		}
		
		return null;
	}
	
	/**
	 * The option whether a full or only an update harvest will be processed
	 * will be discovered. If update is specified the date from which on has to
	 * be set, otherwise an error occurs.
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
			
			if (!optionValue.equalsIgnoreCase ("update")) {
				
				logger.warn ("neither 'full' nor 'update' as harvest-type set, guessing 'update'");
			}
			
			if (cmd.hasOption ('d')) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("full harvest: " + Boolean.FALSE);
				
				harvester.setFullharvest (false);
				
				return false;
				
			} else throw new ParseException ("UpdateHarvest without a Date from which on the harvest shall be start isn't allowed!");
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
	 * 
	 * @param optionValue
	 *            the repository URL
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
