/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public static enum moduls {
		Harvester, Aggregator
	}
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
			
			this.prop = HelperMethods.loadPropertiesFromFile ("/usr/local/tomcat/webapps/restserver/WEB-INF/dbprop.xml");
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#createConnection()
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#closeConnection()
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#getConnetion()
	 */
	
	public Connection getConnetion ( ) {
		
		return conn;
	}
	
	public static void main (String [ ] args) {
		
		System.out.println ("DB-Test");
		
		if (args [0].equals ("--createDB")) {
			
			createDB ( );
		}
	}

	/**
	 * 
	 */
	
	private static void createDB ( ) {
		
		DBAccessInterface db = createDBAccess ( );
		int [ ] updateCounts = null;
		BufferedReader file = null;
		
		try {
			
			file = new BufferedReader (new FileReader ("/home/mkuehn/workspace/oa-netzwerk-develop/db-schema/oan-db-model.sql"));
			StringBuffer sql = new StringBuffer ("");
			db.createConnection ( );
			Statement stmt = conn.createStatement ( );
			db.setAutoCom (false);
			
			for (String line; (line = file.readLine ( )) != null;) {
				
				if (line.matches (".*;")) {
					
					sql.append (line.replace (';', ' '));
					sql.trimToSize ( );
					System.out.println (sql.toString ( ));
					stmt.addBatch (sql.toString ( ));
					sql = new StringBuffer ("");
					
				} else
					sql.append (line);
			}
			
			updateCounts = stmt.executeBatch ( );
			
			for (int i = 0; i < updateCounts.length; i++)
				System.out.println ("UpdateCount: " + updateCounts);
					
		} catch (BatchUpdateException buex) {
			
			try {
				
				conn.rollback ( );
				buex.printStackTrace ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
			
		} catch (SQLException ex) {
			
			try {
				
				conn.rollback ( );
				
			} catch (SQLException ex1) {
				
				ex1.printStackTrace ( );
			}
			ex.printStackTrace ( );
			
		} catch (InvalidPropertiesFormatException ex) {
			
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			db.setAutoCom (true);
			db.closeConnection ( );
			
			try {
				
				file.close ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
			}
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectObjectEntryId(java.lang.String, java.lang.String)
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectRawRecordData(java.lang.String, java.lang.String)
	 */
	
	public ResultSet selectRawRecordData (String internalOID, String datestamp) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("entering selectRawRecordData");
		
		PreparedStatement pstmt = null;
		//ResultSet rs = null;
		
//		String result = "<NULL />";
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("internalOID: " + internalOID);
			logger.debug ("datestamp :" + datestamp);
		}
		
		try {
			
			if (datestamp.equals ("")) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("1 parameter for select RawRecordData");
				
				pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND collected = (SELECT max(collected) FROM dbo.RawData)");
				pstmt.setInt (1, new Integer (internalOID));
				
			
			} else {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("2 parameter for select RawRecordData");
				
				pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND collected = ?");
				pstmt.setInt (1, new Integer (internalOID));
				pstmt.setString (2, datestamp);
			}
			
			return pstmt.executeQuery ( );
			
			/*rs = pstmt.executeQuery ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("statement: " + rs.getStatement ( ));
			
			if (rs.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("resultbuffer is created");
				
				StringBuffer resultbuffer = new StringBuffer ("<object_id>" + rs.getInt (1) + "</object_id>\n");
				resultbuffer.append ("<collected>" + rs.getDate (2) + "</collected>\n");
				resultbuffer.append ("<data>" + rs.getString (3) + "</data>\n");
				
				result = resultbuffer.toString ( );
			}*/
			
		} catch (SQLException sqlex) {
			
			logger.error (sqlex.getLocalizedMessage ( ));
			sqlex.printStackTrace ( );
			
		} /*finally {
			
			try {
				
				conn.close ( );
				
			} catch (SQLException ex) {
				
				logger.error (ex.getLocalizedMessage ( ));
				ex.printStackTrace ( );
			}
		}*/
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectRawRecordData(java.lang.String)
	 */
	public ResultSet selectRawRecordData (String internalOID) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("entering selectRawRecordData with only one parameter");
		
		return selectRawRecordData (internalOID, "");
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#insertRawRecordData(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String insertRawRecordData (int internalOID, String datestamp,
			String blobbb) {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, repository_timestamp, data) VALUES (?, ?, ?)");
			
			pstmt.setBigDecimal (1, new BigDecimal (internalOID));
			//TODO: insert right Timestamp into repository_timestamp!!!
			pstmt.setDate (2, HelperMethods.today ( ));
			pstmt.setString (3, blobbb);
			pstmt.executeUpdate ( );
			
		} catch (SQLException sqlex) {
			
			logger.error ("insertRawRecordData: SQLException using Object " + internalOID);
			logger.error (sqlex.getLocalizedMessage ( ));
			
			sqlex.printStackTrace ( );
		}
		
		return Integer.toString (internalOID);
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#insertObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	
	public ResultSet insertObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier) {
		
//		createConnection ( );
		
		//PreparedStatement pstmt = null;
		//Statement stmt = null;		
		// ResultSet rs = null;
	//	int object_id = 0;
		
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier) VALUES (?, ?, ?, ?)");
		
		//try {
			
			
			
//			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
/*			ResultSet rs = stmt.executeQuery ("SELECT * FROM dbo.Object FOR UPDATE WHERE object_id = (SELECT MAX (object_id) FROM dbo.Object)");
			
			if (rs.next ( ))
				object_id = rs.getInt (1);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("objectid: " + object_id);
						
			rs.moveToInsertRow ( );
			rs.updateInt (1, ++object_id);
			rs.updateInt (2, repository_id);
			rs.updateDate (3, harvested);
			rs.updateDate (4, repository_datestamp);
			rs.updateString (5, repository_identifier);*/
			
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#getObject(java.lang.String)
	 */
	public ResultSet getObject (int oid) {
		
//		createConnection ( );
		
		PreparedStatement pstmt = null;
		
//		String result = "<NULL />";
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
			
			pstmt.setInt (1, oid);
			
			return pstmt.executeQuery ( );
			
/*			ResultSet rs = pstmt.executeQuery ( );
			
			if (rs.next ( )) {
				
				StringBuffer resultbuffer = new StringBuffer("<object_id>" + rs.getInt (1) + "</object_id>\n");
				resultbuffer.append ("<repository_id>").append (rs.getInt (2)).append ("</repository_id>\n");
				resultbuffer.append ("<harvested>").append (rs.getDate (3)).append ("</harvested>\n");
				resultbuffer.append ("<repository_datestamp>").append (rs.getDate (4)).append ("</repository_datestamp>\n");
				resultbuffer.append ("<repository_identifier>").append (rs.getString (5)).append ("</repository_identifier>\n");
				result = resultbuffer.toString ( );
			}*/
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#updateObject(int, java.sql.Date, java.sql.Date, java.lang.String)
	 */
	public String updateObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier) {

		// TODO Auto-generated method stub
		return "";
	}
} //end of class