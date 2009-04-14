package de.dini.oanetzwerk.servicemodule.dcgenerator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DCGeneratorTest {
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.servicemodule.dcgenerator.DCGenerator#init()}.
	 */
	@Test
	public void testInit ( ) {

		DCGenerator generator = new DCGenerator ( );
		
		assertNotNull (generator);
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.servicemodule.dcgenerator.DCGenerator#run()}.
	 */
	@Test
	public void testRun ( ) {

		fail ("Not yet implemented");
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.servicemodule.dcgenerator.DCGenerator#shutdown()}.
	 */
	@Test
	public void testShutdown ( ) {

		fail ("Not yet implemented");
	}
	
}
