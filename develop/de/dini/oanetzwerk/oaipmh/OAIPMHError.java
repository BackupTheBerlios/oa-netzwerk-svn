package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.openarchives.oai._2.OAIPMHerrorType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ObjectFactory;
import org.openarchives.oai._2.RequestType;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Michael K&uuml;hn
 *
 */

public class OAIPMHError {
	
	OAIPMHerrorcodeType error;
	
	/**
	 * @param bad_argument
	 */
	
	public OAIPMHError (OAIPMHerrorcodeType error) {
		
		this.error = error;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	
	public String toString ( ) {
		
		Writer w = new StringWriter ( );
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OAIPMHtype.class);
			Marshaller m = context.createMarshaller ( );
			m.setProperty (Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty (Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
			this.createErrorMsg ( );
			m.marshal (this.createErrorMsg ( ), w);
			
		} catch (JAXBException ex) {
			
			ex.printStackTrace();
		}
		
		
		return w.toString ( );
	}

	/**
	 * @return 
	 * 
	 */
	
	private JAXBElement <OAIPMHtype> createErrorMsg ( ) {
		
		ObjectFactory obfac = new ObjectFactory ( );
		
		OAIPMHerrorType oaiError = obfac.createOAIPMHerrorType ( );
		
		switch (this.error) {
			case BAD_ARGUMENT:
				oaiError.setCode (OAIPMHerrorcodeType.BAD_ARGUMENT);
				oaiError.setValue ("Sorry! One argument in the request is not valid.");
				break;
			
			case BAD_VERB:
				oaiError.setCode (OAIPMHerrorcodeType.BAD_VERB);
				oaiError.setValue ("Sorry! The requested verb is illegal.");
				break;
			
			case BAD_RESUMPTION_TOKEN:
				oaiError.setCode (OAIPMHerrorcodeType.BAD_RESUMPTION_TOKEN);
				oaiError.setValue ("Sorry! The requested resumptionToken does not exist or has already expired.");
				break;
			
			case CANNOT_DISSEMINATE_FORMAT:
				oaiError.setCode (OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT);
				break;
			
			case ID_DOES_NOT_EXIST:
				oaiError.setCode (OAIPMHerrorcodeType.ID_DOES_NOT_EXIST);
				break;
			
			case NO_METADATA_FORMATS:
				oaiError.setCode (OAIPMHerrorcodeType.NO_METADATA_FORMATS);
				break;
			
			case NO_RECORDS_MATCH:
				oaiError.setCode (OAIPMHerrorcodeType.NO_RECORDS_MATCH);
				break;
			
			case NO_SET_HIERARCHY:
				oaiError.setCode (OAIPMHerrorcodeType.NO_SET_HIERARCHY);
				break;
			
			default:
				oaiError.setValue ("Sorry! I don't know what happened and I don't know what to do. The only thing I know is that YOU should do something completely different!");
				break;
		}
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.getError ( ).add (oaiError);
		
		return obfac.createOAIPMH (oaipmhMsg);
	}
}
