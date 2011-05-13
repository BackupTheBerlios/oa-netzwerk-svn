package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UpdateFromDB {

	public abstract PreparedStatement ServicesScheduling(Connection connection, String name, BigDecimal service_id, String status, String info, boolean periodic, Date nonperiodicDate,
			String periodicInterval, int periodicDays, int jobId) throws SQLException;

	public abstract PreparedStatement Repository(final Connection connection, final Long repository_id, final boolean active) throws SQLException;

	public abstract PreparedStatement Repository(final Connection connection, final BigDecimal repository_id, final Date dateToSet, final String dateField) throws SQLException;

	public abstract PreparedStatement LoginData(Connection connection, String name, String password, String email) throws SQLException;

	public abstract PreparedStatement PrecleanedData(Connection connection, BigDecimal object_id, Date repositoryDateStamp, String metaDataFormat, String data) throws SQLException;

	public abstract PreparedStatement PrecleanedData(Connection connection, BigDecimal object_id, Date repositoryDateStamp, String data) throws SQLException;

	public abstract PreparedStatement Object(Connection connection, BigDecimal object_id, BigDecimal repository_id, Date harvested, Date repository_datestamp, String repository_identifier, boolean testdata, int failureCounter,
			boolean peculiar, boolean outdated, int peculiarCounter) throws SQLException;

}