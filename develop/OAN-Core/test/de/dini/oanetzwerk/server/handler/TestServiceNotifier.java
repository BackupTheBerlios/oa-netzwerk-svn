package de.dini.oanetzwerk.server.handler;


import javax.naming.Context;

import junit.framework.Assert;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;


/**
 * 
 * @author Manuel Klatt-Kafemann
 *
 */
public class TestServiceNotifier {
	
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
	public void test_GET_ServiceID() throws Exception{

		System.out.println("ServiceNotifier");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String [] path = {"1"};
		String strXML = null;
		RestMessage rmsg = null;
		
//		try {		
			
			strXML = serviceNotifier.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: " + rmsg.getListEntrySets().size());


			
			System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
			Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.OK);
//		} catch (NullPointerException nex) {
//			System.out.println(nex.getLocalizedMessage() + nex.getStackTrace());
//		}
	}
	
	
	// negative Service-ID
	@Test
	public void test_GET_ServiceID_serviceIDnegative() throws Exception{
		
		System.out.println("ServiceNotifier/-1");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String [] path = {"-2079"};

		String strXML = serviceNotifier.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}

	
	// Aufruf mit sehr großer Service-ID - sollte kein Resultat liefern
	@Test
	public void test_GET_ServiceID_noObjectFound() throws Exception{

		System.out.println("ServiceNotifier/9999");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String [] path = {"9999"};

		String strXML = serviceNotifier.getKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);

	}
	
	
	@Test
	public void test_GET_noParam() throws Exception{
		
		System.out.println("ServiceNotifier\\");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String[] path = {};

		try {

			String strXML = serviceNotifier.getKeyWord(path);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.fail();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotEnoughParametersException);
			System.out.println(ex);
		}

	}
	
	@Test
	public void test_PUT_correctParams() throws Exception{
		
		System.out.println("ServiceNotify/");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String service_id = "2";
		String[] path = {service_id};
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("service_id", service_id);
		entrySet1.addEntry("inserttime", "2008-08-13");
		entrySet1.addEntry("urgent", "1");
		rms.addEntrySet(entrySet1);
		
		
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
			String strXML = serviceNotifier.putKeyWord(path, body);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("service_ids fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);

	}
	
	
	@Test
	public void test_DELETE_ServiceID() throws Exception{
		
		System.out.println("ServiceNotifier");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String [] path = {"2"};

		String strXML = serviceNotifier.deleteKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("service_ids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}

	
//	@Test
//	public void test_PUT() throws Exception{
//		
//		System.out.println("PUT ObjectServiceStatus\\");
//		
//		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
//		String[] path = {};
//
//		try {
//
//			String strXML = objectServiceStatus.putKeyWord(path, null);
//			// System.out.println(strXML);
//			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
//
//			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
//			
//			
//		} catch (Exception ex) {
//			Assert.assertTrue(ex instanceof NotImplementedException);
//			System.out.println(ex);
//		}
//
//	}
//	
	@Test
	public void test_POST() throws Exception{
		
		System.out.println("PUT ServiceNotifier/");
		
		ServiceNotifier serviceNotifier = new ServiceNotifier();
		String[] path = {};

		try {

			String strXML = serviceNotifier.postKeyWord(path, null);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
			
			
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotImplementedException);
			System.out.println(ex);
		}

	}

	
	
	
}
