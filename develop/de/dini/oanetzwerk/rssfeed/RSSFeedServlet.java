package de.dini.oanetzwerk.rssfeed;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;
import de.dini.oanetzwerk.utils.imf.Title;

/**
 * @author Robin Malitz
 *
 */

public class RSSFeedServlet extends HttpServlet {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (RSSFeedServlet.class);
	
	/**
	 * 
	 */
	
	public RSSFeedServlet ( ) { }
	
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
		
		String strParamFeedType = req.getParameter ("feedType");
		
		SyndFeedOutput mySyndFeedOutput = new SyndFeedOutput();
		
		// feed type für Ausgabe setzen
		SyndFeed mySyndFeed = new SyndFeedImpl();
		if(strParamFeedType == null) {
			mySyndFeed.setFeedType("rss_2.0");
		} else {
			mySyndFeed.setFeedType(strParamFeedType);
		}
		mySyndFeed.setEncoding("UTF-8");
		
		// feed channel anlegen
		mySyndFeed.setTitle("OANetzwerk Test-Feed");
		mySyndFeed.setAuthor("Projekt: Open Access Netzwerk");
		mySyndFeed.setLink("http://www.dini.de/projekte/oa-netzwerk/");
		mySyndFeed.setDescription("Proof of Concept der technischen Realisierung eines RSS Feed Export");
		
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
		
		
		String strXML = "";
		
		// im eingestellten Type zu xml marshallen
		try {
			strXML = mySyndFeedOutput.outputString(mySyndFeed);
		} catch (FeedException fex) {
			logger.error(fex.getLocalizedMessage(), fex);
		}
			
		return strXML;
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
