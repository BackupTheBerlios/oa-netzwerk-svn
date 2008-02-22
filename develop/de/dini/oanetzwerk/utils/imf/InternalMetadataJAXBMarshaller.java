package de.dini.oanetzwerk.utils.imf;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class InternalMetadataJAXBMarshaller {

	public static JAXBContext context;
	private static InternalMetadataJAXBMarshaller instance;
	
	public static void init() {
		try {
			context = JAXBContext.newInstance( InternalMetadata.class, OtherClassification.class, DINISetClassification.class, DNBClassification.class, DDCClassification.class ); 
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
	}
	
	public static InternalMetadataJAXBMarshaller getInstance() {
		if(instance == null) {
			init();
		}
		return instance;
	}
	
	public static String marshall(InternalMetadata imf) {
		String result = null;	
		StringWriter sw = new StringWriter();
		
		init();

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
	
	public static InternalMetadata unmarshall (String xmldata) {
		InternalMetadata result = null;

		init();
		
		try {
			
			Unmarshaller um = context.createUnmarshaller ( ); 
			um.setEventHandler (new javax.xml.bind.helpers.DefaultValidationEventHandler ( ));
			result = (InternalMetadata) um.unmarshal (new StringReader (xmldata));
			
		} catch (JAXBException ex) {
			
			ex.printStackTrace ( );
		}
		
		return result;
	}
	
	
	
	
}
