/**
 * 
 */
package de.dini.oanetzwerk.userfrontend;

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

	public final static String DEFAULT_EXT = ".faces";
	public final static String DEFAULT_PATH = "/oansearch";

	/**
	 * @see javax.servlet.Filter#destroy()
	 */

	public void destroy() {
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */

	public void doFilter(ServletRequest servletrequest,
			ServletResponse servletresponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpservreq = (HttpServletRequest) servletrequest;
		HttpServletResponse httpservres = (HttpServletResponse) servletresponse;
		String uri = httpservreq.getRequestURI();

		if (uri.equals(DEFAULT_PATH + "/") || uri.equals(DEFAULT_PATH)) {
			httpservres.sendRedirect(DEFAULT_PATH + "/start.faces");
			return;
		}

		if (uri.endsWith(DEFAULT_EXT)) {

			int length = uri.length();

			if (!uri.contains("pages")) {
				if (uri.startsWith(DEFAULT_PATH)) {
					String newuri = uri.substring(0, DEFAULT_PATH.length() + 1)
							+ "pages/"
							+ uri.substring(DEFAULT_PATH.length() + 1, uri
									.length());
					System.out.println(newuri);
					httpservres.sendRedirect(newuri);
				}
			} else {
				chain.doFilter(servletrequest, servletresponse);
			}

		} else {
			if (uri.contains(".")) {

				chain.doFilter(servletrequest, servletresponse);
			} else {

				if (!uri.contains(DEFAULT_PATH)) {

					httpservres.sendRedirect(DEFAULT_PATH);
					return;
				}
				String newuri = uri + DEFAULT_EXT;
				httpservres.sendRedirect(newuri);
			}
		}

	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */

	public void init(FilterConfig arg0) throws ServletException {
	}

}
