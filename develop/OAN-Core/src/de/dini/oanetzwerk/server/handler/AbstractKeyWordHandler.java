package de.dini.oanetzwerk.server.handler;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * The KeyWordhandler is an abstract class which provides the necessary method processRequest. This method
 * will be called from the Restserver and chooses the right method to process the Request for the given
 * keyword.
 * All other methods are abstract and must be implemented in sub-classes.
 * All classes MUST implement the interface KeyWord2DatabaseInterface to ensure the existence of the necessary
 * method processRequest.
 * 
 * @author Michael K&uuml;hn
 */

public abstract class AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (AbstractKeyWordHandler.class);
	
	/**
	 * 
	 */
	
	private String dataSource;
	
	/**
	 * 
	 */
	
	protected RestMessage rms;
	
	/**
	 * 
	 */
	
	protected QueryResult result;
	
	/**
	 * This is the standard constructor which calls the super class. 
	 * @param objectName 
	 */
	
	public AbstractKeyWordHandler (String objectName, RestKeyword rkw) {
		
		super ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug (objectName + " is called");
		
		this.rms = new RestMessage (rkw);
	}
	
	/**
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface#processRequest(java.lang.String, java.lang.String[], int)
	 */
	
	final public String processRequest (String data, String [ ] path, HttpVerbEnum verb) throws NotEnoughParametersException, MethodNotImplementedException {
			
		switch (verb) {
			
			case GET:
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("GET case chosen");
				
				return getKeyWord (path);
				
			case DELETE:
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DELETE case chosen");
							
				return deleteKeyWord (path);
				
			case POST:
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("POST case chosen");
				
				return postKeyWord (path, data);
				
			case PUT:
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("PUT case chosen");
		
				return putKeyWord (path, data);
				
			case HEAD:
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("HEAD case chosen");
				
				return headKeyWord (path);
				
			default:
				
				logger.error ("This HTTP-Method is not supported! Please use GET, POST, PUT or DELETE!");
				return "null";
		}
	}
	
	/**
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface#processRequest(java.lang.String, java.lang.String[], int, java.lang.String)
	 */
	
	final public String processRequest (String data, String [ ] path, HttpVerbEnum verb, String dataSource) throws NotEnoughParametersException, MethodNotImplementedException {
		
		this.setDataSource (dataSource);
		
		return processRequest (data, path, verb);
	}
	
	
	/**
	 * @return the dataSource
	 */
	
	protected final String getDataSource ( ) {
		
		if (this.dataSource == null || this.dataSource.equals (""))
			this.dataSource = "jdbc/oanetztest";
		
		return this.dataSource;
	}
	

	
	/**
	 * @param dataSource the dataSource to set
	 */
	
	private final void setDataSource (String dataSource) {
	
		this.dataSource = dataSource;
	}
	
	/**
	 * Auslagerung der in allen Handlerklassen exzessiv benutzten Warning-Codefragmente
	 * Diese Methode kann für beliebige SQL Queries angewendet werden
	 * @param queryResult Resultat eines SQL Queries
	 */
	protected void logWarnings(QueryResult queryResult) {
		if (queryResult.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
	}
	protected void logWarnings() {
		if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
	}
	

	/**
	 * This method handles the HTTP-PUT Request which inserts new data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * @param data the data transmitted within the HTTP-Body which will be inserted
	 * 
	 * @return the response which will be sent back to the client
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException 
	 * @throws WrongStatementException 
	 */
	
	abstract protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException, MethodNotImplementedException;

	/**
	 * This method handles the HTTP-POST Request which updates data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * @param data the data transmitted within the HTTP-Body which will used for the update
	 * 
	 * @return the response which will be sent back to the client
	 * @throws MethodNotImplementedException 
	 * @throws NotEnoughParametersException 
	 */
	
	abstract protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException, NotEnoughParametersException;

	/**
	 * This method handles the HTTP-DELETE Request which deletes data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * 
	 * @return the response which will be sent back to the client
	 * @throws MethodNotImplementedException 
	 * @throws NotEnoughParametersException 
	 */
	
	abstract protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException, NotEnoughParametersException;

	/**
	 * This method handles the HTTP-GET Request which selects data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * 
	 * @return the response which will be sent back to the client
	 * @throws NotEnoughParametersException 
	 */
	
	abstract protected String getKeyWord (String [ ] path) throws NotEnoughParametersException;
	
	/**
	 * This method handles the HTTP-HEAD Request which selects data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * 
	 * @return the response which will be sent back to the client
	 * @throws NotEnoughParametersException 
	 */
	
	protected String headKeyWord (String [ ] path) throws NotEnoughParametersException {
		rms = new RestMessage ();
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("HEAD method is not implemented for ressource '"+ this.getClass().getSimpleName() +"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);	
	}
	
} // end of class
