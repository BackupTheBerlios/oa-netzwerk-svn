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

import de.dini.oanetzwerk.oaipmh.oaipmh.HeaderType;
import de.dini.oanetzwerk.oaipmh.oaipmh.ListIdentifiersType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHObjectFactory;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHerrorcodeType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHtype;
import de.dini.oanetzwerk.oaipmh.oaipmh.RequestType;
import de.dini.oanetzwerk.oaipmh.oaipmh.VerbType;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ListIdentifiers implements OAIPMHVerbs {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ListIdentifiers.class);
	
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
		
		if (parameter.size ( ) < 2) {
			
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		}
		
		OAIPMHObjectFactory obfac = new OAIPMHObjectFactory ( );
		
		ListIdentifiersType listIdents = obfac.createListIdentifiersType ( );
		
		HeaderType header = new HeaderType ( );
		header.setIdentifier ("oai:oanet:1234");
		header.setDatestamp ("2002-07-29");
		header.getSetSpec ( ).add ("ddc:610");
		header.getSetSpec ( ).add ("dini:3");
		header.getSetSpec ( ).add ("dnb:33");
		header.getSetSpec ( ).add ("other:1");
		header.getSetSpec ( ).add ("other:9");
		header.getSetSpec ( ).add ("other:10");
		
		listIdents.getHeader ( ).add (header);
		
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_IDENTIFIERS);
		
		if (parameter.get ("metadataPrefix") [0] != null && !parameter.get ("metadataPrefix") [0].equals (""))
			reqType.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);
		
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setListIdentifiers (listIdents);
		
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
