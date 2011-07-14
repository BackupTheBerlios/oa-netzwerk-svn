package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface SelectFromDB {

	public abstract PreparedStatement ServicesScheduling(Connection connection, int jobId) throws SQLException;

	public abstract PreparedStatement ServicesScheduling(Connection connection) throws SQLException;

	public abstract PreparedStatement UsageData_Counter_ForOID(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement UsageData_Ranking(Connection connection) throws SQLException;

	public abstract PreparedStatement UsageDataOIDsForRepository(Connection connection, Integer repository_id) throws SQLException;

	public abstract PreparedStatement UsageDataOIDs(Connection connection) throws SQLException;

	public abstract PreparedStatement UsageData_Overall_ForMetricsName(Connection connection, BigDecimal object_id, String metrics_name) throws SQLException;

	public abstract PreparedStatement UsageData_Months_ListForMetricsName(Connection connection, BigDecimal object_id, String metrics_name) throws SQLException;

	public abstract PreparedStatement UsageData_Metrics_AllNames(Connection connection) throws SQLException;

	public abstract PreparedStatement UsageData_Metrics(Connection connection, String metrics_name) throws SQLException;

	public abstract PreparedStatement InterpolatedDDCClassification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement InterpolatedDDCClassification_withCategorie(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement RepositoryData(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement AllOIDsByDate(Connection connection, Date from, Date until, String set, BigInteger idOffset, int resultCount, boolean rowCountOnly)
			throws SQLException;

	public abstract PreparedStatement OAIListAll(Connection connection, String set, Date fromDate, Date untilDate, List<BigDecimal> ids) throws SQLException;

	public abstract PreparedStatement OAIListSetsbyID(Connection connection, String set, Date from, Date until, List<BigDecimal> ids) throws SQLException;

	public abstract PreparedStatement AllOIDsByDate(Connection connection, Date from, Date until, String set) throws SQLException;

	public abstract PreparedStatement OAIGetOldestDate(Connection connection) throws SQLException;

	public abstract PreparedStatement OAIListSets(Connection connection) throws SQLException;

	public abstract PreparedStatement DuplicateProbabilities(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DDCCategoryWildcard(Connection connection, String wildcardCategory) throws SQLException;

	public abstract PreparedStatement AllDDCCategories(Connection connection) throws SQLException;

	public abstract PreparedStatement LoginDataLowerCase(Connection connection, String name) throws SQLException;

	public abstract PreparedStatement LoginData(Connection connection, String name) throws SQLException;

	public abstract PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement ObjectServiceStatusID(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement ObjectServiceStatusAll(Connection connection) throws SQLException;

	public abstract PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement LatestOtherCategories(Connection connection, String category) throws SQLException;

	public abstract PreparedStatement DINISetCategoriesByName(Connection connection, String category) throws SQLException;

	public abstract PreparedStatement DNBCategoriesByCategorie(Connection connection, String category) throws SQLException;

	public abstract PreparedStatement DDCCategoriesByCategorie(Connection connection, String category) throws SQLException;

	public abstract PreparedStatement Iso639LanguageByName(Connection connection, String iso639language) throws SQLException;

	public abstract PreparedStatement LanguageByName(Connection connection, String language) throws SQLException;

	public abstract PreparedStatement LatestKeyword(Connection connection, String keyword, String language) throws SQLException;

	public abstract PreparedStatement LatestPerson(Connection connection, String firstname, String lastname) throws SQLException;

	public abstract PreparedStatement Languages(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Keywords(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement AllClassifications(Connection connection, List<BigDecimal> ids) throws SQLException;

	public abstract PreparedStatement AllClassifications(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement OtherClassification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DINISetClassification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DNBClassification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DDCClassification(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Publisher(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement TypeValues(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement DateValues(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Description(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Identifier(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Format(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Contributors(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Editors(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Authors(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement Title(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, Date time, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement WorkflowDBInserted(Connection connection, BigDecimal object_id, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal service_id, BigDecimal repository_id) throws SQLException;

	public abstract PreparedStatement WorkflowDBComplete(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement WorkflowDB(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement ServicesOrder(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement Services(Connection connection, BigDecimal service_id) throws SQLException;

	public abstract PreparedStatement Services(Connection connection, String name) throws SQLException;

	public abstract PreparedStatement RawRecordData(Connection connection, BigDecimal internalOID) throws SQLException;

	public abstract PreparedStatement RawRecordData(Connection connection, BigDecimal internalOID, Date repository_timestamp) throws SQLException;

	public abstract PreparedStatement RawRecordDataHistory(Connection connection, BigDecimal internalOID) throws SQLException;

	public abstract PreparedStatement Repository(Connection connection, BigDecimal repositoryID) throws SQLException;

	public abstract PreparedStatement Repository(Connection connection) throws SQLException;

	public abstract PreparedStatement AllOIDsFromRepositoryIDMarkAsTest(Connection connection, BigDecimal repID) throws SQLException;

	public abstract PreparedStatement AllOIDsFromRepositoryID(Connection connection, BigDecimal repID) throws SQLException;

	public abstract PreparedStatement AllOIDsMarkAsHasFulltextlink(Connection connection) throws SQLException;

	public abstract PreparedStatement AllOIDsMarkAsNotTest(Connection connection) throws SQLException;

	public abstract PreparedStatement AllOIDsMarkAsTest(Connection connection) throws SQLException;

	public abstract PreparedStatement AllOIDs(Connection connection) throws SQLException;

	public abstract PreparedStatement ObjectEntry(Connection connection, BigDecimal repository_id, BigDecimal oid_offset) throws SQLException;

	public abstract PreparedStatement ObjectEntryID(Connection connection, BigDecimal repositoryID, String externalOID) throws SQLException;

	public abstract PreparedStatement ObjectEntry(Connection connection, BigDecimal repository_id, Date repository_datestamp, String repository_identifier) throws SQLException;

	public abstract PreparedStatement ObjectEntry(Connection connection, String repository_identifier) throws SQLException;

	public abstract PreparedStatement ObjectEntry(Connection connection, BigDecimal object_id) throws SQLException;

	public abstract PreparedStatement InternalID(Connection connection, String repository_identifier) throws SQLException;

	public abstract PreparedStatement DDCClassification(Connection connection, BigDecimal object_id, String category) throws SQLException;
	
	public abstract PreparedStatement DDCClassification2(Connection connection, BigDecimal object_id) throws SQLException;
}