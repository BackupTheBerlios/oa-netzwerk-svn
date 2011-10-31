package de.dini.oanetzwerk.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DelegatingResultSet;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.oaipmh.Record;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.StatementConnection;

public class DataStatistics {

	private static Logger logger = Logger.getLogger(DataStatistics.class);

	public static void main(String[] args) {

		new DataStatistics().generate();

	}

	public void generate() {
		LinkedList<Record> recordList = new LinkedList<Record>();

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		// DBAccessNG dbng = new DBAccessNG();

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:jtds:sybase://themis.cms.hu-berlin.de:2025;DatabaseName=oanetzwerktest");
		dataSource.setUsername("oanetdbo");
		dataSource.setPassword("U7%wD8e4");
		dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");

		// dataSource.s

		if (logger.isDebugEnabled())
			System.out.println("Database connection will be prepared!");

		if (dataSource == null) {
			System.out.println("Datasource could not be resolved!");
		}

		Connection connection = null;
		try {

			connection = dataSource.getConnection();

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);
			ex.printStackTrace();
			return;
		}

		PreparedStatement singleStatement;

		try {

			stmtconn = new SingleStatementConnection(connection);

			// connection.setAutoCommit (true);
			singleStatement = null;

			SQLWarning warning = connection.getWarnings();

			while (warning != null) {

				logger.warn("A SQL-Connection-Warning occured: " + warning.getMessage() + " " + warning.getSQLState() + " "
						+ warning.getErrorCode());
				warning = warning.getNextWarning();
			}

			stmtconn.loadStatement(getData(stmtconn.connection));
			queryresult = stmtconn.execute();

			List<BigDecimal> ids = new ArrayList<BigDecimal>();

			System.out.println("Number of results:" + ((DelegatingResultSet)queryresult.getResultSet()).getFetchSize());
			System.out.println("Results: " + queryresult.getClass());

			// while (queryresult.getResultSet().next()) {
			// ids.add(queryresult.getResultSet().getBigDecimal("object_id"));
			// }
			//

			if (queryresult.getWarning() != null) {

				for (Throwable warning2 : queryresult.getWarning()) {

					System.out.println(warning2.getLocalizedMessage());
				}
			}

			Record record = new Record();
			BigDecimal oid;
			BigDecimal tempOid = BigDecimal.valueOf(-1);
			Integer num = 0;
			String ddc;
			String name;
			int distinctObjects = 0;

			HashMap<String, Double> factors = new HashMap<String, Double>();
			HashMap<String, String> categories = new HashMap<String, String>();
			ArrayList<String> tempddcs = new ArrayList<String>();

			int numberOfAll = 47737; // TODO: 47704 to be adjusted
			// int numberOfAll = 48200;
			int numberOfDocuments = 0;

			ArrayList<String> allids = new ArrayList<String>();
			

			while (queryresult.getResultSet().next()) {

				// System.out.println("y");
				ResultSet rs = queryresult.getResultSet();
				
				oid = rs.getBigDecimal("object_id");

				ddc = rs.getString("ddc");
				name = rs.getString("name");

				if (!categories.containsKey(ddc)) {
					categories.put(ddc, name);
				}

//				System.out.println("tempOid: " + tempOid + "     oid: " + oid);

				tempddcs.add(ddc);

				if (!tempOid.equals(oid)) {

					if (!BigDecimal.valueOf(-1).equals(oid)) {
						// System.out.println("XXX");
						// new oid, save last record

						ArrayList<String> copyOftempddcs = new ArrayList<String>(tempddcs);

						for (String tempddc : copyOftempddcs) {

							// System.out.println(tempddc);
							if (Integer.parseInt(tempddc) % 100 == 0) {
								tempddcs.remove(tempddcs.indexOf(tempddc));
								System.out.println("Sorting out ddc " + tempddc);
								
								
								continue;

							}
						}

						int i = 0;
						num = tempddcs.size();

						if (num == 0) {

							numberOfAll--;
							continue;

						}

						for (String d : tempddcs) {
							i++;
							double factor = (factors.get(d) == null ? 0.0 : factors.get(d)) + (1.0 / (((double) num))); 
							factors.put(d, factor);

						}
					}

					// reset
					tempOid = oid;
					if (!BigDecimal.valueOf(-1).equals(oid)) {
						tempddcs = new ArrayList<String>();
					}

				}
			}

			// System.out.println("XXX");
			// // new oid, save last record
			// int i = 0;
			// num = tempddcs.size();
			// for (String d : tempddcs) {
			// i++;
			// double factor = (factors.get(d) == null ? 0 : factors.get(d)) +
			// (1/(((double)i)) ); // TODO: adjust last value
			// factors.put(d, factor);
			// }

			Set<String> keys = factors.keySet();

			// ArrayList<String> addedPcts = new ArrayList<String>();
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);
			nf.setGroupingUsed(false);
			// String formattedvalue = nf.format(adoublevalue);

			// sortedDDCKeys.add()
			// "   Value.: " + Math.round(factors.get(key)) +
			double addedPcts = 0;
			double addedRoundedPcts = 0;

			System.out.println("Size: " + factors.size());
			double allPercentage = 0;
			ArrayList<String> output = new ArrayList<String>();


			for (String key : keys) {
				String outputLine = nf.format((double) 100 / (double) numberOfAll * factors.get(key)) + "; " + categories.get(key)	+ "  (DDC: " + key + ")";
				output.add(outputLine);

				allPercentage += factors.get(key);
				addedPcts += (double) 100 / (double) numberOfAll * factors.get(key);
				addedRoundedPcts += Double
						.parseDouble((nf.format((double) 100 / (double) numberOfAll * factors.get(key)).replace(",", ".")));
			}

			Collections.sort(output);
			Collections.reverse(output);

			for (String line : output) {
				System.out.println(line);
			}

			System.out.println("All Percentages: " + addedPcts);
			System.out.println("All Percentages (rounded): " + addedRoundedPcts);
			System.out.println(numberOfAll);
			
			closeStatementConnection(stmtconn);

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);

		} finally {

			closeStatementConnection(stmtconn);
		}
	}

	public static PreparedStatement getData(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("select o.object_id, c.DDC_Categorie as ddc, d.name from Object o, FullTextLinks f, DDC_Classification c, DDC_Categories d where o.object_id = f.object_id and f.object_id = c.object_id and c.DDC_Categorie = d.DDC_Categorie order by o.object_id");

		return preparedstmt;
	}

	private void closeStatementConnection(StatementConnection stmtconn) {

		try {

			if (stmtconn != null)
				stmtconn.close();

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

//	public static double round(double d, int decimalPlace) {
//		// see the Javadoc about why we use a String in the constructor
//		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
//		BigDecimal bd = new BigDecimal(Double.toString(d));
//		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//		return bd.doubleValue();
//	}

}
