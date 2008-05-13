/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;


/**
 * @author Michael K&uuml;hn
 *
 */

public class MultipleStatementConnection implements StatementConnection {
	
	static Logger logger = Logger.getLogger (MultipleStatementConnection.class);
	public final Connection connection;
	private PreparedStatement multipleStatement;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	protected MultipleStatementConnection (Connection dataSourceConnection) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("MultipleStatementConnection Instance will be prepared");
		
		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (false);
		this.multipleStatement = null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	public void close ( ) throws SQLException {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("closing Statement and Connection");
		
		this.connection.setAutoCommit (true);
		this.multipleStatement.close ( );
		this.multipleStatement = null;
		this.connection.close ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	public QueryResult execute ( ) throws SQLException {
		
		QueryResult result = new QueryResult ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Executing Statement");
		
		try {
			
			this.multipleStatement.execute ( );
			
		} catch (SQLException ex) {
			
			logger.warn ("SQLException occured while executing Statement, performing rollback!");
			logger.error (ex.getLocalizedMessage ( ));
			
			this.connection.rollback ( );
			
			return result;
		}
		
		result.setResultSet (this.multipleStatement.getResultSet ( ));
		result.setUpdateCount (this.multipleStatement.getUpdateCount ( ));
		result.setWarning (this.multipleStatement.getWarnings ( ));
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Returning Result");
		
		return result;
	}
	
	public void commit ( ) throws SQLException {
		
		try {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Committing Transaction");
			
			this.connection.commit ( );
			
		} catch (SQLException ex) {
			
			logger.warn ("SQLException occured while committing Transaction, performing rollback!");
			logger.error (ex.getLocalizedMessage ( ));
			
			this.connection.rollback ( );
			
			ex.printStackTrace ( );	
		}
	}
	
	public void rollback ( ) throws SQLException {
		
		logger.info ("Doing rollback!");
		
		this.connection.rollback ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	public boolean loadStatement (PreparedStatement pstmt) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("loading Statement");
		
		this.multipleStatement = pstmt;
		
		return true;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	
	@Override
	protected void finalize ( ) throws Throwable {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("finalizing MultipleStatementConnection");
		
		this.connection.setAutoCommit (true);
		this.multipleStatement.close ( );
		this.multipleStatement = null;
		this.connection.close ( );
		
		super.finalize ( );
	}
}
