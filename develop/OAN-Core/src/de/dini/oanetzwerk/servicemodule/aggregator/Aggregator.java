/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.MetadataFormatType;
import de.dini.oanetzwerk.utils.exceptions.AggregationFailedException;
import de.dini.oanetzwerk.utils.exceptions.AggregationWarningException;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;


/**
 * @author Manuel Klatt-Kafemann
 * 
 * Aggregator: extracts metadata and stores it in the db
 */
public class Aggregator {

	/**
	 * This is an ArrayList of ObjectIdentifiers where necessary information
	 * about the objects is stored
	 * 
	 * @see de.dini.oanetzwerk.ObjectIdentifier
	 */

	// private ArrayList <ObjectIdentifier> ids;
	/**
	 * The static log4j logger. All logging will be made with this nice static
	 * logger.
	 */

	static Logger logger = Logger.getLogger(Aggregator.class);
	static Logger aggrStateLog = Logger.getLogger("AggregationState");
	
	/**
	 * Properties.
	 */

	private int currentRecordId = 0; // stores object_id which is currently
										// being worked on
	private String time = null; // stores timestamp of this object

	private Properties props; // special aggregator settings like connecting
								// server

	private boolean testing = false; // if set to true, aggregator stores data, but no update to workflow is saved

	private BigDecimal serviceID;
		
	private boolean stopped = false;
	
	private String propertyFilePath = "";
	
	private final Properties getProps ( ) {
		
		return this.props;
	}
	
	
	/**
	 * 
	 * Initialisierung-Routine, die von den verschiedenen Konstruktoren aufgerufen wird
	 * 
	 * bindet Properties korrekt ein und liest SERVICE-ID aus
	 */
	private  void init() {
		DOMConfigurator.configure("log4j.xml");

		try {

			System.out.println("path: " + propertyFilePath);
			this.props = HelperMethods
					.loadPropertiesFromFile(propertyFilePath + "aggregatorprop.xml");

		} catch (InvalidPropertiesFormatException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();

		} catch (FileNotFoundException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();

		} catch (IOException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		}		
		
		this.serviceID = getServiceID("Aggregator");
		
	}
	
	
	/**
	 * Standard Constructor which initialises the log4j and loads necessary
	 * properties.
	 */
	public Aggregator() {
		this.init();

	}
	
	public Aggregator(String propFilePath) {
		
		this.propertyFilePath = propFilePath;
		this.init();

	}

	/**
	 * Special Constructor which initialises the log4j and loads necessary
	 * properties.
	 * 
	 * Sets Testing-Mode.
	 */	
	public Aggregator(boolean testing) {
		this.init();
		this.testing = testing;
	}
	
	
	
