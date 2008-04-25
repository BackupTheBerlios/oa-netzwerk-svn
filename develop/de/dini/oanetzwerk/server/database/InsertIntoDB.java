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

public class InsertIntoDB {

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

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter) VALUES (?, ?, ?, ?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, repository_id);
		preparedstmt.setDate (2, harvested);
		preparedstmt.setDate (3, repository_datestamp);
		preparedstmt.setString (4, repository_identifier);
		preparedstmt.setBoolean (5, testdata);
		preparedstmt.setInt (6, failureCounter);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param repository_timestamp
	 * @param data
	 * @param metaDataFormat
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal object_id, Date repository_timestamp, String data,
			String metaDataFormat) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.RawData (object_id, repository_timestamp, data, MetaDataFormat) VALUES (?, ?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, repository_timestamp);
		preparedstmt.setString (3, data);
		preparedstmt.setString (4, metaDataFormat);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param time
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement WorkflowDB (Connection connection,
			BigDecimal object_id, Date time, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.WorkflowDB (object_id, time, service_id) VALUES (?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, time);
		preparedstmt.setBigDecimal (3, service_id);
		
		return preparedstmt;
	}
}
