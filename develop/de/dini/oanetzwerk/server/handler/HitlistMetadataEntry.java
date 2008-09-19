/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.Editor;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.HitlistMetadata;
import de.dini.oanetzwerk.utils.imf.HitlistMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * @author Robin Malitz
 *
 */

public class HitlistMetadataEntry extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (HitlistMetadataEntry.class);
	private final HitlistMetadataJAXBMarshaller hmMarsh;
	
	/**
	 * 
	 */
	
	public HitlistMetadataEntry ( ) {
		
		super (HitlistMetadataEntry.class.getName ( ), RestKeyword.HitlistMetadataEntry);
		this.hmMarsh = HitlistMetadataJAXBMarshaller.getInstance ( );
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		this.rms = new RestMessage (RestKeyword.HitlistMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");

		// erzeuge hmf-Object, das Schrittweise mit Daten befüllt wird
		HitlistMetadata hmf = new HitlistMetadata();
		
		try {
			
            hmf.setOid(new BigDecimal (path [0]));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.HitlistMetadataEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
			MetadataDBMapper.fillHitlistMetadataFromDB(hmf, stmtconn);
			
			DuplicateProbability dupPro = new DuplicateProbability(new BigDecimal(815), 99.9, 0);
			hmf.addDuplicateProbability(dupPro);

			// FulltextlinkAbfrage
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, hmf.getOid()));
			QueryResult ftlResult = stmtconn.execute ( );
			
			if (ftlResult.getWarning ( ) != null) {
				for (Throwable warning : ftlResult.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (ftlResult.getResultSet ( ).next ( )) {				
				FullTextLink ftl = new FullTextLink();					
				ftl.setUrl(ftlResult.getResultSet().getString("link"));
				ftl.setMimeformat(ftlResult.getResultSet().getString("mimeformat"));
				hmf.addFullTextLink(ftl);				
			}
			
			stmtconn.commit ( );
			
			//logger.debug("hmf (toString) >>>>>> " + hmf);
			
			if(hmf.isEmpty()) {
				// leeres hmf soll Fehler geben
				
				logger.info("leeres hmf über Keyword angefragt");
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("Die Anfrage auf OID ("+hmf.getOid()+") liefert ein leeres hmf aus der Datenbank.");
				
			} else {
				// nichtleeres HMF soll auch verschickt werden

				String xmlData;
				xmlData = hmMarsh.marshall (hmf);				
				res.addEntry ("hitlistmetadata", xmlData);
				//logger.debug("hmf (gemarshelled) >>>>>> " + xmlData);
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (res);
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get InternalMetadataEntry: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
				}
			}
						
			res = null;
			this.result = null;
			dbng = null;
		}
//		
//		String xmlData;
//		xmlData = imMarsch.marshall (imf);
//		
////		RestEntrySet res = new RestEntrySet ( );
//		
//		res.addEntry ("internalmetadata", xmlData);
//		
//		this.rms.setStatus (RestStatusEnum.OK);
//		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		this.rms = new RestMessage (RestKeyword.HitlistMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.HitlistMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

}
