package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

public class InternalMetadataJAXBMarshaller {

	public static JAXBContext context;
	
	@BeforeClass
	public static void init() {
		try {
			context = JAXBContext.newInstance( InternalMetadata.class, OtherClassification.class, DINISetClassification.class, DNBClassification.class, DDCClassification.class ); 
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
	}
	

	
	public String marshall(InternalMetadata imf) {
		String result = null;
		init();

		StringWriter sw = new StringWriter();
		try {
			Marshaller m = context.createMarshaller(); 
			m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
//			m.marshal( imf, System.out );
			m.marshal( imf, sw);
			result = sw.toString();
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
		return result;
	}
	
	
	private InternalMetadata createTestInstance() {
		InternalMetadata myIM = new InternalMetadata();
		return myIM;
	}
	
	@Test
	public void test_IMF_marshalling() {
		
		InternalMetadata originalIM = createTestInstance();
	
		System.out.println("");
		System.out.println(originalIM);
		System.out.println("");
		
		StringWriter sw = new StringWriter();
		try {
			Marshaller m = context.createMarshaller(); 
			m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
			m.marshal( originalIM, System.out );
			m.marshal( originalIM, sw);
		} catch(JAXBException jex) {
			System.err.println(jex);
		}

		try {
			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			//System.out.println(context.toString());
			InternalMetadata receivedIM = (InternalMetadata) um.unmarshal( new StringReader(sw.toString()) );
/*			InternalMetadata receivedIM = (InternalMetadata) um.unmarshal( new StringReader(
		    "<ns2:internalMetadata xmlns:ns2=\"http://oanetzwerk.dini.de/\">" +
			"<typeValueCounter>1</typeValueCounter>" +
		    "<typeValueList>" +
		    "    <number>1</number>" +
		    "    <typeValue>TypeValue</typeValue>" +
		    "</typeValueList>" +
		    "<classificationList>" +
	        "<otherClassification xsi:type=\"OtherClassification.class\">" +
	        "    <value>foo:bar</value>" +
	        "</otherClassification>" +
	        "<diniSetClassification>" +
	        "    <value>foo1</value>" +
	        "</diniSetClassification>" +
	        "<ddcClassification>" +
	        "    <value>foo2</value>" +
	        "</ddcClassification>" +
	        "<dnbClassification>" +
	        "    <value>foo3</value>" +
	        "</dnbClassification>" +
	        "</classificationList>" +
		    "</ns2:internalMetadata>" ));*/
			
			
			
			System.out.println("");
			System.out.println( receivedIM ); 
			System.out.println("");
		} catch(JAXBException jex) {
			System.err.println(jex);
		}

	}
	
}
