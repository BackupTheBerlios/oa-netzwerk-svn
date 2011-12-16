package de.dini.oanetzwerk.server.handler;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class DDCCategories extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (DDCCategories.class);
	
	/**
	 * 
	 */
	
	private static Pattern patternWildcardCategory = null; 
	
	/**
	 * 
	 */
	
	public DDCCategories ( ) {
		super (DDCCategories.class.getName ( ), RestKeyword.DDCCategories);
		patternWildcardCategory = Pattern.compile( "[0-9](x|[0-9]x|[0-9]{2}|[0-9]{2}.[0-9])"); 
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		this.rms = new RestMessage (RestKeyword.DDCCategories);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.DDCCategories+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
//		if (path.length < 1)
//			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
				
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
															
			if(path.length == 0) {
				
				// list of plain categories 
				
				stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
				stmtconn.loadStatement (DBAccessNG.selectFromDB().AllDDCCategories(stmtconn.connection));
				QueryResult allDDCResult = stmtconn.execute ( );

				logWarnings(allDDCResult);

				while (allDDCResult.getResultSet ( ).next ( )) {
					res = new RestEntrySet ( );
					res.addEntry("DDC_Categorie", allDDCResult.getResultSet().getString("DDC_Categorie"));
					res.addEntry("name_deu", allDDCResult.getResultSet().getString("name_deu"));
					res.addEntry("name_eng", allDDCResult.getResultSet().getString("name_eng"));
					res.addEntry("direct_count", allDDCResult.getResultSet().getString("direct_count"));
					res.addEntry("sub_count", allDDCResult.getResultSet().getString("sub_count"));
					this.rms.addEntrySet (res);	
				}			

				stmtconn.commit ( );
				
			} else if(path.length == 1) {
				
				// wildcard Category
				
				String wildcardCategory = path[0];
				
				if(wildcardCategory == null || wildcardCategory.length() == 0) {
					logger.error ("empty wildcard category, value = '" + wildcardCategory + "'");
					this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription ("empty wildcard category, value = '" + wildcardCategory + "'");
					return RestXmlCodec.encodeRestMessage (this.rms);
				} 
				
				Matcher m = patternWildcardCategory.matcher(wildcardCategory); 
				if(!m.matches()) {
					logger.error ("malformed wildcard category, value = '" + wildcardCategory + "'");
					this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription ("Malformed wildcard category, value = '" + wildcardCategory + "', use 'x' for wildcard please!");
					return RestXmlCodec.encodeRestMessage (this.rms);					
				}
				// transform to SQL LIKE string
				wildcardCategory = wildcardCategory.replaceAll("x", "%");
				
				stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
				stmtconn.loadStatement (DBAccessNG.selectFromDB().DDCCategoryWildcard(stmtconn.connection, wildcardCategory));
				QueryResult wildcardDDCResult = stmtconn.execute ( );

				logWarnings(wildcardDDCResult);
				
				logger.debug("wildcardDDCResult = " + wildcardDDCResult);
				System.out.println("wildcardDDCResult = " + wildcardDDCResult);
				
				while (wildcardDDCResult.getResultSet ( ).next ( )) {		
					res.addEntry("wildcardCategory",wildcardCategory);
					res.addEntry("sum", wildcardDDCResult.getResultSet().getString(1));
				}			

				stmtconn.commit ( );
				this.rms.addEntrySet (res);	
			}
			
			this.rms.setStatus (RestStatusEnum.OK);		
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get DDCCategories: " + ex.getLocalizedMessage ( ), ex);
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
						
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		this.rms = new RestMessage (RestKeyword.DDCCategories);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.DDCCategories+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.DDCCategories);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.DDCCategories+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
}
