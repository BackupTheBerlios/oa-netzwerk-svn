package de.dini.oanetzwerk.migration;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp.DelegatingResultSet;

import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.StatementConnection;

public class SybaseToPostgreSQL {

	public static void main(String[] args) {

		new SybaseToPostgreSQL().copyTableContent();
	}

	public void copyTableContent() {



		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		// DBAccessNG dbng = new DBAccessNG();

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:jtds:sybase://themis.cms.hu-berlin.de:2025;DatabaseName=oanetzwerktest");
		dataSource.setUsername("oanetdbo");
		dataSource.setPassword("U7%wD8e4");
		dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
		

		// dataSource.s

		System.out.println("Database connection will be prepared!");

		if (dataSource == null) {
			System.out.println("Datasource could not be resolved!");
		}

		Connection connection = null;
		try {

			connection = dataSource.getConnection();

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);
			ex.printStackTrace();
			return;
		}

		PreparedStatement singleStatement;

		try {

			stmtconn = new SingleStatementConnection(connection);
			singleStatement = null;

			SQLWarning warning = connection.getWarnings();

			while (warning != null) {

				System.out.println("A SQL-Connection-Warning occured: " + warning.getMessage() + " " + warning.getSQLState() + " "
				                + warning.getErrorCode());
				warning = warning.getNextWarning();
			}

			// TODO: adjust
			stmtconn.loadStatement(ddcBrowsingHelp(stmtconn.connection));
			queryresult = stmtconn.execute();

			System.out.println("Number of results:" + ((DelegatingResultSet) queryresult.getResultSet()).getFetchSize());


			if (queryresult.getWarning() != null) {

				for (Throwable warning2 : queryresult.getWarning()) {

					System.out.println(warning2.getLocalizedMessage());
				}
			}


			migrate(queryresult);



			closeStatementConnection(stmtconn);

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);

		} finally {

			closeStatementConnection(stmtconn);
		}
	}
	
	
	public void migrate(QueryResult qs) {
		System.out.println("Running Prepared Statement Batch Update");
		Connection conn = null;

		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/oanetdb";

			
			Properties props = new Properties();
			props.put("user", "postgres");
			props.put("password", "12345");
			props.put("charSet", "UTF-8");
			conn = DriverManager.getConnection(url, props);
//			conn.setClientInfo(properties)
			try {
				conn.setAutoCommit(false);
				
				// TODO: adjust
				PreparedStatement prest = ddcBrowsingHelp(qs, conn);
				

				int count[] = prest.executeBatch();
				conn.commit();
				conn.close();
				System.out.println("Added Successfully!");
			} catch (BatchUpdateException s) {
				System.out.println("SQL statement is not executed!");
				s.printStackTrace();
				s.getNextException().printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	

	

	/****************** Statements ********************/
	
	
	/******** Services ***********/
	public static PreparedStatement services(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("select * from Services");

		return preparedstmt;
	}
	
	private PreparedStatement services(QueryResult qs, Connection conn) throws SQLException {
	    String sql = "INSERT INTO \"Services\" VALUES(?,?)";
	    PreparedStatement prest = conn.prepareStatement(sql);
	    
	    
	    while (qs.getResultSet().next()) {

	    	ResultSet rs = qs.getResultSet();
	    	//copy data
	    	prest.setInt(1, rs.getInt(1));
	    	prest.setString(2, rs.getString(2));
	    	prest.addBatch();
	    }
	    return prest;
    }	
	
	/********* END Services *********/
	
	/********* DDC_Categories *******/
	
	public static PreparedStatement ddcCategories(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("select * from DDC_Categories");

		return preparedstmt;
	}
	
	private PreparedStatement ddcCategories(QueryResult qs, Connection conn) throws SQLException {
	    String sql = "INSERT INTO \"DDC_Categories\" VALUES(?,?,?)";
	    PreparedStatement prest = conn.prepareStatement(sql);
	    
	    
	    int i = 0;
	    while (qs.getResultSet().next()) {

	    	ResultSet rs = qs.getResultSet();
	    	//copy data
	    	prest.setString(1, rs.getString(1));
	        prest.setString(2, rs.getString(2));
	    	prest.setString(3, rs.getString(3));
//	    	System.out.println(rs.getString(2));
	    	prest.addBatch();
	    	i++;
	    	
	    }
	    System.out.println("Copied " + i + " rows.");
	    return prest;
    }	
	
	/***********************/
	
	public static PreparedStatement ddcBrowsingHelp(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("select * from DDC_Browsing_Help");

		return preparedstmt;
	}
	
	private PreparedStatement ddcBrowsingHelp(QueryResult qs, Connection conn) throws SQLException {
	    String sql = "INSERT INTO \"DDC_Browsing_Help\" VALUES(?,?,?,?,?,?)";
	    PreparedStatement prest = conn.prepareStatement(sql);
	    
	    
	    int i = 0;
	    while (qs.getResultSet().next()) {

	    	ResultSet rs = qs.getResultSet();
	    	//copy data
	    	prest.setString(1, rs.getString(1));
	        prest.setString(2, rs.getString(2));
	    	prest.setString(3, rs.getString(3));
	    	prest.setInt(4, rs.getInt(4));
	        prest.setInt(5, rs.getInt(5));
	    	prest.setString(6, rs.getString(6));
	    	prest.addBatch();
	    	i++;
	    	
	    }
	    System.out.println("Copied " + i + " rows.");
	    return prest;
    }	
	
	
	// TODO: other tables
	
	

	private void closeStatementConnection(StatementConnection stmtconn) {

		try {

			if (stmtconn != null)
				stmtconn.close();

		} catch (SQLException ex) {

			ex.printStackTrace();
		}
	}

}
