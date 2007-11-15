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

public class dbAccess {

	/**
	 * @param args
	 */
	
	static Connection conn = null;
	static Statement stmt = null;
	
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
				
		System.setProperty (Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty (Context.PROVIDER_URL, "file:///tmp");
		Properties prop = new Properties ( );
		DataSource ds = null;
//		InitialContext ic = null;
		InitialContext ic2 = null;
		
		@SuppressWarnings("unused")
		int [ ] updateCounts = null;
		
		try {
			
			prop.loadFromXML (new FileInputStream ("/home/mkuehn/workspace/oa-netzwerk-develop/dbprop.xml"));
			
			SybDataSource source = new SybDataSource ( );
			source.setDataSourceName (prop.getProperty ("dataSourceName"));
			source.setServerName ("themis.rz.hu-berlin.de");
			source.setPortNumber (2025);
			source.setDatabaseName ("oanetztest");
			source.setUser (prop.getProperty ("user"));
			source.setPassword (prop.getProperty ("password"));
			new InitialContext ( ).rebind (prop.getProperty ("dataSourceName"), source);
			
/*			Reference cpdsRef = new Reference ("org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS",
					"org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS", null);
			cpdsRef.add (new StringRefAddr ("driver", prop.getProperty ("driver")));
			cpdsRef.add (new StringRefAddr ("url", prop.getProperty ("url")));
			cpdsRef.add (new StringRefAddr ("user", prop.getProperty ("user")));
			cpdsRef.add (new StringRefAddr ("password", prop.getProperty ("password")));
*/
/*			cpdsRef.add (new StringRefAddr ("driver", "com.sybase.jdbc2.jdbc.SybDriver"));
			cpdsRef.add (new StringRefAddr ("url", "jdbc:sybase:Tds:host:1234/database"));
			cpdsRef.add (new StringRefAddr ("user", "foo"));
			cpdsRef.add (new StringRefAddr ("password", "bar"));*/
			//ic.rebind (prop.getProperty ("dataSourceName"), cpdsRef);
			
/*			Reference ref = new Reference ("org.apache.commons.dbcp.datasources.PerUserPoolDataSource",
					"org.apache.commons.dbcp.datasources.PerUserPoolDataSourceFactory", null);
			
			ref.add (new StringRefAddr ("dataSourceName", prop.getProperty ("dataSourceName")));
			ref.add (new StringRefAddr ("defaultMaxActive", prop.getProperty ("defaultMaxActive")));
			ref.add (new StringRefAddr ("defaultMaxIdle", prop.getProperty ("defaultMaxIdle")));
			ref.add (new StringRefAddr ("defaultMaxWait", prop.getProperty ("defaultMaxWait")));
*/			
/*			ref.add (new StringRefAddr ("dataSourceName", "oanetzwerk"));
			ref.add (new StringRefAddr ("defaultMaxActive", "100"));
			ref.add (new StringRefAddr ("defaultMaxIdle", "30"));
			ref.add (new StringRefAddr ("defaultMaxWait", "10000"));
*/
			//ic.rebind (prop.getProperty ("dataSourceName"), ref);
			
			ic2 = new InitialContext ( );
			
			ds = (DataSource) ic2.lookup (prop.getProperty ("dataSourceName"));
			Assert.assertNotNull (ds);
			conn = ds.getConnection ( );
			
			BufferedReader file = new BufferedReader (new FileReader ("/home/mkuehn/workspace/oa-netzwerk-develop/db-schema/oan-db-model.sql"));
			StringBuffer sql = new StringBuffer (""); 
			
			for (String line; (line = file.readLine ( )) != null;) {
				
				sql.append (line);
			}
			
			
			System.out.println (sql.toString ( ));
			
			stmt = conn.createStatement ( );
			conn.setAutoCommit (false);
			stmt.addBatch (sql.toString ( ));
			updateCounts = stmt.executeBatch ( );
			conn.setAutoCommit (true);
			
			file.close ( );
			
/*			stmt.executeUpdate ("DROP TABLE dbo.AggregatorMetadata");
			stmt.executeUpdate ("CREATE TABLE dbo.AggregatorMetadata (" +
					"object_id INTEGER NOT NULL" +
					", harvested TIMESTAMP NOT NULL" +
					", PRIMARY KEY (object_id, harvested)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.Person");
			stmt.executeUpdate ("CREATE TABLE dbo.Person (" +
					"person_id INTEGER NOT NULL" +
					", number INTEGER" +
					", firstname VARCHAR(100)" +
					", lastname CHAR" +
					", title CHAR" +
					", institution CHAR" +
					", email CHAR" +
					", PRIMARY KEY (person_id)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.Keywords");
			stmt.executeUpdate ("CREATE TABLE dbo.Keywords (" +
					"keyword_id INTEGER NOT NULL" +
				    ", keyword VARCHAR(255)" +
				    ", lang CHAR(3)" +
				    ", PRIMARY KEY (keyword_id)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.DCC_Categories");
			stmt.executeUpdate ("CREATE TABLE dbo.DCC_Categories (" +
					"DCC_Categorie CHAR(4) NOT NULL" +
				    ", name VARCHAR(255)" +
				    ", PRIMARY KEY (DCC_Categorie)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.DNB_Categories");
			stmt.executeUpdate ("CREATE TABLE dbo.DNB_Categories (" +
				    "DNB_Categorie INTEGER NOT NULL" +
				    ", name VARCHAR(255)" +
				    ", PRIMARY KEY (DNB_Categorie)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.DINI_Set_Categories");
			stmt.executeUpdate ("CREATE TABLE dbo.DINI_Set_Categories (" +
				    "DINI_set_id INTEGER NOT NULL" +
				    ", name VARCHAR(255) NOT NULL" +
				    ", PRIMARY KEY (DINI_set_id)" +
					");");
			
			stmt.executeUpdate ("DROP TABLE dbo.Other_Categories");
			stmt.executeUpdate ("CREATE TABLE dbo.Other_Categories (" +
				    "other_id INTEGER NOT NULL" +
				    ", name VARCHAR(255) NOT NULL" +
				    ", PRIMARY KEY (other_id)" +
					");");

			stmt.executeUpdate ("DROP TABLE dbo.Repositories");
			stmt.executeUpdate ("CREATE TABLE dbo.Repositories (" +
				    "repository_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (repository_id)" +
					");");

			stmt.executeUpdate ("DROP TABLE dbo.Objec");
			stmt.executeUpdate ("CREATE TABLE dbo.Object (" +
				    "object_id INTEGER NOT NULL" +
				    ", repository_id INTEGER NOT NULL" +
				    ", harvested TIMESTAMP NOT NULL" +
				    ", repository_datestamp DATE" +
				    ", repository_identifier VARCHAR(255) NOT NULL" +
				    ", PRIMARY KEY (object_id)" +
				    ", FK_Object_1 FOREIGN KEY (repository_id)" +
				    	"REFERENCES dbo.Repositories (repository_id)" +
				    ", FK_Object_2 FOREIGN KEY (object_id, harvested)" +
				    	"REFERENCES dbo.AggregatorMetadata (object_id, harvested)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.Titles");
			stmt.executeUpdate ("CREATE TABLE dbo.Titles (" +
				    "object_id INTEGER NOT NULL" +
				    ", title CHAR NOT NULL" +
				    ", number INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id)" +
				    ", FK_Titles_1 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.Object2Author");
			stmt.executeUpdate ("CREATE TABLE dbo.Object2Author (" +
				    "object_id INTEGER NOT NULL" +
				    ", author_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, author_id)" +
				    ", FK_Object2Author_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ", FK_Object2Author_3 FOREIGN KEY (author_id)" +
				    	"REFERENCES dbo.Person (person_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.Object2Contributor");
			stmt.executeUpdate ("CREATE TABLE dbo.Object2Contributor (" +
				    "object_id INTEGER NOT NULL" +
				    ", contributor_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, contributor_id)" +
				    ", FK_Object2Contributor_1 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ", FK_Object2Contributor_2 FOREIGN KEY (contributor_id)" +
				    	"REFERENCES dbo.Person (person_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.Object2Editor");
			stmt.executeUpdate ("CREATE TABLE dbo.Object2Editor (" +
				    "object_id INTEGER NOT NULL" +
				    ", editor_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, editor_id)" +
				    ", FK_Object2Editor_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ", FK_Object2Editor_1 FOREIGN KEY (editor_id)" +
				    	"REFERENCES dbo.Person (person_id)" +
					");");

			stmt.executeUpdate ("DROP TABLE dbo.Object2Keywords");
			stmt.executeUpdate ("CREATE TABLE dbo.Object2Keywords (" +
				    "object_id INTEGER NOT NULL" +
				    ", keyword_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, keyword_id)" +
				    ", FK_Object2Keywords_1 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ", FK_Object2Keywords_2 FOREIGN KEY (keyword_id)" +
				    	"REFERENCES dbo.Keywords (keyword_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.DCC_Classification");
			stmt.executeUpdate ("CREATE TABLE dbo.DCC_Classification (" +
				    "object_id INTEGER NOT NULL" +
				    ", DCC_Categorie CHAR(10) NOT NULL" +
				    ", PRIMARY KEY (object_id, DCC_Categorie)" +
				    ", FK_DCC_Classification_1 FOREIGN KEY (DCC_Categorie)" +
				    	"REFERENCES dbo.DCC_Categories (DCC_Categorie)" +
				    ", FK_DCC_Classification_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.DNB_Classification");
			stmt.executeUpdate ("CREATE TABLE dbo.DNB_Classification (" +
				    "object_id INTEGER NOT NULL" +
				    ", DNB_Categorie INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, DNB_Categorie)" +
				    ", FK_DNB_Classification_1 FOREIGN KEY (DNB_Categorie)" +
				    	"REFERENCES dbo.DNB_Categories (DNB_Categorie)" +
				    ", FK_DNB_Classification_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.DINI_Set_Classificatio");
			stmt.executeUpdate ("CREATE TABLE dbo.DINI_Set_Classification (" +
				    "object_id INTEGER NOT NULL" +
				    ", DINI_set_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, DINI_set_id)" +
				    ", FK_DINI_Set_Classification_1 FOREIGN KEY (DINI_set_id)" +
				    	"REFERENCES dbo.DINI_Set_Categories (DINI_set_id)" +
				    ", FK_DINI_Set_Classification_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.Other_Classification");
			stmt.executeUpdate ("CREATE TABLE dbo.Other_Classification (" +
				    "object_id INTEGER NOT NULL" +
				    ", other_id INTEGER NOT NULL" +
				    ", PRIMARY KEY (object_id, other_id)" +
				    ", FK_Other_Classification_1 FOREIGN KEY (other_id)" +
				    	"REFERENCES dbo.Other_Categories (other_id)" +
				    ", FK_Other_Classification_2 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");

			stmt.executeUpdate ("DROP TABLE dbo.RawData");
			stmt.executeUpdate ("CREATE TABLE dbo.RawData (" +
				    "object_id INTEGER NOT NULL" +
				    ", collected TIMESTAMP NOT NULL" +
				    ", data CHAR" +
				    ", PRIMARY KEY (object_id, collected)" +
				    ", FK_RawData_1 FOREIGN KEY (object_id)" +
				    	"REFERENCES dbo.Object (object_id)" +
				    ");");*/
		
		} catch (BatchUpdateException buex) {
			
			try {
				
				conn.rollback ( );
				
			} catch (SQLException ex) {
				
				ex.printStackTrace ( );
			}
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
			
		} catch (NamingException ex) {
			
			ex.printStackTrace ( );
			
		} /*finally {
			
			if (conn != null) {
				
				try {
					
					conn.close ( );
					
				} catch (SQLException sqlex) {
					
					sqlex.printStackTrace ( );
				}
			}
		}*/ catch (InvalidPropertiesFormatException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
