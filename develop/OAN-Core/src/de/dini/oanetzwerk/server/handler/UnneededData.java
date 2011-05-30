package de.dini.oanetzwerk.server.handler;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.DeleteFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

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
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().PersonWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			// Diese Anfrage braucht etwa 24 sec und sollte daher sparsam, nach einem Aggregatorlauf im Automodus gefeuert werden
						
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().KeywordsWithoutReference (stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().LanguagesWithoutReference(stmtconn.connection));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Iso639LanguagesWithoutReference(stmtconn.connection));
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
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.UnneededData+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	@Override
	protected String postKeyWord (String [ ] path, String data) {

		this.rms = new RestMessage (RestKeyword.UnneededData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.UnneededData+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
		
	@Override
	protected String putKeyWord (String [ ] path, String data)  {
	
		this.rms = new RestMessage (RestKeyword.UnneededData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);		
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.UnneededData+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
