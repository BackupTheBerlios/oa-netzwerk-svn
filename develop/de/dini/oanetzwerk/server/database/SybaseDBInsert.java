/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


/**
 * @author Michael K&uuml;hn
 *
 */

class SybaseDBInsert implements DBInsertInterface {
	
	private static Connection connection = null;
	static Logger logger = Logger.getLogger (SybaseDBInsert.class);
	private PreparedStatement preparedstmt = null;
	
	@Override
	public void prepareConnection (Connection conn) {
		
		connection = conn;
		
		if (logger.isDebugEnabled ( ))
			
			try {
			
				logger.debug ("connection prepared: " + connection.getMetaData ( ));
				
			} catch (SQLException ex) {
				
			ex.printStackTrace();
		}
	}
	
	@Override
	public void closeStatement ( ) throws SQLException {
		
		if (preparedstmt != null) {
			
			preparedstmt.close ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Statement closed!");
		}
		
		preparedstmt = null;
	}
	
	/**
	 * @return 
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertDDCClassification(java.math.BigDecimal, java.lang.String)
	 */
	
	@Override
	public int DDCClassification (BigDecimal object_id, String ddcValue)
			throws SQLException {
		
		this.preparedstmt = connection.prepareStatement ("INSERT INTO dbo.DDC_Classification (object_id, DDC_Categorie) VALUES (?, ?)");
		this.preparedstmt.setBigDecimal (1, object_id);
		this.preparedstmt.setString (2, ddcValue);
			
		if (logger.isDebugEnabled ( ))
				logger.debug ("execute");
			
		return this.preparedstmt.executeUpdate ( );
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertDINISetClassification(java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public void insertDINISetClassification (BigDecimal object_id,
			BigDecimal DINI_set_id) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertDNBClassification(java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public void insertDNBClassification (BigDecimal object_id,
			String DNB_Categorie) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertDateValue(java.math.BigDecimal, int, java.sql.Date)
	 */
	@Override
	public void insertDateValue (BigDecimal object_id, int number, Date value)
			throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertDescription(java.math.BigDecimal, int, java.lang.String)
	 */
	@Override
	public void insertDescription (BigDecimal object_id, int number,
			String description) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertFormat(java.math.BigDecimal, int, java.lang.String)
	 */
	@Override
	public void insertFormat (BigDecimal object_id, int number, String schema_f)
			throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertIdentifier(java.math.BigDecimal, int, java.lang.String)
	 */
	@Override
	public void insertIdentifier (BigDecimal object_id, int number,
			String identifier) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertKeyword(java.lang.String, java.lang.String)
	 */
	@Override
	public void insertKeyword (String keyword, String lang) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertLanguage(java.lang.String)
	 */
	@Override
	public void insertLanguage (String language) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject(java.math.BigDecimal, java.sql.Date, java.sql.Date, java.lang.String, boolean, int)
	 */
	@Override
	public ResultSet insertObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter) throws SQLException {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject2Author(java.math.BigDecimal, java.math.BigDecimal, int)
	 */
	@Override
	public void insertObject2Author (BigDecimal object_id,
			BigDecimal person_id, int number) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject2Contributor(java.math.BigDecimal, java.math.BigDecimal, int)
	 */
	@Override
	public void insertObject2Contributor (BigDecimal object_id,
			BigDecimal person_id, int number) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject2Editor(java.math.BigDecimal, java.math.BigDecimal, int)
	 */
	@Override
	public void insertObject2Editor (BigDecimal object_id,
			BigDecimal person_id, int number) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject2Keyword(java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public void insertObject2Keyword (BigDecimal object_id,
			BigDecimal keyword_id) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertObject2Language(java.math.BigDecimal, java.math.BigDecimal, int)
	 */
	@Override
	public void insertObject2Language (BigDecimal object_id,
			BigDecimal language_id, int number) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertOtherCategories(java.lang.String)
	 */
	@Override
	public void insertOtherCategories (String name) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertOtherClassification(java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public void insertOtherClassification (BigDecimal object_id,
			BigDecimal other_id) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertPerson(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void insertPerson (String firstname, String lastname, String title,
			String institution, String email) throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertPublisher(java.math.BigDecimal, int, java.lang.String)
	 */
	@Override
	public void insertPublisher (BigDecimal object_id, int number, String name)
			throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertRawRecordData(java.math.BigDecimal, java.sql.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public String insertRawRecordData (BigDecimal internalOID, Date datestamp,
			String blobbb, String metaDataFormat) throws SQLException {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertTitle(java.math.BigDecimal, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ResultSet insertTitle (BigDecimal object_id, String qualifier,
			String title, String lang) throws SQLException {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertTypeValue(java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public void insertTypeValue (BigDecimal object_id, String value)
			throws SQLException {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.server.database.DBInsertInterface#insertWorkflowDBEntry(java.math.BigDecimal, java.sql.Date, java.math.BigDecimal)
	 */
	@Override
	public ResultSet insertWorkflowDBEntry (BigDecimal object_id, Date time,
			BigDecimal service_id) throws SQLException {

		// TODO Auto-generated method stub
		return null;
	}

}
