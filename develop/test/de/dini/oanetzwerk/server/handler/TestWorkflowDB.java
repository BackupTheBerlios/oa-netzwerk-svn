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

	
	@Test
	public void test_GET_Complete() throws Exception{
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {"2", "completeRebuild"};

		String strXML = wfdb.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("items found: " + rmsg.getListEntrySets().size());
		
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	
	@Test
	public void test_PUT_notEnoughInBody() throws Exception{
		
		String object_id = "19000";
//		String time = "2008-11-20 14:49:21.273";
		String service_id = "2";
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {};

		
//		System.out.println("ServiceNotify/");
		
//		ServiceNotifier serviceNotifier = new ServiceNotifier();
		
//		String[] path = {service_id};
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("service_id", service_id);
//		entrySet1.addEntry("time", time);
		entrySet1.addEntry("object_id", object_id);
		rms.addEntrySet(entrySet1);
		
		
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
			String strXML = wfdb.putKeyWord(path, body);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);

			
//			System.out.println("workflowdb ids fetched: "
//					+ rmsg.getListEntrySets().size());
//			System.out.println("first: " + rmsg.getListEntrySets().get(0));

		
		
		
//		try {
//			
//		
//		String strXML = wfdb.getKeyWord(path);
//		System.out.println(strXML);
//		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
//
//		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
//			System.out.println(entrySet);
//		}
//
//		Assert.fail();
//		
//		} catch(NotEnoughParametersException ex) {
//			System.out.println(ex);
//		}
		
	}
	
	
	@Test
	public void test_PUT_noParams() throws Exception{
		
		String object_id = "10";
		String time = "2008-12-15 12:09:11.053";
		String service_id = "5";
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {};

		
//		System.out.println("ServiceNotify/");
		
//		ServiceNotifier serviceNotifier = new ServiceNotifier();
		
//		String[] path = {service_id};
		String body = null;

		RestMessage rms = new RestMessage();
		RestEntrySet entrySet1;
		entrySet1 = new RestEntrySet(); 
		entrySet1.addEntry ("service_id", service_id);
		entrySet1.addEntry("time", time);
		entrySet1.addEntry("object_id", object_id);
		rms.addEntrySet(entrySet1);
		
		
		
		body = RestXmlCodec.encodeRestMessage(rms);
		

			
			String strXML = wfdb.putKeyWord(path, body);
			// System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

			for (RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}

			System.out.println("workflowdb ids fetched: "
					+ rmsg.getListEntrySets().size());
			System.out.println("first: " + rmsg.getListEntrySets().get(0));

			Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
		
		
		
//		try {
//			
//		
//		String strXML = wfdb.getKeyWord(path);
//		System.out.println(strXML);
//		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
//
//		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
//			System.out.println(entrySet);
//		}
//
//		Assert.fail();
//		
//		} catch(NotEnoughParametersException ex) {
//			System.out.println(ex);
//		}
		
	}
	
	
}
