/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

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
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class WorkflowDB extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the Service ID");
		
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
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
//		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.WorkflowDB (stmtconn.connection, service_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}

			// Datumsformat wie in DB
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			while (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				RestEntrySet entrySet = new RestEntrySet(); 
				entrySet.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				entrySet.addEntry ("time", formater.format(this.result.getResultSet ( ).getDate (2)));
				this.rms.addEntrySet(entrySet);
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get WorklflowDB: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ));
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
			
//			this.rms.addEntrySet (res);
//			res = null;
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = null;
		BigDecimal service_id = null;
		String time = null;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		try {
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key = " + key);
				
				if (key.equalsIgnoreCase ("object_id"))
					object_id = new BigDecimal (res.getValue (key));
				
				else if (key.equalsIgnoreCase ("service_id"))
					service_id = new BigDecimal (res.getValue (key));
				
				else if (key.equalsIgnoreCase ("time"))
					time = new String (res.getValue (key));
				
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
		if ((object_id == null) | (service_id == null) | (time == null)) {
			// Fehlermeldung generieren und abbrechen
			this.rms = new RestMessage (RestKeyword.WorkflowDB);
			this.rms.setStatus (RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription ("PUT /WorkflowDB/ needs 3 entries in body: object_id, service_id and time");
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		res = new RestEntrySet ( );
		
		try {
			// 1. neuer Eintrag in WorkflowDB  , automatisch wird in Worklist ein Eintrag über Trigger angelegt
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );

			stmtconn.loadStatement (InsertIntoDB.WorkflowDB (stmtconn.connection, object_id, service_id));
			this.result = stmtconn.execute ( );

			if (this.result.getUpdateCount ( ) < 1) {
				//warn, error, rollback, nothing????
			}
			
			stmtconn.commit ( );

			// 2. eingetragenen Zeitwert auslesen
			stmtconn.loadStatement (SelectFromDB.WorkflowDBInserted(stmtconn.connection, object_id, service_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: workflow_id = " + this.result.getResultSet ( ).getBigDecimal (1));

				res.addEntry ("workflow_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				stmtconn.commit ( );

				
				// 4. Löschen der alten Daten
				stmtconn.loadStatement (DeleteFromDB.WorkflowDB (stmtconn.connection, object_id, time, service_id));
				this.result = stmtconn.execute();
				if (this.result.getUpdateCount ( ) < 1) {
//					stmtconn.rollback ( );
//					throw new SQLException ("WorkflowDB entries ");
				} else {
//					stmtconn.commit ( );
				}
				
//				res.addEntry ("workflow_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				stmtconn.commit ( );
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching WorklflowDB Entry found");
			}
			
				
			
			stmtconn.loadStatement (SelectFromDB.WorkflowDBInserted(stmtconn.connection, object_id, service_id));
			
			this.result = stmtconn.execute ( );
			
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
