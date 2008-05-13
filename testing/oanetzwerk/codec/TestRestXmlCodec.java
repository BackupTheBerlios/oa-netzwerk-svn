package de.dini.oanetzwerk.codec;

import org.junit.Test;

public class TestRestXmlCodec {

	final String KEY_FOO1 = "foo1";
	final String KEY_FOO2 = "foo2";
	final String KEY_NULL = "non-existent";
	final String KEY_NOTNULL = "existent";
	

	public RestMessage createTestMsg() {
		RestMessage msg = new RestMessage();
		msg.setKeyword(RestKeyword.ObjectEntry);
		msg.setStatus(RestStatusEnum.OK);
		RestEntrySet entrySet = new RestEntrySet();
		entrySet.addEntry("key1", "value1_üäö");
		entrySet.addEntry("key2", "value2_<bar>");
		entrySet.addEntry("key3", "value3_&foo;");
		msg.addEntrySet(entrySet);
		entrySet = new RestEntrySet();
		entrySet.addEntry("key3", "valueC");
		entrySet.addEntry("key1", "valueA");
		entrySet.addEntry("key2", "valueB");
		msg.addEntrySet(entrySet);
		return msg;
	}
	
	@Test
	public void testCodecForRestMessage() {
		
		RestMessage msg = createTestMsg();
		
		System.out.println(msg);
		
		String strXML = de.dini.oanetzwerk.codec.RestXmlCodec.encodeRestMessage(msg);
		
		System.out.println(strXML);
		
		RestMessage received = de.dini.oanetzwerk.codec.RestXmlCodec.decodeRestMessage(strXML);
		
		System.out.println(received);
	}
		
}
