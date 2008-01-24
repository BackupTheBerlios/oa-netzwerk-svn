/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael Kühn
 *
 */

public class ObjectEntry extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ObjectEntry.class);
	private ResultSet resultset;
	
	public ObjectEntry ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (ObjectEntry.class.getName ( ) + " is called");
	}
		
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Testing stuff

	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns for a given internal ObjectID the key values for this Object.
	 * If the object does not exist "null" will be returned. 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("internal OID = " + path [2]);
		
		resultset = db.getObject (Integer.parseInt (path [2]));
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + resultset.getInt (1) +
							"\n\trepository_id = " + resultset.getInt (2) +
							"\n\tharvested = " + resultset.getDate (3) +
							"\n\trepository_datestamp = " + resultset.getDate (4) +
							"\n\trepository_identifier = " + resultset.getString (5));
				
				mapEntry.put ("object_id", Integer.toString (resultset.getInt (1)));
				mapEntry.put ("repository_id", Integer.toString (resultset.getInt (2)));
				mapEntry.put ("harvested", resultset.getDate (3).toString ( ));
				mapEntry.put ("repository_datestamp", resultset.getDate (4).toString ( ));
				mapEntry.put ("repository_identifier", resultset.getString (5));				
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "ObjectEntry");
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method inserts a new Object entry. The values for the new object will be extracted from the
	 * HTTP-Body's data.
	 * The internal ObjectID of the newly created Object will be returned.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		int repository_id = 0;
		String repository_identifier = "";
		Date repository_datestamp = null;
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		listentries = RestXmlCodec.decodeEntrySet (data);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("repository_id")) {
				
				repository_id = new Integer (mapEntry.get (key));
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				repository_identifier = mapEntry.get (key);
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				try {
					
					repository_datestamp = HelperMethods.extract_repository_datestamp (mapEntry.get (key));
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
			} else continue;
		}

		
		//TODO: write xml
		//int repository_id = extract_repository_id (data);
		//String repository_identifier = extract_repository_identifier (data);
		//Date repository_datestamp = extract_repository_datestamp (data);
		Date harvested = HelperMethods.today ( );
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("The following values will be inserted:\n\tRepository ID = " + repository_id +
					"\n\tHarvested = " + harvested +
					"\n\tRepository Datestamp = " + repository_datestamp +
					"\n\texternal OID = " + repository_identifier);
		
		//resultset = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier);
		String response = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier);
		
		db.closeConnection ( );
		
		listentries = new ArrayList <HashMap <String, String>> ( );
		mapEntry = new HashMap <String ,String> ( );
		mapEntry.put ("oid", response);
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "ObjectEntry");
	}
} // end of class
