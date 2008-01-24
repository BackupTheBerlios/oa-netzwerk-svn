/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author Michael Kühn
 *
 */

public class RawRecordData extends
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (RawRecordData.class);
	private ResultSet resultset;
	
	/**
	 * @param args
	 */
	
	public RawRecordData ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (RawRecordData.class.getName ( ) + " is called");
	}
	
	public static void main (String [ ] args) {
		
		//TODO: Testing stuff
	}
	
	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		
		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (path.length > 3) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("internal OID = " + path [2] + " || Repository-Timestamp = " + path [3]);
			
			this.resultset = db.selectRawRecordData (path [2], path [3]);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("internal OID = " + path [2]);
			
			this.resultset = db.selectRawRecordData (path [2]);
		}
		
/*		String result = "";
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("length = " + path.length);
		
		if (path.length > 3)
			result = db.selectRawRecordData (path [2], path [3]);
		
		else
			result = db.selectRawRecordData (path [2]);
		*/
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tobject_id = " + resultset.getInt (1) +
							"\n\tcollected = " + resultset.getDate (2).toString ( ) +
							"\n\tdata = " + resultset.getString (3));
				
				mapEntry.put ("object_id", Integer.toString (resultset.getInt (1)));
				mapEntry.put ("collected", resultset.getDate (2).toString ( ));
				mapEntry.put ("data", resultset.getString (3));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
/*		return "<OAN-REST xmlns=\"http://localhost/\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"http://localhost/schema.xsd\">\n" +
				"\t<RawRecordData>\n\t\t" + result + "\n\t</RawRecordData>\n" +
				"</OAN-REST>";*/
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "RawRecordData");
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("The following values will be inserted:\n\tinternal OID = " + path [2] +
					"\n\tRepository Datestamp = " + path [3] +
					"\n\tData = " + data);

		String response = db.insertRawRecordData (new Integer (path [2]), path [3], data);
		
		db.closeConnection ( );
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		mapEntry.put ("oid", response);
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "RawRecordData");
	}
}
