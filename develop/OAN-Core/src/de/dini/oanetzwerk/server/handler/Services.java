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
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Robin Malitz
 *
 */

public class Services extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (Services.class);
	
	/**
	 * 
	 */
	
	public Services ( ) {

		super (Services.class.getName ( ), RestKeyword.Services);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		this.rms = new RestMessage (RestKeyword.Services);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.Services+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) {
		
		BigDecimal service_id = null;
		String name = null;
		boolean bGetAll = false;
		
		if (path.length == 0) {
		
			bGetAll = true;
			
		} else if (path.length > 1 && path [0].equalsIgnoreCase ("byName")) {
			
			name = new String (path [1]);
			service_id = null;
			
		} else {
			
			try {
				
				service_id = new BigDecimal (path [0]);
				
			} catch (NumberFormatException ex) {
				
				logger.error (path [0] + " is NOT a number!");
				
				this.rms = new RestMessage (RestKeyword.Services);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a number!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);
			}
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		SingleStatementConnection stmtconn = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			if (bGetAll) {
				
				stmtconn.loadStatement (stmtconn.connection.prepareStatement ("SELECT * FROM dbo.Services"));
				
			} else {
				
				if (service_id == null) {
			
					stmtconn.loadStatement (SelectFromDBSybase.Services (stmtconn.connection, name));
				
				} else {
				
					stmtconn.loadStatement (SelectFromDBSybase.Services (stmtconn.connection, service_id));
				}
				
			}
			
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			boolean foundOne = false;			
			while (this.result.getResultSet ( ).next ( )) {
				foundOne = true;
				RestEntrySet entrySet = new RestEntrySet();
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tservice_id = " + this.result.getResultSet ( ).getInt (1) +
							"\n\tname = " + this.result.getResultSet ( ).getString (2));
				
				entrySet.addEntry ("service_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				entrySet.addEntry ("name", this.result.getResultSet ( ).getString (2));
				
				this.rms.setStatus (RestStatusEnum.OK);
				this.rms.addEntrySet (entrySet);
			}
			if(!foundOne) {
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching Service found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get Services: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.toString());
		
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get Services: " + ex.getLocalizedMessage ( ), ex);
			
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.result = null;
			dbng = null;		}
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		this.rms = new RestMessage (RestKeyword.Services);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.Services+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		String name;
		
		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet entrySet = this.rms.getListEntrySets().get(0);
		
		this.rms = new RestMessage();
		StringBuffer sbPath = new StringBuffer();
		for(String s : path) sbPath.append(s + "/");
		this.rms.setRestURL(sbPath.toString());
		this.rms.setKeyword(RestKeyword.Services);
		
		String strSID = entrySet.getValue("service_id");
		
		if (strSID != null)
			new Integer (strSID);
		
		else {
			
			// SID is missing!
			this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription("no 'service_id' entry given in request body");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		name = entrySet.getValue("name");
		if(name == null) {
			
			// name is missing! 
			this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
			this.rms.setStatusDescription("no 'name' entry given in request body");
			return RestXmlCodec.encodeRestMessage(this.rms);
		} 
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
}
