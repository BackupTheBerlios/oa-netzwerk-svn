/**
 * 
 */
package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Sammy David
 */

public class Repository extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static final String PATH_MARKEDTODAY = "markedtoday";
	private static final String PATH_HARVESTEDTODAY = "harvestedtoday";
	/**
	 * 
	 */

	private static Logger logger = Logger.getLogger(Repository.class);

	/**
	 * 
	 */

	public Repository() {

		super(Repository.class.getName(), RestKeyword.Repository);
	}

	
//	/**
//	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
//	 */
//
//	//@Override		 
//	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
//		
//		if (path.length < 1)
//			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the repository ID");
//		
//		BigDecimal repository_id;
//		
//		try {
//			
//			repository_id = new BigDecimal (path [0]);
//			
//		} catch (NumberFormatException ex) {
//			
//			logger.error (path [0] + " is NOT a number!");
//			
//			this.rms = new RestMessage (RestKeyword.Repository);
//			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//			this.rms.setStatusDescription (path [0] + " is NOT a number!");
//			
//			return RestXmlCodec.encodeRestMessage (this.rms);
//		}
//		
//		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
//		MultipleStatementConnection stmtconn = null;
//		RestEntrySet res = new RestEntrySet ( );
//		
//		try {
//			
//			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
//			
//			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Repository_Sets (stmtconn.connection, repository_id));
//			logger.debug("BEFORE delete RepositorySets");
//			this.result = stmtconn.execute ( );
//			
//			if (this.result.getWarning ( ) != null) 
//				for (Throwable warning : result.getWarning ( ))
//					logger.warn (warning.getLocalizedMessage ( ));
//			logger.debug("AFTER delete RepositorySets");
//
//			logger.debug("suche alle zugehörigen Object-Einträge um die verlinkten Einträge zu löschen. Wird in Schleife durchgeführt da der einzige passende Query nur mit 1000er Batchsize arbeitet");
//			
//			logger.debug("BEFORE select Object");
//			boolean finished = false;
//			List<BigDecimal> oids = new Vector<BigDecimal>();
//			while (!finished) {
//				stmtconn.loadStatement(DBAccessNG.selectFromDB().ObjectEntry(stmtconn.connection, repository_id, new BigDecimal(0)));
//			
//				this.result = stmtconn.execute();
//				if (this.result.getWarning ( ) != null)  {
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//				}
//				else {
//					ResultSet rs = this.result.getResultSet();
//					
//					// abbrechen wenn kein Eintrag mehr enthalten ist
//					if (!rs.next()) {
//						finished = true;
//						break;
//					}
//					oids.add(rs.getBigDecimal("object_id"));
//					while(rs.next()) {
//						oids.add(rs.getBigDecimal("object_id"));
//					}
//				}
//			}
//			logger.debug("AFTER select Object");
//			
//			logger.debug("BEFORE delete AggregatorMetadata");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().AggregatorMetadata(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete AggregatorMetadata");
//			
//			logger.debug("BEFORE delete DDCClassification");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection, oids.get(i), false));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete DDCClassification");
//			
//			logger.debug("BEFORE delete DINI Set Classification");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().DINI_Set_Classification(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete DINI Set Classification");
//			
//			logger.debug("BEFORE delete DNBClassification");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().DNB_Classification(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete DNBClassification");
//			
//			logger.debug("BEFORE delete DateValue");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().DateValues(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete DateValue");
//			
//			logger.debug("BEFORE delete Description");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Description(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Description");
//			
//			logger.debug("BEFORE delete DuplicatePossibilities");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().DuplicatePossibilities(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete DuplicatePossibilities");
//			
//			logger.debug("BEFORE delete Format");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Formats(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Format");
//			
//			logger.debug("BEFORE delete FullTextLinks");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().FullTextLinks(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete FullTextLinks");
//			
//			logger.debug("BEFORE delete Identifier");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Identifiers(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Identifier");
//			
//			logger.debug("BEFORE delete Interpolated_DDC_Classification");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Interpolated_DDC_Classification(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Interpolated DDC Classification");
//			
//			logger.debug("BEFORE delete OAIExportCache");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().OAIExportCache(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete OAIExportCache");
//			
//			logger.debug("BEFORE delete Object2Author");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Author(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Author");
//			
//			logger.debug("BEFORE delete Object2Contributor");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Contributor(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Contributor");
//			
//			logger.debug("BEFORE delete Object2Editor");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Editor(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Editor");
//			
//			logger.debug("BEFORE delete Object2Iso639Language");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Iso639Language(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Iso639Language");
//			
//			logger.debug("BEFORE delete Object2Keywords");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Keywords(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Keywords");
//			
//			logger.debug("BEFORE delete Object2Language");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Language(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object2Language");
//			
//			logger.debug("BEFORE delete Other_Classification");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Other_Classification(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Other_Classification");
//			
//			logger.debug("BEFORE delete Publisher");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Publishers(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Publisher");
//			
//			logger.debug("BEFORE delete RawData");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().RawData(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete RawData");
//	
//			logger.debug("BEFORE delete Titles");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Titles(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Titles");
//			
//			logger.debug("BEFORE delete TypeValue");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().TypeValue(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete TypeValue");
//			
//			logger.debug("BEFORE delete UsageData_Months");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().UsageData_ALL_Months(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete UsageData_Months");
//			
//			logger.debug("BEFORE delete UsageData_Overall");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().UsageData_ALL_Overall(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete UsageData_Overall");
//			
//			logger.debug("BEFORE delete WorkflowDB");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().WorkflowDB(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete WorkflowDB");
//			
//			logger.debug("BEFORE delete Worklist");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Worklist(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Worklist");
//			
//			logger.debug("BEFORE delete orphaned Keywords");
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().KeywordsWithoutReference(stmtconn.connection));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			logger.debug("AFTER delete orphaned Keywords");
//			
//			logger.debug("BEFORE delete orphaned Persons");
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().PersonWithoutReference(stmtconn.connection));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			logger.debug("AFTER delete orphaned Persons");
//			
//			logger.debug("BEFORE delete Object");
//			for (int i = 0; i < oids.size(); i++) {
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object(stmtconn.connection, oids.get(i)));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			}
//			logger.debug("AFTER delete Object");
//			
//			logger.debug("BEFORE delete Repository");
//				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Repositories(stmtconn.connection, repository_id));
//				this.result = stmtconn.execute ( );
//				
//				if (this.result.getWarning ( ) != null) 
//					for (Throwable warning : result.getWarning ( ))
//						logger.warn (warning.getLocalizedMessage ( ));
//			logger.debug("AFTER delete Repository");
//			
//			logger.debug("BEFORE commit");
//			stmtconn.commit ( );
//			logger.debug("AFTER commit");
//			
//			res.addEntry("repository_id", repository_id.toPlainString());
//			this.rms.setStatus(RestStatusEnum.OK);
//			this.rms.addEntrySet(res);
//			
//		} catch (SQLException ex) {
//			
//			try {
//				
//				stmtconn.rollback ( );
//				
//			} catch (SQLException ex1) {
//				
//				logger.error (ex1.getLocalizedMessage ( ), ex1);
//			}
//			
//			logger.error (ex.getLocalizedMessage ( ), ex);
//			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
//			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
//			
//		} catch (WrongStatementException ex) {
//			
//			logger.error (ex.getLocalizedMessage ( ), ex);
//			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
//			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
//			
//		} finally {
//			
//			if (stmtconn != null) {
//				
//				try {
//					
//					stmtconn.close ( );
//					stmtconn = null;
//					
//				} catch (SQLException ex) {
//					
//					logger.error (ex.getLocalizedMessage ( ), ex);
//				}
//			}
//			
//			this.rms.addEntrySet (res);
//			res = null;
//			this.result = null;
//			dbng = null;
//		}
//
//		return RestXmlCodec.encodeRestMessage (this.rms);
//	}
		
	@Override
	protected String deleteKeyWord (String[] path) throws NotEnoughParametersException {
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the repository ID");
		
		BigDecimal repository_id;
		
		try {
			
			repository_id = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.Repository);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"AggregatorMetadata\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DDC_Classification\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DINI_Set_Classification\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DNB_Classification\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DateValues\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Description\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DuplicatePossibilities\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			// we need to delete twice from Duplicate Possibilities
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"DuplicatePossibilities\"",
					"duplicate_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Format\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"FullTextLinks\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Identifier\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Interpolated_DDC_Classification\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"OAIExportCache\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Author\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Contributor\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Editor\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Iso639Language\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Keywords\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object2Language\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Other_Classification\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Publisher\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"RawData\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Titles\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"TypeValue\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"UsageData_Months\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"UsageData_Overall\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"WorkflowDB\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Worklist\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			// cleanup overhead like keywords and persons
			
//			logger.debug("BEFORE delete orphaned Keywords");
//			stmtconn.loadStatement(DBAccessNG.deleteFromDB().KeywordsWithoutReference(stmtconn.connection));
//			this.result = stmtconn.execute ( );
//			
//			if (this.result.getWarning ( ) != null) 
//				for (Throwable warning : result.getWarning ( ))
//					logger.warn (warning.getLocalizedMessage ( ));
//			logger.debug("AFTER delete orphaned Keywords");
			
			logger.debug("BEFORE delete orphaned Persons");
				stmtconn.loadStatement(DBAccessNG.deleteFromDB().PersonWithoutReference(stmtconn.connection));
				this.result = stmtconn.execute ( );
				
				if (this.result.getWarning ( ) != null) 
					for (Throwable warning : result.getWarning ( ))
						logger.warn (warning.getLocalizedMessage ( ));
			logger.debug("AFTER delete orphaned Persons");
			
			// finally delete the objects
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().DeleteFromTableByField(
					stmtconn.connection,
					"\"Object\"",
					"object_id",
					"\"Object\"",
					"object_id",
					"repository_id",
					repository_id
				)
			);
			this.result = stmtconn.execute();
			
			// now delete the Repository_Sets
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().Repository_Sets(stmtconn.connection, repository_id));
			this.result = stmtconn.execute();
			
			// and last but not least delete the repository
			stmtconn.loadStatement(DBAccessNG.deleteFromDB().Repositories(stmtconn.connection, repository_id));
			this.result = stmtconn.execute();
			
			
			
			logger.debug("BEFORE commit");
			stmtconn.commit ( );
			logger.debug("AFTER commit");
			
			res.addEntry("repository_id", repository_id.toPlainString());
			this.rms.setStatus(RestStatusEnum.OK);
			this.rms.addEntrySet(res);
			
		} catch (SQLException ex) {
			
			try {
				
				stmtconn.rollback ( );
				
			} catch (SQLException ex1) {
				
				logger.error (ex1.getLocalizedMessage ( ), ex1);
			}
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				try {
					stmtconn.close ( );
					stmtconn = null;
				} catch (SQLException ex) {
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.rms.addEntrySet (res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * 
	 */

	//@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		BigDecimal repositoryID = null;

		if (path.length == 1) {

			try {

				repositoryID = new BigDecimal(path[0]);

			} catch (NumberFormatException ex) {

				logger.error(path[0] + " is NOT a number!");

				this.rms = new RestMessage(RestKeyword.Repository);
				this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription(path[0] + " is NOT a number!");

				return RestXmlCodec.encodeRestMessage(this.rms);
			}
		}

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		try {
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

			if (repositoryID != null) {
				stmtconn.loadStatement(DBAccessNG.selectFromDB().Repository(stmtconn.connection, repositoryID));
			} else {
				stmtconn.loadStatement(DBAccessNG.selectFromDB().Repository(stmtconn.connection));
			}

			this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());

			if (repositoryID != null) {

				if (this.result.getResultSet().next()) {

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tRepository name = " + this.result.getResultSet().getString("name")
						                + "\n\tRepository URL = " + this.result.getResultSet().getString("url")
						                + "\n\tRepository OAI-URL = " + this.result.getResultSet().getString("oai_url")
						                + "\n\tTest Data = " + this.result.getResultSet().getBoolean("test_data") + "\n\tAmount = "
						                + this.result.getResultSet().getInt("harvest_amount") + "\n\tSleep = "
						                + this.result.getResultSet().getInt("harvest_pause"));

					RestEntrySet res = new RestEntrySet();

					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("url", this.result.getResultSet().getString("url"));
					res.addEntry("oai_url", this.result.getResultSet().getString("oai_url"));
					res.addEntry("test_data", new Boolean(this.result.getResultSet().getBoolean("test_data")).toString());
					res.addEntry("harvest_amount", Integer.toString(this.result.getResultSet().getInt("harvest_amount")));
					res.addEntry("harvest_pause", Integer.toString(this.result.getResultSet().getInt("harvest_pause")));
					res.addEntry("active", new Boolean(this.result.getResultSet().getBoolean("active")).toString());

					Date lastFullHarvestBegin = this.result.getResultSet().getDate("last_full_harvest_begin");
					res.addEntry("last_full_harvest_begin", lastFullHarvestBegin == null ? null : lastFullHarvestBegin.toString());
					Date lastMarkerEraserBegin = this.result.getResultSet().getDate("last_markereraser_begin");
					res.addEntry("last_markereraser_begin", lastMarkerEraserBegin == null ? null : lastMarkerEraserBegin.toString());
					this.rms.setStatus(RestStatusEnum.OK);
					this.rms.addEntrySet(res);

				} else {

					logger.warn("Nothing found!");
					this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
					this.rms.setStatusDescription("No matching Repository found");
				}

			} else {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				RestEntrySet res;

				while (this.result.getResultSet().next()) {

					this.rms.setStatus(RestStatusEnum.OK);

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tRepository name = " + this.result.getResultSet().getString("name")
						                + "\n\tRepository URL = " + this.result.getResultSet().getString("url")
						                + "\n\tRepository OAI-URL = " + this.result.getResultSet().getString("oai_url")
						                + "\n\tTest Data = " + this.result.getResultSet().getBoolean("test_data") + "\n\tAmount = "
						                + this.result.getResultSet().getInt("harvest_amount") + "\n\tSleep = "
						                + this.result.getResultSet().getInt("harvest_pause"));

					res = new RestEntrySet();

					res.addEntry("repository_id", this.result.getResultSet().getBigDecimal("repository_id").toPlainString());
					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("url", this.result.getResultSet().getString("url"));
					res.addEntry("oai_url", this.result.getResultSet().getString("oai_url"));
					res.addEntry("test_data", new Boolean(this.result.getResultSet().getBoolean("test_data")).toString());
					res.addEntry("harvest_amount", Integer.toString(this.result.getResultSet().getInt("harvest_amount")));
					res.addEntry("harvest_pause", Integer.toString(this.result.getResultSet().getInt("harvest_pause")));
					res.addEntry("active", new Boolean(this.result.getResultSet().getBoolean("active")).toString());
					Date lastFullHarvestBegin = this.result.getResultSet().getDate("last_full_harvest_begin");
					res.addEntry("last_full_harvest_begin", lastFullHarvestBegin == null ? null : lastFullHarvestBegin.toString());
					Date lastMarkerEraserBegin = this.result.getResultSet().getDate("last_markereraser_begin");
					res.addEntry("last_markereraser_begin", lastMarkerEraserBegin == null ? null : lastMarkerEraserBegin.toString());
					
					this.rms.addEntrySet(res);
				}

				if (this.rms.getStatus().equals(RestStatusEnum.NO_OBJECT_FOUND_ERROR)) {

					this.rms.setStatusDescription("No Repositories found");
					logger.warn("No Repositories found");
				}
			}

		} catch (SQLException ex) {

			logger.error("An error occured while processing Get Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get Repository: " + ex.getLocalizedMessage(), ex);
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

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException {

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 2 parameters: the keyword and the repository id!");

		BigDecimal repositoryID;

		try {

			repositoryID = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.Repository);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		
		
		// case: update repository info
		if (path.length == 1) {
	
			RestMessage msg = RestXmlCodec.decodeRestMessage(data);
	
			String name 		= null;
			String owner 		= "";
			String ownerEmail  	= "";
			String url 			= null;
			String oaiUrl 		= null;
			String harvestAmount = "10";
			String harvestPause = "5000";
			boolean testData 	= true;
			boolean listRecords = true;
			boolean active 		= false;
	
			List<RestEntrySet> entries = msg.getListEntrySets();
			Iterator<RestEntrySet> iterator = entries.iterator();
			RestEntrySet res;
	
			String key;
			while (iterator.hasNext()) {
				res = iterator.next();
	
				Iterator<String> it = res.getKeyIterator();
				while (it.hasNext()) {
					key = it.next();
	
					if (key.equalsIgnoreCase("name")) {
						name = res.getValue(key);
					} else if (key.equalsIgnoreCase("repoId")) {
						url = res.getValue(key);
					} else if (key.equalsIgnoreCase("url")) {
						url = res.getValue(key);
					} else if (key.equalsIgnoreCase("oaiUrl")) {
						oaiUrl = res.getValue(key);
					} else if (key.equalsIgnoreCase("owner")) {
						owner = res.getValue(key);
					} else if (key.equalsIgnoreCase("ownerEmail")) {
						ownerEmail = res.getValue(key);
					} else if (key.equalsIgnoreCase("harvestAmount")) {
						harvestAmount = res.getValue(key);
					} else if (key.equalsIgnoreCase("harvestPause")) {
						harvestPause = res.getValue(key);
					} else if (key.equalsIgnoreCase("testData")) {
						testData = Boolean.parseBoolean(res.getValue(key));
					} else if (key.equalsIgnoreCase("active")) {
						active = Boolean.parseBoolean(res.getValue(key));
					} else if (key.equalsIgnoreCase("listRecords")) {
						listRecords = Boolean.parseBoolean(res.getValue(key));
					}
	
				}
			}
	
			try {
				if (name == null || name.length() == 0 || url == null || url.length() == 0 || oaiUrl == null || oaiUrl.length() == 0) {
					logger.error("An error occured while processing Put Repository: Name, Url and OAI-Url must be provided!");
					this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription("Name, Url and OAI-Url must be provided!");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}
	
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, name, url, oaiUrl, owner, ownerEmail,
				                Integer.parseInt(harvestAmount), Integer.parseInt(harvestPause), listRecords, testData, active));
	
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
	
			} catch (SQLException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());
	
			} catch (WrongStatementException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
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
	
		// case: setting active property of reposiotry
		if (path.length == 2 && "deactivate".equals(path[1])) {
			
			RestMessage msg = RestXmlCodec.decodeRestMessage(data);
			boolean active 		= false;
		
			try {
	
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, false));
	
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
	
			} catch (SQLException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());
	
			} catch (WrongStatementException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
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
		
		
		
		// case: check for /harvestedtoday/ and /markedtoday/ request
		if (path.length == 2 && (path[1].equals(PATH_HARVESTEDTODAY) || path[1].equals(PATH_MARKEDTODAY))) {
			if (data != null) {
				logger.info("POST-Request to Repository/" + path[0]
				                + "/harvestedtoday/ or /markedtoday/ should not contain any data in body! Ignoring data: \n" + data);
			}

			try {

				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

				if (path[1].equals(PATH_HARVESTEDTODAY)) {

					stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, HelperMethods.today(),
					                "last_full_harvest_begin"));

				} else if (path[1].equals(PATH_MARKEDTODAY)) {

					stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, HelperMethods.today(),
					                "last_markereraser_begin"));
				}

				this.result = stmtconn.execute();

				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);

			} catch (SQLException ex) {

				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());

			} catch (WrongStatementException ex) {

				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
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
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	protected String putKeyWord(String[] path, String data) {

		if (data == null) {
			logger.warn("Request contained no data section. Cannot proceed!");
		}

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		RestMessage msg = RestXmlCodec.decodeRestMessage(data);

		String name 		= null;
		String owner 		= "";
		String ownerEmail  	= "";
		String url 			= null;
		String oaiUrl 		= null;
		String harvestAmount = "10";
		String harvestPause = "5000";
		boolean testData 	= true;
		boolean listRecords = true;
		boolean active 		= false;

		List<RestEntrySet> entries = msg.getListEntrySets();
		Iterator<RestEntrySet> iterator = entries.iterator();
		RestEntrySet res;

		String key;
		while (iterator.hasNext()) {
			res = iterator.next();

			Iterator<String> it = res.getKeyIterator();
			while (it.hasNext()) {
				key = it.next();

				if (key.equalsIgnoreCase("name")) {
					name = res.getValue(key);
				} else if (key.equalsIgnoreCase("url")) {
					url = res.getValue(key);
				} else if (key.equalsIgnoreCase("oaiUrl")) {
					oaiUrl = res.getValue(key);
				} else if (key.equalsIgnoreCase("owner")) {
					owner = res.getValue(key);
				} else if (key.equalsIgnoreCase("ownerEmail")) {
					ownerEmail = res.getValue(key);
				} else if (key.equalsIgnoreCase("harvestAmount")) {
					harvestAmount = res.getValue(key);
				} else if (key.equalsIgnoreCase("harvestPause")) {
					harvestPause = res.getValue(key);
				} else if (key.equalsIgnoreCase("testData")) {
					testData = Boolean.parseBoolean(res.getValue(key));
				} else if (key.equalsIgnoreCase("active")) {
					active = Boolean.parseBoolean(res.getValue(key));
				} else if (key.equalsIgnoreCase("listRecords")) {
					listRecords = Boolean.parseBoolean(res.getValue(key));
				}

			}
		}

		try {
			if (name == null || name.length() == 0 || url == null || url.length() == 0 || oaiUrl == null || oaiUrl.length() == 0) {
				logger.error("An error occured while processing Put Repository: Name, Url and OAI-Url must be provided!");
				this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription("Name, Url and OAI-Url must be provided!");
				return RestXmlCodec.encodeRestMessage(this.rms);
			}

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			stmtconn.loadStatement(DBAccessNG.insertIntoDB().Repository(stmtconn.connection, name, url, oaiUrl, owner, ownerEmail,
			                Integer.parseInt(harvestAmount), Integer.parseInt(harvestPause), listRecords, testData, active));

			this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);

		} catch (SQLException ex) {

			logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
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

		// this.rms = new RestMessage (RestKeyword.Repository);
		// this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		// this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.Repository+"'.");
		// return RestXmlCodec.encodeRestMessage (this.rms);
	}
} // end of class
