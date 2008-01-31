/**
 * 
 */

package de.dini.oanetzwerk.server.handler;


/**
 * @author Michael Kühn
 *
 */

public interface KeyWord2DatabaseInterface {
	
	/**
	 * This method is being called from the Rest-Server for handling the requests.
	 * 
	 * @param data the data transmitted to the Server within the HTTP-body
	 * @param path the request-path
	 * @param i specifies which HTTP-Method has been called
	 * @return Response which will be sent back to the Client
	 */
	
	public String processRequest (String data, String [ ] path, int i);
}
