/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author Michael K&uuml;hn
 *
 */

public class SingleStatementConnection implements StatementConnection {
	
	public final Connection connection;
	private PreparedStatement singleStatement;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	protected SingleStatementConnection (Connection dataSourceConnection) throws SQLException {
		
		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (true);
		this.singleStatement = null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	@Override
	public boolean loadStatement (PreparedStatement pstmt) {
		
		if (this.singleStatement == null) {
			
			this.singleStatement = pstmt;
			
			return true;
			
		} else 
			return false;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	@Override
	public QueryResult execute ( ) throws SQLException {
		
		QueryResult result = new QueryResult ( );
		
		this.singleStatement.execute ( );
		
		result.setResultSet (this.singleStatement.getResultSet ( ));
		result.setUpdateCount (this.singleStatement.getUpdateCount ( ));
		result.setWarning (this.singleStatement.getWarnings ( ));
		
		return result;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	@Override
	public void close ( ) throws SQLException {
		
		this.singleStatement.close ( );
		this.singleStatement = null;
		this.connection.close ( );
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	
	@Override
	protected void finalize ( ) throws Throwable {
		
		this.connection.setAutoCommit (true);
		this.singleStatement.close ( );
		this.singleStatement = null;
		this.connection.close ( );
		
		super.finalize ( );
	}
}
