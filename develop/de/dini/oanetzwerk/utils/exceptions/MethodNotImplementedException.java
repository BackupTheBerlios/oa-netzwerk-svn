/**
 * 
 */

package de.dini.oanetzwerk.utils.exceptions;


/**
 * @author Michael Kühn
 *
 */

@SuppressWarnings("serial")
public class MethodNotImplementedException extends Exception {

	/**
	 * 
	 */
	public MethodNotImplementedException ( ) {

	}

	/**
	 * @param message
	 */
	public MethodNotImplementedException (final String message) {

		super (message);
	}

	/**
	 * @param cause
	 */
	public MethodNotImplementedException (final Throwable cause) {

		super (cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotImplementedException (final String message, final Throwable cause) {

		super (message, cause);
	}
}
