package de.dini.oanetzwerk.server.database.postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DeleteFromDB;

public class DeleteFromDBPostgres implements DeleteFromDB {
	private static Logger logger = Logger.getLogger(DeleteFromDBPostgres.class);

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Description(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Description\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement DateValues(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"DateValues\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Formats(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Format\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Identifiers(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Identifier\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement TypeValue(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"TypeValue\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Titles(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Titles\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Publishers(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Publisher\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Author(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM  \"Object2Author\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Editor(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object2Editor\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Contributor(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object2Contributor\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Language(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object2Language\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Iso639Language(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object2Iso639Language\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object2Keywords(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object2Keywords\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Other_Classification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Other_Classification\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement DDC_Classification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"DDC_Classification\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement DNB_Classification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"DNB_Classification\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement DINI_Set_Classification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM  \"DINI_Set_Classification\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement PersonWithoutReference(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"DELETE FROM \"Person\" " +
			"WHERE " +
			"	(	person_id NOT IN (SELECT person_id FROM \"Object2Author\" GROUP BY person_id) " +
			"AND " +
			"		person_id NOT IN (SELECT person_id FROM \"Object2Editor\" GROUP BY person_id) " +
			"AND 	person_id NOT IN (SELECT person_id FROM \"Object2Contributor\" GROUP BY person_id)" +
			"	)");

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement KeywordsWithoutReference(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"DELETE FROM \"Keywords\" " +
			"WHERE (" +
			"	keyword_id NOT IN (SELECT keyword_id FROM \"Object2Keywords\" GROUP BY keyword_id) " +
			")");

		return preparedstmt;
	}

	public PreparedStatement LanguagesWithoutReference(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Language\" WHERE "
				+ "(language_id NOT IN (SELECT language_id FROM \"Object2Language\" GROUP BY language_id) )");

		return preparedstmt;
	}

	public PreparedStatement Iso639LanguagesWithoutReference(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Iso639Language\" WHERE "
				+ "(language_id NOT IN (SELECT language_id FROM \"Object2Iso639Language\" GROUP BY language_id) )");

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Other_Categories(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Other_Categories\" WHERE "
				+ "(other_id NOT IN (SELECT other_id FROM \"Other_Classification\" GROUP BY other_id) )");

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement DuplicatePossibilities(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"DuplicatePossibilities\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"WorkflowDB\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

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

	public PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, Date time, BigDecimal service_id)
			throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("DELETE FROM \"WorkflowDB\" FROM dbo.WorkflowDB w JOIN dbo.ServicesOrder s ON w.service_id = s.predecessor_id WHERE w.object_id = "
					+ object_id + " and w.time <= " + time + " and s.service_id= " + service_id);
		}
		// TODO: hier nachhaken!!
		PreparedStatement preparedstmt = connection
				.prepareStatement(
					"DELETE FROM \"WorkflowDB\" w " +
					"JOIN dbo.ServicesOrder s ON w.service_id = s.predecessor_id " +
					"WHERE w.object_id = ? and w.time <=? and s.service_id=?");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setDate(2, time);
		preparedstmt.setBigDecimal(3, service_id);

		logger.debug("DELTE-Statement: " + preparedstmt.toString());

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement RawData(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"RawData\" WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement Object(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Object\" where object_id = ? and testdata = 1");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"FullTextLinks\" where object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"ServiceNotify\" WHERE service_id = ?");
		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param name
	 * @return
	 * @throws SQLException
	 */

	public PreparedStatement LoginData(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"LoginData\" WHERE name = ?");
		preparedstmt.setString(1, name);

		return preparedstmt;
	}

	public PreparedStatement Interpolated_DDC_Classification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Interpolated_DDC_Classification\" WHERE object_id=?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public PreparedStatement UsageData_Months(Connection connection, BigDecimal object_id, BigDecimal metrics_id,
			Date counted_for_date) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("DELETE FROM \"UsageData_Months\" WHERE object_id=? AND metrics_id=? AND counted_for_date=?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, metrics_id);
		preparedstmt.setDate(3, counted_for_date);

		return preparedstmt;
	}

	public PreparedStatement UsageData_Overall(Connection connection, BigDecimal object_id, BigDecimal metrics_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("DELETE FROM \"UsageData_Overall\" WHERE object_id=? AND metrics_id=?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, metrics_id);

		return preparedstmt;
	}

	public PreparedStatement UsageData_ALL_Months(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"UsageData_Months\" WHERE object_id=?");

		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public PreparedStatement UsageData_ALL_Overall(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"UsageData_Overall\" WHERE object_id=?");

		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public PreparedStatement Repositories(Connection connection, Long repository_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("DELETE FROM \"Repositories\" WHERE repository_id = ?");
		preparedstmt.setLong(1, repository_id);

		return preparedstmt;
	}
}
