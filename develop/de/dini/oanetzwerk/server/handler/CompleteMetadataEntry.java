package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;

/**
 * @author Robin Malitz
 *
 */

public class CompleteMetadataEntry extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (CompleteMetadataEntry.class);
	
	/**
	 * 
	 */
	
	private final CompleteMetadataJAXBMarshaller cmMarsh;
	
	/**
	 * 
	 */
	
	public CompleteMetadataEntry ( ) {
		
		super (CompleteMetadataEntry.class.getName ( ), RestKeyword.CompleteMetadataEntry);
		this.cmMarsh = CompleteMetadataJAXBMarshaller.getInstance ( );
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		this.rms = new RestMessage (RestKeyword.CompleteMetadataEntry);
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

		// erzeuge cmf-Object, das Schrittweise mit Daten befüllt wird
		CompleteMetadata cmf = new CompleteMetadata();
		
		try {
			
            cmf.setOid(new BigDecimal (path [0]));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.CompleteMetadataEntry);
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
			MetadataDBMapper.fillInternalMetadataFromDB(cmf, stmtconn);
			
			
			DuplicateProbability dupPro = new DuplicateProbability(new BigDecimal(815), 99.9, 0);
			cmf.addDuplicateProbability(dupPro);
			
			// FulltextlinkAbfrage
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, cmf.getOid()));
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
				cmf.addFullTextLink(ftl);				
			}			
			
			stmtconn.commit ( );
			
			//logger.debug("CMF (toString) >>>>>> " + cmf);
			
			if(cmf.isEmpty()) {
				// leeres CMF soll Fehler geben
				
				logger.info("leeres CMF über Keyword angefragt");
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("Die Anfrage auf OID ("+cmf.getOid()+") liefert ein leeres CMF aus der Datenbank.");
				
			} else {
				// nichtleeres IMF soll auch verschickt werden

				String xmlData;
				xmlData = cmMarsh.marshall (cmf);				
				res.addEntry ("completemetadata", xmlData);
				//logger.debug("CMF (gemarshelled) >>>>>> " + xmlData);
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (res);
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get CompleteMetadataEntry: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
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
		this.rms = new RestMessage (RestKeyword.CompleteMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.CompleteMetadataEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
