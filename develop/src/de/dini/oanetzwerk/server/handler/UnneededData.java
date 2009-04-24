package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
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
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Editor;
import de.dini.oanetzwerk.utils.imf.Format;
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
 * @author Manuel Klatt-Kafemann
 * @author Robin Malitz
 * @author Michael K&uuml;hn
 *
 */

public class UnneededData extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	private static Logger logger = Logger.getLogger (UnneededData.class);

	public UnneededData ( ) {
		
		super (UnneededData.class.getName ( ), RestKeyword.UnneededData);

	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.PersonWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			// Diese Anfrage braucht etwa 24 sec und sollte daher sparsam, nach einem Aggregatorlauf im Automodus gefeuert werden
						
			stmtconn.loadStatement (DeleteFromDB.KeywordsWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.LanguagesWithoutReference(stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DeleteFromDB.Iso639LanguagesWithoutReference(stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
			
			stmtconn.commit ( );
			
			this.rms.setStatus(RestStatusEnum.OK);
			this.rms.addEntrySet(res);
			
		} catch (SQLException ex) {
			
			try {
				
				stmtconn.rollback ( );
				
			} catch (SQLException ex1) {
				
				logger.error (ex1.getLocalizedMessage ( ), ex1);
			}
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
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
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	@Override
	protected String getKeyWord (String [ ] path) {
		
		this.rms = new RestMessage (RestKeyword.UnneededData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	@Override
	protected String postKeyWord (String [ ] path, String data) {

		this.rms = new RestMessage (RestKeyword.UnneededData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
		
	@Override
	protected String putKeyWord (String [ ] path, String data)  {
	
		this.rms = new RestMessage (RestKeyword.UnneededData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
