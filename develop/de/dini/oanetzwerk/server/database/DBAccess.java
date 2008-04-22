/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 * @author Manuel Klatt-Kafemann
 * @author Robin Malitz
 */

@Deprecated
public class DBAccess implements DBAccessInterface {
	
	static Logger logger = Logger.getLogger (DBAccess.class);
	
	static Connection conn = null;
	private DataSource ds;
	private Context initCtx;
	private Context envCtx;
	private DBSelectInterface select;
	private DBInsertInterface insert;

	private String database = "Sybase";
	
	private DBAccess ( ) {
				
		try {
			
			initCtx = new InitialContext ( );
			envCtx = (Context) initCtx.lookup ("java:comp/env");
			ds = (DataSource) envCtx.lookup ("jdbc/oanetztest");
			
		} catch (NamingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	public static DBAccessInterface createDBAccess ( ) {
		
		DBAccess db = new DBAccess ( );		
		return db;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#createConnection()
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public void createConnection ( ) {
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("trying to create a connection");
			
			Class <DBSelectInterface> cs = (Class <DBSelectInterface>) Class.forName ("de.dini.oanetzwerk.server.database." + this.database + "DBSelect");
			Class <DBInsertInterface> ci = (Class <DBInsertInterface>) Class.forName ("de.dini.oanetzwerk.server.database." + this.database + "DBInsert");
			this.select = cs.newInstance ( );
			this.insert = ci.newInstance ( );
			this.select.prepareConnection (this.ds.getConnection ( ));
			this.insert.prepareConnection (this.ds.getConnection ( ));
			
			if (logger.isDebugEnabled ( )) {
				
				logger.debug (Class.forName ("de.dini.oanetzwerk.server.database.SybaseDBSelect") + " is created");
				logger.debug (this.select.toString ( ));
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (ClassNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (InstantiationException ex) {
			
			ex.printStackTrace ( );
			logger.error (ex.getLocalizedMessage ( ));
			
		} catch (IllegalAccessException ex) {
			
			ex.printStackTrace();
			logger.error (ex.getLocalizedMessage ( ));
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Connection sucessfully created");
	}
	
	@Deprecated
	public void closeStatement ( ) throws SQLException {
		
		select.closeStatement ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#closeConnection()
	 */
	
	@Deprecated
	public void closeConnection ( ) {
		
		try {
			
			if (!conn.isClosed ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("Trying to close Connection");
				
				if (!conn.getAutoCommit ( )) {
					
					logger.warn ("AutoCommit not enabled, set to true and roled back any pending transaction");
					rollback ( );
					setAutoCom (true);
				}
				
				conn.close ( );
				conn = null;
				
			} else {
				
				logger.warn ("Connection already closed!");
				conn = null;
			}
			
		} catch (SQLException ex) {
			
			logger.error ("Could not close connection: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Connection successfully closed");
	}
	@Deprecated
	public void setAutoCom (boolean ac) {
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("autoCommit is set to " + ac);
			
			conn.setAutoCommit (ac);
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	@Deprecated
	public boolean commit ( ) {
		
		boolean result = true;
		
		try {
			
			conn.commit ( );
			
		} catch (SQLException ex) {
			
			result = false;
			ex.printStackTrace ( );
		}
		
		return result;
	}
	@Deprecated
	public boolean rollback ( ) {
		
		boolean result = true;
		
		try {
			
			conn.rollback ( );
			
		} catch (SQLException ex) {
			
			result = false;
			ex.printStackTrace ( );
		}
		
		return result;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getConnetion()
	 */
	@Deprecated
	public Connection getConnetion ( ) {
		
		if (conn == null)
			createConnection ( );
		
		return conn;
	}
	
	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectObjectEntryId(java.lang.String, java.lang.String)
	 */
	@Deprecated
	public ResultSet selectObjectEntryId (BigDecimal repositoryID, String externalOID) throws SQLException {
		
		return select.ObjectEntryId (repositoryID, externalOID);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectRawRecordData(java.lang.String, java.lang.String)
	 */
	@Deprecated
	public ResultSet selectRawRecordData (BigDecimal internalOID, Date datestamp) throws SQLException {
		
		return select.RawRecordData (internalOID, datestamp);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectRawRecordData(java.lang.String)
	 */
	@Deprecated
	public ResultSet selectRawRecordData (BigDecimal internalOID) throws SQLException {
		
		return select.RawRecordData (internalOID, null);
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertRawRecordData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Deprecated
	public String insertRawRecordData (BigDecimal internalOID, Date datestamp,
			String blobbb, String metaDataFormat) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, repository_timestamp, data, MetaDataFormat) VALUES (?, ?, ?, ?)");
			
			pstmt.setBigDecimal (1, internalOID);
			pstmt.setDate (2, datestamp);
			pstmt.setString (3, blobbb);
			pstmt.setString (4, metaDataFormat);
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error ("insertRawRecordData: SQLException using Object " + internalOID);
			logger.error (sqlex.getLocalizedMessage ( ));
			
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
		
		return internalOID.toPlainString ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	@Deprecated
	public ResultSet insertObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter) {
				
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter) VALUES (?, ?, ?, ?, ?, ?)");
			
			pstmt.setBigDecimal (1, repository_id);
			pstmt.setDate (2, harvested);
			pstmt.setDate (3, repository_datestamp);
			pstmt.setString (4, repository_identifier);
			pstmt.setBoolean (5, testdata);
			pstmt.setInt (6, failureCounter);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
			pstmt = conn.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
			
			pstmt.setBigDecimal (1, repository_id);
			pstmt.setString (2, repository_identifier);
			pstmt.setDate (3, repository_datestamp);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
		//done
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getObject(java.lang.String)
	 */
	@Deprecated	
	public ResultSet getObject (BigDecimal oid) throws SQLException {
		
		return select.Object (oid);
		//done
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#updateObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	
	public ResultSet updateObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter) {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("UPDATE dbo.Object SET harvested = ?, repository_datestamp = ?, testdata = ?, failure_counter = ? WHERE object_id = ?");
			
			pstmt.setDate (1, harvested);
			pstmt.setDate (2, repository_datestamp);
			pstmt.setBoolean (3, testdata);
			pstmt.setInt (4, failureCounter);
			pstmt.setBigDecimal (5, repository_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
			pstmt = conn.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
			
			pstmt.setBigDecimal (1, repository_id);
			pstmt.setString (2, repository_identifier);
			pstmt.setDate (3, repository_datestamp);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
		
		return null;
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getService()
	 */
	public ResultSet selectService (BigDecimal service_id) throws SQLException {
		
		return select.Service (service_id);
	}
	
	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getService()
	 */
	public ResultSet selectService (String name) throws SQLException {
		
		return select.Service (name);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectServicesOrder(java.math.BigDecimal)
	 */
	public ResultSet selectServicesOrder (BigDecimal predecessor_id) throws SQLException {
		
		return select.ServicesOrder (predecessor_id);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectWorkflow (BigDecimal predecessor_id,	BigDecimal service_id) throws SQLException {
		
		return select.Workflow (predecessor_id, service_id);
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertWorkflowDBEntry(java.math.BigDecimal, java.sql.Date, java.math.BigDecimal)
	 */
	public ResultSet insertWorkflowDBEntry (BigDecimal object_id, Date time,
			BigDecimal service_id) {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.WorkflowDB (object_id, time, service_id) VALUES (?, ?, ?)");
			
			pstmt.setBigDecimal (1, object_id);
			pstmt.setDate (2, time);
			pstmt.setBigDecimal (3, service_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
			pstmt = conn.prepareStatement ("SELECT workflow_id FROM dbo.WorkflowDB WHERE object_id = ? AND time = ? AND service_id = ?");
			
			pstmt.setBigDecimal (1, object_id);
			pstmt.setDate (2, time);
			pstmt.setBigDecimal (3, service_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertTitle(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	
	public ResultSet insertTitle (BigDecimal object_id, String qualifier,
			String title, String lang) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Titles (object_id, qualifier, title, lang) VALUES (?,?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setString (2, qualifier);
			pstmt.setString (3, title);
			pstmt.setString (4, lang);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
/*			pstmt = conn.prepareStatement ("SELECT object_id From dbo.Titles WHERE qualifier = ? AND object_id = ?");
			pstmt.setString (1, qualifier);
			pstmt.setBigDecimal (2, object_id);
			
			return pstmt.executeQuery ( );*/
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertDateValue(java.math.BigDecimal, int, java.sql.Date)
	 */
	
	public void insertDateValue (BigDecimal object_id, int number,
			Date value) {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.DateValues (object_id, number, value) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setInt (2, number);
			pstmt.setDate (3, value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertFormat(java.math.BigDecimal, int, java.lang.String)
	 */
	
	public void insertFormat (BigDecimal object_id, int number, String schema_f) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Format (object_id, number, schema_f) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setInt (2, number);
			pstmt.setString (3, schema_f);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertIdentifier(java.math.BigDecimal, int, java.lang.String)
	 */
	
	public void insertIdentifier (BigDecimal object_id, int number,
			String identifier) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Identifier (object_id, number, identifier) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setInt (2, number);
			pstmt.setString (3, identifier);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	
	public ResultSet selectTitle (BigDecimal object_id) throws SQLException {
		
		return select.Title (object_id);
	}

	
	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectAuthors (BigDecimal object_id) throws SQLException {

		return select.Authors (object_id);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectDescription (BigDecimal object_id) throws SQLException {
		
		return select.Description (object_id);
	}	

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectIdentifier (BigDecimal object_id) throws SQLException {
		
		return select.Identifier (object_id);
	}

	/**
	 * @throws SQLException 
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectFormat (BigDecimal object_id) throws SQLException {
		
		return select.Format (object_id);
	}

	public void insertDescription (BigDecimal object_id, int number,
			String description) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Description (object_id, number, abstract) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setInt (2, number);
			pstmt.setString (3, description);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	
	public void insertPublisher (BigDecimal object_id, int number,
			String name) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Publisher (object_id, number, name) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setInt (2, number);
			pstmt.setString (3, name);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	
	public void insertTypeValue (BigDecimal object_id, 
			String value) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.TypeValue (object_id, value) VALUES (?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setString (2, value);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public ResultSet selectContributors(BigDecimal oid) throws SQLException {
		
		return select.Contributors (oid);
	}
	
	public ResultSet selectDateValues(BigDecimal oid) throws SQLException {
		
		return select.DateValues (oid);
	}
	
	public ResultSet selectEditors(BigDecimal oid) throws SQLException {
		
		return select.Editors (oid);
	}
	
	public ResultSet selectPublisher(BigDecimal oid) throws SQLException {
		
		return select.Publisher (oid);
	}

	public ResultSet selectTypeValue(BigDecimal oid) throws SQLException {
		
		return select.TypeValue (oid);
	}
	
	public ResultSet selectDDCClassification(BigDecimal oid) throws SQLException {
		
		return select.DDCClassification (oid);
	}

	public ResultSet selectDNBClassification(BigDecimal oid) throws SQLException {
		
		return select.DNBClassification (oid);
	}
	
	public ResultSet selectDINISetClassification(BigDecimal oid) throws SQLException {
		
		return select.DINISetClassification (oid);
	}
	
	public ResultSet selectOtherClassification(BigDecimal oid) throws SQLException {
		
		return select.OtherClassification (oid);
	}
	
	public ResultSet selectKeywords(BigDecimal oid) throws SQLException {
		
		return select.Keywords (oid);
	}
	
	public ResultSet selectLanguages(BigDecimal oid) throws SQLException {
		
		return select.Languages (oid);
	}

	public void deleteObject2Editor (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Object2Editor WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deletePersonWithoutReference() throws SQLException {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn
					.prepareStatement("DELETE FROM dbo.Person WHERE "
						+ "(person_id NOT IN (SELECT person_id FROM dbo.Object2Author GROUP BY person_id) "
						+ "AND person_id NOT IN (SELECT person_id FROM dbo.Object2Editor GROUP BY person_id) "
						+ "AND person_id NOT IN (SELECT person_id FROM dbo.Object2Contributor GROUP BY person_id))");
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}	
	}
	
	public void deleteObject2Contributor (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Object2Contributor WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteObject2Author (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Object2Author WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	

	public void deleteObject2Language (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Object2Language WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteDDC_Classification (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.DDC_Classification WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteDNB_Classification (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.DNB_Classification WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteDINI_Set_Classification (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.DINI_Set_Classification WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteTypeValue (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.TypeValue WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void deleteDescription (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Description WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteIdentifiers (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Identifier WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteDateValues (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.DateValues WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void deleteFormats (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Format WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deleteTitles (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Titles WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void deletePublishers (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Publisher WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void insertPerson(String firstname,
			String lastname, String title, String institution, String email)
			throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Person (firstname, lastname, title, institution, email) VALUES (?,?,?,?,?)");
			pstmt.setString (1, firstname);
			pstmt.setString (2, lastname);
			pstmt.setString (3, title);
			pstmt.setString (4, institution);
			pstmt.setString (5, email);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public ResultSet selectLatestPerson(String firstname, String lastname) throws SQLException {
		
		return select.LatestPerson (firstname, lastname);
	}
	
	public void insertObject2Author(BigDecimal object_id, BigDecimal person_id, int number)
			throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Object2Author (object_id, person_id, number) VALUES (?,?,?)");
			pstmt.setBigDecimal (1, object_id);
			pstmt.setBigDecimal (2, person_id);
			pstmt.setInt (3, number);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}	

	public void insertObject2Editor(BigDecimal object_id, BigDecimal person_id,
			int number) throws SQLException {
		PreparedStatement pstmt = null;

		try {

			pstmt = conn
					.prepareStatement("INSERT INTO dbo.Object2Editor (object_id, person_id, number) VALUES (?,?,?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, person_id);
			pstmt.setInt(3, number);

			if (logger.isDebugEnabled())
				logger.debug("execute");

			pstmt.executeUpdate();

		} catch (SQLException sqlex) {

			logger.error(sqlex.getLocalizedMessage());
			sqlex.printStackTrace();
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void insertObject2Contributor(BigDecimal object_id, BigDecimal person_id,
			int number) throws SQLException {
		PreparedStatement pstmt = null;

		try {

			pstmt = conn
					.prepareStatement("INSERT INTO dbo.Object2Contributor (object_id, person_id, number) VALUES (?,?,?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, person_id);
			pstmt.setInt(3, number);

			if (logger.isDebugEnabled())
				logger.debug("execute");

			pstmt.executeUpdate();

		} catch (SQLException sqlex) {

			logger.error(sqlex.getLocalizedMessage());
			sqlex.printStackTrace();
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}	

	public void insertKeyword(String keyword, String lang)
			throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Keywords (keyword, lang) VALUES (?,?)");
			pstmt.setString (1, keyword);
			pstmt.setString (2, lang);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	
	public ResultSet selectLatestKeyword(String keyword, String lang) throws SQLException {
		
		return select.LatestKeyword (keyword, lang);
	}
	
	public void insertObject2Keyword(BigDecimal object_id, BigDecimal keyword_id) throws SQLException {
		PreparedStatement pstmt = null;

		try {

			pstmt = conn
					.prepareStatement("INSERT INTO dbo.Object2Keywords (object_id, keyword_id) VALUES (?,?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, keyword_id);

			if (logger.isDebugEnabled())
				logger.debug("execute");

			pstmt.executeUpdate();

		} catch (SQLException sqlex) {

			logger.error(sqlex.getLocalizedMessage());
			sqlex.printStackTrace();
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public ResultSet selectLanguageByName(String language) throws SQLException {
		
		return select.LanguageByName (language);
	}

	public void insertLanguage(String language) throws SQLException {
		PreparedStatement pstmt = null;

		try {

			pstmt = conn
					.prepareStatement("INSERT INTO dbo.Language (language) VALUES (?)");
			pstmt.setString(1, language);

			if (logger.isDebugEnabled())
				logger.debug("execute");

			pstmt.executeUpdate();

		} catch (SQLException sqlex) {

			logger.error(sqlex.getLocalizedMessage());
			sqlex.printStackTrace();
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public void insertObject2Language(BigDecimal object_id, BigDecimal language_id,
			int number) throws SQLException {
		PreparedStatement pstmt = null;

		try {

			pstmt = conn
					.prepareStatement("INSERT INTO dbo.Object2Language (object_id, language_id, number) VALUES (?,?,?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, language_id);
			pstmt.setInt(3, number);

			if (logger.isDebugEnabled())
				logger.debug("execute");

			pstmt.executeUpdate();

		} catch (SQLException sqlex) {

			logger.error(sqlex.getLocalizedMessage());
			sqlex.printStackTrace();
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}	

	public void deleteRawData (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.RawData WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void deleteDuplicatePossibilities (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.DuplicatePossibilities WHERE object_id = ?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	
	public void deleteKeywordsWithoutReference () throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Keywords WHERE "
						+ "(keyword_id NOT IN (SELECT keyword_id FROM dbo.Object2Keywords GROUP BY keyword_id) )");
						
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	
	public void deleteOther_Classification (BigDecimal object_id) throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Other_Classification WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	
	public void deleteOther_Categories () throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("DELETE FROM dbo.Other_Categories WHERE " 
						+"(other_id NOT IN (SELECT other_id FROM dbo.Other_Classification GROUP BY other_id) )");

			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void deleteObject2Keywords(BigDecimal object_id) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement ("DELETE FROM  dbo.Object2Keywords WHERE object_id=?");
			pstmt.setBigDecimal (1, object_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
			throw sqlex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}	
	}

	public void insertOtherCategories(String name) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Other_Categories (name) VALUES (?)");
			pstmt.setString(1, name);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public void insertOtherClassification(BigDecimal object_id,
			BigDecimal other_id) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Other_Classification (object_id, other_id) VALUES (?, ?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, other_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	public ResultSet selectLatestOtherCategories(String name) throws SQLException {
		
		return select.LatestOtherCategories (name);
	}
	
	public ResultSet selectDDCCategoriesByCategorie(String categorie) throws SQLException {

		return select.DDCCategoriesByCategorie (categorie);
	}
	
	public ResultSet selectDNBCategoriesByCategorie(String name) throws SQLException {

		return select.DNBCategoriesByCategorie (name);
	}

	public int insertDDCClassification(BigDecimal object_id, String ddcValue)
			throws SQLException {
		
		return insert.DDCClassification (object_id, ddcValue);
	}

	public void insertDINISetClassification(BigDecimal object_id,
			BigDecimal DINI_set_id) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement ("INSERT INTO dbo.DINI_Set_Classification (object_id, DINI_set_id) VALUES (?, ?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setBigDecimal(2, DINI_set_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

//	public void insertDNBClassification(BigDecimal object_id, BigDecimal DNB_Categorie)
	public void insertDNBClassification(BigDecimal object_id, String DNB_Categorie)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement ("INSERT INTO dbo.DNB_Classification (object_id, DNB_Categorie) VALUES (?, ?)");
			pstmt.setBigDecimal(1, object_id);
			pstmt.setString(2, DNB_Categorie);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}

	public ResultSet selectDINISetCategoriesByName(String name)
			throws SQLException {

		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement ("SELECT DINI_set_id FROM dbo.DINI_Set_Categories WHERE (name = ?)");
			pstmt.setString (1, name);
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			throw ex;
			
		} finally {
			
			try {
				
				pstmt.close ( );
				pstmt = null;
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
				logger.warn (ex.getLocalizedMessage ( ));
			}	
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getRepository(java.math.BigInteger)
	 */
	
	public ResultSet getRepository (BigDecimal repositoryID) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("RepositoryID = " + repositoryID.toPlainString ( ));
		
		return select.Repository (repositoryID);
	}
} //end of class