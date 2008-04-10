/**
 * 
 */
package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * @author Michael K&uuml;hn
 *
 */

public class Repository extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (Repository.class);
	
	public Repository ( ) {
		
		super (Repository.class.getName ( ), RestKeyword.Repository);
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		this.rms = new RestMessage (RestKeyword.Repository);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("RepositoryID = " + path [0]);
		
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			db.createConnection ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("trying to get resultset");
			
			this.resultset = db.getRepository (new BigDecimal (path [0]));
			
			if (logger.isDebugEnabled ( )) {
				
				logger.debug ("Got resultset");
			}
			
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tRepository name = " + this.resultset.getString ("name") +
							"\n\tRepository URL = " + this.resultset.getString ("url") +
							"\n\tRepository OAI-URL = " + this.resultset.getString ("oai_url") +
							"\n\tTest Data = " + this.resultset.getBoolean ("test_data") + 
							"\n\tAmount = " + this.resultset.getInt ("harvest_amount") +
							"\n\tSleep = " + this.resultset.getInt ("harvest_pause"));				
				
				res.addEntry ("name", this.resultset.getString ("name"));
				res.addEntry ("url", this.resultset.getString ("url"));
				res.addEntry ("oai_url", this.resultset.getString ("oai_url"));
				res.addEntry ("test_data", new Boolean (this.resultset.getBoolean ("test_data")).toString ( ));
				res.addEntry ("harvest_amount", Integer.toString (this.resultset.getInt ("harvest_amount")));
				res.addEntry ("harvest_pause", Integer.toString (this.resultset.getInt ("harvest_pause")));
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				logger.warn ("Nothing found!");
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching Repository found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get Repository: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			this.rms.addEntrySet (res);
			this.resultset = null;
			res = null;
			db.closeConnection ( );
		}
				
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.Repository);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.Repository);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
} // end of class
