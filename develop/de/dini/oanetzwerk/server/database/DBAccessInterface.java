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
 * @author Michael Kühn
 *
 */

public interface DBAccessInterface {

	/**
	 * 
	 */
	
	public abstract void createConnection ( );

	/**
	 * 
	 */
	
	public abstract void closeConnection ( );

	/**
	 * @return
	 */
	
	public abstract Connection getConnetion ( );
	
	/**
	 * @param ac
	 */
	
	public abstract void setAutoCom (boolean ac);
	
	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public abstract ResultSet selectObjectEntryId (String repositoryID, String externalOID);

	/**
	 * @param string
	 * @param string2
	 */
	
	public abstract ResultSet selectRawRecordData (BigDecimal object_id, Date datestamp);

	/**
	 * @param string
	 */
	
	public abstract ResultSet selectRawRecordData (BigDecimal object_id);

	/**
	 * @param metaDataFormat 
	 * @param string
	 * @param string2
	 * @param data
	 */
	
	public abstract String insertRawRecordData (BigDecimal object_id, Date datestamp,
			String blobbb, String metaDataFormat);

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 */
	
	public abstract ResultSet insertObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier);

	/**
	 * @param string
	 * @return
	 */
	public abstract ResultSet getObject (int oid);

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 */
	
	public abstract ResultSet updateObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier);

	/**
	 * @return
	 */
	
	public abstract ResultSet selectService (BigDecimal service_id);
	
	/**
	 * @return
	 */
	
	public abstract ResultSet selectService (String name);
	
	/**
	 * @param bigDecimal
	 * @return
	 */
	
	public abstract ResultSet selectServicesOrder (BigDecimal bigDecimal);
	
	/**
	 * @param bigDecimal
	 * @param bigDecimal2
	 * @return
	 */
	
	public abstract ResultSet selectWorkflow (BigDecimal bigDecimal,
			BigDecimal bigDecimal2);
	
	/**
	 * @param object_id
	 * @param time
	 * @param service_id
	 * @return
	 */
	
	public abstract ResultSet insertWorkflowDBEntry (BigDecimal object_id,
			Date time, BigDecimal service_id);
	
	/**
	 * @param object_id
	 * @param qualifier
	 * @param title
	 * @param lang
	 * @return
	 */
	
	public abstract ResultSet insertTitle (BigDecimal object_id, String qualifier,
			String title, String lang);

	/**
	 * @return
	 */
	public abstract boolean commit ( );

	/**
	 * @return
	 */
	public abstract boolean rollback ( );

	/**
	 * @param object_id
	 * @param number
	 * @param extract_datestamp
	 */
	public abstract void insertDateValue (BigDecimal object_id, int number,
			Date extract_datestamp);

	/**
	 * @param object_id
	 * @param number
	 * @param schema_f
	 */
	public abstract void insertFormat (BigDecimal object_id, int number,
			String schema_f);

	/**
	 * @param object_id
	 * @param number
	 * @param identifier
	 */
	public abstract void insertIdentifier (BigDecimal object_id, int number,
			String identifier) throws SQLException;

	public abstract void insertDescription (BigDecimal object_id, int number,
			String description) throws SQLException;

	public abstract void insertPublisher (BigDecimal object_id, int number,
			String name) throws SQLException;
	
	public abstract void insertTypeValue (BigDecimal object_id, 
			String value) throws SQLException;
	
	public abstract void insertPerson (String firstname, String lastname, String title, String institution, String email) throws SQLException;
	
	public abstract ResultSet selectLatestPerson(String firstname, String lastname) throws SQLException;
	
	public abstract void insertObject2Author(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public abstract void insertObject2Editor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public abstract void insertObject2Contributor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	
	public abstract void insertKeyword (String keyword, String lang) throws SQLException;
	
	public abstract ResultSet selectLatestKeyword(String keyword, String lang) throws SQLException;
	
	public abstract void insertObject2Keyword (BigDecimal object_id, BigDecimal keyword_id) throws SQLException;
	
	public abstract ResultSet selectLanguageByName(String language) throws SQLException;
	
	public abstract void insertLanguage(String language) throws SQLException;
	
	public abstract void insertObject2Language(BigDecimal object_id, BigDecimal language_id, int number) throws SQLException;
	
//	public abstract ResultSet selectDDCClassificationByDecimal(String decimal) throws SQLException;
	
	public abstract ResultSet selectTitle(BigDecimal oid);
	
	public abstract ResultSet selectAuthors(BigDecimal oid);
	
	public abstract ResultSet selectEditors(BigDecimal oid);
	
	public abstract ResultSet selectContributors(BigDecimal oid);
	
	public abstract ResultSet selectDescription(BigDecimal oid);
	
	public abstract ResultSet selectIdentifier(BigDecimal oid);
	
	public abstract ResultSet selectFormat(BigDecimal oid);
	
	public abstract ResultSet selectDateValues(BigDecimal oid);
	
	public abstract ResultSet selectTypeValue(BigDecimal oid);
	
	public abstract ResultSet selectPublisher(BigDecimal oid);
	
	public abstract ResultSet selectDDCClassification(BigDecimal oid);
	
	public abstract ResultSet selectDNBClassification(BigDecimal oid);
	
	public abstract ResultSet selectDINISetClassification(BigDecimal oid);
	
	public abstract ResultSet selectOtherClassification(BigDecimal oid);
	
	public abstract ResultSet selectKeywords(BigDecimal oid);
	
	public abstract ResultSet selectLanguages(BigDecimal oid);
	
	
//	public abstract void deleteTitles(BigDecimal object_id) throws SQLException;
	
//	public abstract void deleteDateValues(BigDecimal object_id) throws SQLException;
	
//	public abstract void deleteFormats(BigDecimal object_id) throws SQLException;
	
//	public abstract void deletePublishers(BigDecimal object_id) throws SQLException;
	
}
