/**
 * 
 */
package de.dini.oanetzwerk.admin.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael K&uuml;hn
 *
 */

public class RedirectionFilter implements Filter {
	
	public final static String EXT = "faces";
	
	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	
	public void destroy ( ) { }
	
	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	
	public void doFilter (ServletRequest servletrequest, ServletResponse servletresponse, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpservreq = (HttpServletRequest) servletrequest;
		HttpServletResponse httpservres = (HttpServletResponse) servletresponse;
		String uri = httpservreq.getRequestURI ( );
		
		if (uri.endsWith ("*.jsp")) {
			
			int length = uri.length ( );
			
			String newuri = uri.substring (0, length - 3) + EXT;
			
			httpservres.sendRedirect (newuri);
			
		} else
			httpservres.sendRedirect ("index.faces");
	}
	
	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	
	public void init (FilterConfig arg0) throws ServletException { }
	
}
