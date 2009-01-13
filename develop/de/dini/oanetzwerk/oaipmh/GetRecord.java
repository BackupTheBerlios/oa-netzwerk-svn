package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.openarchives.oai._2.GetRecordType;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ObjectFactory;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.RequestType;
import org.openarchives.oai._2.VerbType;
import org.openarchives.oai._2_0.oai_dc.OaiDcType;
import org.purl.dc.elements._1.ElementType;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Michael K&uuml;hn
 *
 */

public class GetRecord implements OAIPMHVerbs {

	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (Identify.class);
	
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
		
		if (parameter.size ( ) != 3)
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		
		if (!parameter.containsKey ("identifier") || !parameter.containsKey ("metadataPrefix"))
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		
		if (!parameter.get ("metadataPrefix") [0].equalsIgnoreCase ("oai_dc"))
			return new OAIPMHError (OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT).toString ( );
		
		this.dataConnectionToolkit = ConnectionToolkit.getFactory (this.conType);
		
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection ( );
		
		if (!dataConnection.existsIdentifier (parameter.get ("identifier") [0]))
			return new OAIPMHError (OAIPMHerrorcodeType.ID_DOES_NOT_EXIST).toString ( );
		
		ObjectFactory obfac = new ObjectFactory ( );
		GetRecordType getRecord = obfac.createGetRecordType ( );
		
		RecordType record = new RecordType ( );
		
		HeaderType header = new HeaderType ( );
		
		header.setIdentifier (parameter.get ("identifier") [0]);
		header.setDatestamp ("1970-01-01");
		header.getSetSpec ( ).add ("pub-type:unknown");
		
		record.setHeader (header);
		
		MetadataType metadata = new MetadataType ( );
		
//		org.openarchives.oai._2_0.oai_dc.ObjectFactory dcFac = new org.openarchives.oai._2_0.oai_dc.ObjectFactory ( );
//		OaiDcType dcType = dcFac.createOaiDcType ( );
		
		OaiDcType dcType = new OaiDcType ( );
		
		ElementType dctitle = new ElementType ( );
		dctitle.setValue ("Titel");
//		
		JAXBElement <ElementType> jaxb = new JAXBElement <ElementType> (new QName ("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd","dc"), ElementType.class, dctitle);
////		jaxb.setValue (dctitle);
//		
		dcType.getTitleOrCreatorOrSubject ( ).add (jaxb);
		
		metadata.setAny (dcType);
		
		record.setMetadata (metadata);
		
		getRecord.setRecord (record);
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		reqType.setVerb (VerbType.GET_RECORD);
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setGetRecord (getRecord);
		
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
