/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
//import org.apache.log4j.xml.DOMConfigurator;

import com.sybase.jdbc3.jdbc.SybDataSource;

import de.dini.oanetzwerk.utils.HelperMethods;

import junit.framework.Assert;

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
		
		this.prop = HelperMethods.loadPropertiesFromFile ("/home/mkuehn/workspace/oa-netzwerk-develop/dbprop.xml");
		
		try {
			
			ic2 = new InitialContext ( );
			
		} catch (NamingException ex) {
			
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
				
				ex.printStackTrace ( );
			}
			
		} else {
			
			System.out.println ("DataBase not supported");
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#createConnection()
	 */
	
	public void createConnection ( ) {
		
		try {
			
			this.ds = (DataSource) this.ic2.lookup (this.prop.getProperty ("dataSourceName"));
			Assert.assertNotNull (ds);
			conn = this.ds.getConnection ( );
			
		} catch (NamingException ex) {
			
			ex.printStackTrace ( );
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#closeConnection()
	 */
	
	public void closeConnection ( ) {
		
		try {
			
			conn.close ( );
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
		}
	}
	
	public void setAutoCom (boolean ac) {
		
		try {
			
			conn.setAutoCommit (ac);
			
		} catch (SQLException ex) {
			
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
			//conn.setAutoCommit (false);
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
	 * @see de.dini.oanetzwerk.DBAccessInterface#putData(de.dini.oanetzwerk.DBAccess.moduls, int, java.lang.String, java.lang.String)
	 */
	
	public void putData (moduls modul, String repositoryName, String repositoryIdentifier, String repositoryDate, String data) {
		
		switch (modul) {
			
		case Harvester: putHarvester (repositoryName, repositoryIdentifier, repositoryDate, data); break;
		case Aggregator: ; break;
		default: ;
		}
	}

	/**
	 * @param id
	 * @param data
	 * @param repositoryDate 
	 * @param date
	 */
	
	private void putHarvester (String repositoryName, String repositoryIdentifier, String repositoryDate, String data) {
		
		createConnection ( );
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT repository_id FROM dbo.Repositories WHERE name = ?");
			pstmt.setString (1, repositoryName);
			
			rs = pstmt.executeQuery ( );
			
			if (rs.next ( )) {
				
				int repository_id = rs.getInt ("repository_id");
				
				rs.close ( );
				
				int object_id = 0;
				
				pstmt = conn.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_identifier = ?");
				pstmt.setString (1, repositoryIdentifier);
				rs = pstmt.executeQuery ( );
				
				if (rs.next ( )) {
					
					object_id = rs.getInt ("object_id");
					rs.close ( );
					
				} else {
					
					rs.close ( );
					pstmt = conn.prepareStatement ("SELECT MAX(object_id) FROM dbo.Object");
					rs = pstmt.executeQuery ( );
					
					if (rs.next ( )) {
						
						object_id = rs.getInt ("object_id");
						
					} else {
						
						//FEHLER
					}
					rs.close ( );
				}
				
				conn.setAutoCommit (false);
				pstmt = conn.prepareStatement ("INSERT INTO dbo.Object (object_id, repository_id, harvested, repository_datestamp, repository_identifier) VALUES (?, ?, ?, ?, ?)");
				pstmt.setInt (1, object_id);
				pstmt.setInt (2, repository_id);
				pstmt.setDate (3, HelperMethods.today ( ));
				pstmt.setDate (4, Date.valueOf (repositoryDate));
				pstmt.setString (5, repositoryIdentifier);
				pstmt.executeUpdate ( );
				
				pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, collected, data) VALUES (?, ?, ?)");
				pstmt.setInt (1, object_id);
				pstmt.setDate (2, HelperMethods.today ( ));
				pstmt.setString (3, data);
				pstmt.executeUpdate ( );
				
/*				if (pstmt.executeUpdate ( ) != 1) {
					
					conn.rollback ( );
					logger.error ("Can't insert Data");
					pstmt.close ( );
					pstmt = null;
					
				} else {
					
					pstmt.close ( );
					
					pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, collected, data) VALUES (?, ?, ?)");
					pstmt.setInt (1, id);
					pstmt.setDate (2, Date.valueOf ("2007-11-27"));
					pstmt.setString (3, data);
					
					if (pstmt.executeUpdate ( ) != 1) {
						
						conn.rollback ( );
						logger.error ("Can't insert Data");
					}
					
					pstmt.close ( );
					pstmt = null;
				}*/
				conn.commit ( );
				conn.setAutoCommit (true);
				pstmt.close ( );
				
			} else {
				
				// Repository existiert nicht
			}
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			try {
				
				conn.close ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
		}
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectObjectEntryId(java.lang.String, java.lang.String)
	 */
	public String selectObjectEntryId (String repositoryID, String externalOID) {
		
		createConnection ( );
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.repository_identifier = ? and o.repository_id = ?");
			pstmt.setString (1, repositoryID);
			pstmt.setString (2, externalOID);
			
			rs = pstmt.executeQuery ( );
			
			if (rs.next ( )) {
				
				return Integer.toString (rs.getInt ("object_id"), 10);
				
			} else 
				return null;
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			try {
				
				conn.close ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
		}
		
		return "-500";
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectRawRecordData(java.lang.String, java.lang.String)
	 */
	
	public String selectRawRecordData (String internalOID, String datestamp) {

		createConnection ( );
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ? AND collected = ?");
			pstmt.setString (1, internalOID);
			
			if (datestamp.equals (""))
				pstmt.setString (2, "max(collected)");
			
			else
				pstmt.setString (2, datestamp);
			
			rs = pstmt.executeQuery ( );
			
			if (rs.next ( )) {
				
				//TODO: return rawdataset
				
			} else
				return null;
			
		} catch (SQLException sqlex) {
			
		} finally {
			
			try {
				
				conn.close ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
		}
		return "DatabaseError";
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#selectRawRecordData(java.lang.String)
	 */
	public String selectRawRecordData (String internalOID) {

		return selectRawRecordData (internalOID, "");
	}

	/**
	 * @see de.dini.oanetzwerk.DBAccessInterface#insertRawRecordData(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int insertRawRecordData (int internalOID, String datestamp,
			String blobbb) {

		createConnection ( );
		
		PreparedStatement pstmt = null;
		int rs = 0;
		
		try {
			
			pstmt = conn.prepareStatement ("INSERT INTO dbo.RawData (object_id, collected, data) VALUES (?, ?, ?)");
			pstmt.setInt (1, internalOID);
			pstmt.setDate (2, HelperMethods.today ( ));
			pstmt.setString (3, blobbb);
			rs = pstmt.executeUpdate ( );

		} catch (SQLException sqlex) {
			
		} finally {
			
			try {
				
				conn.close ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
		}
		return rs;
	}
}