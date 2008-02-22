/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.FileNotFoundException;
import java.io.IOException; // import java.io.InputStream;
import java.io.StringReader; // import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64; // import
												// org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException; // import
													// org.apache.commons.httpclient.HttpStatus;
// import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator; // import
												// org.w3c.dom.DOMException;
// import org.w3c.dom.Document;
// import org.w3c.dom.NodeList;
import org.xml.sax.InputSource; // import org.xml.sax.SAXException;

// import org.jdom.Content;

import org.jdom.Element;
import org.jdom.Namespace; // import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.RestXmlCodec;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;

/**
 * @author Manuel Klatt-Kafemann
 * 
 * Aggregator: extracts metadata and stores it in the db
 */

/**
 * @author klattman
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
		// TODO Auto-generated method stub

		// zuerst muss die Workflow-DB nach Arbeitsobjekten befragt werden
		if (logger.isDebugEnabled())
			logger.debug("Aggregator AutoMode started");

		String ressource = "WorkflowDB/2/2"; // + id;
		RestClient restclient = RestClient.createRestClient(this.props
				.getProperty("host"), ressource, this.props
				.getProperty("username"), this.props.getProperty("password"));

		String result = restclient.GetData();
		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden

		List<HashMap<String, String>> listentries = new ArrayList<HashMap<String, String>>();
		// HashMap <String, String> mapEntry = new HashMap <String ,String> ( );

		listentries = null;

		listentries = RestXmlCodec.decodeEntrySet(result);

		for (HashMap<String, String> mapEntry : listentries) {

			// mapEntry = listentries.get (0);
			Iterator<String> it = mapEntry.keySet().iterator();
			String key = "";
			String value = "";

			while (it.hasNext()) {
				key = it.next();
				System.out.println("key: " + key);
				// hier wird die Rueckgabe überprüft, ist es einen object_id,
				// dann muss diese bearbeitet werden
				if (key.equalsIgnoreCase("object_id")) {
					value = mapEntry.get(key);
					logger.debug("recognized value: " + value);
					if (!value.equals(""))
						startSingleRecord((new Integer(value).intValue()));
					// else
					// return null;

					// startSingleRecord((new Integer(value).intValue());

					// break;
				}
			}
		}
		// System.out.println("erkannte Werte" + value);
		// logger.debug("recognized value: " + value);
		// if (!value.equals("")) return value;
		// else
		// return null;
		//		

	}

	private void startSingleRecord(int id) {
		// TODO Auto-generated method stub
		this.currentRecordId = id;

		System.out.println("StartSingleRecord:  RecordId="
				+ this.currentRecordId);

		Object data = null;

		// laden der Rohdaten
		data = loadRawData(this.currentRecordId);
		if (data == null) {
			// Daten für dieses Objekt konnten nicht geladen werden
			logger.error("loadRawData not successful");
			return;
		}
		// Auseinandernehmen der Rohdaten
		System.out.println("Geladene Daten: " + data);
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

		System.out.println(new String(Base64.decodeBase64(((String) data)
				.getBytes())));
		try {
			return new String(Base64.decodeBase64(((String) data).getBytes()));
		} catch (Exception e) {
			logger.error("decodeBase64 : ioException\n");
			e.printStackTrace();

		}
		return null;
	}

	private void setAggregationCompleted(int id) {
		// TODO Auto-generated method stub

	}

	private Object storeMetaData(Object data) {
		// es wird eine IMF-Objekt erwartet
		InternalMetadata im = null; 
		if (! (data instanceof InternalMetadata)) {
			// wenn kein IMF-Objekt übergeben wurde, darf auch nichts gespeichert werden
			return null;
		}
		// ansonsten sollte die Verarbeitung beginnen
		im = (InternalMetadata) data;
		InternalMetadataJAXBMarshaller marshaller = InternalMetadataJAXBMarshaller.getInstance ( );
		String xmlData;
		xmlData = marshaller.marshall(im);
		
		System.out.println("### XMLDATA ###");
		System.out.println(xmlData);
		
		// Rest-Client initialisieren
		String resource = "InternalMetadataEntry/" + this.currentRecordId;
		RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));

		try {
			List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
			HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
			
			mapEntry.put ("internalmetadata", xmlData);
			listentries.add (mapEntry);
			String requestxml = RestXmlCodec.encodeEntrySetRequestBody (listentries);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("xml: " + requestxml);
			// abschicken der Daten
			String result = restclient.PutData (requestxml);
		
			
			// auswerten des Resultats
			// wenn gar keine Rückmeldung, dann auf jeden Fall ein Fehler
			if (result == null) {
				System.out.println("REST-Uebertragung fehlgeschlagen");
				return null;
			} else {
				System.out.println("Resultat der Übertragung: " + result);
			}
			
			
			listentries = null;
			mapEntry = null;
			restclient = null;
			
			listentries = RestXmlCodec.decodeEntrySet (result);
			mapEntry = listentries.get (0);
			Iterator <String> it = mapEntry.keySet ( ).iterator ( );
			String key = "";
			String value = "";
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				System.out.println("key = " + key);
				
				if (key.equalsIgnoreCase ("oid")) {
					
					
					value = mapEntry.get (key);
					System.out.println(value);
					break;
				}
			}
//			
//			int intoid = new Integer (value);
//			
//			if (logger.isDebugEnabled ( ))
//				logger.debug ("internalOID: " + intoid);
//			
//			this.ids.get (i).setInternalOID (intoid);
//			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("uploaded rawdata for Database Object " + this.currentRecordId);
	
		
		
		return data;
	}

	@SuppressWarnings("unchecked")
	private Object extractMetaData(Object data) {

		System.out.println("extractMetadata");

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
					System.out.println(contentEntry.getName() + "\n");
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
			System.err.println("Fehler beim Parsen");
			System.err.println("error while decoding XML String: " + e);
			logger.error("error while decoding XML String: " + e);
		}

		// return listEntrySet;

		return im;

		// return null;
	}

	private Object storeCleanedRawData(Object data) {
		// TODO Auto-generated method stub

		String result = (String) data;
		return result;
	}

	private Object checkXML(Object data) {
		// TODO Auto-generated method stub
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

		String result = restclient.GetData();
		// Resultat ist ein XML-Fragment, hier muss das Resultat noch aus dem
		// XML extrahiert werden

		List<HashMap<String, String>> listentries = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mapEntry = new HashMap<String, String>();

		listentries = null;
		mapEntry = null;

		listentries = RestXmlCodec.decodeEntrySet(result);
		mapEntry = listentries.get(0);
		Iterator<String> it = mapEntry.keySet().iterator();
		String key = "";
		String value = "";

		while (it.hasNext()) {

			key = it.next();

			if (key.equalsIgnoreCase("data")) {
				value = mapEntry.get(key);
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

	/**
	 * @param url
	 * @param fullharvest
	 * @param updateFrom
	 * @param repositoryId
	 */
	//	
	// private void processIds (String url, boolean fullharvest, String
	// updateFrom, int repositoryId ) {
	//		
	// HttpClient client = new HttpClient ( );
	// GetMethod getmethod;
	// String resumptionToken = null;
	// Boolean resumptionSet = true;
	//		
	// if (!fullharvest) {
	//			
	// getmethod = new GetMethod (url +
	// "?verb=ListIdentifiers&metadataPrefix=oai_dc&from=" + updateFrom);
	//			
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("Update Harvest: " + url + getmethod.getQueryString ( ));
	//			
	// } else {
	//			
	// getmethod = new GetMethod (url +
	// "?verb=ListIdentifiers&metadataPrefix=oai_dc");
	//			
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("Full Harvest: " + url + getmethod.getQueryString ( ));
	// }
	//		
	// while (resumptionSet) {
	//		
	// client.getParams ( ).setParameter ("http.protocol.content-charset",
	// "UTF-8");
	//			
	// try {
	//				
	// int statuscode = client.executeMethod (getmethod);
	//				
	// logger.info ("ID-List HttpStatusCode: " + statuscode);
	//				
	// if (statuscode != HttpStatus.SC_OK) {
	//					
	// //TODO: proper implementation
	// ;
	// }
	//				
	// resumptionToken = extractIds (getmethod.getResponseBodyAsStream ( ),
	// repositoryId);
	//				
	// if (resumptionToken != null) {
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("ResumptionToken found: " + resumptionToken);
	// else;
	//					
	// if (resumptionToken.equals ("")) {
	//						
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("ResumptionToken empty, IdentifierList complete");
	// else;
	//						
	// resumptionSet = false;
	//						
	// } else {
	//						
	// getmethod = new GetMethod (url + "?verb=ListIdentifiers&resumptionToken="
	// + resumptionToken);
	//						
	// if (logger.isDebugEnabled ( )) {
	//							
	// logger.debug ("ResumptionToken: " + resumptionToken);
	// logger.debug ("ResumptionQuery: " + url + getmethod.getQueryString ( ));
	//							
	// } else;
	// }
	//					
	// } else {
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("no ResumptionToken found, IdentifierList complete");
	//					
	// else;
	//					
	// resumptionSet = false;
	// }
	//			
	// } catch (HttpException ex) {
	//				
	// logger.error (ex.getLocalizedMessage ( ));
	// ex.printStackTrace ( );
	// this.errorretry++;
	//				
	// if (errorretry > 5)
	// System.exit (5);
	//				
	// } catch (IOException ex) {
	//				
	// logger.error (ex.getLocalizedMessage ( ));
	// ex.printStackTrace ( );
	//				
	// this.errorretry++;
	//				
	// if (errorretry > 5)
	// System.exit (5);
	// }
	// }
	// }
	//
	/**
	 * @param responseBody
	 * @param repositoryId
	 * @return
	 */

	// private String extractIds (InputStream responseBody, int repositoryId) {
	//		
	// String resumptionToken = null;
	//		
	// try {
	//			
	// //TODO: xml-handling has to be rewritten!
	//			
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
	// DocumentBuilder builder = factory.newDocumentBuilder ( );
	// Document document = builder.parse (responseBody);
	//			
	// NodeList idNodeList = document.getElementsByTagName ("identifier");
	// NodeList datestampNodeList = document.getElementsByTagName ("datestamp");
	//			
	// if (this.ids == null)
	// this.ids = new ArrayList <ObjectIdentifier> ( );
	//			
	// if (logger.isDebugEnabled ( )) {
	//				
	// logger.debug ("we have " + idNodeList.getLength ( ) + " Ids to extract");
	// logger.debug ("we have " + datestampNodeList.getLength ( ) + " Datestamps
	// to extract");
	// }
	//			
	// for (int i = 0; i < idNodeList.getLength ( ); i++) {
	//				
	// int internalOID;
	// String externalOID = idNodeList.item (i).getTextContent ( );
	// String datestamp = datestampNodeList.item (i).getTextContent ( );
	//				
	// if (logger.isDebugEnabled ( )) {
	//					
	// logger.debug ("List Record No. " + recordno++ + " " + externalOID + " " +
	// datestamp);
	// }
	//				
	// internalOID = objectexists (repositoryId, externalOID);
	//				
	// if (internalOID > 0) {
	//					
	// // we found this object in the database
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug (externalOID + " is " + internalOID + " in our database");
	//					
	// ids.add (new ObjectIdentifier (externalOID, datestamp, internalOID));
	//					
	// } else if (internalOID == -1) {
	//					
	// // we found no object in the database
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug (externalOID + " is not in the database. so we have to
	// create a new object");
	//					
	// ids.add (new ObjectIdentifier (externalOID, datestamp, -1));
	//					
	// } else {
	//					
	// //TODO: better error-handling
	// logger.error ("Error occured!");
	// }
	// }
	//			
	// NodeList rsList = document.getElementsByTagName ("resumptionToken");
	//			
	// if (rsList.getLength ( ) > 0)
	// resumptionToken = rsList.item (0).getTextContent ( );
	//			
	// rsList = null;
	// factory = null;
	// builder = null;
	// document = null;
	//			
	// idNodeList = null;
	// datestampNodeList = null;
	//			
	// } catch (ParserConfigurationException pacoex) {
	//			
	// logger.error (pacoex.getLocalizedMessage ( ));
	// pacoex.printStackTrace ( );
	//			
	// } catch (SAXException sex) {
	//			
	// logger.error (sex.getLocalizedMessage ( ));
	// sex.printStackTrace ( );
	//			
	// } catch (IOException ioex) {
	//			
	// logger.error (ioex.getLocalizedMessage ( ));
	// ioex.printStackTrace ( );
	//			
	// }
	//		
	// return resumptionToken;
	// }
	/**
	 * @param url
	 * @param id
	 */
	//	
	// private void processRecords (String url, int id) {
	//		
	// HttpClient client;
	// GetMethod method;
	//		
	// //String url =
	// "http://edoc.hu-berlin.de/OAI-2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=";
	//		
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("now we process " + this.ids.size ( ) + "Records");
	//		
	// for (int i = 0; i < this.ids.size ( ); i++) {
	//			
	// // zuerst schauen, ob datestamp gleich, wenn ja, harvested auf today
	// setzen, ansonsten metadaten putten
	//			
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("Obect No. " + i + " is processed");
	//			
	// int internalOID = this.ids.get (i).getInternalOID ( );
	//			
	// if (internalOID == -1) {
	//				
	// // when internalOID == -1 than Object is not in the database and we have
	// to create it
	//				
	// String ressource = "ObjectEntry/";
	// RestClient restClient = RestClient.createRestClient
	// (this.props.getProperty ("host"), ressource, this.props.getProperty
	// ("username"), this.props.getProperty ("password"));
	//				
	// GregorianCalendar cal = new GregorianCalendar ( );
	// cal.setTime (this.ids.get (i).getDatestamp ( ));
	//				
	// String datestamp = cal.get (Calendar.YEAR) + "-" + (cal.get
	// (Calendar.MONTH) + 1) + "-" + cal.get (Calendar.DAY_OF_MONTH);
	//				
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("creating Object No. " + i + ": " + this.ids.get
	// (i).getExternalOID ( ));
	//				
	// try {
	//					
	// //TODO: rewrite XML!!!
	// String result = restClient.PutData
	// ("<oanrest>\n\t<request>\n\t\t<entryset>\n\t\t\t<entry
	// key=\"repository_id\">" +
	// id + "</entry>\n\t\t\t<entry key=\"repository_identifier\">" +
	// this.ids.get (i).getExternalOID ( ) + "</entry>\n\t\t\t<entry
	// key=\"repository_datestamp\">" +
	// datestamp + "</entry>\n\t\t</entryset>\n\t</request>\n</oanrest>");
	//					
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
	// DocumentBuilder builder = factory.newDocumentBuilder ( );
	// Document document = builder.parse (new InputSource (new StringReader
	// (result)));
	//					
	// NodeList internalOIDList = document.getElementsByTagName ("OID");
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("resultlength = " + internalOIDList.getLength ( ));
	//					
	// int intoid = new Integer (internalOIDList.item (0).getTextContent ( ));
	//					
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("internalOID: " + intoid);
	//					
	// //tempobjid.setInternalOID (intoid);
	//					
	// this.ids.get (i).setInternalOID (intoid);
	//					
	// //this.ids.set (i, tempobjid);
	//					
	// } catch (SAXException saex) {
	//					
	// logger.error (saex.getLocalizedMessage ( ));
	// saex.printStackTrace ( );
	//					
	// } catch (ParserConfigurationException ex) {
	//					
	// logger.error (ex.getLocalizedMessage ( ));
	// ex.printStackTrace ( );
	//					
	// } catch (IOException ex) {
	//					
	// logger.error (ex.getLocalizedMessage ( ));
	// ex.printStackTrace ( );
	// }
	//				
	// } else {
	//				
	// // Object exists and we have to look if our Rawdata is newer than the
	// database one
	//				
	// if (logger.isDebugEnabled ( ))
	// logger.debug ("observing Object No. " + i + ": " + this.ids.get
	// (i).getExternalOID ( ));
	//				
	// String ressource = "ObjectEntry/" + internalOID + "/";
	// RestClient restclient = RestClient.createRestClient
	// (this.props.getProperty ("host"), ressource, this.props.getProperty
	// ("username"), this.props.getProperty ("password"));
	//				
	// String result = restclient.GetData ( );
	//				
	// try {
	//					
	// //TODO: rewrite XML
	//					
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
	// DocumentBuilder builder = factory.newDocumentBuilder ( );
	// Document document = builder.parse (new InputSource (new
	// StringReader(result)));
	//					
	// NodeList idDatestampList = document.getElementsByTagName
	// ("repository_datestamp");
	//					
	// Date repositoryDate = new SimpleDateFormat ("yyyy-MM-dd").parse
	// (idDatestampList.item (1).getTextContent ( ));
	//					
	// if (repositoryDate.before (this.ids.get (i).getDatestamp ( )) ||
	// repositoryDate.equals (this.ids.get (i).getDatestamp ( ))) {
	//						
	// if (logger.isDebugEnabled ( )) {
	//							
	// logger.debug ("RepositoryDate is " + repositoryDate + " and before or
	// equal the harvested: " + this.ids.get (i).getDatestamp ( ));
	// }
	// //TODO: implement proper change of harvested-timestamp
	//						
	// continue;
	//						
	// }
	// } catch (SAXException saex) {
	//					
	// saex.printStackTrace ( );
	//					
	// } catch (ParserConfigurationException ex) {
	//					
	// ex.printStackTrace ( );
	//					
	// } catch (IOException ex) {
	//					
	// ex.printStackTrace ( );
	//					
	// } catch (DOMException ex) {
	//					
	// ex.printStackTrace ( );
	//					
	// } catch (java.text.ParseException ex) {
	//					
	// ex.printStackTrace ( );
	// }
	// }
	//			
	// client = new HttpClient ( );
	// method = new GetMethod (url +
	// "?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + ids.get
	// (i).getExternalOID ( ));
	// client.getParams ( ).setParameter ("http.protocol.content-charset",
	// "UTF-8");
	//			
	// try {
	//				
	// int statuscode = client.executeMethod (method);
	//				
	// logger.info ("HttpStatusCode: " + statuscode);
	//				
	// if (statuscode != HttpStatus.SC_OK) {
	//					
	// ;
	// }
	//				
	// uploadRawData (HelperMethods.stream2String
	// (method.getResponseBodyAsStream ( )), ids.get (i).getInternalOID ( ),
	// ids.get (i).getDatestamp ( ));
	//				
	// } catch (HttpException ex) {
	//				
	// ex.printStackTrace ( );
	//				
	// } catch (IOException ex) {
	//				
	// ex.printStackTrace ( );
	//								
	// } finally {
	//				
	// method.releaseConnection ( );
	// }
	//			
	// client = null;
	// }
	// }
	//
	/**
	 * @param data
	 * @param internalOID
	 * @param datestamp
	 * @throws IOException
	 * @throws HttpException
	 */

	private void uploadRawData(String data, int internalOID, Date datestamp)
			throws HttpException, IOException {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(datestamp);

		String resource = "RawRecordData/" + internalOID + "/"
				+ cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH + 1)
				+ "-" + cal.get(Calendar.DAY_OF_MONTH) + "/";

		RestClient restclient = RestClient.createRestClient(this.props
				.getProperty("host"), resource, this.props
				.getProperty("username"), this.props.getProperty("password"));
		restclient.PutData(data);

		if (logger.isDebugEnabled())
			logger.debug("uploaded rawdata for Database Object " + internalOID);
	}

}
