package de.dini.oanetzwerk.indexeraccess;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
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
import de.dini.oanetzwerk.utils.imf.InterpolatedDDCClassification;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.RepositoryData;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * @author Robin Malitz
 *
 */

public class IndexerAccessServlet extends HttpServlet {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	
	private static String BASE_URL = "http://oanet.cms.hu-berlin.de/indexeraccess/";
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (IndexerAccessServlet.class);
	
	/**
	 * 
	 */
	
	public IndexerAccessServlet ( ) { }
	
	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init (ServletConfig config) throws ServletException {
		
		super.init (config);
		
//		logger.debug ("INIT");
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
//		logger.debug ("doGet");
		
		req.setCharacterEncoding ("UTF-8");
		resp.setCharacterEncoding ("UTF-8");
		
		String userAgent = req.getHeader("User-Agent");
		
		if (userAgent == null)
			userAgent = "";
		
		if (userAgent.matches ("mozilla") || userAgent.matches ("opera") || userAgent.matches ("msie") || userAgent.matches ("netscape")) {
			
			
		}
		
		resp.setContentType ("text/html");	
		resp.getWriter ( ).write (this.getResponse (req, resp));
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	
	private String getResponse (HttpServletRequest req, HttpServletResponse resp) {
		
		String strParam_oid = req.getParameter("oid");
		BigDecimal bdOID = null;
		try { bdOID = new BigDecimal(strParam_oid); } catch(Exception ex) {}  
		
		if(bdOID == null) {
			return getOverviewPage();
		} else {
			return getOIDPage(bdOID);
		}
		
	}

	/**
	 * @return
	 */
	
	private static String getOverviewPage () {
				
		
		StringBuffer sb = new StringBuffer();		
				
		List<String> listOIDs = new ArrayList<String>();
		
		DBAccessNG dbng = DBAccessNG.getInstance();
		SingleStatementConnection stmtconn = null;

		try {


			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (DBAccessNG.selectFromDB().AllOIDs(stmtconn.connection));	
			QueryResult result = stmtconn.execute ( );

			// log warnings
			if (result.getWarning ( ) != null) {
				for (Throwable warning : result.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}

			// extract oids from db response
			while(result.getResultSet ( ).next ( )) {
				listOIDs.add("" + result.getResultSet().getInt("object_id"));
			} 

		} catch (SQLException ex) {

			logger.error ("An error occured while processing Get: " + ex.getLocalizedMessage ( ));			

		} catch (WrongStatementException ex) {

			logger.error ("An error occured while processing Get: " + ex.getLocalizedMessage ( ));			

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close ( );
					stmtconn = null;

				} catch (SQLException ex) {

					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}

			dbng = null;
		}

		logger.debug("listOIDs = " + listOIDs);
		
		sb.append("<html>\n<body>\n<ul>\n");
		for(String strOID : listOIDs) {
		  sb.append("<li><a href=\"").append(BASE_URL).append("?oid=").append(strOID).append("\">").append(strOID).append("</a></li>\n");
		}
		sb.append("</ul>\n</body>\n</html>");
		
		return sb.toString(); 
	
	}
	
	/**
	 * @param bdOID
	 * @return
	 */
	
	private static String getOIDPage (BigDecimal bdOID) {
	
		
		StringBuffer sb = new StringBuffer();		
				
		CompleteMetadata cmf = new CompleteMetadata();
		cmf.setOid(bdOID);
		
		DBAccessNG dbng = DBAccessNG.getInstance();
		MultipleStatementConnection stmtconn = null;
		@SuppressWarnings("unused")
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
			MetadataDBMapper.fillInternalMetadataFromDB(cmf, stmtconn);
			
            ////////////////////////////
			// DupPro - Abfrage
			////////////////////////////			
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().DuplicateProbabilities (stmtconn.connection, cmf.getOid()));
			QueryResult dupproResult = stmtconn.execute ( );
			
			if (dupproResult.getWarning ( ) != null) {
				for (Throwable warning : dupproResult.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			int num = 0;
			while (dupproResult.getResultSet ( ).next ( )) {
				try {
				  DuplicateProbability dupPro = new DuplicateProbability();
				  dupPro.setNumber(num);
				  dupPro.setReferToOID(new BigDecimal(dupproResult.getResultSet().getString("duplicate_id")));
				  dupPro.setProbability(dupproResult.getResultSet().getDouble("percentage"));
				  cmf.addDuplicateProbability(dupPro);				
				  num++;
				} catch(Exception ex) {
					logger.error("error fetching duplicate possibilities for OID: " + cmf.getOid(), ex);
				}
			}	
			
			///////////////////////
			// FulltextlinkAbfrage
			///////////////////////
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().FullTextLinks (stmtconn.connection, cmf.getOid()));
			QueryResult ftlResult = stmtconn.execute ( );
			
			if (ftlResult.getWarning ( ) != null) {
				for (Throwable warning : ftlResult.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			while (ftlResult.getResultSet ( ).next ( )) {				
				FullTextLink ftl = new FullTextLink();					
				ftl.setUrl(ftlResult.getResultSet().getString("link"));
				ftl.setMimeformat(ftlResult.getResultSet().getString("mimeformat"));
				cmf.addFullTextLink(ftl);				
			}			
			
			////////////////////////////			
			// RepositoryData - Abfrage
			////////////////////////////
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().RepositoryData(stmtconn.connection, cmf.getOid()));
			QueryResult repdataResult = stmtconn.execute ( );
			
			if (repdataResult.getWarning ( ) != null)
				for (Throwable warning : repdataResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			while (repdataResult.getResultSet ( ).next ( )) {
				RepositoryData repData = new RepositoryData(cmf.getOid());
				repData.setRepositoryID(repdataResult.getResultSet().getInt("repository_id"));
				repData.setRepositoryName(repdataResult.getResultSet().getString("name"));
				repData.setRepositoryOAI_BASEURL(repdataResult.getResultSet().getString("oai_url"));
				repData.setRepositoryOAI_EXTID(repdataResult.getResultSet().getString("repository_identifier"));
				repData.setRepositoryURL(repdataResult.getResultSet().getString("url"));
				cmf.setRepositoryData(repData);
			}			
			
			//////////////////////////////
			// InterpolatedDDC - Abfrage
			//////////////////////////////
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().InterpolatedDDCClassification(stmtconn.connection, cmf.getOid()));
			QueryResult interpolatedDDCResult = stmtconn.execute ( );
			
			if (interpolatedDDCResult.getWarning ( ) != null)
				for (Throwable warning : interpolatedDDCResult.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			while (interpolatedDDCResult.getResultSet ( ).next ( )) {
				InterpolatedDDCClassification interpolatedDDC = new InterpolatedDDCClassification();				
				interpolatedDDC.setValue(interpolatedDDCResult.getResultSet().getString("Interpolated_DDC_Categorie"));
				interpolatedDDC.setProbability(interpolatedDDCResult.getResultSet().getDouble("percentage"));
				cmf.addClassfication(interpolatedDDC);
			}
			
			/////////////////
			// COMMIT
			/////////////////
			
			stmtconn.commit ( );
			
			//logger.debug("CMF (toString) >>>>>> " + cmf);			 
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		}  catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get CompleteMetadataEntry: " + ex.getLocalizedMessage ( ), ex);
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
						
			dbng = null;
		}
		
		// render HTML-OUTPUT for complete metadata
		
		sb.append("<html>\n");
		
		if(cmf.isEmpty()) {
			// leeres CMF soll Fehler geben
			
			logger.info("leeres CMF angefragt");
			
		} else {
			
			sb.append("<head>\n");
			
			sb.append("<!-- IndexerAccess v1.0.001 -->\n");
			
		    sb.append("<meta name=\"").append("oid").append("\" value=\"").append(cmf.getOid()).append("\"/>\n");				
						
			for(Title title : cmf.getTitleList()) {
				sb.append("<meta name=\"").append("title").append("\" value=\"").append(StringEscapeUtils.escapeHtml(title.getTitle())).append("\"/>\n");				
			}
			
			for(Author author : cmf.getAuthorList()) {
				sb.append("<meta name=\"").append("author").append("\" value=\"").append(StringEscapeUtils.escapeHtml(author.getFirstname() + " " + author.getLastname())).append("\"/>\n");				
			}
			for(Contributor item : cmf.getContributorList()) {
				sb.append("<meta name=\"").append("contributor").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getFirstname() + " " + item.getLastname())).append("\"/>\n");				
			}
			for(Editor item : cmf.getEditorList()) {
				sb.append("<meta name=\"").append("editor").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getFirstname() + " " + item.getLastname())).append("\"/>\n");				
			}
			
			for(DateValue item : cmf.getDateValueList()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sb.append("<meta name=\"").append("date").append("\" value=\"").append(StringEscapeUtils.escapeHtml(sdf.format(item.getDateValue()))).append("\"/>\n");				
				sb.append("<meta name=\"").append("date_unparsed").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getStringValue())).append("\"/>\n");				
			}

			for(Description item : cmf.getDescriptionList()) {
				sb.append("<meta name=\"").append("description").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getDescription())).append("\"/>\n");				
			}
			
			for(Format item : cmf.getFormatList()) {
				sb.append("<meta name=\"").append("format").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getSchema_f())).append("\"/>\n");				
			}
			
			for(Identifier item : cmf.getIdentifierList()) {
				sb.append("<meta name=\"").append("identifier").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getIdentifier())).append("\"/>\n");				
			}
			
			for(Keyword item : cmf.getKeywordList()) {
				sb.append("<meta name=\"").append("keyword").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getKeyword())).append("\"/>\n");				
			}
			
			for(Language item : cmf.getLanguageList()) {
				sb.append("<meta name=\"").append("language_iso639").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getIso639language())).append("\"/>\n");
				sb.append("<meta name=\"").append("language").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getLanguage())).append("\"/>\n");				
			}
			
			for(Publisher item : cmf.getPublisherList()) {
				sb.append("<meta name=\"").append("publisher").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getName())).append("\"/>\n");				
			}
			
			for(TypeValue item : cmf.getTypeValueList()) {
				sb.append("<meta name=\"").append("type").append("\" value=\"").append(StringEscapeUtils.escapeHtml(item.getTypeValue())).append("\"/>\n");				
			}
			
			for(Classification item : cmf.getClassificationList()) {
				if(item instanceof DINISetClassification) {
					DINISetClassification castedItem = (DINISetClassification)item;
					sb.append("<meta name=\"").append("classification_DINISetClassification").append("\" value=\"").append(StringEscapeUtils.escapeHtml(castedItem.getValue())).append("\"/>\n");									
				} else if(item instanceof DDCClassification) {
					DDCClassification castedItem = (DDCClassification)item;
					sb.append("<meta name=\"").append("classification_DDCClassification").append("\" value=\"").append(StringEscapeUtils.escapeHtml(castedItem.getValue())).append("\"/>\n");									
				} else if(item instanceof InterpolatedDDCClassification) {
					InterpolatedDDCClassification castedItem = (InterpolatedDDCClassification)item;
					sb.append("<meta name=\"").append("classification_DDCClassification").append("\" value=\"").append(StringEscapeUtils.escapeHtml(castedItem.getValue())).append("\"/><!--interpolated-->\n");									
				} else if(item instanceof DNBClassification) {
					DNBClassification castedItem = (DNBClassification)item;
					sb.append("<meta name=\"").append("classification_DNBClassification").append("\" value=\"").append(StringEscapeUtils.escapeHtml(castedItem.getValue())).append("\"/>\n");									
				} else if(item instanceof OtherClassification) {
					OtherClassification castedItem = (OtherClassification)item;
					sb.append("<meta name=\"").append("classification_OtherClassification").append("\" value=\"").append(StringEscapeUtils.escapeHtml(castedItem.getValue())).append("\"/>\n");									
				}
			}
			
			sb.append("</head>\n");

			sb.append("<body>\n");
			if(cmf.getFullTextLinkList().size() > 0) {
			  sb.append("<li><a href=\"").append(cmf.getFullTextLinkList().get(0).getUrl()).append("\">").append(bdOID).append("</a></li>\n");
			}
			sb.append("</body>\n");
		}

		sb.append("</html>\n");		
		
		return sb.toString(); 
			
	}
	

	
	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Post called");
		this.doGet (req, resp);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Delete called");
		this.doGet (req, resp);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Head called");
		this.doGet (req, resp);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Options called");
		this.doGet (req, resp);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Put called");
		this.doGet (req, resp);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.warn ("Trace called");
		this.doGet (req, resp);
	}
	
//	public static void main(String[] args) {
//		System.out.println(IndexerAccessServlet.getOIDPage(new BigDecimal("67")));
//	}
	
}
