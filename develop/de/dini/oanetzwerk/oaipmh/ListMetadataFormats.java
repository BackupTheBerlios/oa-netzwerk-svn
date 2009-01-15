package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import de.dini.oanetzwerk.oaipmh.oaipmh.ListMetadataFormatsType;
import de.dini.oanetzwerk.oaipmh.oaipmh.MetadataFormatType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHObjectFactory;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHerrorcodeType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHtype;
import de.dini.oanetzwerk.oaipmh.oaipmh.RequestType;
import de.dini.oanetzwerk.oaipmh.oaipmh.VerbType;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ListMetadataFormats implements OAIPMHVerbs {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ListMetadataFormats.class);
	
	/**
	 * 
	 */
	
	private ConnectionToolkit dataConnectionToolkit;
	
	/**
	 * 
	 */
	
	//TODO: load ConnectionType from property file
	private DataConnectionType conType = DataConnectionType.DB;
	
	/**
	 * @see de.dini.oanetzwerk.oaipmh.OAIPMHVerbs#processRequest()
	 */
	
	public String processRequest (Map <String, String [ ]> parameter) {
		
		OAIPMHObjectFactory obfac = new OAIPMHObjectFactory ( );
		
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_METADATA_FORMATS);
		
		if (parameter.size ( ) > 1) {
			
			if (parameter.size ( ) == 2 && parameter.containsKey ("identifier")) {
				
				String identifier = parameter.get ("identifier") [0];
				
				this.dataConnectionToolkit = ConnectionToolkit.getFactory (this.conType);
				
				DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection ( );
				
				if (dataConnection.existsIdentifier (identifier))
					reqType.setIdentifier (identifier);
				
				else {
					return new OAIPMHError (OAIPMHerrorcodeType.ID_DOES_NOT_EXIST).toString ( );
				}
				
			} else
				return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		}
		
		ListMetadataFormatsType metaDataFormatsList = obfac.createListMetadataFormatsType ( );
		MetadataFormatType metaDataFormat = new MetadataFormatType ( );
		metaDataFormat.setMetadataPrefix ("oai_dc");
		metaDataFormat.setSchema ("http://www.openarchives.org/OAI/2.0/dc.xsd");
		metaDataFormat.setMetadataNamespace ("http://purl.org/dc/elements/1.1/");
		
		metaDataFormatsList.getMetadataFormat ( ).add (metaDataFormat);
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
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
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return w.toString ( );
	}
}
