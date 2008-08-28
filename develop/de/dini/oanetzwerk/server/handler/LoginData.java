/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.*;
import de.dini.oanetzwerk.codec.*;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Manuel Klatt-Kafemann
 *
 */

public class LoginData extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (LoginData.class);
	
	
	public LoginData ( ) {
		
		super (LoginData.class.getName ( ), RestKeyword.LoginData);
	}	

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		// no parameters -- return ERROR
		if (path.length < 1)
			throw new NotEnoughParametersException(
					"This method needs at least 2 parameters: the keyword and the string containing the name");

		// specific service_id -- return notifier status of that service
		String name = null;
		name = new String(path[0]);
		return RestXmlCodec.encodeRestMessage(getRestMessage(name));
			
	}

	
	protected RestMessage getRestMessage(String name) {
		
		this.rms = new RestMessage (RestKeyword.LoginData);
		RestEntrySet entrySet = new RestEntrySet ( );

		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		
		try {
			
			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );						
			stmtconn.loadStatement (SelectFromDB.LoginData(stmtconn.connection, name));	
			this.result = stmtconn.execute ( );
			
			// log warnings
			if (this.result.getWarning ( ) != null) {
				for (Throwable warning : result.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			// extract data from db response
			while(this.result.getResultSet ( ).next ( )) {
				entrySet = new RestEntrySet();
				entrySet.addEntry("name", this.result.getResultSet().getString("name"));
				entrySet.addEntry("password", this.result.getResultSet().getString("password"));
				entrySet.addEntry("email", this.result.getResultSet().getString("email"));
				entrySet.addEntry("superuser", Boolean.toString(this.result.getResultSet().getBoolean("superuser")));

				this.rms.addEntrySet (entrySet);				
			} 
			this.rms.setStatus (RestStatusEnum.OK);
			
			// error if no service_ids at all
			if(entrySet.getEntryHashMap().isEmpty()) {				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching name found");
				
				entrySet.addEntry ("name", null);
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching service_id found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get LoginData: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get LoginData: " + ex.getLocalizedMessage ( ));
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
			
			
			entrySet = null;
			this.result = null;
			dbng = null;
		}		
		return this.rms;
		
	}
	
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.LoginData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}


	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		String name = null;
		String email = null;
		String password = null;
		@SuppressWarnings("unused")
		boolean superuser = false;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		try {
			
			while (it.hasNext ( )) {
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key = " + key);
				
				if (key.equalsIgnoreCase ("name"))
					name = new String (res.getValue (key));
				else if (key.equalsIgnoreCase ("email"))
					email = new String (res.getValue (key));
				else if (key.equalsIgnoreCase ("password"))
					password = new String (res.getValue (key));
				else if (key.equalsIgnoreCase ("superuser")) {
					String temp = new String (res.getValue (key));
					if (temp.equalsIgnoreCase("false") || temp.equalsIgnoreCase("0"))
							superuser = false;
					if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("1"))
						superuser = true;
				}
				else {
					// andere Keys werden nicht erwartet
					// soll hier ein Eintrag im Logfile auftauchen
					
					continue;
				}
			}
			
		} catch (NumberFormatException ex) {
			
			logger.error (res.getValue (key) + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.LoginData);
			this.rms.setStatusDescription (res.getValue (key) + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		
		this.rms = new RestMessage (RestKeyword.LoginData);
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );			
			
			// 1. Prüfen, ob gewählter Nutzername in anderer Schreibweise schon im System vorhanden ist
			stmtconn.loadStatement (SelectFromDB.LoginDataLowerCase(stmtconn.connection, name));
			this.result = stmtconn.execute ( );
			
			if (this.result.getResultSet().next()) {
				// Name schon vorhanden, muss mit Fehler reagiert werden
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription ("UserName not allowed or already exists.");

			} else {
			
				// 2. Speichern der neuen Nutzerdaten
				stmtconn.loadStatement(InsertIntoDB.LoginData(
						stmtconn.connection, name, password, email));
				this.result = stmtconn.execute();

				if (this.result.getUpdateCount() < 1) {

					// warn, error, rollback, nothing????
				}
				stmtconn.commit();

				// 3. Prüfen, ob korrekt gespeichert wurde
				stmtconn.loadStatement(SelectFromDB.LoginData(
						stmtconn.connection, name));

				this.result = stmtconn.execute();

				if (this.result.getResultSet().next()) {

					res.addEntry("name", this.result.getResultSet().getString(
							"password"));
					res.addEntry("password", this.result.getResultSet()
							.getString("password"));
					res.addEntry("email", this.result.getResultSet().getString(
							"email"));
					res
							.addEntry("superuser", new Boolean(this.result
									.getResultSet().getBoolean("superuser"))
									.toString());
					stmtconn.commit();

					this.rms.setStatus(RestStatusEnum.OK);

				} else {

					this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
					this.rms
							.setStatusDescription("No matching LoginData Entry found");
				}
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
		
		
		
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {

		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the name");
		
		String name = new String (path[0]);
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ServiceNotifier);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.LoginData(stmtconn.connection, name));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				stmtconn.rollback ( );
				throw new SQLException ("LoginData entries could not be deleted");
			} else {
				stmtconn.commit ( );
			}
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("name", name);
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete LoginData: " + ex.getLocalizedMessage ( ), ex);
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete LoginData: " + ex.getLocalizedMessage ( ), ex);
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
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.result = null;
			dbng = null;
		}
				
		return RestXmlCodec.encodeRestMessage (this.rms);
	}	
	
	
}
