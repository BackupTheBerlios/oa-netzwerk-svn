package de.dini.oanetzwerk.utils.imf;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class InternalMetadataJAXBMarshaller {

	private static Logger logger = Logger.getLogger (InternalMetadataJAXBMarshaller.class);
	private static JAXBContext context;
	private static InternalMetadataJAXBMarshaller instance;
		
	private InternalMetadataJAXBMarshaller() {
		try {
			context = JAXBContext.newInstance( InternalMetadata.class, OtherClassification.class, DINISetClassification.class, DNBClassification.class, DDCClassification.class, InterpolatedDDCClassification.class ); 
		} catch(JAXBException jex) {
			logger.error(jex);
		}
	}
	
	public static InternalMetadataJAXBMarshaller getInstance() {
		if(instance == null) {
			instance = new InternalMetadataJAXBMarshaller();
		}
		return instance;
	}
	
	public String marshall(InternalMetadata imf) {
		String result = null;	
		StringWriter sw = new StringWriter();

		try {
			Marshaller m = context.createMarshaller(); 
			m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
			m.marshal( imf, sw);
			result = sw.toString();
		} catch(JAXBException jex) {
			logger.error(jex);
		}
		return result;
	}
	
	public InternalMetadata unmarshall (String xmldata) {
		InternalMetadata result = null;
		
		try {
			
			Unmarshaller um = context.createUnmarshaller ( ); 
			um.setEventHandler (new javax.xml.bind.helpers.DefaultValidationEventHandler ( ));
			result = (InternalMetadata) um.unmarshal (new StringReader (xmldata));
			
		} catch (JAXBException ex) {
			
			//ex.printStackTrace ( );
			logger.error(ex);
		}
		
		return result;
	}
	
}
