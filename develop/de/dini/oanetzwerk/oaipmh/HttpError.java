package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
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

public class HttpError {
	
	/**
	 * 
	 */
	
	private Exception exception;
	
	/**
	 * 
	 */
	
	private int httpStatus;
	
	/**
	 * @param exception
	 * @param httpStatus
	 */
	
	public HttpError (Exception exception, int httpStatus) {
		
		this.exception = exception;
		this.httpStatus = httpStatus;
	}
	
	/**
	 * @return
	 */
	
	public Exception getException ( ) {
		
		return this.exception;
	}
	
	/**
	 * @return
	 */
	
	public int getHttpStatus ( ) {
		
		return this.httpStatus;
	}
	
	/**
	 * @return
	 */
	
	public String getOAIPMHError ( ) {
		
		Writer w = new StringWriter ( );
		
		ObjectFactory obfac = new ObjectFactory ( );
		OAIPMHtype oaipmhMsg =  obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		oaipmhMsg.setRequest (reqType);
		OAIPMHerrorType oaipmhError = obfac.createOAIPMHerrorType ( );
		oaipmhError.setCode (OAIPMHerrorcodeType.BAD_VERB);
		oaipmhError.setValue ("The verb  provided in the request is illegal.");
		oaipmhMsg.getError ( ).add (oaipmhError);
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OAIPMHtype.class);
			Marshaller m = context.createMarshaller ( );
			m.marshal (obfac.createOAIPMH (oaipmhMsg), w);
			
		} catch (JAXBException ex) {
			
			ex.printStackTrace();
		}
		
		return w.toString ( );
	}
}
