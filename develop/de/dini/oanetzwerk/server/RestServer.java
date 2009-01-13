package de.dini.oanetzwerk.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.handler.HttpVerbEnum;
import de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * The restserver provides a servlet, which can be connected by GET, POST, PUT and DELETE. Most servlet containers
 * have to be prepared to support PUT- and DELETE-Methods.
 * With the use of reflection the keyword, which is transmitted via the 2nd path-parameter (splitted by '/'),
 * is loaded and the request will be requested in the instance of the specific-keyword-class.
 * @see {@link de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface}
 * @see {@link de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler}
 * 
 * @author Michael K&uuml;hn
 */

public class RestServer extends HttpServlet implements Serializable {
	
	/**
	 * Serial Number for serialisation.
	 * Serialisation is important for load balancing within the servlet container. 
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * The static log4j logger. All debug logging will be made with the help of this nice static logger.
	 */
	
	private static Logger logger = Logger.getLogger (RestServer.class);
	
	/**
	 * PrintWriter object that is used for sending a response to the client.  
	 */
	
	private PrintWriter out = null;
	
	/**
	 * Assistant object for sending the response to the client.
	 */
	
	private HttpServletResponse response;
	
	/**
	 * Standard Constructor. Does nothing.
	 */
	
	public RestServer ( ) { }
	
	/**
	 * Processes all request, no matter if GET, PUT, POST, DELETE.
	 * For choosing the correct keyword-handler, the second segment of the request path is used to
	 * load an instance of the keyword-handler-object via reflection.
	 * The first two segments of the request path are truncated and the rest is sent to {@link de.dini.oanetzwerk.server.AbstractKeyWordHandler.processRequest}
	 * of the instance of the keyword handler object.
	 * If something goes wrong, which means an exception occurred, logfiles will be filled and an errorMessage will be created. 
	 * 
	 * @param req the http servlet request object received by every do*-method
	 * @param verb HttpVerbEnum specifies the correct verb (GET, PUT, POST, DELETE)
	 * @return the answer for the client
	 */
	
	@SuppressWarnings("unchecked")
	private String processRequest (HttpServletRequest req, HttpVerbEnum verb) {
		
		if (req.getPathInfo ( ) == null || req.getPathInfo ( ).length ( ) < 2) {
			
			// Keyword was not specified, so we have nothing to do beside sending an error to the client 
			
			RestMessage rms = new RestMessage (RestKeyword.UNKNOWN);
			rms.setStatus (RestStatusEnum.NOT_ENOUGH_PARAMETERS_ERROR);
			StringBuffer sbDesc = new StringBuffer ( );
			sbDesc.append ("Sie haben in der URL keine Ressource benannt. Folgende Schlüsselworte sind momentan über die REST-Schnittstelle verfügbar: ");
			
			for (RestKeyword name : RestKeyword.values ( )) {
				
				if (name != RestKeyword.UNKNOWN)
					sbDesc.append (name.toString ( ) + " ");
			}
			
			try {
				
				rms.setStatusDescription (new String ((sbDesc.toString ( )).getBytes ( ), "UTF-8"));
				
			} catch (UnsupportedEncodingException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
			
			return RestXmlCodec.encodeRestMessage (rms);
		}
		
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
			
			if (verb == HttpVerbEnum.POST || verb == HttpVerbEnum.PUT) {
				
				// if verb POST or PUT, there should be some data in the body
				
				InputStream in = req.getInputStream ( );
				
				xml = HelperMethods.stream2String (in);
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("XML: " + new String (Base64.decodeBase64 (xml.getBytes ("UTF-8"))));
			}
			
			// new instance of the requested keyword handler is constructed 
			
			Class <KeyWord2DatabaseInterface> c = (Class <KeyWord2DatabaseInterface>) Class.forName (classname);
			Object o = c.newInstance ( );
			
			String [ ] pathwithoutkeyword = new String [path.length - 2];
			System.arraycopy (path, 2, pathwithoutkeyword, 0, path.length - 2);
			
			// calling the processing method of the object's instance and returning the result
			
			return (((KeyWord2DatabaseInterface) o).processRequest (xml, pathwithoutkeyword, verb));
			
		} catch (ClassNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.CLASS_NOT_FOUND_ERROR);
			
		} catch (InstantiationException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.CLASS_COULD_NOT_BE_INSTANTIATED_ERROR);
			
		} catch (IllegalAccessException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.ILLEGAL_ACCESS_ERROR);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.IO_ERROR);
			
		} catch (NotEnoughParametersException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.NOT_ENOUGH_PARAMETERS_ERROR);
			
		} catch (MethodNotImplementedException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
			return this.createErrorResponse (ex, RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		}
	}
	
	/**
	 * This is the central error message constructing method.
	 * The rest message will be constructed and encoded according to the received error (which means exception)
	 * 
	 * @param ex the throw exception
	 * @param restStatusEnum the status for the rest message
	 * @return the error response for the client
	 */
	
	private String createErrorResponse (Exception ex, RestStatusEnum restStatusEnum) {
		
		if (ex == null) {
			
			// if exception is null, we need a new one to throw, an unknown one
			
			ex = new Exception ("Unknown Error occured");
			logger.warn ("unknown error occured!");
		}
		
		if (restStatusEnum == null) {
			
			// if error status is null, we should create an new unknown one
			
			restStatusEnum = RestStatusEnum.UNKNOWN_ERROR;
			logger.warn ("unknown error occured!");
		}
		
		RestMessage rms = new RestMessage (RestKeyword.UNKNOWN);
		rms.setStatus (restStatusEnum);
		rms.setStatusDescription (ex.getLocalizedMessage ( ));
		
		// setting the http response status to 400, maybe another status could fit better 
		
		this.response.setStatus (HttpServletResponse.SC_BAD_REQUEST);
		
		return RestXmlCodec.encodeRestMessage (rms);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.response = res;
		this.setOANResponseHeader ( );
		
		this.out = this.response.getWriter ( );
		this.out.write (this.processRequest (req, HttpVerbEnum.GET));
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.response = res;
		this.setOANResponseHeader ( );
		
		this.out = this.response.getWriter ( );
		this.out.write (this.processRequest (req, HttpVerbEnum.POST));
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	@Override
	protected void doPut (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.response = res;
		this.setOANResponseHeader ( );
		
		this.out = this.response.getWriter ( );
		this.out.write (this.processRequest (req, HttpVerbEnum.PUT));
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	@Override
	protected void doDelete (HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		this.response = res;
		this.setOANResponseHeader ( );
		
		this.out = this.response.getWriter ( );
		this.out.write (this.processRequest (req, HttpVerbEnum.DELETE));
	}
	
	/**
	 * The response's header will be set here.
	 */
	
	private void setOANResponseHeader ( ) {

		this.response.setCharacterEncoding ("UTF-8");
		this.response.setContentType ("application/xml; charset=UTF-8");
	}
}
