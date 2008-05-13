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
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class ObjectEntryID extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ObjectEntryID.class);
	
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
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.ObjectEntryID (stmtconn.connection, repositoryID, new String (path [1])));
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: internal objectID = " + this.result.getResultSet ( ).getInt ("object_id"));
				
				res.addEntry ("oid", Integer.toString (this.result.getResultSet ( ).getInt ("object_id")));
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal Object-ID found");
				
				res.addEntry ("oid", null);
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching internal Object-ID found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
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
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectEntryID);
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

		this.rms = new RestMessage (RestKeyword.ObjectEntryID);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
