/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Kühn
 *
 */

public class RestServer extends HttpServlet {
	
	PrintWriter out;
	
	protected void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
	}
	
	protected void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );
	}

	protected void doPut (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		out = res.getWriter ( );

		String xml = HelperMethods.stream2String (req.getInputStream ( ));
		System.out.println (xml);
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
