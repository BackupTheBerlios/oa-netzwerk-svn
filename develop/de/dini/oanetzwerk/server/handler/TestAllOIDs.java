package de.dini.oanetzwerk.server.handler;

import junit.framework.Assert;

import org.junit.Test;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

public class TestAllOIDs {

	@Test
	public void testFetchAll() throws Exception{
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}
	
	@Test
	public void testMarkedAsTest() throws Exception{
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"markedAs","test"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}

	@Test
	public void testMarkedAs_Wrong() throws Exception{
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"markedAs","noSuchMark"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}

	@Test
	public void testMarkedAs_Empty() throws Exception{
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
	public void testWrongParameter() throws Exception{
		AllOIDs allOIDs = new AllOIDs();
		String [] path = {"noSuchParameter"};

		String strXML = allOIDs.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);

		for(RestEntrySet entrySet : rmsg.getListEntrySets()) {
			System.out.println(entrySet);
		}

	}
	
}
