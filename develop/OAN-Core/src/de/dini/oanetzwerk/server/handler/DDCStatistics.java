package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class DDCStatistics extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static Logger logger = Logger.getLogger(DDCStatistics.class);
	
	public DDCStatistics() {
		super(DDCStatistics.class.getName(), RestKeyword.DDCStatistics);
	}

	@Override
	protected String deleteKeyWord(String[] path) throws MethodNotImplementedException, NotEnoughParametersException {
		this.rms = new RestMessage(RestKeyword.DDCStatistics);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCStatistics + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	
	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {
		this.rms = new RestMessage(RestKeyword.DDCStatistics);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCStatistics + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}


	@Override
	protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException {

		BigDecimal object_id = null;
		this.rms = new RestMessage(RestKeyword.DDCStatistics);

		@SuppressWarnings("unused")
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the action to execute");


		String action = path[0];

		if (action == null || !action.equals("renew")) {
		
			logger.error("The argument '" + path[0] + "' is not a valid action!");
	
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription("The argument '" + path[0] + "' is not a valid action!");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		try {	
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			
			PreparedStatement prest = DBAccessNG.updateInDB().DDCBrowsingHelpCount(stmtconn.connection);
			prest.executeUpdate();			
	
			prest = DBAccessNG.updateInDB().DDCBrowsingHelpSubCount(stmtconn.connection);
			prest.executeUpdate();		
			
			stmtconn.connection.commit();
				
			this.rms.setStatus (RestStatusEnum.OK);
		
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
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

//			this.rms.addEntrySet(res);
//			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	
	@Override
    protected String putKeyWord(String[] path, String data) throws NotEnoughParametersException, MethodNotImplementedException {
		this.rms = new RestMessage(RestKeyword.DDCStatistics);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCStatistics + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
    }
}
