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
public class TestWorkflowDB {

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
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {};

		try {
			
		
		String strXML = wfdb.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.fail();
		
		} catch(NotEnoughParametersException ex) {
			System.out.println(ex);
		}
		
	}
	
	@Test
	public void test_GET_WrongParam() throws Exception{
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {"NoSuchParam"};

		String strXML = wfdb.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_WrongSID() throws Exception{
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {"-99"};

		String strXML = wfdb.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}

	@Test
	public void test_GET_2() throws Exception{
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {"2"};

		String strXML = wfdb.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("items found: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
}
