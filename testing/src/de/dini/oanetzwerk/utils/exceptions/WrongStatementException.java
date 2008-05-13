/**
 * 
 */

package de.dini.oanetzwerk.utils.exceptions;


/**
 * @author Michael K&uuml;hn
 *
 */

@SuppressWarnings("serial")
public class WrongStatementException extends Exception {
	
	public WrongStatementException ( ) {

		super ( );
	}
	
	public WrongStatementException (String message) {
		
		super (message);
	}
	
	public WrongStatementException (Throwable cause) {
		
		super (cause);
	}
	
	public WrongStatementException (String message, Throwable cause) {

		super (message, cause);
	}
}
