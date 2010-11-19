package de.dini.oanetzwerk.server.handler;


import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;


/**
 * ACHTUNG - Dieser Unit-Test veraendert Echtdaten und stellt in der Regel keine Asserts!
 * Er wird wie ein TestClient benutzt, der Testdaten einspielt, indem er das Keyword mit
 * lokaler Datenbank-Verbindung betreibt, ohne wirklich über REST zu senden... 
 * 
 * @author Robin Malitz
 *
 */
public class TestUsageData {
	
	static int i = 0;
	
	@BeforeClass
	public static void init() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "de.dini.oanetzwerk.server.handler.OneShotInitialContextFactory");
	}
	
	@Before
	public void count() {
		System.out.println("\nTEST " + i);
		i++;
	}
	
	
	
	// Standardfall
	@Test
	public void test_GET_UsageData() throws Exception{

		System.out.println("test_GET_UsageData");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		RestMessage rmsg = null;

		strXML = usageData.getKeyWord(path);
		System.out.println(strXML);
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}
	
	@Test
	public void test_POST_single_Overall() throws Exception{

		System.out.println("test_POST_single_Overall");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "IFABC");
		entrySet1.addEntry("count_overall", "" + (System.currentTimeMillis() % 1000));	
		entrySets.add(entrySet1);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_POST_single_Month() throws Exception{

		System.out.println("test_POST_single_Month");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "IFABC");
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));	
		entrySets.add(entrySet1);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}

	@Test
	public void test_POST_Overall_and_Month() throws Exception{

		System.out.println("test_POST_Overall_and_Month");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "Logec");
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));			
		entrySets.add(entrySet1);
		RestEntrySet entrySet2 = new RestEntrySet();
		entrySet2.addEntry("metrics", "Logec");
		entrySet2.addEntry("count_overall", "" + (System.currentTimeMillis() % 1000));	
		entrySets.add(entrySet2);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	// defekte Dates werden schon beim parsen abgefangen und erhalten die default Werte
	// relativ zum Datum des Einspielens
	@Test
	public void test_POST_Overall_and_Month_with_Dates() throws Exception{

		System.out.println("test_POST_Overall_and_Month_with_Dates");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "Counter");
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));		
		entrySet1.addEntry("last_update", "2009-01-11");
		entrySets.add(entrySet1);
		RestEntrySet entrySet2 = new RestEntrySet();
		entrySet2.addEntry("metrics", "Counter");
		entrySet2.addEntry("count_overall", "" + (System.currentTimeMillis() % 1000));	
		entrySet2.addEntry("relative_to_date", "2009-01-11");
		entrySets.add(entrySet2);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_POST_single_Month_with_Metricsname_wrong() throws Exception{

		System.out.println("test_POST_single_Month_with_Metricsname_wrong");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "NoSuchMetric");
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));	
		entrySets.add(entrySet1);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_POST_single_Overall_without_Metricsname() throws Exception{

		System.out.println("test_POST_single_Overall_without_Metricsname");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));	
		entrySets.add(entrySet1);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_POST_single_Entry_without_count() throws Exception{

		System.out.println("test_POST_single_Entry_without_count");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "IFABC");
		entrySets.add(entrySet1);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_POST_Overall_and_Month_with_wrong_Dates() throws Exception{

		System.out.println("test_POST_Overall_and_Month_with_wrong_Dates");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		
		RestMessage rmsg = new RestMessage(RestKeyword.UsageData);		
		List<RestEntrySet> entrySets = new ArrayList<RestEntrySet>();
		RestEntrySet entrySet1 = new RestEntrySet();
		entrySet1.addEntry("metrics", "Counter");
		entrySet1.addEntry("count_of_month", "" + (System.currentTimeMillis() % 1000));		
		entrySet1.addEntry("last_update", "fh2009-01-11gfh");
		entrySets.add(entrySet1);
		RestEntrySet entrySet2 = new RestEntrySet();
		entrySet2.addEntry("metrics", "Counter");
		entrySet2.addEntry("count_overall", "" + (System.currentTimeMillis() % 1000));	
		entrySet2.addEntry("relative_to_date", "11.67.2009");
		entrySets.add(entrySet2);
		rmsg.setListEntrySets(entrySets );
		
		String strXMLBody = RestXmlCodec.encodeRestMessage(rmsg);
		System.out.println("REQUEST: " + strXMLBody);
		
		strXML = usageData.postKeyWord(path,strXMLBody);
		System.out.println("RESPONSE: " + strXML);
		
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
	}
	
	@Test
	public void test_GET_UsageData_again() throws Exception{

		System.out.println("test_GET_UsageData_again");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		RestMessage rmsg = null;

		strXML = usageData.getKeyWord(path);
		System.out.println(strXML);
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}
	
	@Ignore @Test
	public void test_DELETE_forOID() throws Exception{

		System.out.println("test_DELETE_forOID");
		
		UsageData usageData = new UsageData();
		String [] path = {"1"};
		String strXML = null;
		RestMessage rmsg = null;

		strXML = usageData.deleteKeyWord(path);
		System.out.println(strXML);
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("STATUS: " + rmsg.getStatus());
		System.out.println("STATUSDESC: " + rmsg.getStatusDescription());
		System.out.println("entrysets fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}
}
