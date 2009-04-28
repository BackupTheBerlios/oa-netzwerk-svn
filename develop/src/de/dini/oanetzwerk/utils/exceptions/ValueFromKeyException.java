package de.dini.oanetzwerk.utils.exceptions;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ValueFromKeyException extends Exception {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	public ValueFromKeyException ( ) {
		
		super ( );
	}
	
	/**
	 * @param message
	 */
	public ValueFromKeyException (String message) {

		super (message);
	}
	
	/**
	 * @param cause
	 */
	public ValueFromKeyException (Throwable cause) {

		super (cause);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ValueFromKeyException (String message, Throwable cause) {

		super (message, cause);
	}
}
