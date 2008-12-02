package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.openarchives.oai._2.ListMetadataFormatsType;
import org.openarchives.oai._2.MetadataFormatType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ObjectFactory;
import org.openarchives.oai._2.RequestType;
import org.openarchives.oai._2.VerbType;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ListMetadataFormats implements OAIPMHVerbs {

	/**
	 * @see de.dini.oanetzwerk.oaipmh.OAIPMHVerbs#processRequest()
	 */
	@Override
	public String processRequest ( ) {
		
		ObjectFactory obfac = new ObjectFactory ( );
		ListMetadataFormatsType metaDataFormatsList = obfac.createListMetadataFormatsType ( );
		MetadataFormatType metaDataFormat = new MetadataFormatType ( );
		metaDataFormat.setMetadataPrefix ("oai_dc");
		metaDataFormat.setSchema ("http://www.openarchives.org/OAI/2.0/dc.xsd");
		metaDataFormat.setMetadataNamespace ("http://purl.org/dc/elements/1.1/");
		
		metaDataFormatsList.getMetadataFormat ( ).add (metaDataFormat);
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_METADATA_FORMATS);
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setListMetadataFormats (metaDataFormatsList);
		
		Writer w = new StringWriter ( );
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OAIPMHtype.class);
			Marshaller m = context.createMarshaller ( );
			m.setProperty (Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty (Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
			m.marshal (obfac.createOAIPMH (oaipmhMsg), w);
			
		} catch (JAXBException ex) {
			
			ex.printStackTrace ( );
		}
		
		return w.toString ( );
	}
}
