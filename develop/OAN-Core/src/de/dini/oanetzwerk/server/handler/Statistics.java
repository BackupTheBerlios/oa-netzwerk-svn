/**
 * 
 */
package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * @author Johannes Haubold
 */

public class Statistics extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static Logger logger = Logger.getLogger(Repository.class);

	public Statistics() {

		super(Statistics.class.getName(), RestKeyword.Statistics);
	}

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * 
	 */

	//@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		/*
		 * Schritt 1: nachsehen ob das Keyword parameterlos (alle Statistikdaten) oder mit Parameter (dann nur das geforderte Datum) aufgerufen wurde
		 * Schritt 2: für jedes gewünschte Statistikdatum die Statistikholfunktion aufrufen und die Daten aus der DB ziehen
		 * Schritt 3: Daten verpacken
		 * Schritt 4: Daten über die Restschnittstelle als XML rausjagen
		 */
		
		String keyword = "";
		String output = "";
		keyword = path[0];
		
		if (keyword.equals("RecordsPerDDCCategory")) {
				output += RecordsPerDDCCategory();
		}
		else if (keyword.equals("RecordsPerDINISetCategory")) {
			output += RecordsPerDINISetCategory();
		}
		else if (keyword.equals("RecordsPerIso639Language")) {
				output += RecordsPerIso639Language();
		}
		else if (keyword.equals("RecordsPerRepository")) {
				output += RecordsPerRepository();
		}
		else if (keyword.equals("ObjectCount")) {
				output += ObjectCount();
		}
		else if (keyword.equals("FullTextLinkCount")) {
				output += FullTextLinkCount();
		}
		else if (keyword.equals("getMarkedPublications")) {
			if (path.length > 1) {
				// repository ID provided
				//System.out.println("Path parameter: "+path[1]);
				Long repo_id = new Long(path[1]);
				output += getPeculiarAndOutdated(repo_id);
			}
			else {
				output += getPeculiarAndOutdated();
			}
		}
		else if (keyword.equals("getMarkedPublicationsCount")) {
			if (path.length > 1) {
				// repository id provided
				Long repo_id = new Long(path[1]);
				output += getPeculiarAndOutdatedCount(repo_id);
			}
			else {
				output += getPeculiarAndOutdatedCount();
			}
		}
		else {
			this.rms = new RestMessage(RestKeyword.Statistics);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription("Parameter "+path[0]+" is unknown");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		return output;
	}
	
	private String getPeculiarAndOutdatedCount() {
		return getPeculiarAndOutdatedCount(null);
	}

	private String getPeculiarAndOutdatedCount(Long repo_id) {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			if (repo_id != null) {
				logger.info("getting peculiar and outdated object count for repository "+repo_id);
				ssconn.loadStatement(DBAccessNG.selectFromDB().getPeculiarAndOutdatedCount(ssconn.connection, new BigDecimal(repo_id)));
			}
			else {
				logger.info("getting peculiar and outdated object count for all repositories");
				ssconn.loadStatement(DBAccessNG.selectFromDB().getPeculiarAndOutdatedCount(ssconn.connection));
			}
			this.result = ssconn.execute();
			
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long peculiarCount = rs.getLong(1);
				Long outdatedCount = rs.getLong(2);
				Long repoID = rs.getLong(3);
				String repositoryName = rs.getString(4);
				
				RestEntrySet res = new RestEntrySet();
				res.addEntry("peculiar", peculiarCount.toString());
				res.addEntry("outdated", outdatedCount.toString());
				res.addEntry("repositoryID", repoID.toString());
				res.addEntry("repositoryName", repositoryName);
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	private String getPeculiarAndOutdated() {
		return getPeculiarAndOutdated(null);
	}

	private String getPeculiarAndOutdated(Long repo_id) {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			if (repo_id != null) {
				ssconn.loadStatement(DBAccessNG.selectFromDB().getPeculiarAndOutdatedObjects(ssconn.connection, new BigDecimal(repo_id)));
			}
			else {
				ssconn.loadStatement(DBAccessNG.selectFromDB().getPeculiarAndOutdatedObjects(ssconn.connection));
			}
			this.result = ssconn.execute();
			
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long objectID = rs.getLong(1);
				Long repositoryID = rs.getLong(2);
				Boolean peculiar = rs.getBoolean(3);
				Boolean outdated = rs.getBoolean(4);
				String repoName = rs.getString(5);
				String title = rs.getString(6);
				
				RestEntrySet res = new RestEntrySet();
				res.addEntry("objectID", objectID.toString());
				res.addEntry("repositoryID", repositoryID.toString());
				res.addEntry("peculiar", peculiar.toString());
				res.addEntry("outdated", outdated.toString());
				res.addEntry("repoName", repoName);
				res.addEntry("title", title);
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	private String RecordsPerRepository() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().RecordsPerRepository(ssconn.connection));
			
			this.result = ssconn.execute();
			
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long repoID = rs.getLong(1);
				Long count = rs.getLong(2); // TODO: check ob Long hier wirklich der richtige Datentyp ist
				RestEntrySet res = new RestEntrySet();
				res.addEntry("repository_id", repoID.toString());
				res.addEntry("recordCount", count.toString());
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	
	private String RecordsPerDDCCategory() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().RecordsPerDDCCategory(ssconn.connection));
			
			this.result = ssconn.execute();
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long count = rs.getLong(1);
				String categoryID = rs.getString(2);
				String categoryNameDE = rs.getString(3);
				String categoryNameEN = rs.getString(4);
				
				RestEntrySet res = new RestEntrySet();
				res.addEntry("recordCount", count.toString());
				res.addEntry("categoryID", categoryID);
				res.addEntry("name", categoryNameDE);
				res.addEntry("name_en", categoryNameEN);
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	
	private String ObjectCount() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().ObjectCount(ssconn.connection));
			
			this.result = ssconn.execute();
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long count = rs.getLong(1);
				RestEntrySet res = new RestEntrySet();
				res.addEntry("objectCount", count.toString());
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	private String FullTextLinkCount() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().FullTextLinkCount(ssconn.connection));
			
			this.result = ssconn.execute();
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long count = rs.getLong(1);
				RestEntrySet res = new RestEntrySet();
				res.addEntry("FullTextLinkCount", count.toString());
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	private String RecordsPerIso639Language() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().RecordsPerIso639Language(ssconn.connection));
			
			this.result = ssconn.execute();
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long count = rs.getLong(1);
				Long languageID = rs.getLong(3);
				String language = rs.getString(2);
				
				RestEntrySet res = new RestEntrySet();
				res.addEntry("recordCount", count.toString());
				res.addEntry("languageID", languageID.toString());
				res.addEntry("language", language);
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return RestXmlCodec.encodeRestMessage(this.rms);
	}
	private String RecordsPerDINISetCategory() {
		try {
			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection ssconn = null;
			ssconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			
			ssconn.loadStatement(DBAccessNG.selectFromDB().RecordsPerDINISetCategory(ssconn.connection));
			
			this.result = ssconn.execute();
			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());
			
			ResultSet rs = this.result.getResultSet();
			while(rs.next()) {
				Long count = rs.getLong(1);
				Long DINI_Set_ID = rs.getLong(2);
				String name = rs.getString(3);
				String setNameEng = rs.getString(4);
				String setNameDeu = rs.getString(5);
				
				RestEntrySet res = new RestEntrySet();
				res.addEntry("recordCount", count.toString());
				res.addEntry("DINI_set_id", DINI_Set_ID.toString());
				res.addEntry("name", name);
				res.addEntry("setNameEng", setNameEng);
				res.addEntry("setNameDeu", setNameDeu);
				this.rms.addEntrySet(res);
			}
			ssconn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return RestXmlCodec.encodeRestMessage(this.rms);
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

	
} // end of class

