/**
 * 
 */

package de.dini.oanetzwerk.utils.exceptions;


/**
 * @author Michael Kühn
 *
 */

@SuppressWarnings("serial")
public class AggregationFailedException extends Exception {

	/**
	 * 
	 */
	public AggregationFailedException ( ) {

	}

	/**
	 * @param message
	 */
	public AggregationFailedException (final String message) {

		super (message);
	}

	/**
	 * @param cause
	 */
	public AggregationFailedException (final Throwable cause) {

		super (cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AggregationFailedException (final String message, final Throwable cause) {

		super (message, cause);
	}
}
