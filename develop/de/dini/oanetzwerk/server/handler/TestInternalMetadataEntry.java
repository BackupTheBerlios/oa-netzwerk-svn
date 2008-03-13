package de.dini.oanetzwerk.server.handler;

import org.junit.Test;

import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.InternalMetadataJAXBMarshaller;

public class TestInternalMetadataEntry {

	@Test
	public void testGetKeyWord() {
		String[] path = {"x","x","635"};
		InternalMetadataEntry ime = new InternalMetadataEntry();
		String strXML = ime.getKeyWord(path);
		System.out.println(strXML);
		RestMessage rmsg = RestXmlCodec.decodeRestMessage(strXML);
		String data = rmsg.getListEntrySets().get(0).getValue("internalmetadata");
		System.out.println(data);
		InternalMetadataJAXBMarshaller imMarshaller = InternalMetadataJAXBMarshaller.getInstance();
		InternalMetadata myIM = imMarshaller.unmarshall(data);
		System.out.println(myIM);
	}
	
}
