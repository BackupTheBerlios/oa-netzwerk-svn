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
	
	public static PreparedStatement ObjectEntry (Connection connection, BigDecimal oid) throws SQLException {
		
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
	public static PreparedStatement ObjectEntry (Connection connection,
			BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
		
		preparedstmt.setBigDecimal (1, repository_id);
		preparedstmt.setString (2, repository_identifier);
		preparedstmt.setDate (3, repository_datestamp);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param bigDecimal
	 * @param string
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement ObjectEntryID (Connection connection,
			BigDecimal repositoryID, String externalOID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.repository_id = ? and o.repository_identifier = ?");
		
		preparedstmt.setBigDecimal (1, repositoryID);
		preparedstmt.setString (2, externalOID);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param repositoryID
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Repository (Connection connection,
			BigDecimal repositoryID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, url, oai_url, test_data, harvest_amount, harvest_pause FROM dbo.Repositories WHERE (repository_id = ?)");
		preparedstmt.setBigDecimal (1, repositoryID);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param internalOID
	 * @param repository_timestamp
	 * @return
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID, Date repository_timestamp) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param connection
	 * @param internalOID
	 * @return
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID) {

		// TODO Auto-generated method stub
		return null;
	}
}
