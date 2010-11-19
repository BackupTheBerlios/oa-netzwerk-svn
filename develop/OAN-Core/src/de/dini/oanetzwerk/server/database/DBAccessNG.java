package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * 
 */

public class DBAccessNG {

	private static Logger logger = Logger.getLogger(DBAccessNG.class);

	private static int defaultPoolSize = 3;

	private DataSource datasource;

	private List<Connection> pool = new ArrayList<Connection>();

	private int currentPoolCursor = 0;

	// private Connection dataSourceConnection;
	// private StatementConnection statementConnection;
	// private boolean isSingeleStatementConnection;

	/**
	 * 
	 */
	public DBAccessNG() {

		if (logger.isDebugEnabled())
			logger.debug("DBAccessNG Instance will be prepared!");

		try {

			this.datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup("jdbc/oanetztest");

		} catch (NamingException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 
	 */
	public DBAccessNG(String dataSource) {

		if (logger.isDebugEnabled())
			logger.debug("DBAccessNG Instance will be prepared!");

		try {

			this.datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup(dataSource);

		} catch (NamingException ex) {

			// logger.error("DataSource could not be loaded. We can't connect to the database. Please enter the correct datasource!");
			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 
	 * @return Connection
	 */
	private Connection createConnection() {

		if (logger.isDebugEnabled())
			logger.debug("Database connection will be prepared!");

		if (this.datasource == null) {
			logger.warn("Datasource could not be resolved!");
		}

		Connection connection;
		try {

			connection = this.datasource.getConnection();
			return connection;

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}

	/**
	 * 
	 * @return Connection
	 * @throws SQLException
	 */
	public synchronized Connection getConnection() throws SQLException {

		Connection connection;

		if (pool.size() < defaultPoolSize) {
			logger.info("Connection pool not yet filled up, creating new connection!");
			connection = createConnection();
			pool.add(connection);
			return connection;
		}

		connection = pool.get(currentPoolCursor);

		if (connection == null || connection.isClosed()) {

			connection = createConnection();
			pool.remove(currentPoolCursor);
			pool.add(currentPoolCursor, connection);
		} else {
			logger.info("Existing connection retrieved from pool!");
		}

		currentPoolCursor++;
		if (currentPoolCursor >= defaultPoolSize) {
			currentPoolCursor = 0;
		}

		if (connection == null) {
			throw new SQLException("Connection based on the given data source not available!");
		} 

		return connection;
	}

	/**
	 * @return
	 * @throws WrongStatementException
	 * @throws SQLException
	 */
	public StatementConnection getSingleStatementConnection() throws WrongStatementException, SQLException {

		Connection connection = getConnection();
		
		if (logger.isDebugEnabled())
			logger.debug("Creating new SingleStatementConnection...");

		if (connection == null || connection.isClosed()) {
			throw new SQLException("Connection based on the given data source not available!");
		} 

		return new SingleStatementConnection(connection);
	}

	/**
	 * @return
	 * @throws WrongStatementException
	 * @throws SQLException
	 */
	public StatementConnection getMultipleStatementConnection() throws WrongStatementException, SQLException {

		Connection connection = getConnection();
		
		if (logger.isDebugEnabled())
			logger.debug("Creating new SingleStatementConnection...");

		if (connection == null) {
			throw new SQLException("Connection based on the given data source not available!");
		}

		return new MultipleStatementConnection(connection);
	}

	
//	public PreparedStatement safelyCreatePreparedStatement(Connection connection, String sql) {
//		
//		boolean ok = false;
//		PreparedStatement preparedstmt = null;
//		int retryAttempts = 0;
//		
//		while (!ok && retryAttempts < 100)
//		{
//			try {
//				if (connection == null || connection.isClosed())
//				{
//					System.out.println("Connection is null or closed! This should not happen!");
//				}
//				preparedstmt = connection.prepareStatement(sql.toString());
//				ok = true;
//			} catch (SQLException e) {
//				logger.info("Error while creating PreparedStatement! Trying again...");
//				try {
//					connection = getConnection();
//				} catch (SQLException ex) {
//					logger.info("Error getting connection from pool! Trying again...");
//				}
//			}
//			
//		}
//		return preparedstmt;
//	}
	
	/**
	 * 
	 * @return QueryResult
	 * @throws SQLException
	 */
	public QueryResult executeStatement(StatementConnection statement) throws SQLException {
		QueryResult result;

		try {
			result = statement.execute();

			if (statement instanceof SingleStatementConnection)
				statement.close();

		} catch (SQLException e) {
			throw new SQLException("Connection based on the given data source not available!");
		}
		return result;
	}
}
