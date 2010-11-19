package de.dini.oanetzwerk.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Michael K&uuml;hn
 *
 */

public class TestRestServer {
	
	protected StringWriter writer;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass ( ) throws Exception {
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass ( ) throws Exception {

	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp ( ) throws Exception {
		
		this.writer = new StringWriter ( );
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown ( ) throws Exception {
		
		this.writer.flush ( );
		this.writer.close ( );
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#RestServer()}.
	 */
	@Test
	public final void testRestServer ( ) {

		RestServer server = new RestServer ( );
		assertNotNull (server);
		server = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public final void testDoGetHttpServletRequestHttpServletResponse ( ) throws IOException {
		
		RestServer server = new RestServer ( );
		server.doGet (new NullHttpServletRequest ( ), new NullHttpServletResponse ( ));
		
		assertNotNull (this.writer.getBuffer ( ));
		assertTrue (this.writer.getBuffer ( ).toString ( ).startsWith ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<oanrest xmlns:xsi"));
		assertTrue (this.writer.getBuffer ( ).toString ( ).endsWith ("</oanrest>\n"));
		server = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public final void testDoPostHttpServletRequestHttpServletResponse ( ) throws IOException {

		RestServer server = new RestServer ( );
		server.doPost (new NullHttpServletRequest ( ), new NullHttpServletResponse ( ));
		
		assertNotNull (this.writer.getBuffer ( ));
		assertTrue (this.writer.getBuffer ( ).toString ( ).startsWith ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<oanrest xmlns:xsi"));
		assertTrue (this.writer.getBuffer ( ).toString ( ).endsWith ("</oanrest>\n"));
		server = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public final void testDoPutHttpServletRequestHttpServletResponse ( ) throws IOException {

		RestServer server = new RestServer ( );
		server.doPut (new NullHttpServletRequest ( ), new NullHttpServletResponse ( ));
		
		assertNotNull (this.writer.getBuffer ( ));
		assertTrue (this.writer.getBuffer ( ).toString ( ).startsWith ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<oanrest xmlns:xsi"));
		assertTrue (this.writer.getBuffer ( ).toString ( ).endsWith ("</oanrest>\n"));
		server = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public final void testDoDeleteHttpServletRequestHttpServletResponse ( ) throws IOException {

		RestServer server = new RestServer ( );
		server.doDelete (new NullHttpServletRequest ( ), new NullHttpServletResponse ( ));
		
		assertNotNull (this.writer.getBuffer ( ));
		assertTrue (this.writer.getBuffer ( ).toString ( ).startsWith ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<oanrest xmlns:xsi"));
		assertTrue (this.writer.getBuffer ( ).toString ( ).endsWith ("</oanrest>\n"));
		server = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.RestServer#getServerproperties()}.
	 */
	@Test
	public final void testGetServerproperties ( ) {
		
		RestServer server = new RestServer ( );
		
		assertNull (server.getServerproperties ( ));
		
		server = null;
	}
	
	class NullHttpServletRequest implements HttpServletRequest {
		
		public Hashtable <String, String> parameters = new Hashtable <String, String> ( );
		
		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getAuthType()
		 */
		@Override
		public String getAuthType ( ) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getContextPath()
		 */
		@Override
		public String getContextPath ( ) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getCookies()
		 */
		@Override
		public Cookie [ ] getCookies ( ) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
		 */
		@Override
		public long getDateHeader (String arg0) { return 0; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
		 */
		@Override
		public String getHeader (String arg0) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Enumeration getHeaderNames ( ) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Enumeration getHeaders (String arg0) { return null; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
		 */
		@Override
		public int getIntHeader (String arg0) { return 0; }

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getMethod()
		 */
		@Override
		public String getMethod ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
		 */
		@Override
		public String getPathInfo ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
		 */
		@Override
		public String getPathTranslated ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getQueryString()
		 */
		@Override
		public String getQueryString ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
		 */
		@Override
		public String getRemoteUser ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
		 */
		@Override
		public String getRequestURI ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
		 */
		@Override
		public StringBuffer getRequestURL ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
		 */
		@Override
		public String getRequestedSessionId ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getServletPath()
		 */
		@Override
		public String getServletPath ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getSession()
		 */
		@Override
		public HttpSession getSession ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
		 */
		@Override
		public HttpSession getSession (boolean arg0) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
		 */
		@Override
		public Principal getUserPrincipal ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
		 */
		@Override
		public boolean isRequestedSessionIdFromCookie ( ) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
		 */
		@Override
		public boolean isRequestedSessionIdFromURL ( ) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
		 */
		@Override
		public boolean isRequestedSessionIdFromUrl ( ) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
		 */
		@Override
		public boolean isRequestedSessionIdValid ( ) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
		 */
		@Override
		public boolean isUserInRole (String arg0) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
		 */
		@Override
		public Object getAttribute (String arg0) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getAttributeNames()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Enumeration getAttributeNames ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getCharacterEncoding()
		 */
		@Override
		public String getCharacterEncoding ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getContentLength()
		 */
		@Override
		public int getContentLength ( ) { return 0;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getContentType()
		 */
		@Override
		public String getContentType ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getInputStream()
		 */
		@Override
		public ServletInputStream getInputStream ( ) throws IOException { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getLocalAddr()
		 */
		@Override
		public String getLocalAddr ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getLocalName()
		 */
		@Override
		public String getLocalName ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getLocalPort()
		 */
		@Override
		public int getLocalPort ( ) { return 0;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getLocale()
		 */
		@Override
		public Locale getLocale ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getLocales()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Enumeration getLocales ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter (String key) { 
			
			return (String) this.parameters.get (key);
		}
		
		/**
		 * @param key
		 * @param value
		 */
		
		public void setParameter (String key, String value) {
			
			this.parameters.put (key, value);
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getParameterMap()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Map getParameterMap ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getParameterNames()
		 */
		@Override
		public Enumeration <String> getParameterNames ( ) {
			
			return this.parameters.elements ( );
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
		 */
		@Override
		public String [ ] getParameterValues (String arg0) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getProtocol()
		 */
		@Override
		public String getProtocol ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getReader()
		 */
		@Override
		public BufferedReader getReader ( ) throws IOException { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
		 */
		@Override
		public String getRealPath (String arg0) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getRemoteAddr()
		 */
		@Override
		public String getRemoteAddr ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getRemoteHost()
		 */
		@Override
		public String getRemoteHost ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getRemotePort()
		 */
		@Override
		public int getRemotePort ( ) { return 0;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
		 */
		@Override
		public RequestDispatcher getRequestDispatcher (String arg0) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getScheme()
		 */
		@Override
		public String getScheme ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getServerName()
		 */
		@Override
		public String getServerName ( ) { return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#getServerPort()
		 */
		@Override
		public int getServerPort ( ) { return 0;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#isSecure()
		 */
		@Override
		public boolean isSecure ( ) { return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
		 */
		@Override
		public void removeAttribute (String arg0) { }

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
		 */
		@Override
		public void setAttribute (String arg0, Object arg1) { }

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
		 */
		@Override
		public void setCharacterEncoding (String arg0) throws UnsupportedEncodingException { }
	}
	
	class NullHttpServletResponse implements HttpServletResponse {

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
		 */
		@Override
		public void addCookie (Cookie arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
		 */
		@Override
		public void addDateHeader (String arg0, long arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
		 */
		@Override
		public void addHeader (String arg0, String arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
		 */
		@Override
		public void addIntHeader (String arg0, int arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
		 */
		@Override
		public boolean containsHeader (String arg0) {
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
		 */
		@Override
		public String encodeRedirectURL (String arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
		 */
		@Override
		public String encodeRedirectUrl (String arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
		 */
		@Override
		public String encodeURL (String arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
		 */
		@Override
		public String encodeUrl (String arg0) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#sendError(int)
		 */
		@Override
		public void sendError (int arg0) throws IOException {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
		 */
		@Override
		public void sendError (int arg0, String arg1) throws IOException {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
		 */
		@Override
		public void sendRedirect (String arg0) throws IOException {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
		 */
		@Override
		public void setDateHeader (String arg0, long arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
		 */
		@Override
		public void setHeader (String arg0, String arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
		 */
		@Override
		public void setIntHeader (String arg0, int arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
		 */
		@Override
		public void setStatus (int arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
		 */
		@Override
		public void setStatus (int arg0, String arg1) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#flushBuffer()
		 */
		@Override
		public void flushBuffer ( ) throws IOException {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getBufferSize()
		 */
		@Override
		public int getBufferSize ( ) {
			return 0;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getCharacterEncoding()
		 */
		@Override
		public String getCharacterEncoding ( ) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getContentType()
		 */
		@Override
		public String getContentType ( ) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getLocale()
		 */
		@Override
		public Locale getLocale ( ) {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getOutputStream()
		 */
		@Override
		public ServletOutputStream getOutputStream ( ) throws IOException {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#getWriter()
		 */
		@Override
		public PrintWriter getWriter ( ) throws IOException {
			
			return new PrintWriter (writer);
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#isCommitted()
		 */
		@Override
		public boolean isCommitted ( ) {
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#reset()
		 */
		@Override
		public void reset ( ) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#resetBuffer()
		 */
		@Override
		public void resetBuffer ( ) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#setBufferSize(int)
		 */
		@Override
		public void setBufferSize (int arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
		 */
		@Override
		public void setCharacterEncoding (String arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#setContentLength(int)
		 */
		@Override
		public void setContentLength (int arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
		 */
		@Override
		public void setContentType (String arg0) {
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
		 */
		@Override
		public void setLocale (Locale arg0) {
		}
	}
}
