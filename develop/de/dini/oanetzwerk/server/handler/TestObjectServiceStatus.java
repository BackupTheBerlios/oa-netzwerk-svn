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
public class TestObjectServiceStatus {
	
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
	
	
	
	
	@Test
	public void test_GET_ObjectID() throws Exception{
		
		System.out.println("ObjectServiceStatus");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String [] path = {"633"};

		String strXML = objectServiceStatus.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("oids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	@Test
	public void test_GET_ALL() throws Exception{
		
		System.out.println("ObjectServiceStatus");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String [] path = {"ALL"};

		String strXML = objectServiceStatus.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

//		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
//			System.out.println(entrySet);
//		}

		System.out.println("oids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}	
	
	@Test
	public void test_GET_ObjectID_objectIDnegative() throws Exception{
		
		System.out.println("FullTextLinks/-2079");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String [] path = {"-2079"};

		String strXML = objectServiceStatus.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}

	
	@Test
	public void test_GET_ObjectID_noObjectFound() throws Exception{
		
//		System.out.println("FullTextLinks/1");
//		
//		FullTextLinks fullTextLinks = new FullTextLinks();
//		String [] path = {"1"};
//
//		String strXML = fullTextLinks.getKeyWord(path);
////		System.out.println(strXML);
//		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
//
//		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
//			System.out.println(entrySet);
//		}
//
//		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);
	}
	
	
	@Test
	public void test_GET_noParam() throws Exception{
		
		System.out.println("ObjectServiceStatus\\");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String[] path = {};

		try {

			String strXML = objectServiceStatus.getKeyWord(path);
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
	public void test_DELETE() throws Exception{
		
		System.out.println("DELETE ObjectServiceStatus\\");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String[] path = {};

		try {

			String strXML = objectServiceStatus.deleteKeyWord(path);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
			
			
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotImplementedException);
			System.out.println(ex);
		}

	}

	
	@Test
	public void test_PUT() throws Exception{
		
		System.out.println("PUT ObjectServiceStatus\\");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String[] path = {};

		try {

			String strXML = objectServiceStatus.putKeyWord(path, null);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
			
			
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotImplementedException);
			System.out.println(ex);
		}

	}
	
	@Test
	public void test_POST() throws Exception{
		
		System.out.println("PUT ObjectServiceStatus\\");
		
		ObjectServiceStatus objectServiceStatus = new ObjectServiceStatus();
		String[] path = {};

		try {

			String strXML = objectServiceStatus.postKeyWord(path, null);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NOT_IMPLEMENTED_ERROR);
			
			
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotImplementedException);
			System.out.println(ex);
		}

	}

	
	
	
}
