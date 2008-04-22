/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author Michael K&uuml;hn
 *
 */

public class SelectFromDB {

	/**
	 * @param connection
	 * @param oid 
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Object (Connection connection, BigDecimal oid) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
		
		preparedstmt.setBigDecimal (1, oid);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @param testdata
	 * @param failureCounter
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object (Connection connection,
			BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
		
		preparedstmt.setBigDecimal (1, repository_id);
		preparedstmt.setString (2, repository_identifier);
		preparedstmt.setDate (3, repository_datestamp);
		
		return preparedstmt;
	}
}
