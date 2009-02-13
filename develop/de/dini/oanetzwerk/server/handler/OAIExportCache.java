package de.dini.oanetzwerk.server.handler;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * @author Michael K&uuml;hn
 *
 */

public class OAIExportCache extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface  {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (OAIExportCache.class);
	
	/**
	 * 
	 */
	
	public OAIExportCache ( ) {

		super (OAIExportCache.class.getName ( ), RestKeyword.OAIExportCache);
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException, NotEnoughParametersException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException, NotEnoughParametersException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException, MethodNotImplementedException {

		// TODO Auto-generated method stub
		return null;
	}
}
