/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;


/**
 * @author Michael Kühn
 *
 */

public class Services extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	/**
	 * @param objectName
	 * @param rkw
	 */
	public Services ( ) {

		super (Services.class.getName ( ), RestKeyword.Services);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		db.closeConnection ( );
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
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
		
		this.rms = new RestMessage();
		StringBuffer sbPath = new StringBuffer();
		for(String s : path) sbPath.append(s + "/");
		this.rms.setRestURL(sbPath.toString());
		this.rms.setKeyword(RestKeyword.Services);

		RestEntrySet entrySet = new RestEntrySet();
		
		try {
			
			if (resultset.next ( )) {
				entrySet.addEntry("service_id", Integer.toString (resultset.getInt (1)));
				entrySet.addEntry("name", resultset.getString (2));
			}

			this.rms.addEntrySet(entrySet);
			this.rms.setStatus(RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get Service: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );

			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.toString());
		
		}
				
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );

		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		int service_id;
		String name;
		
		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet entrySet = this.rms.getListEntrySets().get(0);
		
		this.rms = new RestMessage();
		StringBuffer sbPath = new StringBuffer();
		for(String s : path) sbPath.append(s + "/");
		this.rms.setRestURL(sbPath.toString());
		this.rms.setKeyword(RestKeyword.Services);
		
		String strSID = entrySet.getValue("service_id");
		if(strSID != null) {
			service_id = new Integer (strSID);
		} else {
			// SID is missing!
			this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription("no 'service_id' entry given in request");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		name = entrySet.getValue("name");
		if(name == null) {
			// name is missing! 
			this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription("no 'name' entry given in request");
			return RestXmlCodec.encodeRestMessage(this.rms);
		} 
		
		entrySet = new RestEntrySet();
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		//result = bla
		
		db.closeConnection ( );
		
		this.rms.setStatus(RestStatusEnum.OK);
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

}
