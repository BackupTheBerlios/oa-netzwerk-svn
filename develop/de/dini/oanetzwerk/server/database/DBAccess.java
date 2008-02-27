/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.sybase.jdbc3.jdbc.SybDataSource;

import de.dini.oanetzwerk.utils.HelperMethods;

//import junit.framework.Assert;

/**
 * @author Michael Kühn
 *
 */

public class DBAccess implements DBAccessInterface {
	
	static Logger logger = Logger.getLogger (DBAccess.class);
	
	/**
	 * @param args
	 */
	
	static Connection conn = null;
	private Properties prop;
	private DataSource ds;
	private InitialContext ic2;
	
	private DBAccess ( ) {
		
		System.setProperty (Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty (Context.PROVIDER_URL, "file:///tmp");
		
		try {
			
			this.prop = HelperMethods.loadPropertiesFromFile ("dbprop.xml");
//			this.prop = HelperMethods.loadPropertiesFromFile ("/home/mkuehn/apache-tomcat-5.5.25/webapps/restserver/WEB-INF/dbprop.xml");
			//this.prop = HelperMethods.loadPropertiesFromFile ("/usr/local/tomcat/webapps/restserver/WEB-INF/dbprop.xml");
			ic2 = new InitialContext ( );
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (NamingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	public static DBAccessInterface createDBAccess ( ) {
		
		DBAccess db = new DBAccess ( );
		db.setDataSource ( );
		
		return db;
	}
		
	protected void setDataSource ( ) {
		
		DataSource source;
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("trying to set DataSource");
		
		if (this.prop.getProperty ("DataBase").equals ("Sybase")) {
			
			source = new SybDataSource ( );
			((SybDataSource) source).setDataSourceName (this.prop.getProperty ("dataSourceName"));
			((SybDataSource) source).setServerName (this.prop.getProperty ("ServerName")); //themis.rz.hu-berlin.de
			((SybDataSource) source).setPortNumber (new Integer (this.prop.getProperty ("Port"))); //2025
			((SybDataSource) source).setDatabaseName (this.prop.getProperty ("dataSourceName")); //oanetztest
			((SybDataSource) source).setUser (this.prop.getProperty ("user"));
			((SybDataSource) source).setPassword (this.prop.getProperty ("password"));
			
			try {
				
				new InitialContext ( ).rebind (this.prop.getProperty ("dataSourceName"), source);
				
			} catch (NamingException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
			}
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("DataSource successfully set");
			
		} else {
			
			logger.error ("DataBase not supported");
			System.out.println ("DataBase not supported");
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#createConnection()
	 */
	
	public void createConnection ( ) {
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("trying to create a connection");
			
			this.ds = (DataSource) this.ic2.lookup (this.prop.getProperty ("dataSourceName"));
//			Assert.assertNotNull (ds);
			conn = this.ds.getConnection ( );
			
		} catch (NamingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Connection sucessfully created");
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#closeConnection()
	 */
	
	public void closeConnection ( ) {
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Trying to close Connection");
			
			conn.close ( );
			
		} catch (SQLException ex) {
			
			logger.error ("Could close connection: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Connection successfully closed");
	}
	
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
	
	public Connection getConnetion ( ) {
		
		return conn;
	}
	
	public static void main (String [ ] args) {
		
		//TODO: add testing
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectObjectEntryId(java.lang.String, java.lang.String)
	 */
	
	public ResultSet selectObjectEntryId (String repositoryID, String externalOID) {
		
		PreparedStatement pstmt = null;
				
		try {
			
			pstmt = conn.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.repository_identifier = ? and o.repository_id = ?");
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("repositoryID = " + repositoryID + " externalOID = " + externalOID);
			
			pstmt.setString (1, externalOID);
			pstmt.setInt (2, new Integer (repositoryID));
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectRawRecordData(java.lang.String, java.lang.String)
	 */
	
	public ResultSet selectRawRecordData (BigDecimal internalOID, Date datestamp) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("entering selectRawRecordData");
		
		PreparedStatement pstmt = null;
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("internalOID: " + internalOID.toPlainString ( ));
			logger.debug ("datestamp :" + datestamp);
		}
		
		try {
			
			if (datestamp == null) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("1 parameter for select RawRecordData");
				
				pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND repository_timestamp = (SELECT max(repository_timestamp) FROM dbo.RawData WHERE object_id = ?)");
				pstmt.setBigDecimal (1, internalOID);
				pstmt.setBigDecimal (2, internalOID);
				
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("2 parameter for select RawRecordData");
				
				pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND repository_timestamp = ?");
				pstmt.setBigDecimal (1, internalOID);
				pstmt.setDate (2, datestamp);
			}
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectRawRecordData(java.lang.String)
	 */
	public ResultSet selectRawRecordData (BigDecimal internalOID) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("entering selectRawRecordData with only one parameter");
		
		return selectRawRecordData (internalOID, null);
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertRawRecordData(java.lang.String, java.lang.String, java.lang.String)
	 */
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
		}
		
		return internalOID.toPlainString ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#insertObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	
	public ResultSet insertObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier) {
				
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata) VALUES (?, ?, ?, ?, ?)");
			
			pstmt.setInt (1, repository_id);
			pstmt.setDate (2, harvested);
			pstmt.setDate (3, repository_datestamp);
			pstmt.setString (4, repository_identifier);
			pstmt.setBoolean (5, true);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
			pstmt = conn.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
			
			pstmt.setInt (1, repository_id);
			pstmt.setString (2, repository_identifier);
			pstmt.setDate (3, repository_datestamp);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getObject(java.lang.String)
	 */
	
	public ResultSet getObject (int oid) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
			
			pstmt.setInt (1, oid);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#updateObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	
	public ResultSet updateObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier) {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("UPDATE dbo.Object SET harvested = ?, repository_datestamp = ?, testdata = ? WHERE object_id = ?");
			
			pstmt.setDate (1, harvested);
			pstmt.setDate (2, repository_datestamp);
			pstmt.setBoolean (3, true);
			pstmt.setInt (4, repository_id);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
			pstmt.executeUpdate ( );
			
			pstmt = conn.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
			
			pstmt.setInt (1, repository_id);
			pstmt.setString (2, repository_identifier);
			pstmt.setDate (3, repository_datestamp);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getService()
	 */
	public ResultSet selectService (BigDecimal service_id) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT * FROM dbo.Services WHERE service_id = ?");
			pstmt.setBigDecimal (1, service_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#getService()
	 */
	public ResultSet selectService (String name) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT * FROM dbo.Services WHERE name = ?");
			pstmt.setString (1, name);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectServicesOrder(java.math.BigDecimal)
	 */
	public ResultSet selectServicesOrder (BigDecimal predecessor_id) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT predecessor_id FROM dbo.ServicesOrder WHERE service_id = ?");
			pstmt.setBigDecimal (1, predecessor_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectWorkflow (BigDecimal predecessor_id,
			BigDecimal service_id) {

		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? " + 
											"WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) " +
											"OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, service_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
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
			
			pstmt = conn.prepareStatement ("SELECT workflow_id FROM dbo.WorkflowDB WHERE object_id = ? AND repository_identifier = ? AND time = ? AND service_id = ?");
			
			pstmt.setBigDecimal (1, object_id);
			pstmt.setDate (2, time);
			pstmt.setBigDecimal (3, service_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
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
		}
	}

	
	
	
	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectTitle (BigDecimal object_id) {

		PreparedStatement pstmt = null;
		
		try {
			// number ?????
			pstmt = conn.prepareStatement ("SELECT title, qualifier, lang FROM dbo.Titles WHERE object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, object_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
	}

	
	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectAuthors (BigDecimal object_id) {

		PreparedStatement pstmt = null;
		
		try {
			// number ?????
			pstmt = conn.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Author O ON P.person_id = O.person_id WHERE O.object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, object_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectDescription (BigDecimal object_id) {

		PreparedStatement pstmt = null;
		
		try {
			// number ?????
			pstmt = conn.prepareStatement ("SELECT abstract, lang, number FROM dbo.Description WHERE object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, object_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
	}	

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectIdentifier (BigDecimal object_id) {

		PreparedStatement pstmt = null;
		
		try {
			// number ?????
			pstmt = conn.prepareStatement ("SELECT identifier, number FROM dbo.Identifier WHERE object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, object_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBAccessInterface#selectWorkflow(java.math.BigDecimal, java.math.BigDecimal)
	 */
	public ResultSet selectFormat (BigDecimal object_id) {

		PreparedStatement pstmt = null;
		
		try {
			// number ?????
			pstmt = conn.prepareStatement ("SELECT schema_f, number FROM dbo.Format WHERE object_id = ?");
			
			//pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (1, object_id);
			
			return pstmt.executeQuery ( );
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
			
		return null;
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
		}
	}

	
	
} //end of class