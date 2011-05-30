package de.dini.oanetzwerk.bibexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;

import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.Editor;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.RepositoryData;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

/**
 * @author Robin Malitz
 *
 */

public class BibExportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger (BibExportServlet.class);
	private String strParamType;
	private String strParamOIDs;

	
	public BibExportServlet ( ) { }
	
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
		
		logger.debug ("doGet");
		
		req.setCharacterEncoding ("UTF-8");
		resp.setCharacterEncoding ("UTF-8");
		
		String userAgent = req.getHeader("User-Agent");
		
		if (userAgent == null)
			userAgent = "";
		
		if (userAgent.matches ("mozilla") || userAgent.matches ("opera") || userAgent.matches ("msie") || userAgent.matches ("netscape")) {
			
			
		}
		
		strParamType = StringUtils.defaultString(req.getParameter("type"),"bibtex");
		strParamOIDs = StringUtils.defaultString(req.getParameter("OIDs"),"");
		
		if(strParamType.equals("bibtex")) {
	      resp.setContentType ("application/x-bibtex");
	      resp.setHeader("Content-Disposition", "filename=oan_export.bib"); // nach RFC 1806
		} else {
		  resp.setContentType ("text/plain");
	      resp.setHeader("Content-Disposition", "filename=oan_export.bib"); // nach RFC 1806
		}
		
		if(req.getParameter("OIDs") == null) {
  		   resp.setContentType ("text/html");
		   resp.getWriter ( ).write ("<html><body>usage: <url>/bibexport?OIDs=1,26,87&type=bibtex</body></html>");
		} else {
		   resp.getWriter ( ).write (this.getResponse (req, resp));			
		}

	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getResponse (HttpServletRequest req, HttpServletResponse resp) {
		
	    List<BigDecimal> listOIDsToExport = new ArrayList<BigDecimal>();
		BibtexFile bibtexFile = new BibtexFile();
		
		////////////////////////////////////
		// Liste der OID aus Request holen
		////////////////////////////////////
		
		for(String strOID : strParamOIDs.split(",")) {
			try {
				BigDecimal bdOID = new BigDecimal(strOID.trim());
				listOIDsToExport.add(bdOID);
			} catch(Exception ex) {}			
		}
				
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
		
			//////////////////////////////
			// Metadaten aus DB beziehen 
			//////////////////////////////
			
			for(BigDecimal bdOID : listOIDsToExport) {
				CompleteMetadata cmf = getCompleteMetadata(stmtconn, bdOID);
				BibtexEntry bibtexEntry = getBibtextEntryFromCompleteMetadata(bibtexFile, cmf);
				bibtexFile.addEntry(bibtexEntry);
			}
						
		} catch (SQLException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		} catch (WrongStatementException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (stmtconn != null) {
				try {
					stmtconn.close ( );
    				stmtconn = null;
				} catch (SQLException ex) {
				}
			}
			dbng = null;
		}

		String strXML = "";
		
		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    PrintWriter writer = new PrintWriter(bos);
		    bibtexFile.printBibtex(writer);
		    writer.flush();
		    strXML = bos.toString("UTF-8");
		} catch (Exception ex) {
			logger.error("error at serializing bibtextfile : " + ex);
		}
		  
		return strXML;
	}

	private CompleteMetadata getCompleteMetadata(MultipleStatementConnection stmtconn, BigDecimal bdOID) throws SQLException, WrongStatementException {
		// erzeuge cmf-Object, das Schrittweise mit Daten befüllt wird
		CompleteMetadata cmf = new CompleteMetadata();
		cmf.setOid(bdOID);
		
		// ausgelagert in separate Klasse, um den Code für andere Metadatenviews nachzunutzen
		MetadataDBMapper.fillInternalMetadataFromDB(cmf, stmtconn);
		
		////////////////////////////
		// DupPro - Abfrage
		////////////////////////////			
		
		stmtconn.loadStatement (DBAccessNG.selectFromDB().DuplicateProbabilities (stmtconn.connection, cmf.getOid()));
		QueryResult dupproResult = stmtconn.execute ( );
		
		if (dupproResult.getWarning ( ) != null)
			for (Throwable warning : dupproResult.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
		
		int num = 0;
		while (dupproResult.getResultSet ( ).next ( )) {
			try {
			  DuplicateProbability dupPro = new DuplicateProbability();
			  dupPro.setNumber(num);
			  dupPro.setReferToOID(new BigDecimal(dupproResult.getResultSet().getString("duplicate_id")));
			  dupPro.setProbability(dupproResult.getResultSet().getDouble("percentage"));				  
			  dupPro.setReverseProbability(dupproResult.getResultSet().getDouble("reverse_percentage")); 				  
			  cmf.addDuplicateProbability(dupPro);				
			  num++;
			} catch(Exception ex) {
				logger.error("error fetching duplicate possibilities for OID: " + cmf.getOid(), ex);
			}
		}	
					
		//////////////////////////
		// Fulltextlink - Abfrage
		//////////////////////////
		
		stmtconn.loadStatement (DBAccessNG.selectFromDB().FullTextLinks (stmtconn.connection, cmf.getOid()));
		QueryResult ftlResult = stmtconn.execute ( );
		
		if (ftlResult.getWarning ( ) != null)
			for (Throwable warning : ftlResult.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
		
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
		
		stmtconn.commit ( );			
		
		return cmf;
	}

	/**
	 * mapping of CompleteMetadata into BibtexEntry-Objects
	 * 
	 * @param bibtexFile
	 * @param cmf
	 * @return
	 */
	private BibtexEntry getBibtextEntryFromCompleteMetadata(BibtexFile bibtexFile, CompleteMetadata cmf) {

		StringBuffer sb = null;
		DateFormat df_year = new SimpleDateFormat("yyyy");
		DateFormat df_month = new SimpleDateFormat("MM");
		DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		String strBibtexEntryType = "misc";
		
		// TODO: Liste ist unvollständig, Werte sollten eigentlich bei der Aggregation besser gecleaned werden
		for(TypeValue tv : cmf.getTypeValueList()) {
			if("article".equalsIgnoreCase(tv.getTypeValue())) strBibtexEntryType = "article";
			if("dissertation".equalsIgnoreCase(tv.getTypeValue())) strBibtexEntryType = "phdthesis";
			if("proceeding".equalsIgnoreCase(tv.getTypeValue())) strBibtexEntryType = "proceedings";
			if("Thesis".equalsIgnoreCase(tv.getTypeValue())) strBibtexEntryType = "phdthesis";
			if("monograph".equalsIgnoreCase(tv.getTypeValue())) strBibtexEntryType = "book";
		}
		
		BibtexEntry bibtexEntry = bibtexFile.makeEntry(strBibtexEntryType, "ref_OID"+cmf.getOid());
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getAuthorList().size(); i++) {
			Author author = cmf.getAuthorList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(author.getFirstname()+ " " + author.getLastname());			
		}
		bibtexEntry.setField("author", bibtexFile.makeString(sb.toString()));	
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getTitleList().size(); i++) {
			Title title = cmf.getTitleList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(title.getTitle());			
		}
		bibtexEntry.setField("title", bibtexFile.makeString(sb.toString()));	
		
        String strYear = null;
        String strMonth = null;
		for(int i = 0; i < cmf.getDateValueList().size(); i++) {
			DateValue dateValue = cmf.getDateValueList().get(i);
			if(dateValue.getDateValue() != null) {				
			    strYear = df_year.format(dateValue.getDateValue());
			    strMonth = df_month.format(dateValue.getDateValue());
			    bibtexEntry.setField("date", bibtexFile.makeString(sdf.format(dateValue.getDateValue())));
			} else {
				strYear = dateValue.getStringValue();
				bibtexEntry.setField("date", bibtexFile.makeString(dateValue.getStringValue()));
			}
		}
		if(strYear != null) bibtexEntry.setField("year", bibtexFile.makeString(strYear));		
		if(strMonth != null) bibtexEntry.setField("month", bibtexFile.makeString(strMonth));
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getPublisherList().size(); i++) {
			Publisher publisher = cmf.getPublisherList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(publisher.getName());			
		}
		if(sb.toString().length() > 0) {
		    if(strBibtexEntryType.equals("phdhesis") || 
		       strBibtexEntryType.equals("masterthesis") ||
		       strBibtexEntryType.equals("techreport")) {
			    bibtexEntry.setField("school", bibtexFile.makeString(sb.toString()));
	    	} else {
			    bibtexEntry.setField("publisher", bibtexFile.makeString(sb.toString()));			
	    	}
		}
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getPublisherList().size(); i++) {
			Publisher publisher = cmf.getPublisherList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(publisher.getName());			
		}

		// TODO: War Editor überhaupt in den Daten drin???
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getEditorList().size(); i++) {
			Editor editor = cmf.getEditorList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(editor.getFirstname()+" "+editor.getLastname());			
		}
		if(sb.toString().length() > 0) {
	       bibtexEntry.setField("editor", bibtexFile.makeString(sb.toString()));
		}
		
		String strFullTextLink = getBestLink(cmf);
		if(strFullTextLink.length() > 0) {
			bibtexEntry.setField("url", bibtexFile.makeString(strFullTextLink));
		}
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getDescriptionList().size(); i++) {
			Description desc = cmf.getDescriptionList().get(i);
			if(i > 0) sb.append("\n");
			sb.append(desc.getDescription());			
		}
		if(sb.toString().length() > 0) {
			bibtexEntry.setField("abstract", bibtexFile.makeString(sb.toString()));
		}
				
		bibtexEntry.setField("note", bibtexFile.makeString("exportiert am "+sdf.format(new Date())+" durch http://oansuche.open-access.net"));
		
		return bibtexEntry;
	}
	
	public String getBestLink(CompleteMetadata cmf) {
		List<FullTextLink> listFTL = cmf.getFullTextLinkList();
		String strFTL = "";
		if(listFTL != null && listFTL.size() > 0) {
			if(!listFTL.get(0).getUrl().equalsIgnoreCase("na")) strFTL = listFTL.get(0).getUrl();
		}
		if(strFTL.length() == 0) {
			List<Identifier> listIdent = cmf.getIdentifierList();		
			if(listIdent != null && listIdent.size() > 0) {
				for(int i = 0; i < listIdent.size(); i++) {
					String s = listIdent.get(i).getIdentifier();					 
					if(s.startsWith("http://")) strFTL = s; 
					if(s.endsWith(".pdf")) break;
				}
			}	
		}
		return strFTL;
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
