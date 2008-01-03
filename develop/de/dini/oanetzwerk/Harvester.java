/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael Kühn
 *
 */

public class Harvester {
	
	private ArrayList <ObjectIdentifier> ids;
	static Logger logger = Logger.getLogger (Harvester.class);
	private Properties props;
	@SuppressWarnings("unused")
	private String resumptionToken = "";
	
	public Harvester ( ) {
		
		DOMConfigurator.configure ("log4j.xml");
		this.props = HelperMethods.loadPropertiesFromFile ("/home/mkuehn/workspace/oa-netzwerk-develop/harvesterprop.xml");
	}
	
	/**
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
					
					Harvester harvester = new Harvester ( );
					
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
	 * @param optionValue
	 * @return
	 */
	private static String filterDate (String optionValue) {
		
		return optionValue;
	}

	/**
	 * @param optionValue
	 * @param cmd 
	 * @return
	 * @throws ParseException 
	 */
	
	private static boolean filterBool (String optionValue, CommandLine cmd) throws ParseException {
		
		if (optionValue.equalsIgnoreCase ("full"))
			return true;
		
		else {
			
			if (cmd.hasOption ('d') || cmd.hasOption ("updateDate"))
				return false;
			
			else throw new ParseException ("UpdateHarvest without a Date from which on the harvest shall be start isn't allowed!");
		}
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
			
			// zuerst schauen, ob datestamp gleich, wenn ja, harvested auf today setzen, ansonsten metadaten putten
			
			if (this.ids.get (i).getInternalOID ( ) == 0) {
				
				String ressource = "ObjectEntry/";
				RestClient restClient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
				
				ObjectIdentifier tempobjid = this.ids.get (i);
				
				String result = restClient.GetData ( );
				
				try {
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
					DocumentBuilder builder = factory.newDocumentBuilder ( );
					Document document = builder.parse (new InputSource (new StringReader (result)));
					
					NodeList internalOIDList = document.getElementsByTagName ("IOD");
					
					tempobjid.setInternalOID (new Integer(internalOIDList.item (1).getTextContent ( )));
					
					this.ids.set (i, tempobjid);
					
				} catch (SAXException saex) {
					
					saex.printStackTrace ( );
					
				} catch (ParserConfigurationException ex) {
					
					ex.printStackTrace ( );
					
				} catch (IOException ex) {
					
					ex.printStackTrace ( );
				}
				
			} else {
				
				String ressource = "RawRecordData/" + this.ids.get (i).getInternalOID ( ) + "/";
				RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
				
				String result = restclient.GetData ( );
				
				try {
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
					DocumentBuilder builder = factory.newDocumentBuilder ( );
					Document document = builder.parse (new InputSource (new StringReader(result)));
					
					NodeList idDatestampList = document.getElementsByTagName ("datestamp");
					
					Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (idDatestampList.item (1).getTextContent ( ));
					
					if (repositoryDate.before (this.ids.get (i).getDatestamp ( )) || repositoryDate.equals (this.ids.get (i).getDatestamp ( ))) {
						
						//TODO: implement proper change of harvested-timestamp
						
						return;
						
					}
				} catch (SAXException saex) {
					
					saex.printStackTrace ( );
					
				} catch (ParserConfigurationException ex) {
					
					ex.printStackTrace ( );
					
				} catch (IOException ex) {
					
					ex.printStackTrace ( );
				} catch (DOMException ex) {
					
					ex.printStackTrace();
				} catch (java.text.ParseException ex) {
					
					ex.printStackTrace();
				}
			}
			
			client = new HttpClient ( );
			method = new GetMethod (url + "?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + ids.get (i).getId ( ));
			client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
			
			try {
				
				int statuscode = client.executeMethod (method);
				
				logger.info ("HttpStatusCode: " + statuscode);
				
				if (statuscode != HttpStatus.SC_OK) {
					
					;
				}
				
				deliverResult2DB (HelperMethods.stream2String (method.getResponseBodyAsStream ( )), ids.get (i).getId ( ), ids.get (i).getDatestamp ( ));
				
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
	 * @param oid
	 * @param datestamp
	 * @throws IOException 
	 * @throws HttpException 
	 */
	
	private void deliverResult2DB (String data, String oid, Date datestamp) throws HttpException, IOException {
		
		GregorianCalendar cal = new GregorianCalendar ( );
		cal.setTime (datestamp);
		
		String resource = "RawRecordData/" + oid + "/" + cal.get (Calendar.YEAR) + "-" + cal.get (Calendar.MONTH + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH) + "/";
		
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		restclient.PutData (data);
	}

	/**
	 * @param url 
	 * @param fullharvest 
	 * @param updateFrom 
	 * @param repositoryId 
	 * 
	 */
	
	private void processIds (String url, boolean fullharvest, String updateFrom, int repositoryId ) {
		
		HttpClient client = new HttpClient ( );
		GetMethod getmethod;
		
		if (!fullharvest)
			getmethod = new GetMethod (url + "?verb=ListIdentifiers&metadataPrefix=oai_dc&from=" + updateFrom);
			
		else
			getmethod = new GetMethod (url + "?verb=ListIdentifiers&metadataPrefix=oai_dc");
		
		
		client.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		
		try {
			
			int statuscode = client.executeMethod (getmethod);
			
			logger.info ("HttpStatusCode: " + statuscode);
			
			if (statuscode != HttpStatus.SC_OK) {
				
				;
			}
			
			extractIds (getmethod.getResponseBodyAsStream ( ), repositoryId);
		
		} catch (HttpException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
		}
	}
	
	private void extractIds (InputStream responseBody, int repositoryId) {
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (responseBody);
			
			NodeList idNodeList = document.getElementsByTagName ("identifier");
			NodeList datestampNodeList = document.getElementsByTagName ("datestamp");
			
			this.ids = new ArrayList <ObjectIdentifier> ( );
			
			for (int i = 1; i < idNodeList.getLength ( ); i++) {
				
				int internalOID;
				String externalOID = idNodeList.item (i).getTextContent ( );
				String datestamp = datestampNodeList.item (i).getTextContent ( );
				
				if (logger.isDebugEnabled ( )) {
					
					logger.debug ("List Record No. " + i + " " + externalOID  + " " + datestamp);
				}
				
				internalOID = objectexists (repositoryId, externalOID);
				
				if (internalOID > 0)
					ids.add (new ObjectIdentifier (externalOID, datestamp, internalOID));
				
				else {
					
					//TODO: better error-handling
					logger.error ("Error occured!");
				}
			}
			
			NodeList rsList = document.getElementsByTagName ("resumptionToken");
			
			//TODO: implement the proper use of the resumption Token!
			
			if (resumptionToken != "")
				resumptionToken = rsList.item (0).getTextContent ( );
			
		} catch (ParserConfigurationException pacoex) {
			
			pacoex.printStackTrace ( );
			
		} catch (SAXException sex) {
			
			sex.printStackTrace ( );
			
		} catch (IOException ioex) {
			
			ioex.printStackTrace ( );
		}		
	}

	/**
	 * @param oid
	 * @param datestamp 
	 * @param  
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	private int objectexists (int repositoryId, String externalOID) throws ParserConfigurationException, SAXException, IOException {
		
		String ressource = "ObjectEntryID/" + repositoryId + "/" + externalOID + "/";
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), ressource, this.props.getProperty ("username"), this.props.getProperty ("password"));
		
		String result = restclient.GetData ( );
		
		//ressource = "RawRecordData/" + externalOID + "/";
		
		if (logger.isDebugEnabled ( ))
			logger.debug (result);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
		DocumentBuilder builder = factory.newDocumentBuilder ( );
		Document document = builder.parse (new InputSource (new StringReader(result)));
		
		if (document.getElementsByTagName ("NULL").getLength ( ) > 0)
			return 0;
		
		else {
			
			NodeList OIDNodeList = document.getElementsByTagName ("OID");
		
			if (OIDNodeList.getLength ( ) != 1)
				throw new SAXException ("too many datestamps in raw-data-record from database!");
		
			else
				return new Integer (OIDNodeList.item (0).getTextContent ( ));
		}
	}
}

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
	
	final String getId ( ) {
	
		return this.externalOID;
	}

	/**
	 * @param id the id to set
	 */
	
	final void setId (String id) {
	
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
