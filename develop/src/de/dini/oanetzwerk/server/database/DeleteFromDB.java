/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Manuel Klatt-Kafemann
 * @author Michael K&uuml;hn
 */

public class DeleteFromDB {
	
	private static Logger logger = Logger.getLogger (DeleteFromDB.class);
	
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
	
	public static PreparedStatement DateValues (Connection connection,	BigDecimal object_id) throws SQLException {

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
	
	public static PreparedStatement Object2Iso639Language (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM  dbo.Object2Iso639Language WHERE object_id=?");
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
	
	public static PreparedStatement LanguagesWithoutReference (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Language WHERE "
				+ "(language_id NOT IN (SELECT language_id FROM dbo.Object2Language GROUP BY language_id) )");
		
		return preparedstmt;
	}
	
	public static PreparedStatement Iso639LanguagesWithoutReference (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Iso639Language WHERE "
				+ "(language_id NOT IN (SELECT language_id FROM dbo.Object2Iso639Language GROUP BY language_id) )");
		
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

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement DuplicatePossibilities (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.DuplicatePossibilities WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}
	

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement WorkflowDB (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.WorkflowDB WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
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
	
	public static PreparedStatement WorkflowDB (Connection connection, BigDecimal object_id, Date time, BigDecimal service_id) throws SQLException {

		if (logger.isDebugEnabled ( )) {
			logger.debug ("DELETE FROM dbo.WorkflowDB FROM dbo.WorkflowDB w JOIN dbo.ServicesOrder s ON w.service_id = s.predecessor_id WHERE w.object_id = " + object_id +
					" and w.time <= " + time + " and s.service_id= " + service_id);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.WorkflowDB FROM dbo.WorkflowDB w JOIN dbo.ServicesOrder s ON w.service_id = s.predecessor_id WHERE w.object_id = ? and w.time <=? and s.service_id=?");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate(2, time);
		preparedstmt.setBigDecimal (3, service_id);
		
		logger.debug("DELTE-Statement: " + preparedstmt.toString());
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement RawData (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.RawData WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement Object (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.Object where object_id = ? and testdata = 1");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement FullTextLinks (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.FullTextLinks where object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement ServiceNotify (Connection connection, BigDecimal service_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.ServiceNotify WHERE service_id = ?");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement LoginData (Connection connection, String name) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("DELETE FROM dbo.LoginData WHERE name = ?");
		preparedstmt.setString (1, name);
		
		return preparedstmt;
	}
}
