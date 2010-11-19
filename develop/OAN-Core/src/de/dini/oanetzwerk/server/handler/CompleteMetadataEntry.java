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
import de.dini.oanetzwerk.utils.imf.InterpolatedDDCClassification;
import de.dini.oanetzwerk.utils.imf.RepositoryData;

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
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.CompleteMetadataEntry+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");

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
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			////////////////////////////
			// Kern-Metadaten - Abfrage
			////////////////////////////
			
			// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
			MetadataDBMapper.fillInternalMetadataFromDB(cmf, stmtconn);
			
			////////////////////////////
			// DupPro - Abfrage
			////////////////////////////			
			
			stmtconn.loadStatement (SelectFromDB.DuplicateProbabilities (stmtconn.connection, cmf.getOid()));
			QueryResult dupproResult = stmtconn.execute ( );
			
			if (dupproResult.getWarning ( ) != null)
				for (Throwable warning : dupproResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			int num = 0;
			while (dupproResult.getResultSet ( ).next ( )) {
				try {
				  DuplicateProbability dupPro = new DuplicateProbability();
				  dupPro.setNumber(num);
				  dupPro.setReferToOID(new BigDecimal(dupproResult.getResultSet().getString("duplicate_id")));
				  dupPro.setProbability(dupproResult.getResultSet().getDouble("percentage"));				  
				  //TODO: dupPro.setReverseProbability(dupproResult.getResultSet().getDouble("reversePercentage")); 				  
				  cmf.addDuplicateProbability(dupPro);				
				  num++;
				} catch(Exception ex) {
					logger.error("error fetching duplicate possibilities for OID: " + cmf.getOid(), ex);
				}
			}	
						
			//////////////////////////
			// Fulltextlink - Abfrage
			//////////////////////////
			
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, cmf.getOid()));
			QueryResult ftlResult = stmtconn.execute ( );
			
			if (ftlResult.getWarning ( ) != null)
				for (Throwable warning : ftlResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			while (ftlResult.getResultSet ( ).next ( )) {				
				FullTextLink ftl = new FullTextLink();					
				ftl.setUrl(ftlResult.getResultSet().getString("link"));
				ftl.setMimeformat(ftlResult.getResultSet().getString("mimeformat"));
				cmf.addFullTextLink(ftl);				
			}			
			
			////////////////////////////			
			// RepositoryData - Abfrage
			////////////////////////////
			
			stmtconn.loadStatement (SelectFromDB.RepositoryData(stmtconn.connection, cmf.getOid()));
			QueryResult repdataResult = stmtconn.execute ( );
			
			if (repdataResult.getWarning ( ) != null)
				for (Throwable warning : repdataResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			while (repdataResult.getResultSet ( ).next ( )) {
				RepositoryData repData = new RepositoryData(cmf.getOid());
				repData.setRepositoryID(repdataResult.getResultSet().getInt("repository_id"));
				repData.setRepositoryName(repdataResult.getResultSet().getString("name"));
				repData.setRepositoryOAI_BASEURL(repdataResult.getResultSet().getString("oai_url"));
				repData.setRepositoryOAI_EXTID(repdataResult.getResultSet().getString("repository_identifier"));
				repData.setRepositoryURL(repdataResult.getResultSet().getString("url"));
				cmf.setRepositoryData(repData);
			}			
			
			//////////////////////////////
			// InterpolatedDDC - Abfrage
			//////////////////////////////
			
			stmtconn.loadStatement (SelectFromDB.InterpolatedDDCClassification(stmtconn.connection, cmf.getOid()));
			QueryResult interpolatedDDCResult = stmtconn.execute ( );
			
			if (interpolatedDDCResult.getWarning ( ) != null)
				for (Throwable warning : interpolatedDDCResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			while (interpolatedDDCResult.getResultSet ( ).next ( )) {
				InterpolatedDDCClassification interpolatedDDC = new InterpolatedDDCClassification();				
				interpolatedDDC.setValue(interpolatedDDCResult.getResultSet().getString("Interpolated_DDC_Categorie"));
				interpolatedDDC.setProbability(interpolatedDDCResult.getResultSet().getDouble("percentage"));
				cmf.addClassfication(interpolatedDDC);
			}		
			
			/////////////////
			// COMMIT
			/////////////////
			
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
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.CompleteMetadataEntry+"'.");
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
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.CompleteMetadataEntry+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
