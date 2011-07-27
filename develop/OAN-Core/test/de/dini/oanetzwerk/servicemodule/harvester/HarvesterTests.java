/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;


/**
 * @author Michael K&uuml;hn
 *
 */

public class HarvesterTests {
	
	Harvester harvester;
	
	@Before public void setUp ( ) {
		
		harvester = new Harvester("/home/mkuehn/workspace/oa-netzwerk-develop/");
		harvester.setPropertyfile ("harvesterprop.xml");
	}
	
	@org.junit.Test
	public void testprepareHarvester ( ) {
		
		harvester = new Harvester ( );
		assertNotNull (harvester);
		assertTrue (harvester.prepareHarvester (1));
		assertFalse (harvester.getProps ( ).isEmpty ( ));
		harvester = null;
		assertNull (harvester);
	}
	
	@SuppressWarnings("static-access")
	@org.junit.Test
	public void testfilter ( ) {
		
		assertTrue (Harvester.filterAmount ("1") == 1);
		assertFalse (Harvester.filterAmount ("10") == 1);
		
		try {
			
			Harvester.filterAmount ("-10");
			fail ("Here should have raised an IllegalArgumentException");
			
		} catch (IllegalArgumentException expected) {
			
		}
		
		try {
			
			Harvester.filterAmount ("z");
			fail ("Here should have raised an NumberFormatException");
			
		} catch (NumberFormatException expected) {
			
		}
		
		try {
			
			assertTrue (Harvester.filterBool ("full", null));
			assertFalse (Harvester.filterBool ("update", new GnuParser ( ).parse (new Options ( ).addOption (OptionBuilder.withLongOpt ("").create ('d')), new String [ ] {"-d", ""})));
			assertFalse (Harvester.filterBool ("update", new GnuParser ( ).parse (new Options ( ).addOption (OptionBuilder.withLongOpt ("").create ('i')), new String [ ] {"-d", ""})));
			fail ("Here should have raised an IllegalArgumentException");
			
		} catch (ParseException expected ) {
			
		}
	}
}
