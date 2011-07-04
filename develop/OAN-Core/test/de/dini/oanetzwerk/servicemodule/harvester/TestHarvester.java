package de.dini.oanetzwerk.servicemodule.harvester;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHarvester {
	
	static Harvester harvester;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp ( ) throws Exception {
		
		harvester = new Harvester();
		harvester.setPropertyfile ("harvesterprop.xml");
		harvester.prepareHarvester (1);
		harvester.setRepositoryURL ("http://edoc.hu-berlin.de/OAI-2.0");
		harvester.setTestData (true);
		
		ArrayList<ObjectIdentifier> oi = new ArrayList <ObjectIdentifier> ( );
		oi.add (new ObjectIdentifier ("oai:HUBerlin.de:28411", "2009-01-01", 730, null, 0));
		
		harvester.setIds (oi);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUpdateRawData ( ) {
		
		harvester.updateRawData(0);
	}

}
