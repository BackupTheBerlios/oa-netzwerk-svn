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
public class TestDuplicateProbabilities {
	
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
	public void test_GET_ObjectID() throws Exception{

		System.out.println("DuplicateProbabilities");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {"1"};
		String strXML = null;
		RestMessage rmsg = null;

		strXML = duplicate.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: " + rmsg.getListEntrySets().size());


			
			System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
			Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.OK);
	}

	
	
	// Standardfall
	@Test
	public void test_GET_ObjectID_negative() throws Exception{

		System.out.println("DuplicateProbabilities");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {"-1"};
		String strXML = null;
		RestMessage rmsg = null;
		
			
			strXML = duplicate.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: " + rmsg.getListEntrySets().size());

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}

	
	// Aufruf mit sehr großer Object-ID - sollte kein Resultat liefern
	@Test
	public void test_GET_ObjectID_notFound() throws Exception{

		System.out.println("DuplicateProbabilities");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {"99999999"};
		String strXML = null;
		RestMessage rmsg = null;
		
			
			strXML = duplicate.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

//			System.out.println("oids fetched: " + rmsg.getListEntrySets().size());

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);
	}
	
	// Aufruf mit sehr großer Object-ID - sollte kein Resultat liefern
	@Test
	public void test_GET_ObjectID_noParams() throws Exception{

		System.out.println("DuplicateProbabilities");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {};
		String strXML = null;
		RestMessage rmsg = null;
		
		try {	
			strXML = duplicate.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			Assert.fail();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotEnoughParametersException);
			System.out.println(ex);
		}	}	


	
	
	@Test
	public void test_PUT_correctParams() throws Exception{
		
		System.out.println("DuplicateProbabilities/");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String object_id = "2";
		String[] path = {object_id};
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("referToOID", "3");
		entrySet1.addEntry("probability", "90");
		rms.addEntrySet(entrySet1);
		
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("referToOID", "4");
		entrySet1.addEntry("probability", "111.654");
		rms.addEntrySet(entrySet1);
		
		
		
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
			String strXML = duplicate.putKeyWord(path, body);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("ids fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);

	}
	
	
	// Standardfall
	@Test
	public void test_GET_ObjectID_afterPut() throws Exception{

		System.out.println("DuplicateProbabilities/Get after PUT");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {"2"};
		String strXML = null;
		RestMessage rmsg = null;

		strXML = duplicate.getKeyWord(path);
			System.out.println(strXML);
			rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: " + rmsg.getListEntrySets().size());


			
			System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
			Assert.assertEquals(rmsg.getStatus(), RestStatusEnum.OK);
	}
	
	
	
	@Test
	public void test_DELETE_ObjectID() throws Exception{
		
		System.out.println("DuplicateProbabilities");
		
		DuplicateProbabilities duplicate = new DuplicateProbabilities();
		String [] path = {"2"};

		String strXML = duplicate.deleteKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("ids: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
//
//	
////	@Test
////	public void test_PUT() throws Exception{
////		
////		System.out.println("PUT ObjectServiceStatus\\");
////		
////		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
////		String[] path = {};
////
////		try {
////
////			String strXML = objectServiceStatus.putKeyWord(path, null);
////			// System.out.println(strXML);
////			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
////
////			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
////			
////			
////		} catch (Exception ex) {
////			Assert.assertTrue(ex instanceof NotImplementedException);
////			System.out.println(ex);
////		}
////
////	}
////	
//	@Test
//	public void test_POST() throws Exception{
//		
//		System.out.println("PUT ServiceNotifier/");
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
//
//	}
//
//	
//	
//	
}
