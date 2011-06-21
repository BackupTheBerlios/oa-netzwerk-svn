package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import de.dini.oanetzwerk.server.database.postgres.DeleteFromDBPostgres;
import de.dini.oanetzwerk.server.database.postgres.InsertIntoDBPostgres;
import de.dini.oanetzwerk.server.database.postgres.SelectFromDBPostgres;
import de.dini.oanetzwerk.server.database.postgres.UpdateInDBPostgres;
import de.dini.oanetzwerk.server.database.sybase.DeleteFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * 
 */

public class DBAccessNG {

	private static Logger logger = Logger.getLogger(DBAccessNG.class);

	private static int defaultPoolSize = 5;

	private DataSource datasource;

	private List<Connection> pool = new ArrayList<Connection>();

	private int currentPoolCursor = 0;
	
	private static DBAccessNG instance = null;
	
	private static final String PG_DRIVER = "";
	private static final String SYBASE_DRIVER = "";
	
	private static SelectFromDB select;
	private static InsertIntoDB insert;
	private static UpdateFromDB update;
	private static DeleteFromDB delete;
	
	// private Connection dataSourceConnection;
	// private StatementConnection statementConnection;
	// private boolean isSingeleStatementConnection;

	/**
	 * 
	 */
	private DBAccessNG() {

		if (logger.isDebugEnabled())
			logger.debug("DBAccessNG Instance will be prepared!");

		try {
			System.out.println("DBAccessNG - new instance1");
			this.datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup("jdbc/oanetztest");

			initDriverClass();
		} catch (NamingException ex) {

//			this.datasource = new BasicDataSource();
//			this.datasource.
			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 
	 */
	private DBAccessNG(String dataSource) {

		if (logger.isDebugEnabled())
			logger.debug("DBAccessNG Instance will be prepared!");

		try {
			System.out.println("DBAccessNG - new instance2");
			System.out.println("DataSource: " + dataSource);
			this.datasource = (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup(dataSource);

			initDriverClass();
		} catch (NamingException ex) {

			// logger.error("DataSource could not be loaded. We can't connect to the database. Please enter the correct datasource!");
			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	public static synchronized DBAccessNG getInstance() {
		if (instance == null) {
			instance = new DBAccessNG();
		}
		return instance;
	}
	
	public static synchronized DBAccessNG getInstance(String dataSource) {
		if (instance == null) {
			instance = new DBAccessNG(dataSource);
		}
		return instance;
	}
	
	private void initDriverClass() {
		
		BasicDataSource ds = (BasicDataSource) this.datasource;
		if (SYBASE_DRIVER.equals(ds.getDriverClassName())) {
			// init sybase driver classes
			
			select = new SelectFromDBSybase();
			insert = new InsertIntoDBSybase();
			update = new UpdateInDBSybase();
			delete = new DeleteFromDBSybase();
		} else {
			
			// init postgres driver classes as a default
			
			select = new SelectFromDBPostgres();
			insert = new InsertIntoDBPostgres();
			update = new UpdateInDBPostgres();
			delete = new DeleteFromDBPostgres();
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
	public synchronized StatementConnection getSingleStatementConnection() throws WrongStatementException, SQLException {

		Connection connection = getConnection();
		
		if (logger.isDebugEnabled())
			logger.debug("Creating new SingleStatementConnection...");

		if (connection == null || connection.isClosed()) {
			
			for (int i = 0; i < defaultPoolSize; i++) {
				System.out.println("Connection " + i + " is null: " + connection == null + (connection == null ? "" : " closed: " + connection.isClosed()));
				if (pool.get(i) == null || pool.get(i).isClosed()) {
					connection = getConnection();
				}
			}
			
			if (connection == null || connection.isClosed()) {
				
				throw new SQLException("Connection based on the given data source not available!");
			}
			
		} 

		return new SingleStatementConnection(connection);
	}

	/**
	 * @return
	 * @throws WrongStatementException
	 * @throws SQLException
	 */
	public synchronized StatementConnection getMultipleStatementConnection() throws WrongStatementException, SQLException {

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

	public static SelectFromDB selectFromDB() {
    	return select;
    }

	public static InsertIntoDB insertIntoDB() {
    	return insert;
    }

	public static UpdateFromDB updateInDB() {
    	return update;
    }

	public static DeleteFromDB deleteFromDB() {
    	return delete;
    }
	
	

}