	/**
	 * Startes automatic aggregation mode.
	 * 
	 * Checks in DB, which objects have been harvested and need to be aggregated, then runs
	 * StartSingleMode using the aquired IDs.
	 */
	public void startAutoMode(boolean complete) {
		
		boolean newObjects = true;

		while (newObjects == true) {

			// newObjects auf false setzen, damit es zu keiner Endlosschleife
			// kommt
			newObjects = false;

			// complete = true => alle Objekte erneut beabeiten
			if (complete == true)
				if (logger.isDebugEnabled())
					logger.debug("Aggregator AutoMode Complete cycle started");

			
			// zuerst muss die Workflow-DB nach Arbeitsobjekten befragt werden
			if (logger.isDebugEnabled())
				logger.debug("Aggregator AutoMode cycle started");

			// TODO: hardcoded ServiceID !!!
			String serviceId = "2";

			String ressource = "WorkflowDB/" + serviceId; // + id;
			if (complete == true) {
				ressource = ressource + "/completeRebuild";
			}
			RestClient restclient = prepareRestTransmission(ressource);

			// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus
			// dem
			// XML extrahiert werden
			
			if (logger.isDebugEnabled()) logger.debug("BEFORE GET WorkflowDB/" + serviceId);
			RestMessage msgGetWFResponse = restclient.sendGetRestMessage();
			if (logger.isDebugEnabled()) logger.debug("AFTER GET WorkflowDB/" + serviceId);
			
			if (msgGetWFResponse.getStatus() != RestStatusEnum.OK) {
				// TODO: Was nun?
				logger.error("WorkflowDB response failed: "
						+ msgGetWFResponse.getStatus() + "("
						+ msgGetWFResponse.getStatusDescription() + ")");
				return;
			}

			List<RestEntrySet> listEntrySets = msgGetWFResponse
					.getListEntrySets();

			for (RestEntrySet entrySet : listEntrySets) {

				Iterator<String> it = entrySet.getKeyIterator();
				String key = "";
				String value = "";

				String id = null;
				String time = null;

				while (it.hasNext()) {

					key = it.next();
					logger.debug("key: " + key);
					// hier wird die Rueckgabe überprüft, ist es eine object_id,
					// dann muss diese bearbeitet werden
					if (key.equalsIgnoreCase("object_id")) {

						// der Aggregator sollte sich nciht automatisch wieder erneut aufrufen, da er sonst bei Fehlern
						// in einer Endlosschleife verharrt
//						newObjects = true;
						id = entrySet.getValue(key);
						logger.debug("recognized value: " + id);
						if (id.equals("")) {
							logger
									.error("OID from Workflow expected, but was: "
											+ value);
						}
					}
					if (key.equalsIgnoreCase("time")) {
						time = entrySet.getValue(key);
						logger.debug("recognized value: " + time);
						if (time.equals("")) {
							logger
									.error("time-value from Workflow expected, but was: "
											+ value);
						}
					}
					if (id != null && time != null && !id.equals("")
							&& !time.equals("")) {
						startSingleRecord((new Integer(id).intValue()), time);
					}

				}
			}
		}

		logger.debug("# deleteUnneededData() after Automode");
		deleteUnneededData();
		
	}

	public void deleteUnneededData() {
				
		// neue Anfrage auf Delete zum Bereinigen der Datenbank
		RestClient restclient = prepareRestTransmission("UnneededData");
		
		RestMessage msgDeleteResponse = restclient.sendDeleteRestMessage();
		
		if(msgDeleteResponse.getStatus() != RestStatusEnum.OK) {
			logger.error("# delete UnneededData not OK : " + msgDeleteResponse.getStatus() + " : " + msgDeleteResponse.getStatusDescription());
		}
			
	}
	
	/*
	 * implements workflow for a single entry
	 * 
	 * @param if
	 *            object_id that the aggregator is supposed to examine
	 * @return void
	 * 
	 */
	public void startSingleRecord(int id , String time) {

		this.currentRecordId = id;
		this.time = time;

		logger.debug("StartSingleRecord:  RecordId=" + this.currentRecordId + "\ttime="+time);
		aggrStateLog.info("OID: " + id + " - START");
		
		String data = null;
		InternalMetadata imf = null;

		try {

			// Abfolge
			// 1. Laden der Rohdaten
			
			data = loadRawData(this.currentRecordId);			
			
			if (data == null) {
				// Daten für dieses Objekt konnten nicht geladen werden
				throw new AggregationFailedException("loadRawData not successful");
			}

			// 2. Auseinandernehmen der Rohdaten
			if (logger.isDebugEnabled()) {
				logger.debug("retrieved data (Base64-coded): " + data);
			}
			data = decodeBase64(data);
			if (data == null) {
				// keine decodierten Rohdaten vorhanden, eine weitere
				// Bearbeitung macht keinen Sinn
				throw new AggregationFailedException("decodeBase64 failed");
			}

			// 3. Prüfen der Codierung der Rohdaten
			data = checkEncoding(data);
			if (data == null) {
				// beim Check des Encoding trat ein Fehler auf, keine weitere
				// Behandlung möglich
				return;
			}

			// 4. XML-Fehler müssen behoben werden
			data = checkXML(data);
			if (data == null) {
				// beim Prüfen auf XML-Fehler trat ein Fehler auf, keine weitere
				// Bearbeitung möglich
				return;
			}
			// 5. Schreiben der bereinigten Rohdaten
			data = storeCleanedRawData(data);
			if (data == null) {
				// die bereinigten Rohdaten konnten nicht gespeichert werden,
				// eine
				// weitere Bearbeitung sollte nicht erfolgen
				return;
			}

			// Auslesen der Metadaten
			if (logger.isDebugEnabled()) logger.debug("BEFORE extractMetaData");
			imf = extractMetaData(data);
			if (logger.isDebugEnabled()) logger.debug("AFTER extractMetaData");

			if (imf == null || imf.isEmpty()) {
				// die Metadaten konnten nicht ausgelesen werden, keine weitere Bearbeitung sinnvoll
				throw new AggregationFailedException("extracted IMF-Object was null or empty");
			}

			// schreiben der Metadaten
			try {
				imf = storeMetaData(imf);
			} catch (AggregationWarningException aggw) {
				aggrStateLog.error("OID " + id + " - WARNING:" + aggw.getLocalizedMessage());
			}

			// Zustandsänderung für dieses Objekt speichern, falls es sich nicht
			// um einen Testdurchlauf handelt
			setAggregationCompleted(id);
			aggrStateLog.info("OID " + id + " - OK");
			
		} catch (AggregationFailedException ex) {
	      aggrStateLog.fatal("OID " + id + " - Exception: " + ex.getLocalizedMessage());
		}
		this.time = null;
		this.currentRecordId = 0;
		

	}

