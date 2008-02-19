/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class InternalMetadataEntry extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (InternalMetadataEntry.class);
	
	/**
	 * 
	 */
	
	public InternalMetadataEntry ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (InternalMetadataEntry.class.getName ( ) + " is called");
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		//NOT IMPLEMENTED
		logger.warn ("postInternalMetadataEntry is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = new BigDecimal (path [2]);
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("");
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		listentries = RestXmlCodec.decodeEntrySet (data);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("internalmetadata")) {
				
			} else continue;
		}
		//String response = db.insertInternalMetadataEntry ( );
		
		db.closeConnection ( );
		
		//object_id = null;
		
/*		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );*/
		
		//TODO: use real values from the database!
		mapEntry.put ("oid", object_id.toPlainString ( ));
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "InternalMetadataEntry");
	}

	/**
	 * @param args
	 */
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
