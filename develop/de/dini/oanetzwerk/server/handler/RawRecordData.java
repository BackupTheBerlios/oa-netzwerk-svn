package de.dini.oanetzwerk.server.handler;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.UpdateInDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Manuel Klatt-Kafemann
 * @author Robin Malitz
 *
 */

public class RawRecordData extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (RawRecordData.class); 
	
	/**
	 * 
	 */
	
	public RawRecordData ( ) {
		
		super (RawRecordData.class.getName ( ), RestKeyword.RawRecordData);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {
		
		this.rms = new RestMessage (RestKeyword.RawRecordData);
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
			throw new NotEnoughParametersException ("This method needs the object ID as first parameter");
		
		BigDecimal internalOID;
		
		try {
			
			internalOID = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.RawRecordData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		
		try {
			boolean loadhistory = false;
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			if (path.length > 1) {
				
				Date repository_timestamp = null;
				
				if (path [1].equalsIgnoreCase ("history"))
					loadhistory = true;
				
				else {
					
					try {
						
						repository_timestamp = HelperMethods.extract_datestamp (path [1]);
						
					} catch (ParseException ex) {
						
						logger.error (path [1] + " is NOT a datestamp!");
						
						this.rms = new RestMessage (RestKeyword.RawRecordData);
						this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
						this.rms.setStatusDescription (path [0] + " is NOT a datestamp!");
						
						return RestXmlCodec.encodeRestMessage (this.rms);
					}
				
					if (logger.isDebugEnabled ( ))
						logger.debug ("internal OID = " + internalOID + " || Repository-Timestamp = " + repository_timestamp.toString ( ));
					
				}
				
				if (loadhistory)
					stmtconn.loadStatement (SelectFromDB.RawRecordDataHistory (stmtconn.connection, internalOID));
				
				else
					stmtconn.loadStatement (SelectFromDB.RawRecordData (stmtconn.connection, internalOID, repository_timestamp));
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("internal OID = " + internalOID);
				
				stmtconn.loadStatement (SelectFromDB.RawRecordData (stmtconn.connection, internalOID));
			}
			
			this.result = stmtconn.execute ( );
			this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
			
			RestEntrySet res;
			
			while (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getInt (1) +
							"\n\trepository_timestamp = " + this.result.getResultSet ( ).getDate (2).toString ( ) +
							"\n\tdata = " + this.result.getResultSet ( ).getString (3));
				
				res = new RestEntrySet ( );
				
				res.addEntry ("object_id", Integer.toString (this.result.getResultSet ( ).getInt ("object_id")));
				res.addEntry ("repository_timestamp", this.result.getResultSet ( ).getDate ("repository_timestamp").toString ( ));
				res.addEntry ("metaDataFormat", this.result.getResultSet ( ).getString ("MetaDataFormat"));
				
				if (!loadhistory) {
					
					res.addEntry ("data", this.result.getResultSet ( ).getString ("data"));
					res.addEntry ("precleaned_data", this.result.getResultSet ( ).getString ("precleaned_data"));
				}
				
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (res);
			} 
			
			if (this.rms.getStatus ( ).equals (RestStatusEnum.NO_OBJECT_FOUND_ERROR)) {
				
				logger.warn ("no results at all. Continueing...");
				this.rms.setStatusDescription ("No matching RawRecordData found for OID " + internalOID);
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
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
			
			this.result = null;
			dbng = null;
		}
				
		return RestXmlCodec.encodeRestMessage (rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 2)
			throw new NotEnoughParametersException ("This method needs 3 parameters: the keyword and the Repository-Timestamp");
		
		BigDecimal object_id;
		Date repository_timestamp;
		
		try {
				
			object_id = new BigDecimal (path [0]);
			repository_timestamp = HelperMethods.extract_datestamp (path [1]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.RawRecordData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
			
		} catch (ParseException ex) {
			
			logger.error (path [1] + " is NOT a datestamp!");
			
			this.rms = new RestMessage (RestKeyword.RawRecordData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a datestamp!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}

		DBAccessNG dbng = new DBAccessNG ( );		
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			if (path.length > 2) {
				
				String metaDataFormat = path [2];
				stmtconn.loadStatement (UpdateInDB.PrecleanedData (stmtconn.connection, object_id, repository_timestamp, metaDataFormat, data));
				
				metaDataFormat = null;
				
			} else {
				
				stmtconn.loadStatement (UpdateInDB.PrecleanedData (stmtconn.connection, object_id, repository_timestamp, data));
			}
				
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			res.addEntry ("ObjectID", (object_id.toPlainString ( )));
			
			this.rms.setStatus (RestStatusEnum.OK);
		
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Post RawRecordData: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Post RawRecordData: " + ex.getLocalizedMessage ( ));
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
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}
		
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 2)
			throw new NotEnoughParametersException ("This method needs 4 parameters: the keyword, the object ID, the repository timestamp and the metadataformat");
		
		BigDecimal object_id;
		Date repository_timestamp;
		String metaDataFormat;
		
		if (path.length == 2)
			metaDataFormat = "oai_dc";
		
		else
			metaDataFormat = new String (path [2]);
		
		try {
			
			object_id = new BigDecimal (path [0]);
			repository_timestamp = HelperMethods.extract_datestamp (path [1]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.RawRecordData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
			
		} catch (ParseException ex) {
			
			logger.error (path [1] + " is NOT a datestamp!");
			this.rms = new RestMessage (RestKeyword.RawRecordData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [1] + " is NOT a datestamp!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );		
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		if (logger.isDebugEnabled ( ))
			try {
				logger.debug ("The following values will be inserted:\n\tinternal OID = " + object_id +
						"\n\tRepository Datestamp = " + repository_timestamp +
						"\n\tData = " + new String (Base64.decodeBase64((data).getBytes ("UTF-8"))) +
						"\n\tMetaDataFormat = " + metaDataFormat);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.warn (e.getLocalizedMessage());
			}
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (InsertIntoDB.RawRecordData (stmtconn.connection, object_id, repository_timestamp, data, metaDataFormat));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			res.addEntry ("oid", Integer.toString (this.result.getUpdateCount ( )));
			
			this.rms.setStatus (RestStatusEnum.OK);
		
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Put RawRecordData: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Put RawRecordData: " + ex.getLocalizedMessage ( ));
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
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
