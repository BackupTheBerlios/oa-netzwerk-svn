/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.RestXmlCodec;
import de.dini.oanetzwerk.utils.imf.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Michael Kühn
 *
 */

public class InternalMetadataEntry extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (InternalMetadataEntry.class);
	
	/**
	 * 
	 */
	
	public InternalMetadataEntry ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (InternalMetadataEntry.class.getName ( ) + " is called");
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected String getKeyWord (String [ ] path) {

		InternalMetadata myIM = new InternalMetadata();
		myIM.addAuthor("Max Mustermann");
		myIM.addAuthor("Mustermann, Max");
		myIM.addAuthor("Test Spitze<>");
		myIM.addClassfication("foo:bar");
		myIM.addClassfication("pub-type:foo1");
		myIM.addClassfication("ddc:foo2");
		myIM.addClassfication("dnb:foo3");
		myIM.addDateValue("DateValue");
		myIM.addDescription("Beschreibung xyz");
		myIM.addFormat("Format");
		myIM.addIdentifier("Identifier");
		myIM.addKeyword("Schlüsselwort1");
		myIM.addKeyword("Schlüsselwort2");
		myIM.addKeyword("Schlüsselwort3");
		myIM.addLanguage("ger");
		myIM.addPublisher("Publisher");
		myIM.addTitle("Mustertitel", null, null);
		myIM.addTypeValue("TypeValue");
		
		InternalMetadataJAXBMarshaller marshaller = new InternalMetadataJAXBMarshaller();
		String xmlData;
		xmlData = marshaller.marshall (myIM);
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		mapEntry.put ("internalmetadata", xmlData);
		listentries.add (mapEntry);
		String requestxml = RestXmlCodec.encodeEntrySetRequestBody (listentries);
		
		return requestxml;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		//NOT IMPLEMENTED
		logger.warn ("postInternalMetadataEntry is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = new BigDecimal (path [2]);
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("");
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		listentries = RestXmlCodec.decodeEntrySet (data);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		InternalMetadata imf = null;
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("internalmetadata")) {
				
				imf = new InternalMetadataJAXBMarshaller ( ).unmarshall (mapEntry.get (key));
				
			} else continue;
		}
		
		
//		List <Title> titlelist = imf.getTitles ( );
		//List <Author> authors;
		//List <Keyword> keywords = imf.getKeywords ( );
//		List <Description> descriptions = imf.getDescriptions ( );
		//List <Publisher> publishers;
		List <DateValue> dateValues = imf.getDateValues ( );
		List <Format> formatList = imf.getFormatList ( );
		List <Identifier> identifierList = imf.getIdentifierList ( );
//		List <TypeValue> typeValueList = imf.getTypeValueList ( );
		//List <Language> languageList;
		//List<Classification> classificationList;
		
		//ResultSet resultset;
		
		db.setAutoCom (false);
		
		//for (Title title : titlelist)			
			//db.insertTitle (object_id, title.getQualifier ( ), title.getTitle ( ), title.getLang ( ));
		
		//for (Keyword keyword : keywords)
		//	db.insertkeyword ( );
		
		//for (Description description : descriptions)
		//	db.insertDescription ( );
		
		for (DateValue dateValue : dateValues)
			
			try {
				
				db.insertDateValue (object_id, dateValue.getNumber ( ), HelperMethods.extract_datestamp (dateValue.getDateValue ( )));
				
			} catch (ParseException ex) {
				
				logger.error ("Datestamp with datevalue incorrect");
				ex.printStackTrace();
			}
		
		for (Format format : formatList)
			db.insertFormat (object_id, format.getNumber ( ), format.getSchema_f ( ));
		
		for (Identifier identifier : identifierList)
			db.insertIdentifier (object_id, identifier.getNumber ( ), identifier.getIdentifier ( ));
		
		//for (TypeValue typeValue : typeValueList)
		//	db.insertTypeValue ( );
		
		db.commit ( );
		db.setAutoCom (true);
		
		db.closeConnection ( );
		
		mapEntry.put ("oid", object_id.toPlainString ( ));
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "InternalMetadataEntry");
	}

	/**
	 * @param args
	 */
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
