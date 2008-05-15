package de.dini.oanetzwerk.server.handler;

import javax.naming.Context;

import org.junit.Before;
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
public class TestServices {

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
	public void testNoParam() throws Exception{
		Services keyword = new Services();
		String [] path = {};
		
			String strXML = keyword.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
			
			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			

	}
	
	@Test
	public void testById() throws Exception{
		Services keyword = new Services();
		String [] path = {"1"};
		
			String strXML = keyword.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
			
			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			

	}
	
	@Test
	public void testByName() throws Exception{
		Services keyword = new Services();
		String [] path = {"byName","Harvester"};
		
			String strXML = keyword.getKeyWord(path);
			System.out.println(strXML);
			RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
			
			for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
				System.out.println(entrySet);
			}
			

	}
}
