package de.dini.oanetzwerk.server.handler;


import javax.naming.Context;

import junit.framework.Assert;

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
public class TestLoginData {
	
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
	public void test_GET_Name() throws Exception{

		System.out.println("GET /LoginData/klattman");

		LoginData loginData = new LoginData();
		String[] path = { "klattman" };
		String strXML = null;
		RestMessage rmsg = null;

		strXML = loginData.getKeyWord(path);
		System.out.println(strXML);
		rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("entries fetched: " + rmsg.getListEntrySets().size());

		System.out.println("first: " + rmsg.getListEntrySets().get(0));

		Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.OK);
	}
	
	
//	// negative Service-ID
//	@Test
//	public void test_GET_ServiceID_serviceIDnegative() throws Exception{
//		
//		System.out.println("ServiceNotifier/-1");
//		
//		ServiceNotifier serviceNotifier = new ServiceNotifier();
//		String [] path = {"-2079"};
//
//		String strXML = serviceNotifier.getKeyWord(path);
////		System.out.println(strXML);
//		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
//
//		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
//			System.out.println(entrySet);
//		}
//
//		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
//	}

	
	// Aufruf mit unbekanntem Namen - sollte kein Resultat liefern
	@Test
	public void test_GET_ServiceID_noObjectFound() throws Exception{

		System.out.println("GET /LoginData/maxmurks");
		
		LoginData loginData = new LoginData();
		String[] path = { "maxmurks" };

		String strXML = loginData.getKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);

	}
	
	
	@Test
	public void test_GET_noParam() throws Exception{
		
		System.out.println("GET /LoginData//");
		
		LoginData loginData = new LoginData();
		String[] path = {};

		try {

			String strXML = loginData.getKeyWord(path);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("entries fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.fail();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotEnoughParametersException);
			System.out.println(ex);
		}

	}

	
	@Test
	public void test_PUT_nameExists() throws Exception{
		
		System.out.println("PUT /LoginData/     {klattman}");
		
		LoginData loginData = new LoginData();
		String [] path = null;
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("name", "klattman");
		entrySet1.addEntry("password", "12345");
		entrySet1.addEntry("email", "asc@def.gh");
		rms.addEntrySet(entrySet1);
		
		body = RestXmlCodec.encodeRestMessage(rms);
		
		String strXML = loginData.putKeyWord(path, body);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("entries fetched: "
				+ rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));

		Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.WRONG_PARAMETER);

	}
	
	
	@Test
	public void test_PUT_correctParams() throws Exception{
		
		System.out.println("PUT /LoginData/      {maxmurks}");
		
		LoginData loginData = new LoginData();
		String [] path = null;
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("name", "testuser1");
		entrySet1.addEntry("password", "12345");
		entrySet1.addEntry("email", "asc@def.gh");
		rms.addEntrySet(entrySet1);
		
		
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
		String strXML = loginData.putKeyWord(path, body);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("entries fetched: "
				+ rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));

		Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.OK);

	}
	
	
	@Test
	public void test_DELETE_ServiceID() throws Exception{
		
		System.out.println("DELETE /LoginData/maxmurks");
		
		LoginData loginData = new LoginData();
		String [] path = {"testuser1"};

		String strXML = loginData.deleteKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("entries fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}

	
//	@Test
//	public void test_POST() throws Exception{
//		
//		System.out.println("PUT LoginData/");
//		
//		ServiceNotifier serviceNotifier = new ServiceNotifier();
//		String[] path = {};
//
//		try {
//
//			String strXML = serviceNotifier.postKeyWord(path, null);
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

//	}

	
	
	
}
