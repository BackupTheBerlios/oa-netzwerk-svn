package de.dini.oanetzwerk.oaipmh;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHerrorcodeType;

/**
 * @author Michael K&uuml;hn
 *
 */

public class OAIPMHHandler extends HttpServlet {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (OAIPMHHandler.class);
	
	/**
	 * 
	 */
	
	public OAIPMHHandler ( ) { }
	
	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init (ServletConfig config) throws ServletException {
		
		super.init (config);
		new File ("webapps/oaipmh/resumtionToken").mkdirs ( );
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding ("UTF-8");
		resp.setCharacterEncoding ("UTF-8");
		
		String userAgent = req.getHeader("User-Agent");
		
		if (userAgent == null)
			userAgent = "";
		
		if (userAgent.matches ("mozilla") || userAgent.matches ("opera") || userAgent.matches ("msie") || userAgent.matches ("netscape")) {
			
			resp.setContentType ("text/html");
			
		} else {
		
			resp.setContentType ("application/xml");
		
		}
		resp.getWriter ( ).write (this.getResponse (req, resp));
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getResponse (HttpServletRequest req, HttpServletResponse resp) {
		
		String verb = req.getParameter ("verb");
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Verb: " + verb);
		
		Map parameterMap = req.getParameterMap ( );
		
		String classname = "de.dini.oanetzwerk.oaipmh." + verb;
		
		try {
			
			Class <OAIPMHVerbs> c = (Class <OAIPMHVerbs>) Class.forName (classname);
			Object o = c.newInstance ( );
			
			return ((OAIPMHVerbs) o).processRequest (parameterMap);
			
		} catch (ClassNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			return (this.getErrorMessage (ex, HttpStatus.SC_NOT_IMPLEMENTED));
			
		} catch (InstantiationException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			return (this.getErrorMessage (ex, HttpStatus.SC_INTERNAL_SERVER_ERROR));
			
		} catch (IllegalAccessException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			return (this.getErrorMessage (ex, HttpStatus.SC_INTERNAL_SERVER_ERROR));
		}
	}

	/**
	 * @param exception
	 * @param httpStatus
	 * @return
	 */
	
	private String getErrorMessage (Exception exception, int httpStatus) {
		
		return new OAIPMHError (OAIPMHerrorcodeType.BAD_VERB).toString ( );
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
