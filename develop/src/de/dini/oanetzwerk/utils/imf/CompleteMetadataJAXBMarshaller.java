package de.dini.oanetzwerk.utils.imf;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;


public class CompleteMetadataJAXBMarshaller {

	private static Logger logger = Logger.getLogger (CompleteMetadataJAXBMarshaller.class);
	private static JAXBContext context;
	private static CompleteMetadataJAXBMarshaller instance;
		
	private CompleteMetadataJAXBMarshaller() {
		try {
			context = JAXBContext.newInstance( CompleteMetadata.class, InternalMetadata.class, OtherClassification.class, DINISetClassification.class, DNBClassification.class, DDCClassification.class, InterpolatedDDCClassification.class ); 
		} catch(JAXBException jex) {
			logger.error(jex);
		}
	}
	
	public static CompleteMetadataJAXBMarshaller getInstance() {
		if(instance == null) {
			instance = new CompleteMetadataJAXBMarshaller();
		}
		return instance;
	}
	
	public String marshall(CompleteMetadata cmf) {
		String result = null;	
		StringWriter sw = new StringWriter();

		try {
			Marshaller m = context.createMarshaller(); 
			m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
			m.marshal( cmf, sw);
			result = sw.toString();
		} catch(JAXBException jex) {
			logger.error(jex);
		}
		return result;
	}
	
	public CompleteMetadata unmarshall (String xmldata) {
		CompleteMetadata result = null;
		
		try {
			
			Unmarshaller um = context.createUnmarshaller ( ); 
			um.setEventHandler (new javax.xml.bind.helpers.DefaultValidationEventHandler ( ));
			result = (CompleteMetadata) um.unmarshal (new StringReader (xmldata));
			
		} catch (JAXBException ex) {
			
			//ex.printStackTrace ( );
			logger.error(ex);
		}
		
		return result;
	}
	
}
