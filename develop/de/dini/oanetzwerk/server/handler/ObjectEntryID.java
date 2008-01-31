/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.RestXmlCodec;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author Michael Kühn
 *
 */

public class ObjectEntryID extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ObjectEntryID.class);
	private ResultSet resultset;
	
	public ObjectEntryID ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (ObjectEntryID.class.getName ( ) + " is called");
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {
		
		//TODO: Testing!!!
		
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		//NOT IMPLEMENTED
		logger.warn ("deleteObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns for a given RepositoryID and a given external ObjectID an
	 * internal ObjectID is it exists. When it does not exist "null" will be returned.
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("RepositoryID = " + path [2] + " || externalOID = " + path [3]);
		
		resultset = db.selectObjectEntryId (path [2], path [3]);
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: internal objectID = " + resultset.getInt ("object_id"));

				mapEntry.put ("oid", Integer.toString (resultset.getInt ("object_id")));
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal objectID found");
				
				mapEntry.put ("oid", null);
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "ObjectEntryId");
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {

		//NOT IMPLEMENTED
		logger.warn ("putObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}
}
