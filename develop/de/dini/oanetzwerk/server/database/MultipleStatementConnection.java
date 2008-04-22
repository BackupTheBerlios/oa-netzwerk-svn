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

public class MultipleStatementConnection implements StatementConnection {
	
	public final Connection connection;
	private PreparedStatement multipleStatement;
	
	/**
	 * @param connection
	 * @throws SQLException 
	 */
	
	protected MultipleStatementConnection (Connection dataSourceConnection) throws SQLException {

		this.connection = dataSourceConnection;
		this.connection.setAutoCommit (false);
		this.multipleStatement = null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#close()
	 */
	
	@Override
	public void close ( ) throws SQLException {

		this.connection.setAutoCommit (true);
		this.multipleStatement.close ( );
		this.multipleStatement = null;
		this.connection.close ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#execute()
	 */
	
	@Override
	public QueryResult execute ( ) throws SQLException {
		
		QueryResult result = new QueryResult ( );
		
		try {
			
			this.multipleStatement.execute ( );
			
		} catch (SQLException ex) {
			
			rollback ( );
			return result;
		}
		
		result.setResultSet (this.multipleStatement.getResultSet ( ));
		result.setUpdateCount (this.multipleStatement.getUpdateCount ( ));
		result.setWarning (this.multipleStatement.getWarnings ( ));
		
		return result;
	}
	
	public void commit ( ) throws SQLException {
		
		try {
			
			this.connection.commit ( );
			
		} catch (SQLException ex) {
			
			this.connection.rollback ( );
			ex.printStackTrace ( );	
		}
	}
	
	public void rollback ( ) throws SQLException {
		
		this.connection.rollback ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.database.StatementConnection#loadStatement(java.sql.PreparedStatement)
	 */
	
	@Override
	public boolean loadStatement (PreparedStatement pstmt) {

		this.multipleStatement = pstmt;
		
		return true;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize ( ) throws Throwable {
		
		this.connection.setAutoCommit (true);
		this.multipleStatement.close ( );
		this.multipleStatement = null;
		this.connection.close ( );
		
		super.finalize ( );
	}
}
