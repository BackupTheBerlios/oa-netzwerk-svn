package de.dini.oanetzwerk.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.userfrontend.SearchClientException;
import de.dini.oanetzwerk.utils.HelperMethods;

public class TestSearchClient {

	private static SearchClient mySearchClient = null;
	private static Properties props = null;
	
	@BeforeClass
	public static void prepareClient() throws Exception {
		props = new Properties();
		props = HelperMethods.loadPropertiesFromFile ("searchclientprop.xml");		
		mySearchClient = new SearchClient(props);
	}
	
	@Test
	public void sendSimpleQuery() throws SearchClientException {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("foo");		  
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendMultiWordQuery() throws SearchClientException {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("foo bar");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendSpecialCharQuery() throws SearchClientException {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("����+!");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendEmptyQuery() throws SearchClientException {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService("");
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendNullQuery() throws SearchClientException {
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		listOIDs = mySearchClient.querySearchService(null);
		System.out.println("result = " + listOIDs);
	}
	
	@Test
	public void sendSimpleQuery_Exceptions() throws Exception {
		
		props = HelperMethods.loadPropertiesFromFile ("searchclientprop.xml");
		props.put("url", "http://kaputteurl.diegibtsnicht");
		mySearchClient = new SearchClient(props);
		
		List<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
		try {
		  listOIDs = mySearchClient.querySearchService("foo");	
		  System.out.println("result = " + listOIDs);
		  Assert.fail();
		} catch(SearchClientException scex) {
			System.out.println("korrekte Exception: " + scex);
		}
		
		props = HelperMethods.loadPropertiesFromFile ("searchclientprop.xml");
		props.put("password", ".fh.hfg.h.ghf");
		mySearchClient = new SearchClient(props);
		
		listOIDs = new ArrayList<BigDecimal>();
		try {
		  listOIDs = mySearchClient.querySearchService("foo");	
		  System.out.println("result = " + listOIDs);
		  Assert.fail();
		} catch(SearchClientException scex) {
			System.out.println("korrekte Exception: " + scex);
		}
	}
}
