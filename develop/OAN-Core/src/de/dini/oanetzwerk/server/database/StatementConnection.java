/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author Michael K&uuml;hn
 *
 */

public interface StatementConnection {
	
	abstract public void close ( ) throws SQLException;
	
	abstract public boolean loadStatement (PreparedStatement pstmt);
	
	abstract public QueryResult execute ( ) throws SQLException;
}
