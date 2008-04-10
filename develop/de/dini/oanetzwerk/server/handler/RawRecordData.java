/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.*;

/**
 * @author Michael K&uuml;hn
 * @author Manuel Klatt-Kafemann
 * @author Robin Malitz
 *
 */

public class RawRecordData extends
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	static Logger logger = Logger.getLogger (RawRecordData.class);
	
	/**
	 * 
	 */
	
	public RawRecordData ( ) {
		
		super (RawRecordData.class.getName ( ), RestKeyword.RawRecordData);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {
		
		//NOT IMPLEMENTED
		logger.warn ("deleteRawRecordData is not implemented");
		throw new MethodNotImplementedException ("This method is not implemented due to specification!");
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the object ID");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		RestEntrySet res = null;
		
		try {
			
			db.createConnection ( );
			
			
			if (path.length > 1) {
				
				Date repository_timestamp = null;
				
				try {
					
					repository_timestamp = HelperMethods.extract_datestamp (path [3]);
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("internal OID = " + path [0] + " || Repository-Timestamp = " + repository_timestamp.toString ( ));
				
				this.resultset = db.selectRawRecordData (new BigDecimal (path [0]), repository_timestamp);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("internal OID = " + path [0]);
				
				this.resultset = db.selectRawRecordData (new BigDecimal (path [0]));
			}
			
			res = new RestEntrySet ( );
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tobject_id = " + this.resultset.getInt (1) +
							"\n\trepository_timestamp = " + this.resultset.getDate (2).toString ( ) +
							"\n\tdata = " + this.resultset.getString (3));
				
				res.addEntry ("object_id", Integer.toString (this.resultset.getInt ("object_id")));
				res.addEntry ("repository_timestamp", this.resultset.getDate ("repository_timestamp").toString ( ));
				//TODO: MetaDataFormat
				res.addEntry ("data", this.resultset.getString ("data"));
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				logger.warn ("no results at all. Continueing...");
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching RawRecordData found");
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			db.rollback ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			db.closeConnection ( );
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
		logger.warn ("postRawRecordData is not implemented");
		throw new MethodNotImplementedException ("This method is not implemented due to specification!");
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		if (path.length < 3)
			throw new NotEnoughParametersException ("This method needs 4 parameters: the keyword, the object ID, the repository timestamp and the metadataformat");
		
		BigDecimal object_id = new BigDecimal (path [0]);
		Date repository_timestamp = null;
		String metaDataFormat = path [2];
		
		try {
			
			repository_timestamp = HelperMethods.extract_datestamp (path [1]);
			
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
		
		RestEntrySet res = new RestEntrySet ( );
		
		res.addEntry ("oid", response);
		this.rms.setStatus (RestStatusEnum.OK);
		this.rms.addEntrySet (res);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
