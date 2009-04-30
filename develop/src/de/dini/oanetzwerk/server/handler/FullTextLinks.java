package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Manuel Klatt-Kafemann
 * 
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class FullTextLinks extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (FullTextLinks.class);
	
	/**
	 * 
	 */
	
	public FullTextLinks ( ) {
		
		super (FullTextLinks.class.getName ( ), RestKeyword.FullTextLinks);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.FullTextLinks);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.FullTextLinks);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.FullTextLinks (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null)
				for (Throwable warning : this.result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				stmtconn.rollback ( );
				throw new SQLException ("FullTextLinks entries could not be deleted");
				
			} else {
				
				stmtconn.commit ( );
			}
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("oid", object_id.toPlainString ( ));
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete FullTextLinks: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete FullTextLinks: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.result = null;
			dbng = null;
		}
				
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
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);

			if (object_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.FullTextLinks);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
				
			}
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.FullTextLinks);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		SingleStatementConnection stmtconn = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null)
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			RestEntrySet entrySet;
			boolean resultSetWasEmpty = true;
			while (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				entrySet = new RestEntrySet(); 
				entrySet.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal ("object_id").toPlainString ( ));
				entrySet.addEntry("mimeformat", this.result.getResultSet().getString("mimeformat"));
				entrySet.addEntry("link", this.result.getResultSet().getString("link"));
				this.rms.addEntrySet(entrySet);

				resultSetWasEmpty = false;
			}
			
			if (resultSetWasEmpty == true) {
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
			} else {
				this.rms.setStatus (RestStatusEnum.OK);
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get WorklflowDB: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ), ex);
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.FullTextLinks);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = null;
		String mimeformat = null;
		String link = null;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		try {
			
			object_id = new BigDecimal (path [0]);

			if (object_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.FullTextLinks);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
				
			}
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.FullTextLinks);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		try {
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key = " + key);
				
//				if (key.equalsIgnoreCase ("object_id"))
//					object_id = new BigDecimal (res.getValue (key));

//				else if (key.equalsIgnoreCase ("mimeformat"))
				if (key.equalsIgnoreCase ("mimeformat"))
					mimeformat = new String (res.getValue (key));
				else if (key.equalsIgnoreCase ("link"))
					link = new String (res.getValue (key));
				
				else continue;
			}
			
		} catch (NumberFormatException ex) {
			
			logger.error (res.getValue (key) + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.FullTextLinks);
			this.rms.setStatusDescription (res.getValue (key) + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		
		this.rms = new RestMessage (RestKeyword.FullTextLinks);
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (InsertIntoDB.FullTextLinks (stmtconn.connection, object_id, mimeformat, link));
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null)
				for (Throwable warning : this.result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			stmtconn.commit ( );
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, object_id));
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getResultSet ( ).next ( )) {
				
				res.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal ("object_id").toPlainString ( ));
				stmtconn.commit ( );
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching WorklflowDB Entry found");
			}
			
		} catch (SQLException ex) {
			
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
}
