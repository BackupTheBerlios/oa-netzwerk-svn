/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author Michael K&uuml;hn
 *
 */

public class DeleteFromDB {

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Description (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Description WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DateValues (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.DateValues WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;

	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Formats (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Format WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Identifiers (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Identifier WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement TypeValue (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.TypeValue WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Titles (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Titles WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Publishers (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Publisher WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Author (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Author WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Editor (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Editor WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Contributor (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Contributor WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Language (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Language WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Keywords (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Keywords WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Other_Classification (
			Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Other_Classification WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DDC_Classification (Connection connection,
			BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.DDC_Classification WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DNB_Classification (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.DNB_Classification WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DINI_Set_Classification (
			Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.DINI_Set_Classification WHERE object_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement PersonWithoutReference (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Person WHERE "
				+ "(person_id NOT IN (SELECT person_id FROM dbo.Object2Author GROUP BY person_id) "
				+ "AND person_id NOT IN (SELECT person_id FROM dbo.Object2Editor GROUP BY person_id) "
				+ "AND person_id NOT IN (SELECT person_id FROM dbo.Object2Contributor GROUP BY person_id))");
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement KeywordsWithoutReference (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Keywords WHERE "
				+ "(keyword_id NOT IN (SELECT keyword_id FROM dbo.Object2Keywords GROUP BY keyword_id) )");
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Other_Categories (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Other_Categories WHERE " 
				+"(other_id NOT IN (SELECT other_id FROM dbo.Other_Classification GROUP BY other_id) )");
				return preparedstmt;
	}

}