package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

public interface InsertIntoDB {

	public abstract PreparedStatement ServicesScheduling(Connection connection, String name, BigDecimal service_id, String status, String info, boolean periodic, Timestamp nonperiodicDate,
			String periodicInterval, int periodicDays) throws SQLException;

	public abstract PreparedStatement Repository(Connection connection, String name, String url, String oaiUrl, String owner, String ownerEmail, Integer harvestAmount,
			Integer harvestPause, boolean listRecords, boolean testData, boolean active) throws SQLException;

	public abstract PreparedStatement Repository(Connection connection, String name, String url, String oaiUrl, Integer harvestAmount, Integer harvestPause, boolean listRecords,
			boolean testData, boolean active) throws SQLException;

	public abstract PreparedStatement UsageData_Overall(Connection connection, BigDecimal object_id, BigDecimal metrics_id, long counter, Date last_update) throws SQLException;

	public abstract PreparedStatement UsageData_Months(Connection connection, BigDecimal object_id, BigDecimal metrics_id, long counter, Date counted_for_date) throws SQLException;

	public abstract PreparedStatement InterpolatedDDCClassification(Connection connection, BigDecimal object_id, String ddcValue, BigDecimal percentage) throws SQLException;

	public abstract PreparedStatement DuplicatePossibilities(Connection connection, BigDecimal object_id, BigDecimal duplicate_id, BigDecimal percentage, BigDecimal reverse_percentage) throws SQLException;

	public abstract PreparedStatement LoginData(Connection connection, String name, String password, String email) throws SQLException;

	public abstract PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id, String inserttime, boolean urgent, boolean complete) throws SQLException;

	public abstract PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id, String mimeformat, String link) throws SQLException;

	public abstract PreparedStatement OtherClassification(Connection connection, BigDecimal object_id, BigDecimal other_id) throws SQLException;

	public abstract PreparedStatement OtherCategories(Connection connection, String category) throws SQLException;

	public abstract PreparedStatement DINISetClassification(Connection connection, BigDecimal object_id, BigDecimal dini_set_id) throws SQLException;

	public abstract PreparedStatement DINISetClassification(Connection connection, BigDecimal object_id, BigDecimal dini_set_id, boolean generated) throws SQLException;
	
	public abstract PreparedStatement DNBClassification(Connection connection, BigDecimal object_id, String category) throws SQLException;

	public abstract PreparedStatement DDCClassification(Connection connection, BigDecimal object_id, String ddcValue) throws SQLException;

	public abstract PreparedStatement Object2Iso639Language(Connection connection, BigDecimal object_id, BigDecimal language_id, int number, boolean generated) throws SQLException;

	public abstract PreparedStatement Object2Language(Connection connection, BigDecimal object_id, BigDecimal language_id, int number) throws SQLException;

	public abstract PreparedStatement Iso639Language(Connection connection, String iso639language) throws SQLException;

	public abstract PreparedStatement Language(Connection connection, String language) throws SQLException;

	public abstract PreparedStatement Object2Keyword(Connection connection, BigDecimal object_id, BigDecimal keyword_id) throws SQLException;

	public abstract PreparedStatement Keyword(Connection connection, String keyword, String language) throws SQLException;

	public abstract PreparedStatement Object2Editor(Connection connection, BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;

	public abstract PreparedStatement Object2Author(Connection connection, BigDecimal object_id, BigDecimal person_id, int number) throws SQLException;

	public abstract PreparedStatement Person(Connection connection, String firstname, String lastname, String title, String institution, String email) throws SQLException;

	public abstract PreparedStatement Publisher(Connection connection, BigDecimal object_id, int number, String name) throws SQLException;

	public abstract PreparedStatement TypeValue(Connection connection, BigDecimal object_id, String typeValue) throws SQLException;

	public abstract PreparedStatement Description(Connection connection, BigDecimal object_id, int number, String description) throws SQLException;

	public abstract PreparedStatement Identifier(Connection connection, BigDecimal object_id, int number, String identifier) throws SQLException;

	public abstract PreparedStatement Format(Connection connection, BigDecimal object_id, int number, String schema_f) throws SQLException;

	public abstract PreparedStatement DateValue(Connection connection, BigDecimal object_id, int number, Date extract_datestamp, String origValue) throws SQLException;

	public abstract PreparedStatement Title(Connection connection, BigDecimal object_id, String qualifier, String title, String lang) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement RawRecordData(Connection connection, BigDecimal object_id, Date repository_timestamp, String data, String metaDataFormat) throws SQLException;

	public abstract PreparedStatement Object(Connection connection, BigDecimal repository_id, Date harvested, Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter)
			throws SQLException;

    public abstract PreparedStatement DDCClassification(Connection connection, BigDecimal object_id, Set<String> categories, boolean generated) throws SQLException;
    
    public abstract PreparedStatement DDCCategory (Connection connection, String category, String name, String enName) throws SQLException ;
 
    public abstract PreparedStatement DDCClassification(Connection connection, BigDecimal object_id, String category, boolean generated) throws SQLException;
}