/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Contributor;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.Editor;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * @author Robin Malitz
 *
 */

public class DDCCategories extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (DDCCategories.class);
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
				
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
															
			if(path.length == 0) {
				
				// list of plain categories 
				
				stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
				stmtconn.loadStatement (SelectFromDB.AllDDCCategories(stmtconn.connection));
				QueryResult allDDCResult = stmtconn.execute ( );

				if (allDDCResult.getWarning ( ) != null) {
					for (Throwable warning : allDDCResult.getWarning ( )) {
						logger.warn (warning.getLocalizedMessage ( ));
					}
				}

				while (allDDCResult.getResultSet ( ).next ( )) {		
					res.addEntry(allDDCResult.getResultSet().getString(1), allDDCResult.getResultSet().getString(2));
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
				stmtconn.loadStatement (SelectFromDB.DDCCategoryWildcard(stmtconn.connection, wildcardCategory));
				QueryResult wildcardDDCResult = stmtconn.execute ( );

				if (wildcardDDCResult.getWarning ( ) != null) {
					for (Throwable warning : wildcardDDCResult.getWarning ( )) {
						logger.warn (warning.getLocalizedMessage ( ));
					}
				}
				
				logger.debug("wildcardDDCResult = " + wildcardDDCResult);

				while (wildcardDDCResult.getResultSet ( ).next ( )) {		
					res.addEntry("wildcardCategory",wildcardCategory);
					res.addEntry("sum", wildcardDDCResult.getResultSet().getString(1));
				}			

				stmtconn.commit ( );
				
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			this.rms.addEntrySet (res);			
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get DDCCategories: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
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
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

}
