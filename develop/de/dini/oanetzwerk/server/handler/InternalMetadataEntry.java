/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.*;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.imf.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Michael Kühn
 *
 */

public class InternalMetadataEntry extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (InternalMetadataEntry.class);
	private InternalMetadataJAXBMarshaller imMarsch;
	
	/**
	 * 
	 */
	
	public InternalMetadataEntry ( ) {
		
		super (InternalMetadataEntry.class.getName ( ), RestKeyword.InternalMetadataEntry);
		imMarsch = InternalMetadataJAXBMarshaller.getInstance ( );
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
	
	@Override
	protected String getKeyWord (String [ ] path) {
		
		// erzeuge imf-Object, das Schrittweise mit Daten befüllt wird
		InternalMetadata imf = new InternalMetadata();
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		BigDecimal oid = new BigDecimal (path [2]);
		
		// Auswertung der Titel
		this.resultset = db.selectTitle (oid);
		
		if (this.resultset == null)
			logger.warn ("resultset empty!");
		
		try {
			
			while (this.resultset.next ( )) {
				
				Title temp = new Title ( );
				temp.setTitle (this.resultset.getString ("title"));
				temp.setQualifier (this.resultset.getString ("qualifier"));
				temp.setLang (this.resultset.getString ("lang"));
				//temp.setNumber (rs.getInt ("number"));
				imf.addTitle (temp);
			}
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
		}
		
		String xmlData;
		xmlData = imMarsch.marshall (imf);
		
		RestEntrySet res = new RestEntrySet ( );
		
		res.addEntry ("internalmetadata", xmlData);
		
		this.rms.setStatus (RestStatusEnum.OK);
		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
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
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		InternalMetadata imf = null;
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("internalmetadata")) {
				
				imf = imMarsch.unmarshall (res.getValue (key));
				
			} else continue;
		}
		
		
		List <Title> titlelist = imf.getTitles ( );
		//List <Author> authors;
		//List <Keyword> keywords = imf.getKeywords ( );
		//List <Publisher> publishers;
		List <DateValue> dateValues = imf.getDateValues ( );
		List <Format> formatList = imf.getFormatList ( );
		List <Identifier> identifierList = imf.getIdentifierList ( );
		List <Description> descriptionList = imf.getDescriptions();
		List <TypeValue> typeValueList = imf.getTypeValueList ( );
		//List <Language> languageList;
		//List<Classification> classificationList;
		
		//ResultSet resultset;
		
		db.setAutoCom (false);
		
		
		//for (Keyword keyword : keywords)
		//	db.insertkeyword ( );
		
		//for (Description description : descriptions)
		//	db.insertDescription ( );

		try {

			for (Title title : titlelist)			
				db.insertTitle (object_id, title.getQualifier ( ), title.getTitle ( ), title.getLang ( ));
			
			for (DateValue dateValue : dateValues)

				try {

					db.insertDateValue(object_id, dateValue.getNumber(),
							HelperMethods.extract_datestamp(dateValue
									.getDateValue()));

				} catch (ParseException ex) {

					logger.error("Datestamp with datevalue incorrect");
					ex.printStackTrace();
				}

			for (Format format : formatList)
				db.insertFormat(object_id, format.getNumber(), format
						.getSchema_f());

			for (Identifier identifier : identifierList)
				db.insertIdentifier(object_id, identifier.getNumber(),
						identifier.getIdentifier());

			for (Description description : descriptionList)
				db.insertDescription(object_id, description.getNumber(), description.getDescription());
			
//			for (TypeValue typeValue : typeValueList)
//				db.insertTypeValue ( );

			db.commit();
			db.setAutoCom(true);

		} catch (SQLException sqlex) {
			db.rollback();
		}
		
		
		db.closeConnection ( );
		
		this.rms = new RestMessage (RestKeyword.InternalMetadataEntry);
		res = new RestEntrySet ( );
		
		res.addEntry ("oid", object_id.toPlainString ( ));
		this.rms.setStatus (RestStatusEnum.OK);
		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}
}
