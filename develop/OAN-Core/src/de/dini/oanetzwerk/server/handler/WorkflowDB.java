package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Manuel Klatt-Kafemann
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class WorkflowDB extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (WorkflowDB.class);
	
	/**
	 * 
	 */
	
	public WorkflowDB ( ) {

		super (WorkflowDB.class.getName ( ), RestKeyword.WorkflowDB);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		
		this.rms = new RestMessage (RestKeyword.WorkflowDB);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.WorkflowDB+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		boolean complete = false;
		boolean forSpecificRepoOnly = false;
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the Service ID");
		
		if (path.length == 2) {
			if (path[1].equals("completeRebuild"))
				complete = true;
		}
		
		if (path.length >= 3) {
			if (path[2].equals("forRepository"))
				complete = false;
				forSpecificRepoOnly = true;
		}
		
		BigDecimal service_id;
		
		try {
			
			service_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.WorkflowDB);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		
		BigDecimal repository_id = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			if (complete == false) {
				// nur neu zu bearbeitende Daten laden
				
				if (forSpecificRepoOnly) {
					repository_id = new BigDecimal (path [2]);
					stmtconn.loadStatement (DBAccessNG.selectFromDB().WorkflowDB (stmtconn.connection, service_id, repository_id));
				}
				stmtconn.loadStatement (DBAccessNG.selectFromDB().WorkflowDB (stmtconn.connection, service_id));
				
			} else if (complete == true) {
				// neu zu bearbeitende Daten und schon ehemals bearbeitete Daten laden
				stmtconn.loadStatement (DBAccessNG.selectFromDB().WorkflowDBComplete (stmtconn.connection, service_id));
			}
			this.result = stmtconn.execute ( );
			
			logWarnings();

			// Datumsformat wie in DB
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			while (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				RestEntrySet entrySet = new RestEntrySet(); 
				entrySet.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				entrySet.addEntry ("time", formatter.format(this.result.getResultSet ( ).getTimestamp(2)));

				//				if (forSpecificRepoOnly) {
//					entrySet.addEntry ("", formater.format(this.result.getResultSet ( ).getDate (2)));
//					entrySet.addEntry ("time", formater.format(this.result.getResultSet ( ).getDate (3)));
//				} else 
				this.rms.addEntrySet(entrySet);
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get WorklflowDB: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get WorklflowDB: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [2] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.WorkflowDB);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
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
		
		this.rms = new RestMessage (RestKeyword.WorkflowDB);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.WorkflowDB+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = null;
		BigDecimal service_id = null;
		Date time = null;
		boolean newObject = false;
		
		long putStart = System.currentTimeMillis();
		
		if (path.length == 1) {
			if (path[0].equals("newObject")) newObject = true;
		}
			
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		try {
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key = " + key);
				
				if (key.equalsIgnoreCase ("object_id")) {
					
					object_id = new BigDecimal (res.getValue (key));
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("object_id: " + res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("service_id")) {
					
					service_id = new BigDecimal (res.getValue (key));
					
					if (logger.isDebugEnabled ( ))
						logger.debug ("object_id: " + res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("time")) {
					
					try {

						time = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.S").parse (new String (res.getValue (key)));
						
					} catch (ParseException ex) {
						try {
							time = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").parse (new String (res.getValue (key)));
						} catch (ParseException e) {
							logger.error (ex.getLocalizedMessage ( ), e);
						}
					}
				}
//					time = new String (res.getValue (key));
				
				else continue;
			}
			
		} catch (NumberFormatException ex) {
			
			logger.error (res.getValue (key) + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.WorkflowDB);
			this.rms.setStatusDescription (res.getValue (key) + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		this.rms = new RestMessage (RestKeyword.WorkflowDB);

		// Prüfen, ob überhaupt Daten übergeben wurden
		if ((object_id == null) | (service_id == null) | ((newObject == false && time == null))) {
			// Fehlermeldung generieren und abbrechen
			this.rms = new RestMessage (RestKeyword.WorkflowDB);
			if (time == null) {
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription ("Incorrect timestamp format, expected format is YYYY-MM-DD HH:MM:SS");
			} else {
				this.rms.setStatus (RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
				this.rms.setStatusDescription ("PUT /WorkflowDB/ needs 3 entries in body: object_id, service_id and time");
			}
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		res = new RestEntrySet ( );
		
		try {
			// 1. neuer Eintrag in WorkflowDB  , automatisch wird in Worklist ein Eintrag über Trigger angelegt
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );

			
			logger.info("PUT WorkflowDB process " + Long.toString(System.currentTimeMillis() - putStart));
			stmtconn.loadStatement (DBAccessNG.insertIntoDB().WorkflowDB (stmtconn.connection, object_id, service_id));
			this.result = stmtconn.execute ( );
			logWarnings();
			
			stmtconn.commit ( );
			logger.info("PUT WorkflowDB process2 " + Long.toString(System.currentTimeMillis() - putStart));

			// 2. eingetragenen Zeitwert auslesen
			stmtconn.loadStatement (DBAccessNG.selectFromDB().WorkflowDBInserted(stmtconn.connection, object_id, service_id));
			this.result = stmtconn.execute ( );

			logWarnings();
			
			if (this.result.getResultSet ( ).next ( )) {

				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: workflow_id = " + this.result.getResultSet ( ).getBigDecimal (1));

				res.addEntry ("workflow_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				stmtconn.commit ( );

				logger.debug("newObject = " + newObject);
				
				logger.info("PUT WorkflowDB process3 " + Long.toString(System.currentTimeMillis() - putStart));
				// 3. Löschen der alten Daten
				if (newObject == false) {

					stmtconn.loadStatement (DBAccessNG.deleteFromDB().WorkflowDB (stmtconn.connection, object_id, new Timestamp(time.getTime()), service_id));
					this.result = stmtconn.execute();

					logWarnings();
				}

				stmtconn.commit ( );
				logger.info("PUT WorkflowDB process4 " + Long.toString(System.currentTimeMillis() - putStart));
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching WorklflowDB Entry found");
			}
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().WorkflowDBInserted(stmtconn.connection, object_id, service_id));
			
			this.result = stmtconn.execute ( );
			logger.info("PUT WorkflowDB process5 " + Long.toString(System.currentTimeMillis() - putStart));
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: workflow_id = " + this.result.getResultSet ( ).getBigDecimal (1));
		
				res.addEntry ("workflow_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				stmtconn.commit ( );
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching WorklflowDB Entry found");
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {

			logger.error (ex.getLocalizedMessage ( ));
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
