package de.dini.oanetzwerk.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.utils.HelperMethods;

public class TestSearchClient {

	private static SearchClient mySearchClient = null;
	
	@BeforeClass
	public static void prepareClient() throws Exception {
		
		Properties props = new Properties();
		props = HelperMethods.loadPropertiesFromFile ("searchclientprop.xml");

		mySearchClient = new SearchClient(props);
	}
	
	@Test
	public void sendSimpleQuery() {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("foo");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendMultiWordQuery() {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("foo bar");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendSpecialCharQuery() {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("ÄÖÜß+!");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendEmptyQuery() {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendNullQuery() {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService(null);
		System.out.println("result = " + listOIDs);
	}
}
