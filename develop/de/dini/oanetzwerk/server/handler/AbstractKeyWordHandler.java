/**
 *
 */

package de.dini.oanetzwerk.server.handler;

import org.apache.log4j.Logger;


/**
 * @author Michael Kühn
 *
 * The KeyWordhandler is an abstract class which provides the necessary method processRequest. This method
 * will be called from the Restserver and chooses the right method to process the Request for the given
 * keyword.
 * All other methods are abstract and must be implemented in sub-classes.
 * All classes MUST implement the interface KeyWord2DatabaseInterface to ensure the existence of the necessary
 * method processRequest.
 */

public abstract class AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	protected static Logger logger = Logger.getLogger (AbstractKeyWordHandler.class);

	/**
	 * This is the standard constructor which calls the super class. 
	 */
	
	public AbstractKeyWordHandler ( ) {
		
		super ( );
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface#processRequest(java.lang.String, java.lang.String[], int)
	 */
	
	final public String processRequest (String data, String [ ] path, int i) {
			
		switch (i) {
		case 0:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("GET case chosen");
			
			return getKeyWord (path);
			
		case 1:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("DELTE case chosen");
						
			return deleteKeyWord (path);
			
		case 2:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("POST case chosen");
			
			return postKeyWord (path, data);
			
		case 3:
			
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
	 */
	
	abstract protected String putKeyWord (String [ ] path, String data);

	/**
	 * This method handles the HTTP-POST Request which updates data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * @param data the data transmitted within the HTTP-Body which will used for the update
	 * 
	 * @return the response which will be sent back to the client
	 */
	
	abstract protected String postKeyWord (String [ ] path, String data);

	/**
	 * This method handles the HTTP-DELETE Request which deletes data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * 
	 * @return the response which will be sent back to the client
	 */
	
	abstract protected String deleteKeyWord (String [ ] path);

	/**
	 * This method handles the HTTP-GET Request which selects data.
	 * 
	 * @param path the request path from the HTTP-Request
	 * 
	 * @return the response which will be sent back to the client
	 */
	
	abstract protected String getKeyWord (String [ ] path);
} // end of class
