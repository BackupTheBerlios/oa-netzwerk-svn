/**
 * 
 */
package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

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
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameter: the keyword and the Repository ID!");
		
		BigDecimal repositoryID; 
		
		try {
			
			repositoryID = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectEntryID);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.Repository (stmtconn.connection, repositoryID));
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) {
				
				for (Throwable warning : result.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			if (this.result.getResultSet ( ).next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: \n\tRepository name = " + this.result.getResultSet ( ).getString ("name") +
							"\n\tRepository URL = " + this.result.getResultSet ( ).getString ("url") +
							"\n\tRepository OAI-URL = " + this.result.getResultSet ( ).getString ("oai_url") +
							"\n\tTest Data = " + this.result.getResultSet ( ).getBoolean ("test_data") + 
							"\n\tAmount = " + this.result.getResultSet ( ).getInt ("harvest_amount") +
							"\n\tSleep = " + this.result.getResultSet ( ).getInt ("harvest_pause"));				
				
				res.addEntry ("name", this.result.getResultSet ( ).getString ("name"));
				res.addEntry ("url", this.result.getResultSet ( ).getString ("url"));
				res.addEntry ("oai_url", this.result.getResultSet ( ).getString ("oai_url"));
				res.addEntry ("test_data", new Boolean (this.result.getResultSet ( ).getBoolean ("test_data")).toString ( ));
				res.addEntry ("harvest_amount", Integer.toString (this.result.getResultSet ( ).getInt ("harvest_amount")));
				res.addEntry ("harvest_pause", Integer.toString (this.result.getResultSet ( ).getInt ("harvest_pause")));
				
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
			
		} catch (WrongStatementException ex) {

			logger.error ("An error occured while processing Get Repository: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
				}
			}
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
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
