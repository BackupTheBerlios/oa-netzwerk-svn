package de.dini.oanetzwerk.oaipmh;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.openarchives.oai._2.ListSetsType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ObjectFactory;
import org.openarchives.oai._2.RequestType;
import org.openarchives.oai._2.SetType;
import org.openarchives.oai._2.VerbType;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ListSets implements OAIPMHVerbs {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ListSets.class);
	
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
		
		
		if (parameter.size ( ) > 1) {
			
			if (parameter.containsKey ("resumptionToken"))
				return new OAIPMHError (OAIPMHerrorcodeType.BAD_RESUMPTION_TOKEN).toString ( );
			
			return new OAIPMHError (OAIPMHerrorcodeType.BAD_ARGUMENT).toString ( );
		}
		
		ObjectFactory obfac = new ObjectFactory ( );
		
		ListSetsType listSets = obfac.createListSetsType ( );
		listSets.getSet ( ).addAll (this.getSets ( ));
		
		OAIPMHtype oaipmhMsg = obfac.createOAIPMHtype ( );
		oaipmhMsg.setResponseDate (new XMLGregorianCalendarImpl (new GregorianCalendar ( )));
		RequestType reqType = obfac.createRequestType ( );
		reqType.setValue ("http://oanet/oaipmh/oaipmh");
		reqType.setVerb (VerbType.LIST_SETS);
		oaipmhMsg.setRequest (reqType);
		oaipmhMsg.setListSets (listSets);
		
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
	
	private Collection <SetType> getSets ( ) {
		
		this.dataConnectionToolkit = ConnectionToolkit.getFactory (this.conType);
		
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection ( );
		
		ArrayList <String [ ]> sets = dataConnection.getSets ( );
		ArrayList <SetType> setArray = new ArrayList <SetType> ( );
		
		for (String [ ] strings : sets) {
			
			SetType testSet = new SetType ( );
			testSet.setSetSpec (strings [0]);
			testSet.setSetName (strings [1]);
			
			setArray.add (testSet);
		}
		
		return setArray;
	}
}
