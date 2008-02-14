/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.RestXmlCodec;

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
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		
		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		BigDecimal object_id = new BigDecimal (path [2]);
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (path.length > 3) {
			
			Date repository_timestamp = null;
			
			try {
				
				repository_timestamp = HelperMethods.extract_datestamp (path [3]);
				
			} catch (ParseException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
			}
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("internal OID = " + object_id.toPlainString ( ) + " || Repository-Timestamp = " + repository_timestamp.toString ( ));
			
			this.resultset = db.selectRawRecordData (object_id, repository_timestamp);
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("internal OID = " + object_id.toPlainString ( ));
			
			this.resultset = db.selectRawRecordData (object_id);
		}
		
		db.closeConnection ( );
		object_id = null;
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		try {
			
			if (resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tobject_id = " + resultset.getInt (1) +
							"\n\trepository_timestamp = " + resultset.getDate (2).toString ( ) +
							"\n\tdata = " + resultset.getString (3));
				
				mapEntry.put ("object_id", Integer.toString (resultset.getInt ("object_id")));
				mapEntry.put ("repository_timestamp", resultset.getDate ("repository_timestamp").toString ( ));
				//TODO: MetaDataFormat
				mapEntry.put ("data", resultset.getString ("data"));
				
			} else {
				
				logger.warn ("no results at all. Continueing...");
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "RawRecordData");
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
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal object_id = new BigDecimal (path [2]);
		Date repository_timestamp = null;
		String metaDataFormat = path [4];
		
		try {
			
			repository_timestamp = HelperMethods.extract_datestamp (path [3]);
			
		} catch (ParseException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("The following values will be inserted:\n\tinternal OID = " + object_id +
					"\n\tRepository Datestamp = " + repository_timestamp +
					"\n\tData = " + data +
					"\n\tMetaDataFormat = " + metaDataFormat);

		String response = db.insertRawRecordData (object_id, repository_timestamp, data, metaDataFormat);
					
		db.closeConnection ( );
		
		object_id = null;
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		
		mapEntry.put ("oid", response);
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "RawRecordData");
	}
}
