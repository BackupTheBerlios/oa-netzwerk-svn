package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.openarchives.oai._2.DeletedRecordType;
import org.openarchives.oai._2.DescriptionType;
import org.openarchives.oai._2.GranularityType;
import org.openarchives.oai._2.IdentifyType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ObjectFactory;
import org.openarchives.oai._2.RequestType;
import org.openarchives.oai._2.VerbType;
import org.openarchives.oai._2_0.oai_identifier.OaiIdentifierType;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Michael K&uuml;hn
 *
 */

public class Identify implements OAIPMHVerbs {
	
	private static Logger logger = Logger.getLogger (Identify.class);
	
	/**
	 * @see de.dini.oanetzwerk.oaipmh.OAIPMHVerbs#processRequest()
	 */
	@Override
	public String processRequest (Map <String, String [ ]> parameter) {
		
		if (parameter.size ( ) > 1)
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		
		ObjectFactory obfac = new ObjectFactory ( );
		IdentifyType identify = obfac.createIdentifyType ( );
		identify.setBaseURL ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		identify.setEarliestDatestamp ("1970-01-01"); //TODO: get earliest datestamp from database
		identify.setGranularity (GranularityType.YYYY_MM_DD);
		identify.setProtocolVersion ("2.0");
		identify.setRepositoryName ("OA Netzwerk OAI-PMH Export Interface");
		identify.getAdminEmail ( ).add ("oanet@oanet.cms.hu-berlin.de");
		identify.setDeletedRecord (DeletedRecordType.NO);
		DescriptionType descr = obfac.createDescriptionType ( );
		OaiIdentifierType oaiIdent = new OaiIdentifierType ( );
		oaiIdent.setDelimiter (":");
		oaiIdent.setRepositoryIdentifier ("oanet");
		oaiIdent.setSampleIdentifier ("oai:oanet:152");
		oaiIdent.setScheme ("oai");
		descr.setAny (oaiIdent);
		
		identify.getDescription ( ).add (descr);
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		reqType.setVerb (VerbType.IDENTIFY);
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setIdentify (identify);
		
		Writer w = new StringWriter ( );
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OaiIdentifierType.class, OAIPMHtype.class);
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