package de.dini.oanetzwerk.oaipmh;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 *
 */

class DBDataConnection extends DataConnection {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (DBDataConnection.class);
	
	/**
	 * 
	 */
	
	private DBAccessNG dbng; 
	
	/**
	 * 
	 */
	
	public DBDataConnection ( ) {
		
		this.dbng = new DBAccessNG ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getEarliestDataStamp()
	 */
	
	@Override
	public String getEarliestDataStamp ( ) {
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		Date datestamp = null;
		String eartliestDate = "1646-07-01";
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.OAIGetOldestDate (stmtconn.connection));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			if (queryresult.getResultSet ( ).next ( )) {
				
				datestamp = queryresult.getResultSet ( ).getDate (1);
				eartliestDate = datestamp.toString ( );
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return eartliestDate;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#existsIdentifier(java.lang.String)
	 */
	
	@Override
	public boolean existsIdentifier (String identifier) {
		
		String id [ ] = identifier.split (":");
		
		if (id.length != 3)
			return false;
		
		if (!id [0].equals ("oai") || !id [1].equals ("oanet"))
			return false;
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			return false;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.ObjectEntry (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			if (queryresult.getResultSet ( ).next ( )) {
				
				if (!queryresult.getResultSet ( ).getBigDecimal (1).equals (null) && queryresult.getResultSet ( ).getBigDecimal (1).equals (bdID)) 
					return true;
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return false;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSets()
	 */
	
	@Override
	public ArrayList <String [ ]> getSets ( ) {

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		ArrayList <String [ ]> setList = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.OAIListSets (stmtconn.connection));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			setList = new ArrayList <String[]> ( );
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				setList.add (new String [ ] {queryresult.getResultSet ( ).getString (1), queryresult.getResultSet ( ).getString (2)});
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return setList;
	}
}
