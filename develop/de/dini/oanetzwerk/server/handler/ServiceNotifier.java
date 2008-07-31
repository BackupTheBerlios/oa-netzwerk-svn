/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.*;
import de.dini.oanetzwerk.codec.*;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class ServiceNotifier extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ServiceNotifier.class);
	
	
	public ServiceNotifier ( ) {
		
		super (ServiceNotifier.class.getName ( ), RestKeyword.ServiceNotifier);
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
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");

		// specific service_id -- return notifier status of that service
		try {
			BigDecimal service_id = null;		
			service_id = new BigDecimal (path [0]);
			
			if (service_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.ServiceNotifier);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
				
			}
			return RestXmlCodec.encodeRestMessage (getRestMessage(service_id));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ServiceNotifier);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
	}

	
	protected RestMessage getRestMessage(BigDecimal object_id) {
		
		this.rms = new RestMessage (RestKeyword.ServiceNotifier);
		RestEntrySet entrySet = new RestEntrySet ( );

		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		
		try {
			
			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );						
			stmtconn.loadStatement (SelectFromDB.ObjectServiceStatusAll(stmtconn.connection));	
			this.result = stmtconn.execute ( );
			
			// log warnings
			if (this.result.getWarning ( ) != null) {
				for (Throwable warning : result.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			// extract oids from db response
			while(this.result.getResultSet ( ).next ( )) {
				entrySet = new RestEntrySet();
				entrySet.addEntry ("service_id", Integer.toString (this.result.getResultSet ( ).getInt ("object_id")));	
				entrySet.addEntry ("inserttime", (this.result.getResultSet ( ).getTimestamp("inserttime")).toString());
				entrySet.addEntry ("finishtime", (this.result.getResultSet ( ).getTimestamp("finishtime")).toString());
				entrySet.addEntry ("urgent", Boolean.toString (this.result.getResultSet ( ).getBoolean("urgent")));
				this.rms.addEntrySet (entrySet);								
			} 
			this.rms.setStatus (RestStatusEnum.OK);
			
			// error if no oids at all
			if(entrySet.getEntryHashMap().isEmpty()) {				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal objectID found");
				
				entrySet.addEntry ("oid", null);
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching internal objectID found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectServiceStatus: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectServiceStatus: " + ex.getLocalizedMessage ( ));
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

		this.rms = new RestMessage (RestKeyword.ServiceNotifier);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ServiceNotifier);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ServiceNotifier);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}	
	
	
}
