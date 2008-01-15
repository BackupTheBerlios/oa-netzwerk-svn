/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

/**
 * @author Michael Kühn
 *
 */
public abstract class AbstractKeyWordHandler implements Modul2Database {

	protected static Logger logger = Logger.getLogger (AbstractKeyWordHandler.class);

	/**
	 * 
	 */
	public AbstractKeyWordHandler ( ) {

		super ( );
	}

	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest(java.lang.String, java.lang.String[], int)
	 */
	
	public String processRequest (String data, String [ ] path, int i) {
			
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
			break;
		}
		
		return "null";
	}

	/**
	 * @param path
	 * @param data
	 * @return
	 */
	abstract protected String putKeyWord (String [ ] path, String data);

	/**
	 * @param path
	 * @param data
	 * @return
	 */
	abstract protected String postKeyWord (String [ ] path, String data);

	/**
	 * @param path
	 * @return
	 */
	abstract protected String deleteKeyWord (String [ ] path);

	/**
	 * @param path
	 * @return
	 */
	abstract protected String getKeyWord (String [ ] path);

}