	/**
	 * @param data
	 *            Daten, die als Base64-String ankommen und decodiert werden
	 *            sollen
	 * @return byte[] enthält base64-decodierten String
	 */
	private String decodeBase64(String data) {
		// Daten müssen Base64-decodiert werden

		String result = null;
		
		try {
			result = new String(Base64.decodeBase64(((String) data).getBytes("UTF-8")));
			if (logger.isDebugEnabled()) {
				logger.debug("Decoded blob begin:\n\n " + result + "\n\nDecoded Blob end");
			}
		} catch (Exception e) {
			logger.error("decodeBase64 : ioException\n");
			e.printStackTrace();

		}
		return result;
	}

	
	/**
	 * Searches in DB for the service-id
	 * 
	 * @param string Name of this Service
	 * @return BigDecimal ID of the service with the given name
	 */	
	private BigDecimal getServiceID(String name) {
		String result;
		
		// Retrieving ServiceID
		
		result = prepareRestTransmission("Services/byName/"+name+"/").GetData();
		
		//restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		//result = restclient.GetData ( );
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no ServiceIDs at all from the server");
			System.exit (1);
		}
		
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
				
				if (!res.getValue (key).equalsIgnoreCase ("Aggregator"))
					logger.warn ("Should read Aggregator and read " + res.getValue (key) + " instead!");
				
