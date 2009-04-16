/**
 * 
 */

package de.dini.oanetzwerk.utils.exceptions;


/**
 * @author Manuel Klatt-Kafemann
 *
 */

@SuppressWarnings("serial")
public class AggregationWarningException extends Exception {

	/**
	 * 
	 */
	public AggregationWarningException ( ) {

	}

	/**
	 * @param message
	 */
	public AggregationWarningException (final String message) {

		super (message);
	}

	/**
	 * @param cause
	 */
	public AggregationWarningException (final Throwable cause) {

		super (cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AggregationWarningException (final String message, final Throwable cause) {

		super (message, cause);
	}
}
