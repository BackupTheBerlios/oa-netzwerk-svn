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
@Deprecated
public interface DBAccessInterface {

	/**
	 * 
	 */
	@Deprecated
	public abstract void createConnection ( );

	/**
	 * 
	 */
	@Deprecated
	public void closeStatement ( ) throws SQLException;
	@Deprecated
	public abstract void closeConnection ( );

	/**
	 * @return
	 */
	@Deprecated
	public abstract Connection getConnetion ( );
	
	/**
	 * @param ac
	 */
	@Deprecated
	public abstract void setAutoCom (boolean ac);
	
	/**
	 * @param string
	 * @param string2
	 * @return
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectObjectEntryId (BigDecimal repositoryID, String externalOID) throws SQLException;

	/**
	 * @param string
	 * @param string2
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectRawRecordData (BigDecimal object_id, Date datestamp) throws SQLException;

	/**
	 * @param string
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectRawRecordData (BigDecimal object_id) throws SQLException;

	/**
	 * @param metaDataFormat 
	 * @param string
	 * @param string2
	 * @param data
	 */
	@Deprecated
	public abstract String insertRawRecordData (BigDecimal object_id, Date datestamp,
			String blobbb, String metaDataFormat);

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @param failureCounter 
	 * @param testdata 
	 * @return
	 */
	@Deprecated
	public abstract ResultSet insertObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter);

	/**
	 * @param oid
	 * @return
	 */
	@Deprecated
	public abstract ResultSet getObject (BigDecimal oid) throws SQLException;

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 */
	@Deprecated
	public abstract ResultSet updateObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter);

	/**
	 * @return
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectService (BigDecimal service_id) throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectService (String name) throws SQLException;
	
	/**
	 * @param bigDecimal
	 * @return
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectServicesOrder (BigDecimal bigDecimal) throws SQLException;
	
	/**
	 * @param bigDecimal
	 * @param bigDecimal2
	 * @return
	 * @throws SQLException 
	 */
	@Deprecated
	public abstract ResultSet selectWorkflow (BigDecimal bigDecimal,
			BigDecimal bigDecimal2) throws SQLException;
	
	/**
	 * @param object_id
	 * @param time
	 * @param service_id
	 * @return
	 */
	@Deprecated
	public abstract ResultSet insertWorkflowDBEntry (BigDecimal object_id,
			Date time, BigDecimal service_id);
	
	/**
	 * @param object_id
	 * @param qualifier
	 * @param title
	 * @param lang
	 * @return
	 */
	@Deprecated
	public abstract ResultSet insertTitle (BigDecimal object_id, String qualifier,
			String title, String lang);

	/**
	 * @return
	 */
	@Deprecated
	public abstract boolean commit ( );

	/**
	 * @return
	 */
	@Deprecated
	public abstract boolean rollback ( );

	/**
	 * @param object_id
	 * @param number
	 * @param extract_datestamp
	 */
	@Deprecated
	public abstract void insertDateValue (BigDecimal object_id, int number,
			Date extract_datestamp);

	/**
	 * @param object_id
	 * @param number
	 * @param schema_f
	 */
	@Deprecated
	public abstract void insertFormat (BigDecimal object_id, int number,
			String schema_f);

	/**
	 * @param object_id
	 * @param number
	 * @param identifier
	 */
	@Deprecated
	public abstract void insertIdentifier (BigDecimal object_id, int number,
			String identifier) throws SQLException;
	@Deprecated
	public abstract void insertDescription (BigDecimal object_id, int number,
			String description) throws SQLException;
	@Deprecated
	public abstract void insertPublisher (BigDecimal object_id, int number,
			String name) throws SQLException;
	@Deprecated
	public abstract void insertTypeValue (BigDecimal object_id, 
			String value) throws SQLException;
	@Deprecated
	public abstract void insertPerson (String firstname, String lastname, String title, String institution, String email) throws SQLException;
	@Deprecated
	public abstract ResultSet selectLatestPerson(String firstname, String lastname) throws SQLException;
	@Deprecated
	public abstract void insertObject2Author(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	@Deprecated
	public abstract void insertObject2Editor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	@Deprecated
	public abstract void insertObject2Contributor(BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;
	@Deprecated
	public abstract void insertKeyword (String keyword, String lang) throws SQLException;
	@Deprecated
	public abstract ResultSet selectLatestKeyword(String keyword, String lang) throws SQLException;
	@Deprecated
	public abstract void insertObject2Keyword (BigDecimal object_id, BigDecimal keyword_id) throws SQLException;
	@Deprecated
	public abstract ResultSet selectLanguageByName(String language) throws SQLException;
	@Deprecated
	public abstract void insertLanguage(String language) throws SQLException;
	@Deprecated
	public abstract void insertObject2Language(BigDecimal object_id, BigDecimal language_id, int number) throws SQLException;
	@Deprecated
	public abstract void insertOtherCategories(String name) throws SQLException;
	@Deprecated
	public abstract void insertOtherClassification(BigDecimal object_id, BigDecimal other_id) throws SQLException;
	@Deprecated
	public abstract ResultSet selectLatestOtherCategories(String name) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDDCCategoriesByCategorie(String categorie) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDNBCategoriesByCategorie(String name) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDINISetCategoriesByName(String name) throws SQLException;
	@Deprecated
	public abstract int insertDDCClassification (BigDecimal object_id, String ddcValue) throws SQLException;
	@Deprecated
	public abstract void insertDNBClassification (BigDecimal object_id, String DNB_Categorie) throws SQLException;
	@Deprecated
	public abstract void insertDINISetClassification (BigDecimal object_id, BigDecimal DINI_set_id) throws SQLException;
	@Deprecated
	public abstract ResultSet selectTitle(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectAuthors(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectEditors(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectContributors(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDescription(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectIdentifier(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectFormat(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDateValues(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectTypeValue(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectPublisher(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDDCClassification(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDNBClassification(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectDINISetClassification(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectOtherClassification(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectKeywords(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet selectLanguages(BigDecimal oid) throws SQLException;
	@Deprecated
	public abstract ResultSet getRepository (BigDecimal repositoryID) throws SQLException;
	@Deprecated
	public abstract void deleteTitles(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDateValues(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteFormats(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deletePublishers(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteIdentifiers(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDescription(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteTypeValue(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteObject2Author(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteObject2Editor(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteObject2Contributor(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deletePersonWithoutReference() throws SQLException;
	@Deprecated
	public abstract void deleteObject2Language(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDDC_Classification(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDNB_Classification(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDINI_Set_Classification(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteRawData(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteDuplicatePossibilities(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteObject2Keywords(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteKeywordsWithoutReference() throws SQLException;
	@Deprecated
	public abstract void deleteOther_Classification(BigDecimal object_id) throws SQLException;
	@Deprecated
	public abstract void deleteOther_Categories() throws SQLException;
}
