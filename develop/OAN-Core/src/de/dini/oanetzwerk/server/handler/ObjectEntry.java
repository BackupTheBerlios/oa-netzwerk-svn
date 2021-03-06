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
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.DeleteFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
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
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DateValues (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification (stmtconn.connection, object_id, false));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Description (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().FullTextLinks(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Interpolated_DDC_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DINI_Set_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DNB_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().DuplicatePossibilities (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Formats (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Identifiers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Author (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Contributor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Editor (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Iso639Language (stmtconn.connection, object_id, false));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Keywords (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object2Language (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Other_Classification (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Publishers (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Titles (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().TypeValue (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().UsageData_ALL_Months (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().UsageData_ALL_Overall (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().WorkflowDB (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Worklist (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().RawData (stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Object (stmtconn.connection, object_id));
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
		
		// case of /ObjectEntry/fromRepositoryID/<repositoryID>/oidOffset/<oid-offset> request
		
		if (path.length == 4) {
		
			if (!path[0].equals("fromRepositoryID") && !path[2].equals("oidOffset")) {
				
				return HelperMethods.getRestFailureMessage(RestKeyword.ObjectEntry, RestStatusEnum.WRONG_PARAMETER, 
						"Invalid Request URL for resource /ObjectEntry ! Please check the REST-API for proper usage!");
			}

			BigDecimal repositoryID;
			BigDecimal oidOffset;
			
			try {
				
				repositoryID = new BigDecimal (path [1]);
				
			} catch (NumberFormatException ex) {
				
				return HelperMethods.getRestFailureMessage(RestKeyword.ObjectEntry, RestStatusEnum.WRONG_PARAMETER, 
						path [1] + " is NOT a number!");
			}

			try {
				
				oidOffset = new BigDecimal (path [3]);
				
			} catch (NumberFormatException ex) {
				
				return HelperMethods.getRestFailureMessage(RestKeyword.ObjectEntry, RestStatusEnum.WRONG_PARAMETER, 
						path [3] + " is NOT a number!");
			}

			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection stmtconn = null;
			RestEntrySet res = new RestEntrySet ( );
			
			try {
				
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
				
				stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectEntry (stmtconn.connection, repositoryID, oidOffset));
				this.result = stmtconn.execute ( );
				
				logWarnings();
				
				while (this.result.getResultSet ( ).next ( )) {
					
					if (logger.isDebugEnabled ( )) 
						logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
					
					res = new RestEntrySet(); 
					res.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal ("object_id").toPlainString ( ));
					res.addEntry ("repository_id", this.result.getResultSet ( ).getBigDecimal ("repository_id").toPlainString ( ));
					res.addEntry ("harvested", this.result.getResultSet ( ).getDate ("harvested").toString ( ));
					res.addEntry ("repository_datestamp", this.result.getResultSet ( ).getDate ("repository_datestamp").toString ( ));
					res.addEntry ("repository_identifier", this.result.getResultSet ( ).getString ("repository_identifier"));
					res.addEntry ("testdata", Boolean.toString (this.result.getResultSet ( ).getBoolean ("testdata")));
					res.addEntry ("failure_counter", Integer.toString (this.result.getResultSet ( ).getInt ("failure_counter")));
					res.addEntry ("peculiar", Boolean.toString (this.result.getResultSet ( ).getBoolean ("peculiar")));
					res.addEntry ("outdated", Boolean.toString (this.result.getResultSet ( ).getBoolean ("outdated")));
					res.addEntry ("peculiar_counter", Integer.toString (this.result.getResultSet ( ).getInt ("peculiar_counter")));
					this.rms.addEntrySet(res);
				}
				
				this.rms.setStatus (RestStatusEnum.OK);
				
				
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
		
		// case of /ObjectEntry/<oid> request
		
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
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectEntry (stmtconn.connection, objectEntryID));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
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
				res.addEntry ("peculiar", Boolean.toString (this.result.getResultSet ( ).getBoolean ("peculiar")));
				res.addEntry ("outdated", Boolean.toString (this.result.getResultSet ( ).getBoolean ("outdated")));
				
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
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("doing Post Object Entry");
		
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
		Date harvested = null;
		boolean testdata = true;
		int failureCounter = 0;
		
		boolean peculiar = false;
		boolean outdated = false;
		Integer peculiarCounter = 0;
		
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
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("repository_id: " + repository_id);
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				if (res.getValue (key) != null)
					repository_identifier = res.getValue (key);
				
				else repository_identifier = "";
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("repository_identifier: " + repository_identifier);
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				if (res.getValue (key) != null) {
					
					try {
						
						repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
						
					} catch (ParseException ex) {
						
						logger.error (ex.getLocalizedMessage ( ), ex);
					}
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("repository_datestamp: " + repository_datestamp);
					
				} else repository_datestamp = null;
			} else if (key.equalsIgnoreCase ("harvested")) {
				
				if (res.getValue (key) != null) {
					
					try {
						
						harvested = HelperMethods.extract_datestamp (res.getValue (key));
						
					} catch (ParseException ex) {
						
						logger.error (ex.getLocalizedMessage ( ), ex);
					}
									
				} else harvested = null;
			
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failure_counter")) {
				
				try {
				
					failureCounter = new Integer (res.getValue (key));
				
				} catch (NumberFormatException ex) {
					
					logger.error ("ObjectId " + object_id + " created a NumberFormatException!");
					logger.error (ex.getLocalizedMessage ( ), ex);
					
					failureCounter = 10;
				}
				
			} else if (key.equalsIgnoreCase ("peculiar")) {
				
				peculiar = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("outdated")) {
				
				outdated = new Boolean (res.getValue (key));				
			
			} else if (key.equalsIgnoreCase ("peculiar_counter")) {
				
				peculiarCounter = new Integer(res.getValue (key));				
			
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				logger.info (key + " found with value: " + res.getValue (key));
				
				continue;
			}
		}

		// if object is not marked as unavailable/peculiar, the request is an update, so we should update the harvested datestamp
		if (peculiarCounter == 0)
		{
			harvested = HelperMethods.today ( );
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());	
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Updating ObjectEntry " + object_id + " in database");
			
			stmtconn.loadStatement (DBAccessNG.updateInDB().Object (stmtconn.connection, object_id, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter, peculiar, outdated, peculiarCounter));
			this.result = stmtconn.execute ( );
					
			logWarnings();
			
			stmtconn.commit ( );
			stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectEntry (stmtconn.connection, repository_id, repository_datestamp, repository_identifier));
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
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method inserts a new Object entry. The values for the new object will be extracted from the
	 * HTTP-Body's data.
	 * The internal ObjectID of the newly created Object will be returned.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("doing Put ObjectEntry");
		
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
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("repository_id: " + repository_id);
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				repository_identifier = res.getValue (key);
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("repository_identifier: " + repository_identifier);
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				try {
					
					repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("repository_datestamp: " + repository_datestamp);
				
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failureCounter")) {
				
				failureCounter = new Integer (res.getValue (key));
				
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				logger.debug (key + " found with value: " + res.getValue (key));
				continue;
			}
		}

		Date harvested = HelperMethods.today ( );
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());		
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
			
			stmtconn.loadStatement (DBAccessNG.insertIntoDB().Object (stmtconn.connection, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				
				//warn, error, rollback, nothing????
			}
			
			stmtconn.commit ( );
			stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectEntry (stmtconn.connection, repository_id, repository_datestamp, repository_identifier));
			this.result = stmtconn.execute ( );
			
			logWarnings();

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
