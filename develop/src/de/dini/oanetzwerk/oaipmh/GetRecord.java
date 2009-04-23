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
import de.dini.oanetzwerk.oaipmh.oaipmh.GetRecordType;
import de.dini.oanetzwerk.oaipmh.oaipmh.HeaderType;
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
		
		OAIPMHObjectFactory obfac = new OAIPMHObjectFactory ( );
		GetRecordType getRecord = obfac.createGetRecordType ( );
		
		RecordType record = new RecordType ( );
		
		HeaderType header = new HeaderType ( );
		
		header.setIdentifier (parameter.get ("identifier") [0]);
		header.setDatestamp (dataConnection.getDateStamp (parameter.get ("identifier") [0]));
		
		for (String classification : dataConnection.getClassifications (parameter.get ("identifier") [0]))
			header.getSetSpec ( ).add (classification);
		
		record.setHeader (header);
		
		MetadataType metadata = new MetadataType ( );
		
		OAIDCType oaidctype = new OAIDCType ( );
		
		for (String title : dataConnection.getTitles (parameter.get ("identifier") [0]))
			oaidctype.getTitle ( ).add (title);
		
		for (String creator : dataConnection.getCreators (parameter.get ("identifier") [0]))
			oaidctype.getCreator ( ).add (creator);
		
		for (String subject : dataConnection.getSubjects (parameter.get ("identifier") [0]))
			oaidctype.getSubject ( ).add (subject);
		
		for (String description : dataConnection.getDescriptions (parameter.get ("identifier") [0]))
			oaidctype.getDescription ( ).add (description);
		
		for (String publisher : dataConnection.getPublishers (parameter.get ("identifier") [0]))
			oaidctype.getPublisher ( ).add (publisher);
		
		for (String date : dataConnection.getDates (parameter.get ("identifier") [0]))
			oaidctype.getDate ( ).add (date);
		
		for (String type : dataConnection.getTypes (parameter.get ("identifier") [0]))
			oaidctype.getType ( ).add (type);
		
		for (String format : dataConnection.getFormats (parameter.get ("identifier") [0]))
			oaidctype.getFormat ( ).add (format);
		
		for (String identifier : dataConnection.getIdentifiers (parameter.get ("identifier") [0]))
			oaidctype.getIdentifier ( ).add (identifier);
		
		for (String language : dataConnection.getLanguages (parameter.get ("identifier") [0]))
			oaidctype.getLanguage ( ).add (language);
		
		metadata.setAny (oaidctype);
		record.setMetadata (metadata);
		getRecord.setRecord (record);
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		reqType.setVerb (VerbType.GET_RECORD);
		reqType.setIdentifier (parameter.get ("identifier") [0]);
		reqType.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setGetRecord (getRecord);
		
		Writer w = new StringWriter ( );
		
		try {
			
			JAXBContext context = JAXBContext.newInstance (OAIDCType.class, OAIPMHtype.class);
			
			Marshaller m = context.createMarshaller ( );
			m.setProperty (Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty (Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new OAIPMH_NamespacePrefixMapper()); 
			m.marshal (obfac.createOAIPMH (oaipmhMsg), w);
			
		} catch (JAXBException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		return w.toString ( );
	}
}
