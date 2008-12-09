package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

public class SingleStatementConnection implements StatementConnection {
	
	private static Logger logger = Logger.getLogger (SingleStatementConnection.class);
	public final Connection connection;
	private PreparedStatement singleStatement = null;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	protected SingleStatementConnection (Connection dataSourceConnection) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("SingleStatementConnection Instance will be prepared");
		
		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (true);
		this.singleStatement = null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	public boolean loadStatement (PreparedStatement pstmt) {
		
		if (this.singleStatement == null) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("loading Statement");
			
			this.singleStatement = pstmt;
			
			return true;
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("cannot load Statement");
			
			return false;
		}
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	public QueryResult execute ( ) throws SQLException {
		
		QueryResult result = new QueryResult ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("executing Statement");
		
		this.singleStatement.execute ( );
		
		result.setResultSet (this.singleStatement.getResultSet ( ));
		result.setUpdateCount (this.singleStatement.getUpdateCount ( ));
		result.setWarning (this.singleStatement.getWarnings ( ));
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Returning Result");
		
		return result;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	public void close ( ) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("closing Statement and Connection");
		
		if (!this.connection.isClosed ( )) {
	
			this.connection.setAutoCommit (true);
			
			if (this.singleStatement != null) {
				
				this.singleStatement.close ( );
				this.singleStatement = null;
			}
			
			this.connection.close ( );
		}
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	
	@Override
	protected void finalize ( ) throws Throwable {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("finalizing SingleStatementConnection");
		
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
