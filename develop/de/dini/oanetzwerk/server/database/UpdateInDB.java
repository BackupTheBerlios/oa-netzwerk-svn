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

public class UpdateInDB {

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

		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.Object SET harvested = ?, repository_datestamp = ?, testdata = ?, failure_counter = ? WHERE object_id = ?");
		preparedstmt.setDate (1, harvested);
		preparedstmt.setDate (2, repository_datestamp);
		preparedstmt.setBoolean (3, testdata);
		preparedstmt.setInt (4, failureCounter);
		preparedstmt.setBigDecimal (5, repository_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param data
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String data) throws SQLException {
		
		return PrecleanedData (connection, object_id, repositoryDateStamp, "oai_dc", data);
	}
	
	public static PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String metaDataFormat, String data) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.RawData SET precleaned_data = ? WHERE object_id = ? AND repository_timestamp = ? AND MetaDataFormat = ?");
		preparedstmt.setString (1, data);
		preparedstmt.setBigDecimal (2, object_id);
		preparedstmt.setDate(3, repositoryDateStamp);
		preparedstmt.setString(4, metaDataFormat);
		
		return preparedstmt;
	}
}
