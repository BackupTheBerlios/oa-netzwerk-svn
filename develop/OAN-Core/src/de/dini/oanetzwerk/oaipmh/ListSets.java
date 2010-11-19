package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import de.dini.oanetzwerk.oaipmh.DataConnection;


/**
 * @author Sammy David
 * @author Michael Kühn
 *
 */

public class ListSets extends AbstractOAIPMHVerb {
	
	private static Logger logger = Logger.getLogger (ListSets.class);
	
	
	public String processRequest (Map <String, String [ ]> parameter) {
		
		String errorMsg = checkForBadArguments(parameter);
		if (StringUtils.isNotEmpty(errorMsg)) {
			return errorMsg;
		}
		
		if (parameter != null && parameter.size ( ) > 1) {
			
			if (parameter.containsKey ("resumptionToken"))
				return new OAIPMHError (OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN).toString ( );
			
			return new OAIPMHError (OAIPMHErrorcodeType.BAD_ARGUMENT).toString ( );
		}
		
		
		ListSetsType listSets = new ListSetsType();
		listSets.getSets().addAll(this.getSets ( ));
		

		RequestType reqType = new RequestType();
		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		reqType.setVerb (VerbType.LIST_SETS);
		oaipmhMsg.setListSets (listSets);
		
		Writer w = new StringWriter ( );
		
		try {
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);
            IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
            mctx.setIndent(2);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mctx.setOutput(bos, "UTF-8");
            mctx.marshalDocument(oaipmhMsg, "UTF-8", false, w);
            
			return w.toString();
		} catch (JiBXException e) {
			logger.warn(e.getMessage() + e);
			System.err.println(e.getLocalizedMessage ( ) +  e);
		}
		
		return null;
	}

	/**
	 * @return
	 */
	
	private Collection <SetType> getSets ( ) {
				
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