package de.dini.oanetzwerk.servicemodule.dcgenerator;

/**
 * @author Michael K&uuml;hn
 *
 */

public class RunDCGenerator {
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		DCGenerator dcGenerator = new DCGenerator ( );
		
		dcGenerator.init ( );
		dcGenerator.run ( );
		dcGenerator.shutdown ( );
	}
}
