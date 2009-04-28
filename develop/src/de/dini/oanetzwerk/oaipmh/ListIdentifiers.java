package de.dini.oanetzwerk.oaipmh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

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
import de.dini.oanetzwerk.oaipmh.oaipmh.ResumptionTokenType;
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
	
	private String metadataPrefix;
	
	/**
	 * 
	 */
	
	private BigInteger completeListSize = BigInteger.valueOf (0);
	
	/**
	 * 
	 */
	
	private BigInteger resumptionTokenCursor = BigInteger.valueOf (0);
	
	/**
	 * 
	 */
	
	private String from;
	
	/**
	 * 
	 */
	
	private String until;
	
	/**
	 * 
	 */
	
	private String set;
	
	/**
	 * 
	 */
	
	private String resumptionToken = "";
	
	/**
	 * 
	 */
	//TODO: load ConnectionType from property file
	private DataConnectionType conType = DataConnectionType.DB;
	
	/**
	 * @see de.dini.oanetzwerk.oaipmh.OAIPMHVerbs#processRequest()
	 */
	
	public String processRequest (Map <String, String [ ]> parameter) {
		
		if (parameter.size ( ) < 2)
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		
		if (parameter.containsKey ("resumptionToken")) {
			
			this.resumptionToken = parameter.get ("resumptionToken") [0];
			
		} else if (!parameter.containsKey ("metadataPrefix"))
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		
		else if (!parameter.get ("metadataPrefix") [0].equalsIgnoreCase ("oai_dc"))
			return new OAIPMHError (OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT).toString ( );
		
		else
			this.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);
			
		if (parameter.containsKey ("from"))
			this.setFrom (parameter.get ("from") [0]);
		
		if (parameter.containsKey ("until"))
			this.setUntil (parameter.get ("until") [0]);
		
		if (parameter.containsKey ("set"))
			this.setSet (parameter.get ("set") [0]);

		OAIPMHObjectFactory obfac = new OAIPMHObjectFactory ( );
		RequestType reqType = obfac.createRequestType ( );
		
		ListIdentifiersType listIdents = obfac.createListIdentifiersType ( );
		
		ArrayList <HeaderType> headers = this.getHeaders ( );
		
		if (headers.size ( ) == 0)
			return new OAIPMHError (OAIPMHerrorcodeType.NO_RECORDS_MATCH).toString ( );
		
		listIdents.getHeader ( ).addAll (headers);
		
		if (this.resumptionToken != null) {
			
			ResumptionTokenType resToType = new ResumptionTokenType ( );
			
			GregorianCalendar cal = new GregorianCalendar ( );
			cal.add (GregorianCalendar.DAY_OF_MONTH, 1);
			
			resToType.setExpirationDate (new XMLGregorianCalendarImpl (cal));
			resToType.setValue (this.resumptionToken);
			resToType.setCompleteListSize (this.completeListSize);
			resToType.setCursor (this.resumptionTokenCursor);
			
			listIdents.setResumptionToken (resToType);
		}
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		
		reqType.setValue ("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_IDENTIFIERS);
		
		if (!this.getMetadataPrefix ( ).equals (""))
			reqType.setMetadataPrefix (this.getMetadataPrefix ( ));
		
		if (!this.getFrom ( ).equals (""))
			reqType.setFrom (this.getFrom ( ));
		
		if (!this.getUntil ( ).equals (""))
			reqType.setUntil (this.getUntil ( ));
		
		if (!this.getSet ( ).equals (""))
			reqType.setSet (this.getSet ( ));
		
		if (parameter.containsKey ("resumptionToken"))
			reqType.setResumptionToken (parameter.get ("resumptionToken") [0]);
		
//		if (parameter.containsKey ("metadataPrefix"))
//			reqType.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);
		
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
	
	/**
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	private ArrayList <HeaderType> getHeaders ( ) {
		
		LinkedList <Record> recordList;
		int token = 0;
		
		if (this.resumptionToken.equals ("")) {
		
			this.dataConnectionToolkit = ConnectionToolkit.getFactory (this.conType);
			DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection ( );
			
			recordList = dataConnection.getIdentifierList (this.getFrom ( ), this.getUntil ( ), this.getSet ( ));
			
			if (recordList.size ( ) == 0)
				return new ArrayList <HeaderType> ( );
			
			if (recordList.size ( ) > 10) {			//TODO: get this value from propertyfile
				
				this.resumptionToken = "oanetToken" + UUID.randomUUID ( ).hashCode ( );
				
				this.completeListSize = BigInteger.valueOf (recordList.size ( ));
				
				try {
					
					ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream ("webapps/oaipmh/resumtionToken/" + this.resumptionToken));
					oos.writeObject (recordList);
					
				} catch (FileNotFoundException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
					recordList = new LinkedList <Record> ( );
					
				} catch (IOException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
					recordList = new LinkedList <Record> ( );
				}
				
			} else
				this.resumptionToken = null;
			
		} else {
			
			token = Integer.parseInt (this.resumptionToken.substring (resumptionToken.length ( ) - 2, resumptionToken.length ( )));
			
			logger.debug ("Token: " + token);
			this.resumptionTokenCursor = BigInteger.valueOf (token * 10);	//TODO: get this value from propertyfile
			
			this.resumptionToken = this.resumptionToken.substring (0, resumptionToken.length ( ) - 3);
			
			try {
				
				ObjectInputStream ois = new ObjectInputStream (new FileInputStream ("webapps/oaipmh/resumtionToken/" + this.resumptionToken));
				recordList = (LinkedList <Record>) ois.readObject ( );
				
			} catch (FileNotFoundException ex) {

				logger.error (ex.getLocalizedMessage ( ), ex);
				recordList = new LinkedList <Record> ( );
				
			} catch (IOException ex) {

				logger.error (ex.getLocalizedMessage ( ), ex);
				recordList = new LinkedList <Record> ( );
				
			} catch (ClassNotFoundException ex) {

				logger.error (ex.getLocalizedMessage ( ), ex);
				recordList = new LinkedList <Record> ( );
			}
		}
		
		this.completeListSize = BigInteger.valueOf (recordList.size ( ));
		
		ArrayList <HeaderType> headers = new ArrayList <HeaderType> ( );
		
		for (int i = token * 10; i < recordList.size ( ) && i < token * 10 + 10; i++) { //TODO: get this value from propertyfile
			
			if (i == 0)
				continue;
			
			logger.debug ("i = " + i);
			Record record = recordList.get (i);
			HeaderType header = new HeaderType ( );
			
			header.setIdentifier ("oai:oanet:" + record.getHeader ( ).getIdentifier ( ));
			header.setDatestamp (record.getHeader ( ).getDatestamp ( ));
			
			for (String set : record.getHeader ( ).getSet ( )) {
				
				header.getSetSpec ( ).add (set);
			}
			
			headers.add (header);
		}
		
		if (((token + 1) * 10) > recordList.size ( ))	//TODO: get this value from propertyfile
			this.resumptionToken = "";
		
		else
			this.resumptionToken = this.resumptionToken + "." + String.format ("%2d", ++token).replace (' ', '0');
		
		logger.debug ("resumptionToken to write: " + this.resumptionToken);
		
		return headers;
	}
	
	/**
	 * @return the metadataPrefix
	 */
	
	public final String getMetadataPrefix ( ) {
		
		if (this.metadataPrefix == null)
			this.metadataPrefix = "";
		
		return this.metadataPrefix;
	}
	
	/**
	 * @param metadataPrefix the metadataPrefix to set
	 */
	
	public final void setMetadataPrefix (String metadataPrefix) {
		
		this.metadataPrefix = metadataPrefix;
	}
	
	/**
	 * @return the from
	 */
	
	public final String getFrom ( ) {
		
		if (this.from == null)
			this.from = "";
		
		return this.from;
	}
	
	/**
	 * @param from the from to set
	 */
	
	public final void setFrom (String from) {
	
		this.from = from;
	}
	
	/**
	 * @return the until
	 */
	
	public final String getUntil ( ) {
		
		if (this.until == null)
			this.until = "";
		
		return this.until;
	}
	
	/**
	 * @param until the until to set
	 */
	
	public final void setUntil (String until) {
	
		this.until = until;
	}
	
	/**
	 * @return the set
	 */
	
	public final String getSet ( ) {
		
		if (this.set == null)
			this.set = "";
		
		return this.set;
	}
	
	/**
	 * @param set the set to set
	 */
	
	public final void setSet (String set) {
	
		this.set = set;
	}
}