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
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class ObjectEntryID extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ObjectEntryID.class);
	
	/**
	 * 
	 */
	
	public ObjectEntryID ( ) {
		
		super (ObjectEntryID.class.getName ( ), RestKeyword.ObjectEntryID);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectEntryID);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.ObjectEntryID+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns for a given RepositoryID and a given external ObjectID an
	 * internal ObjectID is it exists. When it does not exist "null" will be returned.
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 2)
			throw new NotEnoughParametersException ("This method needs at least 3 parameters: the keyword, the repository ID and the external Object ID");
		
		BigDecimal repositoryID; 
		
		try {
			
			repositoryID = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectEntryID);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		StringBuffer externalOID = new StringBuffer ( );
		boolean checkRawData = false;
		
		externalOID.append (path [1]);
		
		if (path.length > 2) {
		
			for (int i = 2; i < path.length; i++) {
				
				if (path [i].equalsIgnoreCase ("existsRawData")) {
					
					checkRawData = true;
					break;
					
				} else
					externalOID.append ("/" + path [i]);
			}
		}
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDBSybase.ObjectEntryID (stmtconn.connection, repositoryID, externalOID.toString ( )));
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : this.result.getWarning ( )) {
					
					this.rms.setStatusDescription (this.rms.getStatusDescription ( ) + "\n" + warning.getLocalizedMessage ( ));
					logger.warn (warning.getLocalizedMessage ( ));
				}
				
				this.rms.setStatus (RestStatusEnum.SQL_WARNING);
			}
			
			if (this.result.getResultSet ( ).next ( )) {
				
				BigDecimal internalOID = this.result.getResultSet ( ).getBigDecimal ("object_id");
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: internal objectID = " + internalOID);
				
				res.addEntry ("oid", internalOID.toPlainString ( ));
				
				this.rms.addEntrySet (res);
				
				if (checkRawData) {
					
					stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
					stmtconn.loadStatement (SelectFromDBSybase.RawRecordData (stmtconn.connection, internalOID));
					
					this.result = stmtconn.execute ( );
					
					if (this.result.getWarning ( ) != null) {
						
						for (Throwable warning : result.getWarning ( )) {
							
							logger.warn (warning.getLocalizedMessage ( ));
							this.rms.setStatusDescription (this.rms.getStatusDescription ( ) + "\n" + warning.getLocalizedMessage ( ));
						}
						
						this.rms.setStatus (RestStatusEnum.SQL_WARNING);
					}
					
					if (this.result.getResultSet ( ).next ( )) {
					
						if (this.result.getResultSet ( ).getString ("data") == null || (this.result.getResultSet ( ).getString ("data").equals (""))) {
							
							this.rms.setStatus (RestStatusEnum.NO_RAWDATA_FOUND);
							this.rms.setStatusDescription (this.rms.getStatusDescription ( ) + "\nNo RawData found");
						}
						
					} else {
						
						this.rms.setStatus (RestStatusEnum.NO_RAWDATA_FOUND);
					}
					
				} else
					this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal Object-ID found");
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching internal Object-ID found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ), ex);
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectEntryID);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.ObjectEntryID+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectEntryID);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.ObjectEntryID+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
