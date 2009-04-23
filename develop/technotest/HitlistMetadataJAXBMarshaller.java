package technotest;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.OtherClassification;


public class HitlistMetadataJAXBMarshaller {

	private static Logger logger = Logger.getLogger (HitlistMetadataJAXBMarshaller.class);
	private static JAXBContext context;
	private static HitlistMetadataJAXBMarshaller instance;
		
	private HitlistMetadataJAXBMarshaller() {
		try {
			context = JAXBContext.newInstance( HitlistMetadata.class, InternalMetadata.class, OtherClassification.class, DINISetClassification.class, DNBClassification.class, DDCClassification.class ); 
		} catch(JAXBException jex) {
			logger.error(jex);
		}
	}
	
	public static HitlistMetadataJAXBMarshaller getInstance() {
		if(instance == null) {
			instance = new HitlistMetadataJAXBMarshaller();
		}
		return instance;
	}
	
	public String marshall(HitlistMetadata hmf) {
		String result = null;	
		StringWriter sw = new StringWriter();

		try {
			Marshaller m = context.createMarshaller(); 
			m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
			m.marshal( hmf, sw);
			result = sw.toString();
		} catch(JAXBException jex) {
			logger.error(jex);
		}
		return result;
	}
	
	public HitlistMetadata unmarshall (String xmldata) {
		HitlistMetadata result = null;
		
		try {
			
			Unmarshaller um = context.createUnmarshaller ( ); 
			um.setEventHandler (new javax.xml.bind.helpers.DefaultValidationEventHandler ( ));
			result = (HitlistMetadata) um.unmarshal (new StringReader (xmldata));
			
		} catch (JAXBException ex) {
			
			//ex.printStackTrace ( );
			logger.error(ex);
		}
		
		return result;
	}
	
}
