package de.dini.oanetzwerk.rssfeed;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

import de.dini.oanetzwerk.search.SearchClient;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MetadataDBMapper;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.search.SearchClientException;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.RepositoryData;
import de.dini.oanetzwerk.utils.imf.Title;

/**
 * @author Robin Malitz
 *
 */

public class RSSFeedServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger (RSSFeedServlet.class);
	
	private Properties search_props = null;	
	private SearchClient mySearchClient = null;
	
	public RSSFeedServlet ( ) { }
	
	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init (ServletConfig config) throws ServletException {
		
      super.init (config);
      try {
   	    search_props = HelperMethods.loadPropertiesFromFileWithinWebcontainer("webapps/findnbrowse/WEB-INF/searchclientprop.xml");
   	    mySearchClient = new SearchClient(search_props);
      } catch (IOException ioex) {
    	  throw new ServletException("Error at setup of search service client, ", ioex);
      }
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
		
		resp.setContentType ("text/xml");	
		resp.getWriter ( ).write (this.getResponse (req, resp));
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getResponse (HttpServletRequest req, HttpServletResponse resp) {
						
		////////////////////////////////////////////////////
		// Feed-Type bestimmen und Feed-Container anlegen
		////////////////////////////////////////////////////
		
		String strParamFeedType = req.getParameter ("feedType");
		SyndFeed mySyndFeed = new SyndFeedImpl();
		if(strParamFeedType == null) {
			mySyndFeed.setFeedType("rss_2.0");
		} else {
			mySyndFeed.setFeedType(strParamFeedType);
		}
		mySyndFeed.setEncoding("UTF-8");
		
		/////////////////////////////////////////////////////////////
		// unterschiedliche Inhalte als Liste an Eintraegen fuellen
		/////////////////////////////////////////////////////////////
		
		if(req.getParameter("test") != null) {
			
			mySyndFeed.setTitle("OANetzwerk Test-Feed");
			mySyndFeed.setAuthor("Projekt: Open Access Netzwerk");
			mySyndFeed.setLink("http://oansuche.open-access.net");
			mySyndFeed.setDescription("Proof of Concept der technischen Realisierung eines RSS Feed Export");
			mySyndFeed = getTestFeed(mySyndFeed);
		
		} else if(req.getParameter("recent10") != null) {
			
			mySyndFeed.setTitle("OANetzwerk Recent-10-Feed");
			mySyndFeed.setAuthor("Projekt: Open Access Netzwerk");
			mySyndFeed.setLink("http://oansuche.open-access.net");
			mySyndFeed.setDescription("Liefert die 10 jüngsten Einträge.");
			mySyndFeed = getRecent10Feed(mySyndFeed);
			
		} else if(req.getParameter("search") != null) {
			
			String suchString = req.getParameter("search").trim();
			mySyndFeed.setTitle("OANetzwerk Alterting-Feed - abonierter Suchbegriff '"+ StringEscapeUtils.escapeHtml(suchString)+"'");
			mySyndFeed.setAuthor("Projekt: Open Access Netzwerk");
			mySyndFeed.setLink("http://oansuche.open-access.net");
			mySyndFeed.setDescription("Liefert alle Treffer, die auch die Suche nach dem Suchbegriff momentan ergibt.");
			mySyndFeed = getSearchFeed(mySyndFeed, suchString);
			
		} else {
			
			mySyndFeed.setTitle("OANetzwerk Feed-Feature: Genereller Hinweis");
			mySyndFeed.setAuthor("Projekt: Open Access Netzwerk");
			mySyndFeed.setLink("http://oansuche.open-access.net");
			mySyndFeed.setDescription("Rufen Sie die URL, die sie gerade abfragen, mit den Parametern 'recent10' oder 'search=<IhrSuchbegriff>' ab!");
			
		}
						
		// im eingestellten Type zu xml marshallen
		String strXML = "";
		SyndFeedOutput mySyndFeedOutput = new SyndFeedOutput();
		try {
			strXML = mySyndFeedOutput.outputString(mySyndFeed);
		} catch (FeedException fex) {
			logger.error(fex.getLocalizedMessage(), fex);
		}
			
		return strXML;
	}

	private SyndFeed getTestFeed(SyndFeed mySyndFeed) {		
		
		// entries anlegen
		List<SyndEntry> listEntries = new ArrayList<SyndEntry>();
		for (int i = 0; i < 5; i++) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setAuthor("Autor "+i);
			entry.setTitle("Titel "+i);
			entry.setPublishedDate(new Date(System.currentTimeMillis()));
			SyndContent scDesc = new SyndContentImpl();
			scDesc.setType("text/plain");
			StringBuffer sb = new StringBuffer();
			Random rnd = new Random();
			for(int j = 0; j < 20; j++) {
				sb.append(RandomStringUtils.randomAlphabetic(3 + rnd.nextInt(12))).append(" ");
			}			
			scDesc.setValue(sb.toString());
			entry.setDescription(scDesc);
			listEntries.add(entry);
		}
		
		mySyndFeed.setEntries(listEntries);
		
		// so aggregiert man einen Feed
		try {
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed inFeed = input.build(new XmlReader(new URL("http://open-access.net/de/rss/")));
			listEntries.addAll(inFeed.getEntries());
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}
		
		
		
		
		
		

		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		List<SyndEntry> listDBEntries = new ArrayList<SyndEntry>();
		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			QueryResult queryResult;
			stmtconn.loadStatement (stmtconn.connection.prepareStatement ("select TOP 10 object_id, repository_datestamp, repository_identifier from dbo.Object order by repository_datestamp DESC"));
			queryResult = stmtconn.execute ( );

			while (queryResult.getResultSet ( ).next ( )) {
				SyndEntry entry = new SyndEntryImpl();
				entry.setTitle(queryResult.getResultSet ( ).getString (1) + " --- "  +queryResult.getResultSet ( ).getString (3));
				entry.setPublishedDate(queryResult.getResultSet ( ).getDate(2));
				listDBEntries.add(entry);
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

		if(listDBEntries != null && listDBEntries.size() > 0) {
			listEntries.addAll(listDBEntries);
		}
		
		return mySyndFeed;
	}
	
	private SyndFeed getRecent10Feed(SyndFeed mySyndFeed) {		
		
		List<SyndEntry> listEntries = new ArrayList<SyndEntry>();	
		List<BigDecimal> listOIDsToExport = new ArrayList<BigDecimal>();
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		
		/////////////////////////////////
		// letzten 10 Einträge erfragen
		/////////////////////////////////
		
		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			QueryResult queryResult;
			stmtconn.loadStatement (stmtconn.connection.prepareStatement ("select TOP 10 object_id, repository_datestamp, repository_identifier from dbo.Object order by repository_datestamp DESC"));
			queryResult = stmtconn.execute ( );

			while (queryResult.getResultSet ( ).next ( )) {
				try {
				  BigDecimal bdOID = null;
				  bdOID = new BigDecimal(queryResult.getResultSet ( ).getString (1));
				  //logger.debug("recent oid fetched: " + bdOID);
				  listOIDsToExport.add(bdOID);
				} catch (NumberFormatException ex) {/*NO OP*/}
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

		}

		//////////////////////////////
		// Metadaten aus DB beziehen 
		//////////////////////////////
		
		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
					
			for(BigDecimal bdOID : listOIDsToExport) {
				CompleteMetadata cmf = getCompleteMetadata(stmtconn, bdOID);
				Date dateRepo = getRepositoryDatestamp(stmtconn, bdOID);
				//logger.debug("recent cmf fetched: " + cmf);
				if(!cmf.isEmpty()) {
				  SyndEntry entry = getSyndEntryFromCompleteMetadata(cmf, dateRepo);				
			      if(entry != null) listEntries.add(entry);
				}
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

		mySyndFeed.setEntries(listEntries);
		
		return mySyndFeed;
	}
	
	private SyndFeed getSearchFeed(SyndFeed mySyndFeed, String suchString) {		
		
		List<SyndEntry> listEntries = new ArrayList<SyndEntry>();
		
		// Treffer abfragen
		
		List<BigDecimal> listOIDsToExport = new ArrayList<BigDecimal>();
		try {
			listOIDsToExport = mySearchClient.querySearchService(suchString);
		} catch(SearchClientException scex) {
			logger.error("SearchClientException: " + scex);
			return mySyndFeed;
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
				Date dateRepo = getRepositoryDatestamp(stmtconn, bdOID);
				SyndEntry entry = getSyndEntryFromCompleteMetadata(cmf, dateRepo);
				if(entry != null) listEntries.add(entry);
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

		mySyndFeed.setEntries(listEntries);
		
		return mySyndFeed;
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
	
	private Date getRepositoryDatestamp(MultipleStatementConnection stmtconn, BigDecimal bdOID) throws SQLException, WrongStatementException {

		Date dateRepo = null;	
		
		stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectEntry(stmtconn.connection, bdOID));
		QueryResult oeResult = stmtconn.execute ( );
		
		if (oeResult.getWarning ( ) != null)
			for (Throwable warning : oeResult.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
		
		while (oeResult.getResultSet ( ).next ( )) {
			try {
				dateRepo = HelperMethods.sql2javaDate(oeResult.getResultSet().getDate("repository_datestamp"));
			} catch(Exception ex) {
				logger.error("error fetching object entry for OID: " + bdOID, ex);
			}
		}	
					
		stmtconn.commit ( );			
		
		return dateRepo;
	}
	
	/**
	 * formatiert die Inhalte der Datensaetze ineinander um 
	 * 
	 * @param cmf
	 * @return
	 */
	private SyndEntry getSyndEntryFromCompleteMetadata(CompleteMetadata cmf, Date dateRepo) {
		SyndEntry entry = new SyndEntryImpl();
		StringBuffer sb = null;
		
		/////////////
		// Authors
		/////////////
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getAuthorList().size(); i++) {
			Author author = cmf.getAuthorList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(author.getFirstname()+ " " + author.getLastname());			
		}
		String strAuthor = sb.toString();
		entry.setAuthor(strAuthor);
		
		List<SyndPerson> listSyndPerson = new ArrayList<SyndPerson>();
		for(int i = 0; i < cmf.getAuthorList().size(); i++) {
			Author author = cmf.getAuthorList().get(i);		
			SyndPerson syndPerson = new SyndPersonImpl();
			syndPerson.setName(author.getFirstname()+ " " + author.getLastname());
			listSyndPerson.add(syndPerson);			
		}
		entry.setAuthors(listSyndPerson);
		
		//////////////////
		// Category
		//////////////////
		
		List<SyndCategory> listSyndCategory = new ArrayList<SyndCategory>();
		for(int i = 0; i < cmf.getKeywordList().size(); i++) {
			Keyword keyword = cmf.getKeywordList().get(i);		
			SyndCategoryImpl cat = new SyndCategoryImpl();
			cat.setName(keyword.getKeyword());
			//cat.setTaxonomyUri("http://purl.org/dc/terms/subject");
			listSyndCategory.add(cat);			
		}
		entry.setCategories(listSyndCategory);
		
		//////////////////
		// Date
		//////////////////
				
		Date date = null;
//		DateValue dateValue = null;
//		for(DateValue dv : cmf.getDateValueList()) {
//			dateValue = dv;
//			if(dateValue.getDateValue() != null) date = dateValue.getDateValue();
//		}
		date = dateRepo;
		
		String sDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		if(date == null) {
			date = new Date(0);
			logger.debug("kein Datum");
		} else {
			sDate = " ("+sdf.format(date)+")";
			logger.debug("Datum = '" + date + "' --> '"+sDate+"'");
		}
		entry.setPublishedDate(date);

		/////////////////
		// Description
		/////////////////
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getDescriptionList().size(); i++) {
			Description desc = cmf.getDescriptionList().get(i);
			if(i > 0) sb.append("<br/>\n");
			sb.append(desc.getDescription());			
		}
		SyndContent scDesc = new SyndContentImpl();
		scDesc.setType("text/html");
		logger.debug("Zusammenfassung = '" + sb.toString() + "'");
		if(sb.toString().trim().length() > 0) {			
			String s = sb.toString();
			if(s.length() > 400) {
                s = s.substring(0, 399);
                s = s.substring(0, s.lastIndexOf(" "));
    			s = s + " [...]";
    			logger.debug("trimmed s = '" + s + "'");
			}			
			scDesc.setValue("<p><b>" + strAuthor + sDate + "</b><br/>\n" + s + "</p>");
		} else {
			scDesc.setValue("<p><b>" + strAuthor + sDate + "</b><br/>\n" + "(keine Zusammenfassung verf&uuml;gbar)</p>");			
		}
		entry.setDescription(scDesc);
		
		//////////////////
		// Link
		//////////////////
		
		String strFullTextLink = getBestLink(cmf);
		if(strFullTextLink.length() > 0) {
			entry.setLink(strFullTextLink);
		}
		
		//////////////////
		// Title
		//////////////////
		
		sb = new StringBuffer();
		for(int i = 0; i < cmf.getTitleList().size(); i++) {
			Title title = cmf.getTitleList().get(i);
			if(i > 0) sb.append(", ");
			sb.append(title.getTitle());			
		}
		String strTitle = sb.toString();
		if(strTitle.length() > 0) {
		    entry.setTitle(strTitle);		
		} else {
			if(strFullTextLink.length() > 0) {
				entry.setTitle(strFullTextLink);
			} else {
				return null;
			}
		}
		
		return entry;
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
