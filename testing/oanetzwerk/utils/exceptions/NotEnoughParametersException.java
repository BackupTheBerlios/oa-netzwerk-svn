/**
 * 
 */

package de.dini.oanetzwerk.utils.exceptions;


/**
 * @author Michael Kühn
 *
 */

@SuppressWarnings("serial")
public class NotEnoughParametersException extends Exception {

	/**
	 * 
	 */
	public NotEnoughParametersException ( ) {

	}

	/**
	 * @param message
	 */
	public NotEnoughParametersException (String message) {

		super (message);
	}

	/**
	 * @param cause
	 */
	public NotEnoughParametersException (Throwable cause) {

		super (cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotEnoughParametersException (String message, Throwable cause) {

		super (message, cause);
	}
}
