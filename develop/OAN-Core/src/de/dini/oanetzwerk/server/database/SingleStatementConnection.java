package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

public class SingleStatementConnection implements StatementConnection {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (SingleStatementConnection.class);
	
	/**
	 * 
	 */
	
	public final Connection connection;
	
	/**
	 * 
	 */
	
	private PreparedStatement singleStatement = null;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	public SingleStatementConnection (Connection dataSourceConnection) throws SQLException {
		
		logger.info("Creating new singlestatement connection!");
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("SingleStatementConnection Instance will be prepared");
		
		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (true);
		this.singleStatement = null;
		
		SQLWarning warning = this.connection.getWarnings ( );
		
		while (warning != null) {
			
			logger.warn ("A SQL-Connection-Warning occured: " + warning.getMessage ( ) + " " + warning.getSQLState ( ) + " " + warning.getErrorCode ( ));
			warning = warning.getNextWarning ( );
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	public boolean loadStatement (PreparedStatement pstmt) {
		
		logger.info("loading statement...");
		
		if (this.singleStatement == null) {
			
//			if (logger.isDebugEnabled ( ))
//				logger.debug ("loading Statement");
			
			this.singleStatement = pstmt;
			
			return true;
			
		} else {
			
//			if (logger.isDebugEnabled ( ))
//				logger.debug ("cannot load Statement");
			
			return false;
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	public QueryResult execute ( ) throws SQLException {
		
		logger.info("executing statement...");
		
		QueryResult result = new QueryResult ( );
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("executing Statement");
		
		if (this.singleStatement.getWarnings ( ) != null)
			logger.warn ("A SQL-Statement-Warning occured: " + this.singleStatement.getWarnings ( ).getMessage ( ) + " " + this.singleStatement.getWarnings ( ).getSQLState ( ) + " " + this.singleStatement.getWarnings ( ).getErrorCode ( ));
		
		this.singleStatement.execute ( );
		
		if (this.singleStatement.getWarnings ( ) != null)
			logger.warn ("A SQL-Statement-Warning occured: " + this.singleStatement.getWarnings ( ).getMessage ( ) + " " + this.singleStatement.getWarnings ( ).getSQLState ( ) + " " + this.singleStatement.getWarnings ( ).getErrorCode ( ));
		
		result.setResultSet (this.singleStatement.getResultSet ( ));
		result.setUpdateCount (this.singleStatement.getUpdateCount ( ));
		result.setWarning (this.singleStatement.getWarnings ( ));
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("Returning Result");
		
		return result;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	public void close ( ) throws SQLException {
		
		logger.info("clsoing connection...");
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("closing Statement and Connection");
		
		if (!this.connection.isClosed ( )) {
	

			this.connection.setAutoCommit (true);
			
			if (this.singleStatement != null) {
				
				this.singleStatement.close ( );
				this.singleStatement = null;
			}
			
			this.connection.close ( );
		} else {
			logger.info("connection was closed already!");
		}
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	
	@Override
	protected void finalize ( ) throws Throwable {
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("finalizing SingleStatementConnection");
		
		if (!this.connection.isClosed ( )) {
		
			this.connection.setAutoCommit (true);
			
			if (this.singleStatement != null) {
				
				this.singleStatement.close ( );
				this.singleStatement = null;
			}
			
			this.connection.close ( );
		}
		
		super.finalize ( );
	}
}
