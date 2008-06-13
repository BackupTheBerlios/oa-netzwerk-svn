/**
 * 
 */

package de.dini.oanetzwerk.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.*;
import de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

//TODO: Comments!!!

/**
 * @author Michael K&uuml;hn
 *
 */

@SuppressWarnings("serial")
public class RestServer extends HttpServlet {
	
	static Logger logger = Logger.getLogger (RestServer.class);
	PrintWriter out = null;
	
	/**
	 * Standard Constructor 
	 */
	
	public RestServer ( ) {
		
	}
	
	/**
	 * @param req
	 * @param i 
	 * @return
	 */
	
	//TODO: Better name for variable i
	
	@SuppressWarnings("unchecked")
	private String processRequest (HttpServletRequest req, int i) {
		
		String path [ ] = req.getPathInfo ( ).split ("/");
		
		if (path.length < 1)
			return createErrorResponse (new NotEnoughParametersException ("No keyword specified"), RestStatusEnum.NOT_ENOUGH_PARAMETERS_ERROR);
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("servlet: " + req.getContextPath ( ));
			logger.debug ("PATH: " + req.getPathInfo ( ));
			logger.debug ("PATH[0]: " + path [0]);
			logger.debug ("PATH[1]: " + path [1]);
		}
		
		String xml = "";
		String classname = "de.dini.oanetzwerk.server.handler." + path [1];
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Class to be loaded: " + classname);
			
		try {
			
			if (i > 1)
				 xml = HelperMethods.stream2String (req.getInputStream ( ));
			
			Class <KeyWord2DatabaseInterface> c = (Class <KeyWord2DatabaseInterface>) Class.forName (classname);
			Object o = c.newInstance ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug (Class.forName (classname) + " is created");
			
			String [ ] pathwithoutkeyword = new String [path.length - 2];
			System.arraycopy (path, 2, pathwithoutkeyword, 0, path.length - 2);
			
			return (((KeyWord2DatabaseInterface) o).processRequest (xml, pathwithoutkeyword, i));
			
		} catch (ClassNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.CLASS_NOT_FOUND_ERROR);
			
		} catch (InstantiationException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.CLASS_COULD_NOT_BE_INSTANTIATED_ERROR);
			
		} catch (IllegalAccessException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.ILLEGAL_ACCESS_ERROR);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.IO_ERROR);
			
		} catch (NotEnoughParametersException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.NOT_ENOUGH_PARAMETERS_ERROR);
			
		} catch (MethodNotImplementedException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
			return createErrorResponse (ex, RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		}
		
		// This section is unreachable
	}

	/**
	 * @param ex
	 * @param restStatusEnum 
	 * @return
	 */
	
	private String createErrorResponse (Exception ex, RestStatusEnum restStatusEnum) {
		
		if (ex == null) {
			
			ex = new Exception ("Unknown Error occured");
			logger.warn ("unknown error occured!");
		}
		
		if (restStatusEnum == null) {
			
			restStatusEnum = RestStatusEnum.UNKNOWN_ERROR;
			logger.warn ("unknown error occured!");
		}
		
		RestMessage rms = new RestMessage (RestKeyword.UNKNOWN);
		rms.setStatus (restStatusEnum);
		rms.setStatusDescription (ex.getLocalizedMessage ( ));
		
		return RestXmlCodec.encodeRestMessage (rms);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	protected void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.out = res.getWriter ( );
		this.out.write (processRequest (req, 0));
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	protected void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.out = res.getWriter ( );
		this.out.write (processRequest (req, 2));
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	protected void doPut (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
		out.write (processRequest (req, 3));
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	protected void doDelete (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
		out.write (processRequest (req, 1));
	}
}
