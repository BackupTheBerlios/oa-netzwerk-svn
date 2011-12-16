package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface DeleteFromDB {

	public abstract PreparedStatement AggregatorMetadata(Connection connection, BigDecimal object_id) throws SQLException;
	public abstract PreparedStatement OAIExportCache(Connection connection, BigDecimal object_id) throws SQLException;
	public abstract PreparedStatement Worklist(Connection connection, BigDecimal object_id) throws SQLException;
	public abstract PreparedStatement Repositories(Connection connection, BigDecimal repository_id) throws SQLException;

	public abstract PreparedStatement UsageData_ALL_Overall(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement UsageData_ALL_Months(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement UsageData_Overall(Connection connection, BigDecimal object_id, BigDecimal metrics_id) throws SQLException;

	public abstract PreparedStatement UsageData_Months(Connection connection, BigDecimal object_id, BigDecimal metrics_id, Date counted_for_date) throws SQLException;

	public abstract PreparedStatement Interpolated_DDC_Classification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement LoginData(Connection connection, String name) throws SQLException;

	public abstract PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement RawData(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, Timestamp time, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DuplicatePossibilities(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Other_Categories(Connection connection) throws SQLException;

	public abstract PreparedStatement Iso639LanguagesWithoutReference(Connection connection) throws SQLException;

	public abstract PreparedStatement LanguagesWithoutReference(Connection connection) throws SQLException;

	public abstract PreparedStatement KeywordsWithoutReference(Connection connection) throws SQLException;

	public abstract PreparedStatement PersonWithoutReference(Connection connection) throws SQLException;

	public abstract PreparedStatement DINI_Set_Classification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DNB_Classification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DDC_Classification(Connection connection, BigDecimal object_id, boolean deleteGeneratedOnly) throws SQLException;

	public abstract PreparedStatement Other_Classification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object2Keywords(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object2Iso639Language(Connection connection, BigDecimal object_id, boolean deleteGeneratedOnly) throws SQLException;

	public abstract PreparedStatement Object2Iso639Language(Connection connection, BigDecimal object_id, BigDecimal languageId, boolean deleteGeneratedOnly) throws SQLException;
	
	public abstract PreparedStatement Object2Language(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object2Contributor(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object2Editor(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Object2Author(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Publishers(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Titles(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement TypeValue(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Identifiers(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Formats(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DateValues(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Description(Connection connection, BigDecimal object_id) throws SQLException;
	
	public abstract PreparedStatement ServicesScheduling(Connection connection, BigDecimal jobId) throws SQLException;
	
	public abstract PreparedStatement DDC_Classification(Connection connection, BigDecimal object_id, String category, boolean deleteGeneratedOnly) throws SQLException;

	public abstract PreparedStatement Repository_Sets(Connection connection, BigDecimal repository_id) throws SQLException;
	public abstract PreparedStatement DeleteFromTableByField(Connection connection, String table, String field, String joinTable, String joinTableField, String joinTableFilterField, BigDecimal joinTableParam) throws SQLException;
	public abstract PreparedStatement PersonAsBatch(Connection connection, int batchSize) throws SQLException;
}