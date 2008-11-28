package de.dini.oanetzwerk.server.handler;

import java.util.Iterator;

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
public class TestDDCCategories {
	
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
		
		System.out.println("DDCCategories\\");
		
		DDCCategories ddcCategories = new DDCCategories();
		String [] path = {};

		String strXML = ddcCategories.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = entrySet.getValue(key);
				System.out.println(key + "|" + value);
			}
		}
				
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
	
	@Test
	public void test_GET_WildcardCategory_Nonsense() throws Exception{
		
		System.out.println("DDCCategories\\foobar*");
		
		DDCCategories ddcCategories = new DDCCategories();
		String [] path = {"foobar*"};

		String strXML = ddcCategories.getKeyWord(path);
//		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
				
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_WildcardCategory_Empty() throws Exception{
		
		System.out.println("DDCCategories\\");
		
		DDCCategories ddcCategories = new DDCCategories();
		String [] path = {""};

		String strXML = ddcCategories.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
				
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.WRONG_PARAMETER);
	}
	
	@Test
	public void test_GET_WildcardCategory_X() throws Exception{
		
		System.out.println("DDCCategories\\5x");
		
		DDCCategories ddcCategories = new DDCCategories();
		String [] path = {"5x"};

		String strXML = ddcCategories.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		System.out.println("categories fetched: " + rmsg.getListEntrySets().size());
		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			Iterator<String> it = entrySet.getKeyIterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = entrySet.getValue(key);
				System.out.println(key + "|" + value);
			}
		}		
		
		Assert.assertEquals(rmsg.getStatus(),RestStatusEnum.OK);
	}
}
