/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.utils.HelperMethods;

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
	
	private ArrayList <ObjectIdentifier> ids;
	
	/**
	 * The static log4j logger. All logging will be made with this nice static logger.
	 */
	
	static Logger logger = Logger.getLogger (Harvester.class);
	
	/**
	 * Properties. 
	 */
	
	private int recordno = 0;
	
	private Properties props;

	private int errorretry = 0;
		
	/**
	 * Standard Constructor which initialises the log4j and loads necessary properties.
	 */
	
	public Harvester ( ) {
		
		DOMConfigurator.configure ("log4j.xml");
		this.props = HelperMethods.loadPropertiesFromFile ("harvesterprop.xml");
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
					
					Harvester harvester = new Harvester ( );
					
					/* 
					 * firstly we have to collect some data from the repository, which have to be processed
					 * when we finished processing all these data, we have the IDs form all records we have
					 * to collect. This is the next step: we catch the records for all the IDs and put the
					 * raw datas into the database, if they don't exist or newer than in the database.
					 */
					
					harvester.processIds (url, fullharvest, updateDate, id);
					harvester.processRecords (url, id);
					
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

	/**
	 * The date which comes from the command line will be filtered and observed.
	 * 
	 * @param optionValue the date string which is filtered
	 * @return the proper date string when it could be extracted null otherwise
	 */
	
	private static String filterDate (String optionValue) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("filtered Date: " + optionValue);
		
		//TODO: proper implementation
		
		return optionValue;
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
	 * ensures the right value for the repository ID.
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
	
	private void processIds (String url, boolean fullharvest, String updateFrom, int repositoryId ) {
		
		HttpClient client = new HttpClient ( );
		GetMethod getmethod;
		String resumptionToken = null;
		Boolean resumptionSet = true;
		
		if (!fullharvest) {
			
			getmethod = new GetMethod (url + "?verb=ListIdentifiers&metadataPrefix=oai_dc&from=" + updateFrom);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Update Harvest: " + url + getmethod.getQueryString ( ));
			
		} else {
			
			getmethod = new GetMethod (url + "?verb=ListIdentifiers&metadataPrefix=oai_dc");
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Full Harvest: " + url + getmethod.getQueryString ( ));
		}
		
		while (resumptionSet) {
		
			client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
			
			try {
				
				int statuscode = client.executeMethod (getmethod);
				
				logger.info ("ID-List HttpStatusCode: " + statuscode);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					//TODO: proper implementation
					;
				}
				
				resumptionToken = extractIds (getmethod.getResponseBodyAsStream ( ), repositoryId);
				
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
						
						getmethod = new GetMethod (url + "?verb=ListIdentifiers&resumptionToken=" + resumptionToken);
						
						if (logger.isDebugEnabled ( )) {
							
							logger.debug ("ResumptionToken: " + resumptionToken);
							logger.debug ("ResumptionQuery: " + url + getmethod.getQueryString ( ));
							
						} else;
					}
					
				} else {
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("no ResumptionToken found, IdentifierList complete");
					
					else;
					
					resumptionSet = false;
				}
			
			} catch (HttpException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
				this.errorretry++;
				
				if (errorretry > 5)
					System.exit (5);
				
			} catch (IOException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
				
				this.errorretry++;
				
				if (errorretry > 5)
					System.exit (5);
			}
		}
	}

	/**
	 * @param responseBody
	 * @param repositoryId
	 * @return
	 */
	
	private String extractIds (InputStream responseBody, int repositoryId) {
		
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
					
					//TODO: better error-handling
					logger.error ("Error occured!");
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
	
	private void processRecords (String url, int repositoryID) {
		
		HttpClient client;
		GetMethod method;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("now we process " + this.ids.size ( ) + "Records");
		
		for (int i = 0; i < this.ids.size ( ); i++) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Object No. " + i + " is processed");
			
			int internalOID = this.ids.get (i).getInternalOID ( );
			
			if (internalOID == -1) {
				
				// when internalOID == -1 than Object is not in the database and we have to create it
				
				String ressource = "ObjectEntry/";
				RestClient restClient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
				
				GregorianCalendar cal = new GregorianCalendar ( );
				cal.setTime (this.ids.get (i).getDatestamp ( ));
				
				String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("creating Object No. " + i + ": " + this.ids.get (i).getExternalOID ( ));
				
				try {
					
					List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
					HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
					mapEntry.put ("repository_id", Integer.toString (repositoryID));
					mapEntry.put ("repository_identifier", this.ids.get (i).getExternalOID ( ));
					mapEntry.put ("repository_datestamp", datestamp);
					listentries.add (mapEntry);
					
					String requestxml = RestXmlCodec.encodeEntrySetRequestBody (listentries);
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("xml: " + requestxml);
					
					String result = restClient.PutData (requestxml);
					
					listentries = null;
					mapEntry = null;
					
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
					
					this.ids.get (i).setInternalOID (intoid);
					
				} catch (IOException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
			} else {
				
				// Object exists and we have to look if our Rawdata is newer than the database one
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("observing Object No. " + i + ": " + this.ids.get (i).getExternalOID ( ));
				
				String ressource = "ObjectEntry/" + internalOID + "/";
				RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
				
				String result = restclient.GetData ( );
				
				List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
				HashMap <String, String> mapEntry = new HashMap <String ,String> ( );

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
					
					if (repositoryDate.before (this.ids.get (i).getDatestamp ( )) || repositoryDate.equals (this.ids.get (i).getDatestamp ( ))) {
						
						if (logger.isDebugEnabled ( )) {
							
							logger.debug ("RepositoryDate is " + repositoryDate + " and before or equal the harvested: " + this.ids.get (i).getDatestamp ( ));
						}
						//TODO: implement proper change of harvested-timestamp
						
						continue;
					}
					
					//TODO: rewrite XML
					
/*					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
					DocumentBuilder builder = factory.newDocumentBuilder ( );
					Document document = builder.parse (new InputSource (new StringReader(result)));
					
					NodeList idDatestampList = document.getElementsByTagName ("repository_datestamp");
					
					Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (idDatestampList.item (1).getTextContent ( ));
					
					if (repositoryDate.before (this.ids.get (i).getDatestamp ( )) || repositoryDate.equals (this.ids.get (i).getDatestamp ( ))) {
						
						if (logger.isDebugEnabled ( )) {
							
							logger.debug ("RepositoryDate is " + repositoryDate + " and before or equal the harvested: " + this.ids.get (i).getDatestamp ( ));
						}
						//TODO: implement proper change of harvested-timestamp
						
						continue;
						
					}
				} catch (SAXException saex) {
					
					saex.printStackTrace ( );
					
				} catch (ParserConfigurationException ex) {
					
					ex.printStackTrace ( );
					
				} catch (IOException ex) {
					
					ex.printStackTrace ( );
					
				} catch (DOMException ex) {
					
					ex.printStackTrace ( );*/
					
				} catch (java.text.ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
			}
			
			client = new HttpClient ( );
			method = new GetMethod (url + "?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + ids.get (i).getExternalOID ( ));
			client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
			
			try {
				
				int statuscode = client.executeMethod (method);
				
				logger.info ("HttpStatusCode: " + statuscode);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					;
				}
				
				uploadRawData (HelperMethods.stream2String (method.getResponseBodyAsStream ( )), ids.get (i).getInternalOID ( ), ids.get (i).getDatestamp ( ));
				
			} catch (HttpException ex) {
				
				ex.printStackTrace ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
								
			} finally {
				
				method.releaseConnection ( );
			}
			
			client = null;
		}
	}

	/**
	 * @param data
	 * @param internalOID
	 * @param datestamp
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private void uploadRawData (String data, int internalOID, Date datestamp) throws HttpException, IOException {
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (datestamp);
		
		String resource = "RawRecordData/" + internalOID + "/" + cal.get (Calendar.YEAR) + "-" + cal.get (Calendar.MONTH + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH) + "/";
		
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		restclient.PutData (data);
		
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
