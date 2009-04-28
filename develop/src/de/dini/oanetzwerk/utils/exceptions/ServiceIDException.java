package de.dini.oanetzwerk.utils.exceptions;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ServiceIDException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ServiceIDException ( ) {

		super ( );
	}
	
	/**
	 * @param message
	 */
	public ServiceIDException (String message) {

		super (message);
	}
	
	/**
	 * @param cause
	 */
	public ServiceIDException (Throwable cause) {

		super (cause);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ServiceIDException (String message, Throwable cause) {

		super (message, cause);
	}
}
