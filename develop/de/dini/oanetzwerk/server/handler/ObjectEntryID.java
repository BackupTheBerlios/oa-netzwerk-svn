/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.*;
import de.dini.oanetzwerk.codec.*;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * @author Michael Kühn
 *
 */

public class ObjectEntryID extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ObjectEntryID.class);
	private ResultSet resultset;
	
	public ObjectEntryID ( ) {
		
		super (ObjectEntryID.class.getName ( ), RestKeyword.ObjectEntryID);
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {
		
		//TODO: Testing!!!		
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {

		//NOT IMPLEMENTED
		logger.warn ("deleteObjectEntryID is not implemented");
		throw new MethodNotImplementedException ( );
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns for a given RepositoryID and a given external ObjectID an
	 * internal ObjectID is it exists. When it does not exist "null" will be returned.
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 4)
			throw new NotEnoughParametersException ("This method needs at least 3 parameters: the keyword the repository ID and the external Object ID");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("RepositoryID = " + path [2] + " || externalOID = " + path [3]);
		
		this.resultset = db.selectObjectEntryId (path [2], path [3]);
		
		db.closeConnection ( );
		
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: internal objectID = " + resultset.getInt ("object_id"));
				
				res.addEntry ("oid", Integer.toString (resultset.getInt ("object_id")));
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal objectID found");
				
				res.addEntry ("oid", null);
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND);
				this.rms.setStatusDescription ("No matching internal objectID found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			this.rms.addEntrySet (res);
			this.resultset = null;
			res = null;
		}
		
		return RestXmlCodec.encodeRestMessage (rms);
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new MethodNotImplementedException ( );
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		//NOT IMPLEMENTED
		logger.warn ("putObjectEntryID is not implemented");
		throw new MethodNotImplementedException ( );
	}
}
