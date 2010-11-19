package de.dini.oanetzwerk.servicemodule.harvester;

import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestOAIRecoveryMatcher {

	public static final String xmlTestdaten =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">" +
		"   <responseDate>2008-02-14T14:54:47Z</responseDate>" +
		"   <request verb=\"ListIdentifiers\" from=\"2002-01-01\" set=\"ddc:510\" metadataPrefix=\"oai_dc\">http://edoc.hu-berlin.de/OAI-2.0</request>" +
		"  <ListIdentifiers>" +
		"        <header>" +
		"          <identifier>oai:HUBerlin.de:28652</identifier>" +
		"          <datestamp>2008-02-14</datestamp>" +
		"          <setSpec>pub-type:monograph</setSpec>" +
		"          <setSpec>ddc:100</setSpec>" +
		"          <setSpec>ddc:500</setSpec>" +
		"          <setSpec>ddc:510</setSpec>" +
		"          <setSpec>ddc:550</setSpec>" +
		"          <setSpec>ddc:570</setSpec>" +
		"          <setSpec>ddc:580</setSpec>" +
		"          <setSpec>ddc:620</setSpec>" +
		"          <setSpec>ddc:630</setSpec>" +
		"        </header>" +
		"	     <header>" +
		"          <identifier>oai:HUBerlin.de:28277</identifier>" +
		"          <datestamp>2007-11-06</datestamp>" +
		"          <setSpec>pub-type:article</setSpec>" +
		"          <setSpec>ddc:020</setSpec>" +
		"        </header>" +
		"        <resumptionToken expirationDate=\"2008-02-15T15:06:30Z\" completeListSize=\"5057\" cursor=\"0\">1203001608</resumptionToken>" +
		"  </ListIdentifiers>" +
		"</OAI-PMH>";
	
	@BeforeClass
	public static void init() {

	}
	
	@Test
	public void testRecoverResumptiontoken() {
		String rtoken = OAIRecoveryMatcher.recoverResumptionToken(xmlTestdaten); 
		System.out.println("ResumptionToken: " + rtoken);			
	}
	
	@Test
	public void testRecoverIdentifiers() {
		Set<String> ids = OAIRecoveryMatcher.recoverIdentifiers(xmlTestdaten);
		if(ids != null) {
			for(String id : ids) {
				System.out.println("Identifier: " + id);
			}
		} else {
			System.out.println("identifier list is null");
		}
	}
}
