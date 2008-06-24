/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Title;

/**
 * @author Manuel Klatt-Kafemann
 * 
 * Aggregator: extracts metadata and stores it in the db
 */

/**
 * @author Manuel Klatt-Kafemann
 * 
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

	/**
	 * Properties.
	 */

	private int currentRecordId = 0; // stores object_id which is currently
										// being worked on

	private Properties props; // special aggregator settings like connecting
								// server

	/**
	 * Standard Constructor which initialises the log4j and loads necessary
	 * properties.
	 */

	public Aggregator() {

		DOMConfigurator.configure("log4j.xml");

		try {

			this.props = HelperMethods
					.loadPropertiesFromFile("aggregatorprop.xml");

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
	}

	/**
	 * Main class which have to be called.
	 * 
	 * @param args
	 * @throws ParseException
	 */

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		// Aggregator a = new Aggregator();
		// a.startAutoMode();
		// System.exit(0);
		//		

		Options options = new Options();

		options.addOption("h", false, "show help");

		options
				.addOption(OptionBuilder
						.withLongOpt("itemId")
						.withArgName("ID")
						.withDescription(
								"Id of the database object, that shall be extracted and converted")
						.withValueSeparator().hasArg().create('i'));
		options.addOption(OptionBuilder.withLongOpt("auto").withDescription(
				"Automatic mode, no given ID is necessary")

		.create('a'));

		if (args.length > 0) {

			try {

				CommandLine cmd = new GnuParser().parse(options, args);

				if (cmd.hasOption("h"))
					HelperMethods.printhelp("java "
							+ Aggregator.class.getCanonicalName(), options);

				else if (cmd.hasOption("itemId") || cmd.hasOption('i')
						|| cmd.hasOption('a') || cmd.hasOption("auto")) {

					int id = 0;

					// Bestimmen, ob nur eine einzelne ID übergeben wurde oder
					// der Auto-Modus genutzt werden soll
					if (cmd.getOptionValue('i') != null)
						id = filterId(cmd.getOptionValue('i'));
					if (cmd.getOptionValue("itemId") != null)
						id = filterId(cmd.getOptionValue("itemId"));

					// Here we go: create a new instance of the aggregator

					Aggregator aggregator = new Aggregator();

					// hier wird entweder die spezifische Objekt-ID übergeben
					// oder ein Auto-Durchlauf gestartet
					if (id > 0) {
						aggregator.startSingleRecord(id);
					} else {
						aggregator.startAutoMode();
					}

				} else {

					HelperMethods.printhelp("java "
							+ Aggregator.class.getCanonicalName(), options);
					System.exit(1);
				}

			} catch (ParseException parex) {

				logger.error(parex.getMessage());
				System.out.println(parex.getMessage());
				HelperMethods.printhelp("java "
						+ Aggregator.class.getCanonicalName(), options);
				System.exit(1);
			}

		} else {

			HelperMethods.printhelp("java "
					+ Aggregator.class.getCanonicalName(), options);
			System.exit(1);
		}
	}

	private void startAutoMode() {

		// zuerst muss die Workflow-DB nach Arbeitsobjekten befragt werden
		if (logger.isDebugEnabled())
			logger.debug("Aggregator AutoMode started");
		
		//TODO: hardcoded ServiceID !!!
		
		String ressource = "WorkflowDB/2"; // + id;
		RestClient restclient = RestClient.createRestClient(this.props
				.getProperty("host"), ressource, this.props
				.getProperty("username"), this.props.getProperty("password"));

		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden
		RestMessage msgGetWFResponse = restclient.sendGetRestMessage();
		if(msgGetWFResponse.getStatus() != RestStatusEnum.OK) {
			//TODO: Was nun?
			logger.error("WorkflowDB response failed: " + msgGetWFResponse.getStatus() + "(" + msgGetWFResponse.getStatusDescription() + ")");
			return;
		}
		
		
		List<RestEntrySet> listEntrySets = msgGetWFResponse.getListEntrySets();
				
		for (RestEntrySet entrySet : listEntrySets) {

			Iterator<String> it = entrySet.getKeyIterator();
			String key = "";
			String value = "";

			while (it.hasNext()) {
				key = it.next();
				logger.debug("key: " + key);
				// hier wird die Rueckgabe überprüft, ist es einen object_id,
				// dann muss diese bearbeitet werden
				if (key.equalsIgnoreCase("object_id")) {
					value = entrySet.getValue(key);
					logger.debug("recognized value: " + value);
					if (!value.equals("")) {
						startSingleRecord((new Integer(value).intValue()));
					} else {
						logger.error("OID from Workflow expected, but was: " + value);
					}
				}
			}
		}

	}

	/*
	 * implements workflow for a single entry
	 * 
	 * 
	 * 
	 */
	private void startSingleRecord(int id) {

		this.currentRecordId = id;

		logger.debug("StartSingleRecord:  RecordId=" + this.currentRecordId);

		Object data = null;

		// laden der Rohdaten
		data = loadRawData(this.currentRecordId);
		if (data == null) {
			// Daten für dieses Objekt konnten nicht geladen werden
			logger.error("loadRawData not successful");
			return;
		}
		// Auseinandernehmen der Rohdaten
		logger.debug("Geladene Rohdaten (noch Base64-codiert): " + data);
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("retrieved data: " + data);
		}

		// data = decodeBase64(((String) data).getBytes());
		data = decodeBase64(data);

		// Prüfen der Codierung der Rohdaten
		data = checkEncoding(data);
		if (data == null) {
			// beim Check des Encoding trat ein Fehler auf, keine weitere
			// Behandlung möglich
			return;
		}

		// XML-Fehler müssen behoben werden
		data = checkXML(data);
		if (data == null) {
			// beim Prüfen auf XML-Fehler trat ein Fehler auf, keine weitere
			// Bearbeitung möglich
			return;
		}
		// Schreiben der bereinigten Rohdaten
		data = storeCleanedRawData(data);
		if (data == null) {
			// die bereinigten Rohdaten konnten nicht gespeichert werden, eine
			// weitere Bearbeitung sollte nicht erfolgen
			return;
		}

		// Auslesen der Metadaten
		data = extractMetaData(data);
		if (data == null) {
			// die Metadaten konnten nicht ausgelesen werden, keine weitere
			// Bearbeitung sinnvoll
			return;
		}

		// schreiben der Metadaten
		data = storeMetaData(data);
		if (data == null) {
			// Schreiben der Metadaten ist fehlgeschlagen, Objekt sollte später
			// noch einmal prozessiert werden,
			// jetzt keine weitere Bearbeitung sinnvoll
			return;
		}

		// Zustandsänderung für dieses Objekt speichern
		setAggregationCompleted(id);

	}

	/**
	 * @param data
	 *            Daten, die als Base64-String ankommen und decodiert werden
	 *            sollen
	 * @return byte[] enthält base64-decodierten String
	 */
	private Object decodeBase64(Object data) {
		// Daten müssen Base64-decodiert werden

		System.out.println("\n## Decodiert: \n\n" + new String(Base64.decodeBase64(((String) data)
				.getBytes())));
		System.out.println("## Ende decodierte Informationen\n\n\n");
		
		try {
			return new String(Base64.decodeBase64(((String) data).getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error("decodeBase64 : ioException\n");
			e.printStackTrace();

		}
		return null;
	}

	private void setAggregationCompleted(int id) {
		// TODO Auto-generated method stub

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
	private Object storeMetaData(Object data) {
		
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
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));

		RestMessage msgGetResponse = null;

		// -------------------------------------------		
		
		logger.debug("# GET ");
			
		// GET-Anfrage auf "InternalMetadataEntry"
		msgGetResponse = restclient.sendGetRestMessage();
		
		
		// Gibt es das schon unter der OID?
		if(msgGetResponse.getStatus() == RestStatusEnum.OK) {
			
			// OK = existiert schon
			logger.debug("# DELETE necessary:" + msgGetResponse);
			
			// neue Anfrage auf Delete zum löschen des bereits existierenden Datensatzes
			restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
			RestMessage msgDeleteResponse = restclient.sendDeleteRestMessage();
			logger.debug("### RESPONSE ###\n\n"+ msgDeleteResponse);
			RestEntrySet tmpEntrySet = msgGetResponse.getListEntrySets().get(0);
			logger.debug("oid nach Delete: " + tmpEntrySet.getValue("oid"));
			
		} else if (msgGetResponse.getStatus() == RestStatusEnum.NO_OBJECT_FOUND_ERROR) {
			
			//existiert noch nicht - alles ok so!
			logger.debug("# DELETE not necessary: " + msgGetResponse);
			
		} else {
			
			logger.error("GET liefert Fehler: " + msgGetResponse);
			return null;
			
		}

		// ----------------------------------------
		
		try {

			logger.debug("# PUT");

			RestMessage msgPutRequest = new RestMessage();
			msgPutRequest.setKeyword (RestKeyword.InternalMetadataEntry);
			msgPutRequest.setStatus (RestStatusEnum.OK);

			RestEntrySet res = new RestEntrySet ( );
			res.addEntry ("internalmetadata", xmlInternalMetadata);
			msgPutRequest.addEntrySet (res);

			// abschicken der Daten	
			restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
			RestMessage msgPutResponse = restclient.sendPutRestMessage(msgPutRequest);

			// auswerten des Resultats
			// wenn gar keine Rückmeldung, dann auf jeden Fall ein Fehler
			if (msgPutResponse == null || msgPutRequest.getStatus() != RestStatusEnum.OK) {
				logger.error("REST-Uebertragung fehlgeschlagen");
				return null;
			} else {
				logger.debug("Resultat der Übertragung: " + msgPutResponse);
			}

		} catch (IOException ex) {

			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}		

		return data;
	}

	@SuppressWarnings("unchecked")
	private Object extractMetaData(Object data) {

		logger.debug("extractMetadata");

		InternalMetadata im = new InternalMetadata();
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();

		@SuppressWarnings("unused")
		Namespace namespace = Namespace.getNamespace("oai_dc",
				"http://www.openarchives.org/OAI/2.0/oai_dc/");
		try {
			// InputSource is = new InputSource("testdata.xml");
			// doc = builder.build(is);
			doc = builder
					.build(new InputSource(new StringReader((String) data)));

			logger.debug("** doc generated");

			ElementFilter filter = new ElementFilter("OAI-PMH");
			String metadataFormat = null;
			Iterator iterator = doc.getDescendants(filter);
			// Bestimmung, welches Metadatenformat vorliegt
			while (iterator.hasNext()) {
				Element contentSet = (Element) iterator.next();
				List contentList = contentSet.getChildren();
				Iterator iteratorContent = contentList.iterator();
				while (iteratorContent.hasNext()) {
					Element contentEntry = (Element) iteratorContent.next();
					logger.debug(contentEntry.getName() + "\n");
					if (contentEntry.getName().equals("request")) {
						if ((contentEntry.getAttribute("metadataPrefix")
								.getValue()).equals("oai_dc")) {
							metadataFormat = "oai_dc";
							// System.out.println("oai_dc found");
						}
						// System.out.println("request found");
					}
				}

			}

			// Auswertung des ermittelten Metadatenformats

			if (metadataFormat.equals("oai_dc")) {
				// System.out.println("IMFGeneratorDCSimple wird gestartet");
				IMFGeneratorDCSimple imGen = new IMFGeneratorDCSimple();
				im = imGen.generateIMF((String) data);
			}

			// if (im != null) return im;

		} catch (Exception e) {
			logger.error("error while decoding XML String: " + e);
		}

		// return listEntrySet;

		if (im != null) {
			logger.debug("## InternalMetadata after Extraction: \n\n" + im.toString());
		}
		
		return im;

		// return null;
	}

	private Object storeCleanedRawData(Object data) {
		// TODO Auto-generated method stub

		String result = (String) data;
		return result;
	}

	private Object checkXML(Object data) {
		// TODO checkXML not implemented
		String result = (String) data;
		return result;
	}

	/**
	 * @param data
	 * @return
	 */
	private Object checkEncoding(Object data) {
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
	private Object loadRawData(int id) {
		if (logger.isDebugEnabled())
			logger.debug("loadRawData started");

		String ressource = "RawRecordData/" + id;
		RestClient restclient = RestClient.createRestClient(this.props
				.getProperty("host"), ressource, this.props
				.getProperty("username"), this.props.getProperty("password"));

		//String result = restclient.GetData();
		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden		
		//RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		RestMessage response = restclient.sendGetRestMessage();
		
		//TODO: hier Fehlerbehandlung via rms.getStatus()
		
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
			return null;
	}

	/**
	 * ensures the right value for the repository ID.
	 * 
	 * @param optionValue
	 *            a string representing a number greater or equal zero.
	 * @return an integer which represents the repository ID
	 * @throws ParseException
	 *             when the repository ID is negative
	 */
	private static int filterId(String optionValue) throws ParseException {

		int i = new Integer(optionValue);

		if (i < 0)
			throw new ParseException("ItemID must not be negative!");

		if (logger.isDebugEnabled())
			logger.debug("filtered ItemID: " + i);

		return i;
	}

	
}
