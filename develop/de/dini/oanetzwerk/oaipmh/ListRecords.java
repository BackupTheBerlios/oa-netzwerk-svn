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

import de.dini.oanetzwerk.oaipmh.oaidc.OAIDCType;
import de.dini.oanetzwerk.oaipmh.oaipmh.HeaderType;
import de.dini.oanetzwerk.oaipmh.oaipmh.ListRecordsType;
import de.dini.oanetzwerk.oaipmh.oaipmh.MetadataType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHObjectFactory;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHerrorcodeType;
import de.dini.oanetzwerk.oaipmh.oaipmh.OAIPMHtype;
import de.dini.oanetzwerk.oaipmh.oaipmh.RecordType;
import de.dini.oanetzwerk.oaipmh.oaipmh.RequestType;
import de.dini.oanetzwerk.oaipmh.oaipmh.VerbType;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ListRecords implements OAIPMHVerbs {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ListRecords.class);
	
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
	 * @see de.dini.oanetzwerk.oaipmh.OAIPMHVerbs#processRequest(java.util.Map)
	 */
	
	public String processRequest (Map <String, String [ ]> parameter) {
		
		if (parameter.size ( ) < 2) {
			
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		}
		
		OAIPMHObjectFactory obfac = new OAIPMHObjectFactory ( );
		
		ListRecordsType listRecord = obfac.createListRecordsType ( );
		
		RecordType record = new RecordType ( );
		
		HeaderType header = new HeaderType ( );
		header.setIdentifier ("oai:oanet:1234");
		header.setDatestamp ("2002-07-29");
		header.getSetSpec ( ).add ("ddc:610");
		header.getSetSpec ( ).add ("dini:3");
		header.getSetSpec ( ).add ("dnb:33");
		header.getSetSpec ( ).add ("other:1");
		header.getSetSpec ( ).add ("other:9");
		header.getSetSpec ( ).add ("other:10");
		
		record.setHeader (header);
		
		MetadataType metadata = new MetadataType ( );
		
		OAIDCType oaidctype = new OAIDCType ( );
		
		oaidctype.getTitle ( ).add ("Gastrointestinaler Sauerstofftransport und Laktatstoffwechsel während des normothermen kardiopulmonalen Bypasses beim Menschen");
		oaidctype.getCreator ( ).add ("Jürgen Birnbaum");
		oaidctype.getPublisher ( ).add ("Medizinische Fakultät - Universitätsklinikum Charité");
		oaidctype.getDate ( ).add ("1998-03-02");
		oaidctype.getIdentifier ( ).add ("http://edoc.hu-berlin.de/dissertationen/medizin/birnbaum-juergen/PDF/Birnbaum.pdf");
		
		metadata.setAny (oaidctype);
		
		record.setMetadata (metadata);
		
		listRecord.getRecord ( ).add (record);
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_RECORDS);
		
		if (parameter.get ("metadataPrefix") [0] != null && !parameter.get ("metadataPrefix") [0].equals (""))
			reqType.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);
		
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setListRecords (listRecord);
		
		Writer w = new StringWriter ( );
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OAIDCType.class, OAIPMHtype.class);
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
