package de.dini.oanetzwerk.server.handler;

import javax.naming.Context;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * 
 * @author malitzro
 *
 */
public class TestWorkflowDB {

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "de.dini.oanetzwerk.server.handler.OneShotInitialContextFactory");
	}

	@Test
	public void test() throws Exception{
		WorkflowDB wfdb = new WorkflowDB();
		String [] path = {"2","2"};
		
			String strXML = wfdb.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
			
			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			

	}
	
}
