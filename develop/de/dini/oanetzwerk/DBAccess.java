/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sybase.jdbc3.jdbc.SybDataSource;

import junit.framework.Assert;

/**
 * @author Michael Kühn
 *
 */

public class DBAccess implements DBAccessInterface {

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
		this.prop = new Properties ( );
		
		try {
			
			ic2 = new InitialContext ( );
			
		} catch (NamingException ex) {
			
			ex.printStackTrace();
		}
	}
	
	public static DBAccessInterface createDBAccess ( ) {
		
		DBAccess db = new DBAccess ( );
		db.setProperties ( );
		db.setDataSource ( );
		
		return db;
	}
	
	protected void setProperties ( ) {
		
		try {
			
			this.prop.loadFromXML (new FileInputStream ("/home/mkuehn/workspace/oa-netzwerk-develop/dbprop.xml"));
			
		} catch (InvalidPropertiesFormatException ex) {
			
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			
		}
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
		
		try {
			
			BufferedReader file = new BufferedReader (new FileReader ("/home/mkuehn/workspace/oa-netzwerk-develop/db-schema/oan-db-model.sql"));
			StringBuffer sql = new StringBuffer ("");
			db.createConnection ( );
			Statement stmt = conn.createStatement ( );
			conn.setAutoCommit (false);
			
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
			conn.setAutoCommit (true);
			
			for (int i = 0; i < updateCounts.length; i++)
				System.out.println ("UpdateCount: " + updateCounts);
			
			file.close ( );
					
		} catch (BatchUpdateException buex) {
			
			try {
				
				conn.rollback ( );
				buex.printStackTrace ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
			
		} catch (InvalidPropertiesFormatException ex) {
			
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			db.closeConnection ( );
		}
	}
}
