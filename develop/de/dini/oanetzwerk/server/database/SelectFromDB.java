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
	 * @throws SQLException 
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID, Date repository_timestamp) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = ?");
		preparedstmt.setBigDecimal (1, internalOID);
		preparedstmt.setDate (2, repository_timestamp);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param internalOID
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = (SELECT max(rdmax.repository_timestamp) FROM dbo.RawData rdmax WHERE rdmax.object_id = ?)");
		preparedstmt.setBigDecimal (1, internalOID);
		preparedstmt.setBigDecimal (2, internalOID);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Services (Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE name = ?");
		preparedstmt.setString (1, name);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param objectEntryID
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Services (Connection connection,
			BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE service_id = ?");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement ServicesOrder (Connection connection,
			BigDecimal service_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT predecessor_id FROM dbo.ServicesOrder WHERE service_id = ?");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement WorkflowDB (Connection connection,
			BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? " + 
				"WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) " +
				"OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");

		preparedstmt.setBigDecimal (1, service_id);

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

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT workflow_id FROM dbo.WorkflowDB WHERE object_id = ? AND time = ? AND service_id = ?");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, time);
		preparedstmt.setBigDecimal (3, service_id);
		
		return preparedstmt;
	}
}
