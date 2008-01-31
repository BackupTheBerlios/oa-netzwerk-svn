/**
 * 
 */

package de.dini.oanetzwerk;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * @author Michael Kühn
 *
 */

public class Services extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	private ResultSet resultset;
	
	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		db.closeConnection ( );
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (path [2].equalsIgnoreCase ("byName"))
			this.resultset = db.selectService (new String (path [3]));
		
		else 
			this.resultset = db.selectService (new BigDecimal (path [2]));
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				mapEntry.put ("service_id", Integer.toString (resultset.getInt (1)));
				mapEntry.put ("name", resultset.getString (2));
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get Service: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "Services");
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );

		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		int service_id;
		String name;
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		listentries = RestXmlCodec.decodeEntrySet (data);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("service_id"))
				service_id = new Integer (mapEntry.get (key));
			
			else if (key.equalsIgnoreCase ("name"))
				name = mapEntry.get (key);
			
			else continue;
		}
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		//result = bla
		
		db.closeConnection ( );
		
		listentries = new ArrayList <HashMap <String, String>> ( );
		mapEntry = new HashMap <String ,String> ( );
		
		mapEntry.put ("", "");
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "ObjectEntry");
	}

	/**
	 * @param args
	 */
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
