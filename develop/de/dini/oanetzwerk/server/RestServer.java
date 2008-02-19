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
//import org.apache.log4j.xml.DOMConfigurator;

import de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael Kühn
 *
 */

@SuppressWarnings("serial")
public class RestServer extends HttpServlet {
	
	static Logger logger = Logger.getLogger (RestServer.class);
	PrintWriter out;
	
	public RestServer ( ) {
		
	}
	
	protected void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
		out.write (processRequest (req, 0));
	}
	
	/**
	 * @param req
	 * @param i 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String processRequest (HttpServletRequest req, int i) {
		
		String path [ ] = req.getPathInfo ( ).split ("/");
		
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
			
			return (((KeyWord2DatabaseInterface) o).processRequest (xml, path, i));
			
		} catch (ClassNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (InstantiationException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IllegalAccessException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
		}

		return null;
	}

	protected void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
		out.write (processRequest (req, 2));
	}

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
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}
}