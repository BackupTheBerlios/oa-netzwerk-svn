package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class DDCEntry  extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static Logger logger = Logger.getLogger(DDCEntry.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String OIDS_PARAM = "OIDS";

	public DDCEntry() {
		super(DDCEntry.class.getName(), RestKeyword.UsageData);
	}

	@Override
	protected String deleteKeyWord(String[] path) throws MethodNotImplementedException, NotEnoughParametersException {
		this.rms = new RestMessage(RestKeyword.DDCEntry);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCEntry + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	
	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {
		this.rms = new RestMessage(RestKeyword.DDCEntry);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCEntry + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}


	@Override
	protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException {

		BigDecimal object_id = null;

		@SuppressWarnings("unused")
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!", ex);

			this.rms = new RestMessage(RestKeyword.DDCEntry);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = null;
		List<RestEntrySet> requestEntrySets = this.rms.getListEntrySets();

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		String key = "";

		try {
			boolean errorHappended = false;
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			Iterator<RestEntrySet> itSets = requestEntrySets.iterator();
			List<String> categories = new ArrayList<String>();
			
			while (itSets.hasNext()) {
				res = itSets.next();

				String ddc_category = null;


				Iterator<String> it = res.getKeyIterator();
				
				try {
				while (it.hasNext()) {
					key = it.next();
					if (logger.isDebugEnabled())
						logger.debug("key = " + key);

					if (key.equalsIgnoreCase("ddc_category")) {
						ddc_category = res.getValue(key);
					} 
					else
						continue;
					
					
					if (ddc_category != null && ddc_category.trim().length() == 3 ) {
						Integer.parseInt(ddc_category);
						
					} else {
						this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
						this.rms.setListEntrySets(requestEntrySets);
						this.rms.setStatusDescription("Invalid DDC category name supplied!' ('"+ ddc_category +"')");
						res.addEntry("ERROR_OCCURED_HERE", "Invalid DDC category name supplied!' ('"+ ddc_category +"')");
						return RestXmlCodec.encodeRestMessage(this.rms);
					}
					
					categories.add(ddc_category);
					
				}
				}catch (NumberFormatException e) {
					this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
					this.rms.setListEntrySets(requestEntrySets);
					this.rms.setStatusDescription("Invalid DDC category name supplied!' ('"+ ddc_category +"')");
					res.addEntry("ERROR_OCCURED_HERE", "Invalid DDC category name supplied!' ('"+ ddc_category +"')");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}

				this.rms = new RestMessage(RestKeyword.DDCEntry);

				// Prüfen, ob überhaupt Daten übergeben wurden

				if (ddc_category == null) {

					this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
					this.rms.setListEntrySets(requestEntrySets);
					this.rms.setStatusDescription("No DDC category name supplied!'");
					res.addEntry("ERROR_OCCURED_HERE", "No DDC category name supplied! Please specify a DDC category for the 'ddc_category' key.");
					return RestXmlCodec.encodeRestMessage(this.rms);
				} 				

		
			}
			
			PreparedStatement prest = DBAccessNG.insertIntoDB().DDCClassification(stmtconn.connection, object_id, categories, true);
			int count[] = prest.executeBatch();			
			int insertedRows = 0;
			for (int i = 0; i < count.length; i ++) {
				insertedRows += count[i];
            }
			
			logger.info("Insert-Query successful, " + insertedRows + " rows affected!");

			
			stmtconn.connection.commit();
			
			this.rms.setStatus (RestStatusEnum.OK);
		}  catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setKeyword(RestKeyword.DDCEntry);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setKeyword(RestKeyword.DDCEntry);
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

			this.rms.addEntrySet(res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);

	}
	
	@Override
    protected String putKeyWord(String[] path, String data) throws NotEnoughParametersException, MethodNotImplementedException {
		this.rms = new RestMessage(RestKeyword.DDCEntry);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '" + RestKeyword.DDCEntry + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
    }
}