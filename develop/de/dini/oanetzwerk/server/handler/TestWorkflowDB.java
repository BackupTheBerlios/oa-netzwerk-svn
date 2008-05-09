package de.dini.oanetzwerk.server.handler;

import org.junit.Test;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

public class TestWorkflowDB {

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
