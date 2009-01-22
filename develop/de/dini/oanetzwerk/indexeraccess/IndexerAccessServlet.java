package de.dini.oanetzwerk.indexeraccess;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
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
	@SuppressWarnings("unchecked")
	private String getResponse (HttpServletRequest req, HttpServletResponse resp) {
		
		String strParam_oid = req.getParameter("oid");
		BigDecimal bdOID = null;
		try { bdOID = new BigDecimal(strParam_oid); } catch(Exception ex) {}  
		
		if(bdOID == null) {
			return getOverviewPage(req, resp);
		} else {
			return getOIDPage(req, resp, bdOID);
		}
		
	}

	@SuppressWarnings("unchecked")
	private String getOverviewPage (HttpServletRequest req, HttpServletResponse resp) {
				
		
		StringBuffer sb = new StringBuffer();		
				
		List<String> listOIDs = new ArrayList<String>();
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;

		try {


			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			stmtconn.loadStatement (SelectFromDB.AllOIDs(stmtconn.connection));	
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
	
	@SuppressWarnings("unchecked")
	private String getOIDPage (HttpServletRequest req, HttpServletResponse resp, BigDecimal bdOID) {
	
		
		StringBuffer sb = new StringBuffer();		
				
		CompleteMetadata cmf = new CompleteMetadata();
		cmf.setOid(bdOID);
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
			MetadataDBMapper.fillInternalMetadataFromDB(cmf, stmtconn);
			
			//TODO: remember to update this!!!
			DuplicateProbability dupPro = new DuplicateProbability(new BigDecimal(815), 99.9, 0);
			cmf.addDuplicateProbability(dupPro);
			
			// FulltextlinkAbfrage
			stmtconn.loadStatement (SelectFromDB.FullTextLinks (stmtconn.connection, cmf.getOid()));
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
			
		    sb.append("<meta name=\"").append("oid").append("\" value=\"").append(cmf.getOid()).append("\"/>\n");				
						
			for(Title title : cmf.getTitleList()) {
				sb.append("<meta name=\"").append("title").append("\" value=\"").append(title.getTitle()).append("\"/>\n");				
			}
			
			for(Author author : cmf.getAuthorList()) {
				sb.append("<meta name=\"").append("author").append("\" value=\"").append(author.getFirstname() + " " + author.getLastname()).append("\"/>\n");				
			}
			for(Contributor item : cmf.getContributorList()) {
				sb.append("<meta name=\"").append("contributor").append("\" value=\"").append(item.getFirstname() + " " + item.getLastname()).append("\"/>\n");				
			}
			for(Editor item : cmf.getEditorList()) {
				sb.append("<meta name=\"").append("editor").append("\" value=\"").append(item.getFirstname() + " " + item.getLastname()).append("\"/>\n");				
			}
			
			for(DateValue item : cmf.getDateValueList()) {
				sb.append("<meta name=\"").append("date").append("\" value=\"").append(item.getDateValue()).append("\"/>\n");				
			}

			for(Description item : cmf.getDescriptionList()) {
				sb.append("<meta name=\"").append("description").append("\" value=\"").append(item.getDescription()).append("\"/>\n");				
			}
			
			for(Format item : cmf.getFormatList()) {
				sb.append("<meta name=\"").append("format").append("\" value=\"").append(item.getSchema_f()).append("\"/>\n");				
			}
			
			for(Identifier item : cmf.getIdentifierList()) {
				sb.append("<meta name=\"").append("identifier").append("\" value=\"").append(item.getIdentifier()).append("\"/>\n");				
			}
			
			for(Keyword item : cmf.getKeywordList()) {
				sb.append("<meta name=\"").append("keyword").append("\" value=\"").append(item.getKeyword()).append("\"/>\n");				
			}
			
			for(Language item : cmf.getLanguageList()) {
				sb.append("<meta name=\"").append("language").append("\" value=\"").append(item.getLanguage()).append("\"/>\n");				
			}
			
			for(Publisher item : cmf.getPublisherList()) {
				sb.append("<meta name=\"").append("publisher").append("\" value=\"").append(item.getName()).append("\"/>\n");				
			}
			
			for(TypeValue item : cmf.getTypeValueList()) {
				sb.append("<meta name=\"").append("type").append("\" value=\"").append(item.getTypeValue()).append("\"/>\n");				
			}
			
			for(Classification item : cmf.getClassificationList()) {
				if(item instanceof DINISetClassification) {
					DINISetClassification castedItem = (DINISetClassification)item;
					sb.append("<meta name=\"").append("classification_DINISetClassification").append("\" value=\"").append(castedItem.getValue()).append("\"/>\n");									
				} else if(item instanceof DDCClassification) {
					DDCClassification castedItem = (DDCClassification)item;
					sb.append("<meta name=\"").append("classification_DDCClassification").append("\" value=\"").append(castedItem.getValue()).append("\"/>\n");									
				} else if(item instanceof DNBClassification) {
					DNBClassification castedItem = (DNBClassification)item;
					sb.append("<meta name=\"").append("classification_DNBClassification").append("\" value=\"").append(castedItem.getValue()).append("\"/>\n");									
				} else if(item instanceof OtherClassification) {
					OtherClassification castedItem = (OtherClassification)item;
					sb.append("<meta name=\"").append("classification_OtherClassification").append("\" value=\"").append(castedItem.getValue()).append("\"/>\n");									
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
	
}
