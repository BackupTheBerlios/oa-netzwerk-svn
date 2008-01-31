/**
 * 
 */

package de.dini.oanetzwerk;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.dini.oanetzwerk.utils.HelperMethods;


/**
 * @author Michael Kühn
 *
 */

public class WorkflowDB extends AbstractKeyWordHandler implements
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
		
		this.resultset = db.selectWorkflow (new BigDecimal (path [2]), new BigDecimal (path [3]));
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				mapEntry.put ("workflow_id", Integer.toString (resultset.getInt (1)));
				mapEntry.put ("object_id", Integer.toString (resultset.getInt (2)));
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
		
		db.closeConnection ( );
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = null;
		BigDecimal service_id = null;
		Date time = null;
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		listentries = RestXmlCodec.decodeEntrySet (data);
		mapEntry = listentries.get (0);
		Iterator <String> it = mapEntry.keySet ( ).iterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("service_id"))
				object_id = new BigDecimal (mapEntry.get (key));
			
			else if (key.equalsIgnoreCase ("name")) {
				
				try {
					
					time = HelperMethods.extract_repository_datestamp (mapEntry.get (key));
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
			} else if (key.equalsIgnoreCase ("service_id"))
				service_id = new BigDecimal (mapEntry.get (key));
			
			else continue;
		}

		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		this.resultset = db.insertWorkflowDBEntry (object_id, time, service_id);
		
		db.closeConnection ( );
		
		listentries = new ArrayList <HashMap <String, String>> ( );
		mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
		
				mapEntry.put ("workflow_id", resultset.getBigDecimal (1).toPlainString ( ));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
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
