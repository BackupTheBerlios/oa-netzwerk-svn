package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import de.dini.oanetzwerk.oaipmh.oaidc.OAIDCType;

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
		header.setDatestamp (dataConnection.getDateStamp (parameter.get ("identifier") [0]));
		
		for (String classification : dataConnection.getClassifications (parameter.get ("identifier") [0])) {
			
			header.getSetSpec ( ).add (classification);
		}
		
		record.setHeader (header);
		
		MetadataType metadata = new MetadataType ( );
		
		OAIDCType oaidctype = new OAIDCType ( );
		
		for (String title : dataConnection.getTitles (parameter.get ("identifier") [0])) {
			
			oaidctype.getTitle ( ).add (title);
		}
		
		for (String creator : dataConnection.getCreators (parameter.get ("identifier") [0])) {
			
			oaidctype.getCreator ( ).add (creator);
		}
		
		for (String subject : dataConnection.getSubjects (parameter.get ("identifier") [0])) {
			
			oaidctype.getSubject ( ).add (subject);
		}
		
		for (String description : dataConnection.getDescriptions (parameter.get ("identifier") [0])) {
			
			oaidctype.getDescription ( ).add (description);
		}
		
//		oaidctype.getDescription ( ).add ("In dieser Arbeit untersuchen wir die Struktur von Gausschen Prozessen, die durch gewisse lineare Transformationen von zwei Gausschen Martingalen erzeugt werden. Die Klasse dieser Transformationen ist durch nanzmathematische Gleichgewichtsmodelle mit heterogener Information motiviert.     In Kapital 2 bestimmen wir für solche Prozesse, die zunächst in einer erweiterten Filtrierung konstruiert werden, die kanonische Zerlegung als Semimartin-gale in ihrer eigenen Filtrierung. Die resultierende Drift wird durch Volterra-Kerne beschrieben. Insbesondere charakterisieren wir diejenigen Prozesse, die in ihrer eigenen Filtrierung eine Brownsche Bewegung bilden. In Kapital 3 konstruieren wir neue orthogonale Zerlegungen der Brownschen Filtrierungen.  In den Kapitaln 4 bis 6 wenden wir unsere Resultate zur Charakterisierung Brownscher Bewegungen im Kontext nanzmathematischer Modelle an, in denen es Marktteilnehmer mit zusätzlicher Insider-Information gibt. Wir untersuchen Erweiterungen eines Gleichgewichtsmodells von Kyle [42] und Back [7], in denen die Insider-Information in verschiedener Weise durch Gaussche Martingale spezifiziert wird. Insbesondere klären wir die Struktur von Insider-Strategien, die insofern unaufallig bleiben, als sich die resultierende Gesamtnachfrage wie eine Brownsche Bewegung verhält.");
//		oaidctype.getDescription ( ).add ("In this thesis, we study Gaussian processes generated by certain linear transformations of two Gaussian martingales. This class of transformations is motivated by  nancial equilibrium models with heterogeneous information. In Chapter 2 we derive the canonical decomposition of such processes, which are constructed in an enlarged ltration, as semimartingales in their own ltration. The resulting drift is described in terms of Volterra kernels. In particular we characterize  those processes which are Brownian motions in their own ltration. In Chapter 3 we construct new orthogonal decompositions of Brownian ltrations.   In Chapters 4 to 6 we are concerned with applications of our characterization results in the context of mathematical models of insider trading. We analyze extensions of the nancial equilibrium model of Kyle [42] and Back [7] where the Gaussian martingale describing the insider information is specified in various ways. In particular we discuss the structure of insider strategies which remain inconspicuous in the sense that the resulting cumulative demand is again a Brownian motion.");
		
		for (String publisher : dataConnection.getPublishers (parameter.get ("identifier") [0])) {
			
			oaidctype.getPublisher ( ).add (publisher);
		}
		
//		oaidctype.getPublisher ( ).add ("Mathematisch-Naturwissenschaftliche Fakultät II");
		
		for (String date : dataConnection.getDates (parameter.get ("identifier") [0])) {
			
			oaidctype.getDate ( ).add (date);
		}
		
//		oaidctype.getDate ( ).add ("1999-06-08");
		
		for (String type : dataConnection.getTypes (parameter.get ("identifier") [0])) {
			
			oaidctype.getType ( ).add (type);
		}
		
//		oaidctype.getType ( ).add ("Text");
//		oaidctype.getType ( ).add ("dissertation");
		
		for (String format : dataConnection.getFormats (parameter.get ("identifier") [0])) {
			
			oaidctype.getFormat ( ).add (format);
		}
		
//		oaidctype.getFormat ( ).add ("application/pdf");
		
		for (String identifier : dataConnection.getIdentifiers (parameter.get ("identifier") [0])) {
			
			oaidctype.getIdentifier ( ).add (identifier);
		}
		
//		oaidctype.getIdentifier ( ).add ("http://edoc.hu-berlin.de/dissertationen/mathe/wu-ching-tang/PDF/Wu.pdf");
//		oaidctype.getFormat ( ).add ("application/postscript");
//		oaidctype.getIdentifier ( ).add ("http://edoc.hu-berlin.de/dissertationen/mathe/wu-ching-tang/PS/Wu.ps");
		
		for (String language : dataConnection.getLanguages (parameter.get ("identifier") [0])) {
			
			oaidctype.getLanguage ( ).add (language);
		}
		
//		oaidctype.getLanguage ( ).add ("eng");
		
		metadata.setAny (oaidctype);
		
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
