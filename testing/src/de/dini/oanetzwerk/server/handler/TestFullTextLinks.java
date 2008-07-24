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
 * @author malitzro
 *
 */
public class TestFullTextLinks {
	
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
		
		System.out.println("FullTextLinks");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String [] path = {"2078"};

		String strXML = fullTextLinks.getKeyWord(path);
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
	public void test_GET_ObjectID_objectIDnegative() throws Exception{
		
		System.out.println("FullTextLinks/-2079");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String [] path = {"-2079"};

		String strXML = fullTextLinks.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}

	
	@Test
	public void test_GET_ObjectID_noObjectFound() throws Exception{
		
		System.out.println("FullTextLinks/1");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String [] path = {"1"};

		String strXML = fullTextLinks.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);
	}
	
	
	@Test
	public void test_GET_noParam() throws Exception{
		
		System.out.println("FullTextLinks\\");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String[] path = {};

		try {

			String strXML = fullTextLinks.getKeyWord(path);
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
		
		System.out.println("FullTextLinks\\");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String oid = "207";
		String[] path = {oid};
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("object_id", path[0]);
		entrySet1.addEntry("mimeformat", "application/pdf");
		entrySet1.addEntry("link", "http://edoc.hu-berlin.de");
		rms.addEntrySet(entrySet1);
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
			String strXML = fullTextLinks.putKeyWord(path, body);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("oids fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);

	}
	
	
	@Test
	public void test_DELETE_ObjectID() throws Exception{
		
		System.out.println("FullTextLinks");
		
		FullTextLinks fullTextLinks = new FullTextLinks();
		String [] path = {"635"};

		String strXML = fullTextLinks.deleteKeyWord(path);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		System.out.println("oids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	
	
}
