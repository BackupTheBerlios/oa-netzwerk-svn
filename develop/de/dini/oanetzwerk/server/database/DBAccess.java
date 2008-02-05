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
			
			this.prop = HelperMethods.loadPropertiesFromFile ("/home/mkuehn/apache-tomcat-5.5.25/webapps/restserver/WEB-INF/dbprop.xml");
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
				
				pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND repository_timestamp = (SELECT max(repository_timestamp) FROM dbo.RawData)");
				pstmt.setBigDecimal (1, internalOID);
				
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
			String blobbb) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, repository_timestamp, data) VALUES (?, ?, ?)");
			
			pstmt.setBigDecimal (1, internalOID);
			pstmt.setDate (2, datestamp);
			pstmt.setString (3, blobbb);
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
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier) VALUES (?, ?, ?, ?)");
			
			pstmt.setInt (1, repository_id);
			pstmt.setDate (2, harvested);
			pstmt.setDate (3, repository_datestamp);
			pstmt.setString (4, repository_identifier);
			
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
			
			pstmt = conn.prepareStatement ("UPDATE dbo.Object SET harvested=?, repository_datestamp=? WHERE object_id = ?");
			
			pstmt.setInt (1, repository_id);
			pstmt.setDate (2, harvested);
			pstmt.setDate (3, repository_datestamp);
			pstmt.setString (4, repository_identifier);
			
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
			
			pstmt = conn.prepareStatement ("SELECT w1.workflow_id, w1.object_id FROM WorkflowDB w1 WHERE w1.service_id = ? and (SELECT 1 FROM workflow_db w2 WHERE w2.object_id = w1.object_id and w2.service_id = ? and w2.time > w1.time) != 1");
			pstmt.setBigDecimal (1, predecessor_id);
			pstmt.setBigDecimal (2, service_id);
			
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
			
			pstmt = conn.prepareStatement ("INSERT INTO WorkflowDB (object_id, time, service_id) VALUES (?, ?, ?)");
			
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
} //end of class