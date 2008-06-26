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
public class TestAllOIDs {
	
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
	public void test_GET_NoParams() throws Exception{
		
		System.out.println("AllOIDs\\");
		
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {};

		String strXML = allOIDs.getKeyWord(path);
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
	public void test_GET_markedAs_test() throws Exception{
		
		System.out.println("AllOIDs\\markedAs\\test");
		
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"markedAs","test"};

		String strXML = allOIDs.getKeyWord(path);
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
	public void test_GET_markedAs_noSuchMark() throws Exception{
		
		System.out.println("AllOIDs\\markedAs\\noSuchMark");
		
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"markedAs","noSuchMark"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);

	}

	@Test
	public void test_GET_markedAs_noMarkGiven() throws Exception{

		System.out.println("AllOIDs\\markedAs\\");
		
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"markedAs"};

		try {
			String strXML = allOIDs.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			Assert.fail();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotEnoughParametersException);
			System.out.println(ex);
		}
	}

	@Test
	public void test_GET_WrongParam() throws Exception{
		
		System.out.println("AllOIDs\\noSuchParameter\\");
		
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"noSuchParameter"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_fromRepositoryID_1() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\1\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","1"};


		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("oids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	@Test
	public void test_GET_fromRepositoryID_NoIdGiven() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID"};

		try {
			String strXML = allOIDs.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			Assert.fail();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof NotEnoughParametersException);
			System.out.println(ex);
		}
	}
	
	@Test
	public void test_GET_fromRepositoryID_NoNumberGiven() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","NotANumber"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_fromRepositoryID_NoSuchID() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\-1\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","-1"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.NO_OBJECT_FOUND_ERROR);
	}
	
	@Test
	public void test_GET_fromRepositoryID_1_markedAs_test() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\1\\markedAs\\test\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","1","markedAs","test"};


		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("oids fetched: " + rmsg.getListEntrySets().size());
		System.out.println("first: " + rmsg.getListEntrySets().get(0));

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	@Test
	public void test_GET_fromRepositoryID_1_wrongParameter_test() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\1\\wrongParameter\\test\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","1","wrongParameter","test"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_fromRepositoryID_1_markedAs_NoSuchMark() throws Exception{

		System.out.println("AllOIDs\\fromRepositoryID\\1\\markedAs\\NoSuchMark\\");

		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"fromRepositoryID","1","markedAs","NoSuchMark"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
}
