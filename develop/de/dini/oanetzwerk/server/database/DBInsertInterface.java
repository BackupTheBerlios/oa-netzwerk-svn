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

public interface DBInsertInterface {
	
	public String insertRawRecordData (BigDecimal internalOID, Date datestamp, String blobbb, String metaDataFormat) throws SQLException;
	
	public ResultSet insertObject (BigDecimal repository_id, Date harvested, Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter) throws SQLException;
	
	public ResultSet insertWorkflowDBEntry (BigDecimal object_id, Date time, BigDecimal service_id) throws SQLException;
	
	public ResultSet insertTitle (BigDecimal object_id, String qualifier, String title, String lang) throws SQLException;
	
	public void insertDateValue (BigDecimal object_id, int number, Date value) throws SQLException;
	
	public void insertFormat (BigDecimal object_id, int number, String schema_f) throws SQLException;
	
	public void insertIdentifier (BigDecimal object_id, int number, String identifier) throws SQLException;
	
	public void insertDescription (BigDecimal object_id, int number, String description) throws SQLException;
	
	public void insertPublisher (BigDecimal object_id, int number, String name) throws SQLException;
	
	public void insertTypeValue (BigDecimal object_id, String value) throws SQLException;
	
	public void insertPerson(String firstname, String lastname, String title, String institution, String email) throws SQLException;
	
	public void insertObject2Author(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public void insertObject2Editor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public void insertObject2Contributor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public void insertKeyword(String keyword, String lang) throws SQLException;
	
	public void insertObject2Keyword(BigDecimal object_id, BigDecimal keyword_id) throws SQLException;
	
	public void insertLanguage(String language) throws SQLException;
	
	public void insertObject2Language(BigDecimal object_id, BigDecimal language_id, int number) throws SQLException;
	
	public void insertOtherCategories(String name) throws SQLException;
	
	public void insertOtherClassification(BigDecimal object_id, BigDecimal other_id) throws SQLException;
	
	public int DDCClassification (BigDecimal object_id, String ddcValue) throws SQLException;
	
	public void insertDINISetClassification(BigDecimal object_id, BigDecimal DINI_set_id) throws SQLException;
	
	public void insertDNBClassification(BigDecimal object_id, String DNB_Categorie) throws SQLException;

	void prepareConnection (Connection conn);
	
	void closeStatement ( ) throws SQLException;
}
