package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
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
import de.dini.oanetzwerk.server.database.UpdateInDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class ObjectEntry extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ObjectEntry.class);
	
	/**
	 * 
	 */
	
	public ObjectEntry ( ) {
		
		super (ObjectEntry.class.getName ( ), RestKeyword.ObjectEntry);
	}
	
	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.DateValues (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.DDC_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Description (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.DINI_Set_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.DNB_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.DuplicatePossibilities (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Formats (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Identifiers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Author (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Contributor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Editor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Keywords (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object2Language (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Other_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Publishers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Titles (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.TypeValue (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.WorkflowDB (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.RawData (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.loadStatement (DeleteFromDB.Object (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				stmtconn.rollback ( );
				throw new SQLException ("Test Data could not be deleted");
				
			} else {
				
				stmtconn.commit ( );
			}
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("oid", object_id.toPlainString ( ));
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete ObjectEntry: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete ObjectEntry: " + ex.getLocalizedMessage ( ), ex);
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
	 * This method returns for a given internal ObjectID the key values for this Object.
	 * If the object does not exist "null" will be returned. 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
		BigDecimal objectEntryID;
		
		try {
			
			objectEntryID = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.ObjectEntry (stmtconn.connection, objectEntryID));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ), warning);
				}
			}
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1) +
							"\n\trepository_id = " + this.result.getResultSet ( ).getBigDecimal (2) +
							"\n\tharvested = " + this.result.getResultSet ( ).getDate (3).toString ( ) +
							"\n\trepository_datestamp = " + this.result.getResultSet ( ).getDate (4).toString ( ) +
							"\n\trepository_identifier = " + this.result.getResultSet ( ).getString (5));
				
				res.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal ("object_id").toPlainString ( ));
				res.addEntry ("repository_id", this.result.getResultSet ( ).getBigDecimal ("repository_id").toPlainString ( ));
				res.addEntry ("harvested", this.result.getResultSet ( ).getDate ("harvested").toString ( ));
				res.addEntry ("repository_datestamp", this.result.getResultSet ( ).getDate ("repository_datestamp").toString ( ));
				res.addEntry ("repository_identifier", this.result.getResultSet ( ).getString ("repository_identifier"));
				res.addEntry ("testdata", Boolean.toString (this.result.getResultSet ( ).getBoolean ("testdata")));
				res.addEntry ("failure_counter", Integer.toString (this.result.getResultSet ( ).getInt ("failure_counter")));
				res.addEntry ("peculiar", Integer.toString (this.result.getResultSet ( ).getInt ("peculiar")));
				res.addEntry ("outdated", Integer.toString (this.result.getResultSet ( ).getInt ("outdated")));
				
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (res);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ), ex);
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
			
			res = null;
			this.result = null;
			dbng = null;
		}
				
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectEntry);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		BigDecimal repository_id = new BigDecimal (0);
		String repository_identifier = "";
		Date repository_datestamp = null;
		boolean testdata = true;
		int failureCounter = 0;
		int peculiar = 0;
		int outdated = 0;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("repository_id")) {
				
				if (res.getValue (key) != null)
					repository_id = new BigDecimal (res.getValue (key));
				
				else repository_id = new BigDecimal (-1);
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				if (res.getValue (key) != null)
					repository_identifier = res.getValue (key);
				
				else repository_identifier = "";
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				if (res.getValue (key) != null) {
					
					try {
						
						repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
						
					} catch (ParseException ex) {
						
						logger.error (ex.getLocalizedMessage ( ));
						ex.printStackTrace ( );
					}
					
				} else repository_datestamp = null;
				
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failureCounter")) {
				
				failureCounter = new Integer (res.getValue (key));

			} else if (key.equalsIgnoreCase ("peculiar")) {
				
				peculiar = new Integer (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("outdated")) {
				
				outdated = new Integer (res.getValue (key));				
				
				
				
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				continue;
			}
		}

		Date harvested = HelperMethods.today ( );
		
		DBAccessNG dbng = new DBAccessNG ( );		
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (UpdateInDB.Object (stmtconn.connection, object_id, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter));
			this.result = stmtconn.execute ( );
						
			if (this.result.getUpdateCount ( ) < 1) {
				
				//TODO:warn, error, rollback, nothing????
			}
			
			stmtconn.commit ( );
			stmtconn.loadStatement (SelectFromDB.ObjectEntry (stmtconn.connection, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter));
			this.result = stmtconn.execute ( );
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: object_id = " + this.result.getResultSet ( ).getInt (1));
				
				res.addEntry ("oid", Integer.toString (this.result.getResultSet ( ).getInt (1)));
				stmtconn.commit ( );
				this.rms.setStatus (RestStatusEnum.OK);
				
				this.rms.addEntrySet (res);
				res = null;
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
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
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method inserts a new Object entry. The values for the new object will be extracted from the
	 * HTTP-Body's data.
	 * The internal ObjectID of the newly created Object will be returned.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal repository_id = new BigDecimal (0);
		String repository_identifier = "";
		Date repository_datestamp = null;
		boolean testdata = true;
		int failureCounter = 0;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("repository_id")) {
				
				repository_id = new BigDecimal (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				repository_identifier = res.getValue (key);
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				try {
					
					repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failureCounter")) {
				
				failureCounter = new Integer (res.getValue (key));
				
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				continue;
			}
		}

		Date harvested = HelperMethods.today ( );
		
		DBAccessNG dbng = new DBAccessNG ( );		
		MultipleStatementConnection stmtconn = null;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("The following values will be inserted:\n\tRepository ID = " + repository_id +
					"\n\tHarvested = " + harvested +
					"\n\tRepository Datestamp = " + repository_datestamp +
					"\n\texternal OID = " + repository_identifier);
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (InsertIntoDB.Object (stmtconn.connection, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.commit ( );
			stmtconn.loadStatement (SelectFromDB.ObjectEntry (stmtconn.connection, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter));
			this.result = stmtconn.execute ( );
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: object_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				res.addEntry ("oid", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				stmtconn.commit ( );
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
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
	
} // end of class
