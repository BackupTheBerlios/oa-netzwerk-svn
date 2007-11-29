/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Michael Kühn
 *
 */

@SuppressWarnings("serial")
public class RestServer extends HttpServlet {
	
	static Logger logger = Logger.getLogger (RestServer.class);
	PrintWriter out;
	
	public RestServer ( ) {
		
		//DOMConfigurator.configure ("log4j.xml");
	}
	
	protected void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
	}
	
	protected void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
	}

	@SuppressWarnings("unchecked")
	protected void doPut (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
		
		System.out.println (req.getRemoteUser ( ) + req.getRemoteHost ( ));
		
		String classname = "de.dini.oanetzwerk." + req.getRemoteUser ( ) + "2Database";
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("Class to be loaded: " + classname);
		
		String xml = HelperMethods.stream2String (req.getInputStream ( ));
		
		try {
			
			Class <Modul2Database> c = (Class <Modul2Database>) Class.forName (classname);
			Object o = c.newInstance ( );
			
			((Modul2Database) o).processRequest (xml);
			
		} catch (ClassNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (InstantiationException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IllegalAccessException ex) {
			
			ex.printStackTrace ( );
		}
	}

	protected void doDelete (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}
}
