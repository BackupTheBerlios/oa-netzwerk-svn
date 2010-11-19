package de.dini.oanetzwerk.server.handler;

import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * Interface which has to be implemented by all rest-keywords to provide a correct processRequest-method.
 * 
 * @author Michael K&uuml;hn
 */

public interface KeyWord2DatabaseInterface {
	
	/**
	 * This method is being called from the Rest-Server for handling all requests.
	 * 
	 * @param data the data transmitted to the Server within the HTTP-body
	 * @param path the request-path (without server name and keyword)
	 * @param verb specifies which HTTP-Method has been called
	 * @return Response which will be sent back to the Client
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException
	 */
	
	public String processRequest (String data, String [ ] path, HttpVerbEnum verb) throws NotEnoughParametersException, MethodNotImplementedException;
	
	/**
	 * This method is being called from the Rest-Server for handling all requests.
	 * 
	 * @param data the data transmitted to the Server within the HTTP-body
	 * @param path the request-path (without server name and keyword)
	 * @param verb specifies which HTTP-Method has been called
	 * @param dataSource the data source where the handler will connect to
	 * @return Response which will be sent back to the Client
	 * @throws NotEnoughParametersException 
	 * @throws MethodNotImplementedException
	 */
	
	public String processRequest (String data, String [ ] path, HttpVerbEnum verb, String dataSource) throws NotEnoughParametersException, MethodNotImplementedException;
}
