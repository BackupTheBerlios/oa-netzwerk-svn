/**
 * 
 */

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

public class MultipleStatementConnection implements StatementConnection {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (MultipleStatementConnection.class);
	
	/**
	 * 
	 */
	
	public final Connection connection;
	
	/**
	 * 
	 */
	
	private PreparedStatement multipleStatement;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	protected MultipleStatementConnection (Connection dataSourceConnection) throws SQLException {
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("MultipleStatementConnection Instance will be prepared");
		
		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (false);
		this.multipleStatement = null;
		
		SQLWarning warning = this.connection.getWarnings ( );
		
		while (warning != null) {
			
			logger.warn ("A SQL-Connection-Warning occured: " + warning.getMessage ( ) + " " + warning.getSQLState ( ) + " " + warning.getErrorCode ( ));
			warning = warning.getNextWarning ( );
		}
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	public void close ( ) throws SQLException {
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("closing Statement and Connection");
		
		if (!this.connection.isClosed ( )) {
		
			this.connection.setAutoCommit (true);
			
			if (this.multipleStatement != null) {
				
				this.multipleStatement.close ( );
				this.multipleStatement = null;
			}
			
			this.connection.close ( );
		}
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	public QueryResult execute ( ) throws SQLException {
		
		QueryResult result = new QueryResult ( );
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("Executing Statement: ");
		
		if (this.multipleStatement.getWarnings ( ) != null)
			logger.warn ("A SQL-Statement-Warning occured: " + this.multipleStatement.getWarnings ( ).getMessage ( ) + " " + this.multipleStatement.getWarnings ( ).getSQLState ( ) + " " + this.multipleStatement.getWarnings ( ).getErrorCode ( ));
		
		try {
			
			this.multipleStatement.execute ( );
			
			if (this.multipleStatement.getWarnings ( ) != null)
				logger.warn ("A SQL-Statement-Warning occured: " + this.multipleStatement.getWarnings ( ).getMessage ( ) + " " + this.multipleStatement.getWarnings ( ).getSQLState ( ) + " " + this.multipleStatement.getWarnings ( ).getErrorCode ( ));
			
		} catch (SQLException ex) {
			
			logger.warn ("SQLException occured while executing Statement, performing rollback!");
			//logger.error (ex.getLocalizedMessage ( ));
			
			this.connection.rollback ( );
			
			throw ex;
			//return result;
		}
		
		result.setResultSet (this.multipleStatement.getResultSet ( ));
		result.setUpdateCount (this.multipleStatement.getUpdateCount ( ));
		result.setWarning (this.multipleStatement.getWarnings ( ));
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("Returning Result");
		
		return result;
	}
	
	/**
	 * @throws SQLException
	 */
	
	public void commit ( ) throws SQLException {
		
		try {
			
//			if (logger.isDebugEnabled ( ))
//				logger.debug ("Committing Transaction");
			
			if (this.multipleStatement.getWarnings ( ) != null)
				logger.warn ("A SQL-Statement-Warning occured: " + this.multipleStatement.getWarnings ( ).getMessage ( ) + " " + this.multipleStatement.getWarnings ( ).getSQLState ( ) + " " + this.multipleStatement.getWarnings ( ).getErrorCode ( ));
			
			this.connection.commit ( );
			
			if (this.multipleStatement.getWarnings ( ) != null)
				logger.warn ("A SQL-Statement-Warning occured: " + this.multipleStatement.getWarnings ( ).getMessage ( ) + " " + this.multipleStatement.getWarnings ( ).getSQLState ( ) + " " + this.multipleStatement.getWarnings ( ).getErrorCode ( ));
			
		} catch (SQLException ex) {
			
			logger.warn ("SQLException occured while committing Transaction, performing rollback!");
			//logger.error (ex.getLocalizedMessage ( ));
			
			this.connection.rollback ( );
						
			//ex.printStackTrace ( );
			
			throw ex;
		}
	}
	
	/**
	 * @throws SQLException
	 */
	
	public void rollback ( ) throws SQLException {
		
//		logger.info ("Doing rollback!");
		
		this.connection.rollback ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	public boolean loadStatement (PreparedStatement pstmt) {
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("loading Statement");
		
		this.multipleStatement = pstmt;
		
		return true;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	
	@Override
	protected void finalize ( ) throws Throwable {
		
//		if (logger.isDebugEnabled ( ))
//			logger.debug ("finalizing MultipleStatementConnection");
		
		if (!this.connection.isClosed ( )) {
			
			this.connection.setAutoCommit (true);
			
			if (this.multipleStatement != null) {
				
				this.multipleStatement.close ( );
				this.multipleStatement = null;
			}
			
			this.connection.close ( );
		}
		
		super.finalize ( );
	}
}