				continue;
				
			} else {
				
				logger.warn ("Unknown RestMessage key received: " + res.getValue (key));
				continue;
			}
		}
		
		return thisServiceID;
	}

	/**
	 * @param string
	 * @return
	 */
	private RestClient prepareRestTransmission (String resource) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("prepareRestTransmission");
		
		return HelperMethods.prepareRestTransmission(new File(propertyFilePath + "restclientprop.xml"), resource, props);
	}

	
	/**
	 * 
	 * setzt Eintrag für aktuelles Objekt in der Workflow-DB
	 * 
	 * wenn Testing == True ist, wird kein Eintrag eingefügt 
	 * (entsprechender Konstruktor muss aufgerufen werden)
	 * @param id ID des zu bearbeitenden Objekts
	 */	
	private void setAggregationCompleted(int id) {
		if (this.testing == true) {
			// Workflow shall not be updated
			return;
		} else {
			// Updating WorkflowDB
			
			//resource = "WorkflowDB/";
			
			logger.debug("set aggregation completed for " + id);
			RestMessage rms;
			RestEntrySet res;
			String result;
			
			rms = new RestMessage ( );
			
			rms.setKeyword (RestKeyword.WorkflowDB);
			rms.setStatus (RestStatusEnum.OK);
			
			res = new RestEntrySet ( );
			
			res.addEntry ("object_id", Integer.toString (id));
			res.addEntry ("service_id", this.serviceID.toPlainString ( ));
			res.addEntry ("time", this.time);
			rms.addEntrySet (res);
			
			String requestxml = RestXmlCodec.encodeRestMessage (rms);
			
			//restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
			
			result = new String ( );
			
			try {
				
				if (logger.isDebugEnabled()) logger.debug("BEFORE PUT WorkflowDB/"+id);
				result = prepareRestTransmission ("WorkflowDB/").PutData (requestxml);
				if (logger.isDebugEnabled()) logger.debug("AFTER PUT WorkflowDB/"+id);

				//result = restclient.PutData (requestxml);
				
			} catch (UnsupportedEncodingException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
			}
			
			rms = null;
			res = null;
			//restclient = null;
			
			String value = getValueFromKey (result, "workflow_id");
			
			if (value == null) {
				
				logger.error ("I cannot the WORKFLOW-DB entry for this id, an error has occured. Skipping this object and continue with next.");
				return;
			}
			
			int workflowid = new Integer (value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("workflow_id is: " + workflowid);

		}
	}

	/**
	 * 
	 * folgende Funktionsweise wird implementiert
	 * 1. Abfrage mit GET, ob schon Daten vorhanden sind
	 *    (OK, sonst NO_OBJECT_FOUND_ERROR)
	 * 2. sind Daten vorhanden, diese mit DELETE löschen
	 * 3. PUT ausführen, um die neuen Daten zu speichern
	 * 
	 * @param data
	 * @return
	 */
	private InternalMetadata storeMetaData(InternalMetadata data) throws AggregationFailedException, AggregationWarningException {
		
		logger.debug("### storeMetaData - Begin ###");
		
		// es wird ein IMF-Objekt erwartet
		InternalMetadata im = null; 
		
		if (! (data instanceof InternalMetadata)) {
			// wenn kein IMF-Objekt übergeben wurde, darf auch nichts gespeichert werden
			return null;
		}

		// ansonsten sollte die Verarbeitung beginnen
		// IMF marschallen, d.h. serialisieren zu XML
		im = (InternalMetadata) data;
		
		InternalMetadataJAXBMarshaller marshaller = InternalMetadataJAXBMarshaller.getInstance ( );
		String xmlInternalMetadata;
		xmlInternalMetadata = marshaller.marshall(im);
			
		// Rest-Client auf InternalMetadataEntry mit aktueller OID initialisieren
		String resource = "InternalMetadataEntry/" + this.currentRecordId;
		RestClient restclient = prepareRestTransmission(resource);
		
		RestMessage msgGetResponse = null;

		// -------------------------------------------		
		
		logger.debug("# GET ");
			
		// GET-Anfrage auf "InternalMetadataEntry"
		
		if (logger.isDebugEnabled()) logger.debug("BEFORE GET InternalMetadataEntry/"+this.currentRecordId);
		msgGetResponse = restclient.sendGetRestMessage();
		if (logger.isDebugEnabled()) logger.debug("AFTER GET InternalMetadataEntry/"+this.currentRecordId);		
		
		// Gibt es das schon unter der OID?
		if(msgGetResponse.getStatus() == RestStatusEnum.OK) {
			
			// OK = existiert schon
			logger.debug("# DELETE necessary:" + msgGetResponse);
			
			// neue Anfrage auf Delete zum löschen des bereits existierenden Datensatzes
			restclient = prepareRestTransmission(resource);
			
			if (logger.isDebugEnabled()) logger.debug("BEFORE DELETE InternalMetadataEntry/"+this.currentRecordId);
			RestMessage msgDeleteResponse = restclient.sendDeleteRestMessage();
			if (logger.isDebugEnabled()) logger.debug("AFTETR DELETE InternalMetadataEntry/"+this.currentRecordId);
			
			
			logger.debug("### RESPONSE ###\n\n"+ msgDeleteResponse);
			RestEntrySet tmpEntrySet = msgGetResponse.getListEntrySets().get(0);
			logger.debug("oid nach Delete: " + tmpEntrySet.getValue("oid"));
			
		} else if (msgGetResponse.getStatus() == RestStatusEnum.NO_OBJECT_FOUND_ERROR) {
			
			//existiert noch nicht - alles ok so!
			logger.debug("# DELETE not necessary: " + msgGetResponse);
			
		} else {
			
			logger.error("GET liefert Fehler: " + msgGetResponse);
			
			throw new AggregationFailedException("getting old InternalMetadata failed: ");
		}

		// ----------------------------------------
		


			logger.debug("# PUT");

			RestMessage msgPutRequest = new RestMessage();
			msgPutRequest.setKeyword (RestKeyword.InternalMetadataEntry);
			msgPutRequest.setStatus (RestStatusEnum.OK);

			RestEntrySet res = new RestEntrySet ( );
			res.addEntry ("internalmetadata", xmlInternalMetadata);
			msgPutRequest.addEntrySet (res);

			// abschicken der Daten	
			restclient = prepareRestTransmission(resource);
			
			RestMessage msgPutResponse = null;
			try {
				logger.debug("internal metadata to send via PUT: " + xmlInternalMetadata);
				
				if (logger.isDebugEnabled()) logger.debug("BEFORE PUT InternalMetadataEntry/"+this.currentRecordId);
				msgPutResponse = restclient.sendPutRestMessage(msgPutRequest);
				if (logger.isDebugEnabled()) logger.debug("AFTER PUT InternalMetadataEntry/"+this.currentRecordId);

				
			} catch (IOException ioex) {

				logger.error (ioex.getLocalizedMessage ( ));
				ioex.printStackTrace ( );
				
				throw new AggregationFailedException("putting InternalMetadata failed: ", ioex);
			}	

			
			// auswerten des Resultats
			// wenn gar keine Rückmeldung, dann auf jeden Fall ein Fehler
			if (msgPutResponse == null || msgPutResponse.getStatus() != RestStatusEnum.OK) {
				if (msgPutResponse.getStatus() == RestStatusEnum.AGGREGATION_WARNING) {
					logger.error(msgPutResponse.getStatusDescription());
					throw new AggregationWarningException(msgPutResponse.getStatusDescription());
				} else {
					logger.error("REST-Uebertragung fehlgeschlagen: " + msgPutResponse);
					throw new AggregationFailedException("putting InternalMetadata failed, server responded with error:\n" + msgPutResponse.getStatus() + " - " + msgPutResponse.getStatusDescription());
				}
			} else {
				logger.debug("Resultat der Übertragung: " + msgPutResponse);
			}

		return data;
	}

	/**
	 * determindes which metadata format is used in rawdata
	 * 
	 * @param xmlRawdata
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private MetadataFormatType getMDFTypeFromRawdata(String xmlRawdata) {
		MetadataFormatType result = MetadataFormatType.UNKNOWN;
		
		try {
		
			org.jdom.Document doc;
			SAXBuilder builder = new SAXBuilder();

			doc = builder.build(new InputSource(new StringReader(xmlRawdata)));

			logger.debug("** doc generated");

			ElementFilter filter = new ElementFilter("metadata");
			Iterator iterator = doc.getDescendants(filter);

			while (iterator.hasNext()) {
				Element contentSet = (Element) iterator.next();
				List contentList = contentSet.getChildren();
				Iterator iteratorContent = contentList.iterator();
				while (iteratorContent.hasNext()) {
					Element contentEntry = (Element) iteratorContent.next();
					logger.debug(contentEntry.getName() + "\n");
					if ((contentEntry.getNamespacePrefix() +":"+ contentEntry.getName()).equals("oai_dc:dc")) {
							result = MetadataFormatType.OAI_DC;
					}
				}

			}
		} catch(Exception ex) {
	      logger.error("error finding metadata format type in xml rawdata: " + ex.getLocalizedMessage());
		}

		return result;
	}
	
	/**
	 * zieht den eigentlichen Metadatensatz als IMF-Objekt aus dem XML heraus, indem es
	 * eine auf das Metadaten-Format bezogene GeneratorKlasse benutzt 
	 * 
	 * @param xmlRawdata
	 * @return
	 * @throws AggregationFailedException
	 */
	private InternalMetadata extractMetaData(String xmlRawdata) throws AggregationFailedException {

		logger.debug("extractMetadata");

		InternalMetadata im = new InternalMetadata();
		
			// Auswertung des ermittelten Metadatenformats
			MetadataFormatType metadataFormat = getMDFTypeFromRawdata(xmlRawdata);
			switch(metadataFormat) {
				case OAI_DC: 
					IMFGeneratorDCSimple imGen = new IMFGeneratorDCSimple(new BigDecimal(this.currentRecordId));
					im = imGen.generateIMF(xmlRawdata);					
					break;
				// <--- neue Formate werden hier auf entsprechende Generator-Klassen gemapped
				case UNKNOWN:
			    default:
			    	im = null;
			    	throw new AggregationFailedException("No IMF-Object can be created, metadata type: " + metadataFormat);
			}
			
		if (im != null) {
			logger.debug("## InternalMetadata after Extraction: \n\n" + im.toString());
			return im;
		} else
			throw new AggregationFailedException("No IMF-Object was created");
		
	}

	
	/**
	 * This method stores cleaned raw data in DB
	 * 
	 * @param data
	 * 				Decoded  and Cleaned Rawdata
	 * @return	
	 * 			correctly UTF-8-encoded metadatablob
	 */
	private String storeCleanedRawData(String data) throws AggregationFailedException {
		// TODO Auto-generated method stub

		String result = (String) data;
		return result;
	}
	
	
	/**
	 * This method checks raw data for UTF-8 encoding errors
	 * 
	 * @param data
	 * 				Decoded Rawdata
	 * @return	
	 * 			correctly UTF-8-encoded metadatablob
	 */
	private String checkXML(String data) throws AggregationFailedException {
		// TODO checkXML not implemented
		String result = (String) data;
		return result;
	}


	/**
	 * This method checks raw data for UTF-8 encoding errors
	 * 
	 * @param data
	 * 				Decoded Rawdata
	 * @return	
	 * 			correctly UTF-8-encoded metadatablob
	 */
	private String checkEncoding(String data) throws AggregationFailedException {
		String result = (String) data;

		// noch zu klären, wie Fehler in der Codierung ausgegeben werden
		// (Exception),
		// oder ob der Rückgabetyp geändert werden kann und ein weiterer Wert
		// übergeben werden kann

		return result;
	}

	
	/**
	 * loadRawData retrieve RawData from the database using Rest
	 * 
	 * @param id
	 *            id of the object that shall be retrieved
	 * @return data of the object
	 */
	private String loadRawData(int id) throws AggregationFailedException {
		if (logger.isDebugEnabled())
			logger.debug("loadRawData started");

		String ressource = "RawRecordData/" + id;
		RestClient restclient = prepareRestTransmission(ressource);

		//String result = restclient.GetData();
		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden		
		//RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (logger.isDebugEnabled()) logger.debug("BEFORE GET RawRecordData/" + id);
		RestMessage response = restclient.sendGetRestMessage();
		if (logger.isDebugEnabled()) logger.debug("AFTER GET RawRecordData/" + id);
		
		//TODO: hier Fehlerbehandlung via rms.getStatus()
		
		if(response.getStatus() != RestStatusEnum.OK) {
			throw new AggregationFailedException("could not fetch rawdata for OID " + id + ":\n" + response);
		}
		
		RestEntrySet entrySet = response.getListEntrySets().get(0);
		Iterator<String> it = entrySet.getKeyIterator();
		
		String key = "";
		String value = "";

		while (it.hasNext()) {
			key = it.next();
			if (key.equalsIgnoreCase("data")) {
				value = entrySet.getValue(key);
				break;
			}
		}

		logger.debug("recognized value: " + value);
		if (!value.equals(""))
			return value;
		else
			throw new AggregationFailedException("loadRawData not successful, no DATA-value via REST retrieved");
//			return null;
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
			
			else
				logger.error ("Rest-Error occured: " + rms.getStatusDescription ( ));
			
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
	
	public boolean isStopped() {
    	return stopped;
    }

	public void setStopped(boolean stopped) {
    	this.stopped = stopped;
    }
	
}
