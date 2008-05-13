/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Michael K&uuml;hn
 *
 */

interface DBSelectInterface {
	
	/**
	 * @param repositoryID
	 * @param externalOID
	 * @return
	 * @throws SQLException
	 */
	ResultSet ObjectEntryId (BigDecimal repositoryID, String externalOID) throws SQLException;
	
	/**
	 * @param internalOID
	 * @param datestamp
	 * @return
	 * @throws SQLException
	 */
	ResultSet RawRecordData (BigDecimal internalOID, Date datestamp) throws SQLException;
	
	/**
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet Title (BigDecimal object_id) throws SQLException;
	
	/**
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet Authors (BigDecimal object_id) throws SQLException;
	
	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Editors (BigDecimal oid) throws SQLException;
	
	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Contributors (BigDecimal oid) throws SQLException;

	/**
	 * @param object_id
	 * @return
	 */
	
	ResultSet Format (BigDecimal object_id) throws SQLException;

	/**
	 * @param object_id
	 * @return
	 */
	ResultSet Identifier (BigDecimal object_id) throws SQLException;
	
	/**
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet Description (BigDecimal object_id) throws SQLException;

	/**
	 * @param oid
	 * @return
	 */
	ResultSet DateValues (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 */
	ResultSet TypeValue (BigDecimal oid) throws SQLException;
	
	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Publisher(BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet DDCClassification (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet DNBClassification (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet DINISetClassification (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet OtherClassification (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Keywords (BigDecimal oid) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Languages (BigDecimal oid) throws SQLException;

	/**
	 * @param firstname
	 * @param lastname
	 * @return
	 * @throws SQLException
	 */
	ResultSet LatestPerson (String firstname, String lastname) throws SQLException;

	/**
	 * @param keyword
	 * @param lang
	 * @return
	 * @throws SQLException
	 */
	ResultSet LatestKeyword (String keyword, String lang) throws SQLException;

	/**
	 * @param language
	 * @return
	 * @throws SQLException
	 */
	ResultSet LanguageByName (String language) throws SQLException;

	/**
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	ResultSet LatestOtherCategories (String name) throws SQLException;

	/**
	 * @param categorie
	 * @return
	 * @throws SQLException
	 */
	ResultSet DDCCategoriesByCategorie (String categorie) throws SQLException;

	/**
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	ResultSet DNBCategoriesByCategorie (String name) throws SQLException;

	/**
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	ResultSet Object (BigDecimal oid) throws SQLException;

	/**
	 * @param repositoryID
	 * @return
	 * @throws SQLException
	 */
	ResultSet Repository (BigDecimal repositoryID) throws SQLException;

	/**
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet Service (BigDecimal service_id) throws SQLException;

	/**
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	ResultSet Service (String name) throws SQLException;

	/**
	 * @param predecessor_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet ServicesOrder (BigDecimal predecessor_id) throws SQLException;

	/**
	 * @param predecessor_id
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	ResultSet Workflow (BigDecimal predecessor_id, BigDecimal service_id)
			throws SQLException;

	/**
	 * @param conn
	 */
	void prepareConnection (Connection conn);

	/**
	 * @throws SQLException
	 */
	void closeStatement ( ) throws SQLException;
}
