package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class UsageStatistics extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface{

	private static Logger logger = Logger.getLogger(UsageStatistics.class);
	
	public UsageStatistics() {
		
		super(UsageStatistics.class.getName(), RestKeyword.UsageStatistics);
	}

	@Override
	protected String putKeyWord(String[] path, String data)
			throws NotEnoughParametersException, MethodNotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String postKeyWord(String[] path, String data)
			throws MethodNotImplementedException, NotEnoughParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String deleteKeyWord(String[] path)
			throws MethodNotImplementedException, NotEnoughParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * 
	 */

	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		DBAccessNG dbng = new DBAccessNG(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet();

		try {
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			if(path.length == 0) {

				
	
				stmtconn.loadStatement(DBAccessNG.selectFromDB().UsageData_Ranking(stmtconn.connection));
	
	
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage());
	
	
				while (this.result.getResultSet().next()) {
	
					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tUsageStatistics object_id = " + this.result.getResultSet().getInt("object_id"));
					res = new RestEntrySet ( );
	
					res.addEntry("object_id", Integer.toString(this.result.getResultSet().getInt("object_id")));
					this.rms.addEntrySet(res);
					
					
				}
			}else if(path.length != 0){
				
				if(path[0] == null || path[0].length() == 0) {
					logger.error ("empty OID, value = '" + path[0] + "'");
					this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription ("empty OID, value = '" + path[0] + "'");
					return RestXmlCodec.encodeRestMessage (this.rms);
				} 
				BigDecimal oid = new BigDecimal(path[0]);
				stmtconn.loadStatement(DBAccessNG.selectFromDB().UsageData_Counter_ForOID(stmtconn.connection, oid));
				this.result = stmtconn.execute();
				
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage());
				if (this.result.getResultSet().next()) {
					
					if (logger.isDebugEnabled())
						logger.info("DB returned: \n\tUsageStatistics object_id = " + this.result.getResultSet().getInt("object_id"));
					res = new RestEntrySet ( );
	
					res.addEntry("object_id", Integer.toString(this.result.getResultSet().getInt("object_id")));
					res.addEntry("counter", Integer.toString(this.result.getResultSet().getInt("counter")));
	
					this.rms.addEntrySet(res);					
				}
				
			}
			this.rms.setStatus(RestStatusEnum.OK);


		} catch (SQLException ex) {

			logger.error("An error occured while processing Get UsageStatistics: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get UsageStatistics: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close();
					stmtconn = null;

				} catch (SQLException ex) {

					logger.error(ex.getLocalizedMessage(), ex);
				}
			}

			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

}
