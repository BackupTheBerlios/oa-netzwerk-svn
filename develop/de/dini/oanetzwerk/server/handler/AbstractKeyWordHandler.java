package de.dini.oanetzwerk.server.handler;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
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

	protected static Logger logger = Logger.getLogger (AbstractKeyWordHandler.class);
	protected RestMessage rms;
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
			
		default:
			
			logger.error ("This HTTP-Method is not supported! Please use GET, POST, PUT or DELETE!");
			return "null";
		}
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
} // end of class
