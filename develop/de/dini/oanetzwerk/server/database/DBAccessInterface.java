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
	 * @throws SQLException 
	 */
	public abstract ResultSet selectObjectEntryId (BigDecimal repositoryID, String externalOID) throws SQLException;

	/**
	 * @param string
	 * @param string2
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectRawRecordData (BigDecimal object_id, Date datestamp) throws SQLException;

	/**
	 * @param string
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectRawRecordData (BigDecimal object_id) throws SQLException;

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
	 * @param failureCounter 
	 * @param testdata 
	 * @return
	 */
	
	public abstract ResultSet insertObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter);

	/**
	 * @param oid
	 * @return
	 */
	public abstract ResultSet getObject (BigDecimal oid) throws SQLException;

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 */
	
	public abstract ResultSet updateObject (BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter);

	/**
	 * @return
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectService (BigDecimal service_id) throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectService (String name) throws SQLException;
	
	/**
	 * @param bigDecimal
	 * @return
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectServicesOrder (BigDecimal bigDecimal) throws SQLException;
	
	/**
	 * @param bigDecimal
	 * @param bigDecimal2
	 * @return
	 * @throws SQLException 
	 */
	
	public abstract ResultSet selectWorkflow (BigDecimal bigDecimal,
			BigDecimal bigDecimal2) throws SQLException;
	
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

	public abstract void insertOtherCategories(String name) throws SQLException;
	
	public abstract void insertOtherClassification(BigDecimal object_id, BigDecimal other_id) throws SQLException;
	
	public abstract ResultSet selectLatestOtherCategories(String name) throws SQLException;
	
	public abstract ResultSet selectDDCCategoriesByCategorie(String categorie) throws SQLException;
	
	public abstract ResultSet selectDNBCategoriesByCategorie(String name) throws SQLException;
	
	public abstract ResultSet selectDINISetCategoriesByName(String name) throws SQLException;
	
	public abstract void insertDDCClassification (BigDecimal object_id, String ddcValue) throws SQLException;
	
//	public abstract void insertDNBClassification (BigDecimal object_id, BigDecimal DNB_Categorie) throws SQLException;
	public abstract void insertDNBClassification (BigDecimal object_id, String DNB_Categorie) throws SQLException;
	
	public abstract void insertDINISetClassification (BigDecimal object_id, BigDecimal DINI_set_id) throws SQLException;
		
	public abstract ResultSet selectTitle(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectAuthors(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectEditors(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectContributors(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectDescription(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectIdentifier(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectFormat(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectDateValues(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectTypeValue(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectPublisher(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectDDCClassification(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectDNBClassification(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectDINISetClassification(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectOtherClassification(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectKeywords(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet selectLanguages(BigDecimal oid) throws SQLException;
	
	public abstract ResultSet getRepository (BigDecimal repositoryID) throws SQLException;
	
	public abstract void deleteTitles(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteDateValues(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteFormats(BigDecimal object_id) throws SQLException;
	
	public abstract void deletePublishers(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteIdentifiers(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteDescription(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteTypeValue(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteObject2Author(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteObject2Editor(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteObject2Contributor(BigDecimal object_id) throws SQLException;
	
	public abstract void deletePersonWithoutReference() throws SQLException;
	
	public abstract void deleteObject2Language(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteDDC_Classification(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteDNB_Classification(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteDINI_Set_Classification(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteRawData(BigDecimal object_id) throws SQLException;

	public abstract void deleteDuplicatePossibilities(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteObject2Keywords(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteKeywordsWithoutReference() throws SQLException;
	
	public abstract void deleteOther_Classification(BigDecimal object_id) throws SQLException;
	
	public abstract void deleteOther_Categories() throws SQLException;
}
