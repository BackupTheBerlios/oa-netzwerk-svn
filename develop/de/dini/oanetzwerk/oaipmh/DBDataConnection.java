package de.dini.oanetzwerk.oaipmh;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

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

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getCreators(java.lang.String)
	 */
	
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
				
				//TODO: more generic please, add email and institution as well, when not null
				
				if (queryresult.getResultSet ( ).getString (4) == null || queryresult.getResultSet ( ).getString (4).equals (""))
					creators.add (queryresult.getResultSet ( ).getString (2) + " " + queryresult.getResultSet ( ).getString (3));
				
				else
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

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDates(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getDates (String identifier) {

		ArrayList <String> dates = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return dates;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.DateValues (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				dates.add (queryresult.getResultSet ( ).getDate ("value").toString ( ));
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
		
		return dates;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDescriptions(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getDescriptions (String identifier) {

		ArrayList <String> descriptions = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return descriptions;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Description (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				descriptions.add (queryresult.getResultSet ( ).getString ("abstract"));
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
		
		return descriptions;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getFormats(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getFormats (String identifier) {

		ArrayList <String> formats = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return formats;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Format (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				formats.add (queryresult.getResultSet ( ).getString ("schema_f"));
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
		
		return formats;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifiers(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getIdentifiers (String identifier) {

		ArrayList <String> identifieres = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return identifieres;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Identifier (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				identifieres.add (queryresult.getResultSet ( ).getString ("identifier"));
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
		
		return identifieres;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getLanguages(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getLanguages (String identifier) {

		ArrayList <String> languages = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return languages;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Languages (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				languages.add (queryresult.getResultSet ( ).getString ("language"));
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
		
		return languages;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getPublishers(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getPublishers (String identifier) {

		ArrayList <String> publishers = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return publishers;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.Publisher (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				publishers.add (queryresult.getResultSet ( ).getString ("name"));
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
		
		return publishers;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTypes(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getTypes (String identifier) {

		ArrayList <String> types = new ArrayList <String> ( );
		String id [ ] = identifier.split (":");
		
		BigDecimal bdID;
		
		try {
			
			bdID = new BigDecimal (id [2]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return types;
		}
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.TypeValues (stmtconn.connection, bdID));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				types.add (queryresult.getResultSet ( ).getString ("value"));
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
		
		return types;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifier(java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public LinkedList <Record> getIdentifier (String from, String until, String set) {
		
		LinkedList <Record> recordList = new LinkedList <Record> ( );
		Date fromDate;
		Date untilDate;
		
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult  = null;
		
		try {
			
			if (from != null && !from.equals (""))
				fromDate = Date.valueOf (from);
			
			else
				fromDate = null;
			
			if (until != null && !until.equals (""))
				untilDate = Date.valueOf (until);
			
			else
				untilDate = null;
			
			if (fromDate != null && untilDate != null)
				
				if (untilDate.before (fromDate))
					throw new IllegalArgumentException ("Until before From!");
					
		} catch (IllegalArgumentException ex) {
			
			logger.warn (ex.getLocalizedMessage ( ));
			throw ex;
		}
		
		try {
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.AllOIDsByDate (stmtconn.connection, fromDate, untilDate, set));
			
			queryresult = stmtconn.execute ( );
			
			if (queryresult.getWarning ( ) != null) {
				
				for (Throwable warning : queryresult.getWarning ( )) {
					
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (queryresult.getResultSet ( ).next ( )) {
				
				Record record = new Record ( );
				
				record.getHeader ( ).setIdentifier (queryresult.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				record.getHeader ( ).setDatestamp (queryresult.getResultSet ( ).getDate (2).toString ( ));
				
				recordList.add (record);
			}
			
			stmtconn.close ( );
			
			for (Record record : recordList) {
				
				stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection ( );
				stmtconn.loadStatement (SelectFromDB.OAIListSetsbyID (stmtconn.connection, new BigDecimal(record.getHeader ( ).getIdentifier ( ))));
				
				if (queryresult.getWarning ( ) != null) {
					
					for (Throwable warning : queryresult.getWarning ( )) {
						
						logger.warn (warning.getLocalizedMessage ( ));
					}
				}
				
				while (queryresult.getResultSet ( ).next ( )) {
					
					record.getHeader ( ).getSet ( ).add (queryresult.getResultSet ( ).getString (1));
				}
				
				stmtconn.close ( );
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
		
		return recordList;
	}
}
