package de.dini.oanetzwerk.validator.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.StatementConnection;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class DBHelper {

	private static Logger logger = Logger.getLogger(DBHelper.class);
	
	public boolean save(StatementConnection stmtconn, PreparedStatement statement) {
				
		try {

			stmtconn.loadStatement(statement);
			QueryResult queryResult = stmtconn.execute();

			if (queryResult.getWarning() != null) {

				for (Throwable warning : queryResult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}
			return true;
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}
		
		return false;
	}
	
	
	
	private void closeStatementConnection(StatementConnection stmtconn) {
		
		try {

			if (stmtconn != null)
				stmtconn.close();

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
		}
	}
	
}
