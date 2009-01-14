package de.dini.oanetzwerk.oaipmh;

import java.math.BigDecimal;
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
			
			if (queryresult.getResultSet ( ).next ( ))
				eartliestDate = queryresult.getResultSet ( ).getDate (1).toString ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
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
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
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
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return setList;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getClassifications()
	 */
	
	@Override
	public ArrayList <String> getClassifications (String identifier) {
		
		ArrayList <String> classifications = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return classifications;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.DDCClassification (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				classifications.add ("ddc:" + queryresult.getResultSet ( ).getString (2));
			}
			
			stmtconn.close ( );
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.DINISetClassification (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				classifications.add ("dini:" + queryresult.getResultSet ( ).getString (2));
				logger.debug (queryresult.getResultSet ( ).getString (1) + " / " + queryresult.getResultSet ( ).getString (2));
			}
			
			stmtconn.close ( );
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.DNBClassification (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				classifications.add ("dnb:" + queryresult.getResultSet ( ).getString (2));
			}
			
			stmtconn.close ( );
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.OtherClassification (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				classifications.add ("other:" + queryresult.getResultSet ( ).getString (2));
				logger.debug (queryresult.getResultSet ( ).getString (1) + " / " + queryresult.getResultSet ( ).getString (2));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return classifications;
	}
	
	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDateStamp()
	 */
	
	@Override
	public String getDateStamp (String identifier) {
		
		String date = "1646-07-01";
		String id [ ] = identifier.split (":");
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return date;
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
				
				date = queryresult.getResultSet ( ).getDate ("repository_datestamp").toString ( );
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return date;
	}

	@Override
	public ArrayList <String> getCreators (String identifier) {

		ArrayList <String> creators = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return creators;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Authors (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				creators.add (queryresult.getResultSet ( ).getString (4) + " " + queryresult.getResultSet ( ).getString (2) + " " + queryresult.getResultSet ( ).getString (3));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return creators;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSubjects(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getSubjects (String identifier) {

		ArrayList <String> subjects = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return subjects;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Keywords (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				subjects.add (queryresult.getResultSet ( ).getString (1));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return subjects;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTitles(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getTitles (String identifier) {

		ArrayList <String> titles = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return titles;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Title (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				titles.add (queryresult.getResultSet ( ).getString (1));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			try {
				
				stmtconn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
		
		return titles;
	}

	@Override
	public ArrayList <String> getDates (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getDescriptions (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getFormats (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getIdentifiers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getLanguages (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getPublishers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getTypes (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}
}
