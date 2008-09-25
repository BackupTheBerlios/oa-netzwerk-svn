/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael K&uuml;hn
 *
 */

public class RepositoryView implements Serializable {
	
	FacesContext ctx = FacesContext.getCurrentInstance ( );
	HttpSession session = (HttpSession) ctx.getExternalContext ( ).getSession (false);
	private Properties props = null;
	private static Logger logger = Logger.getLogger (RepositoryView.class);
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidPropertiesFormatException 
	 * 
	 */
	
	public RepositoryView ( ) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {

		this.props = HelperMethods.loadPropertiesFromFile ("webapps/adminservlet/WEB-INF/admingui.xml");
	}
	
	public HashMap <String, String> getDetail ( ) {
		
		String result = this.prepareRestTransmission ("Repository/" + Long.toString ((Long) this.session.getAttribute ("repositoryItem"))).GetData ( );
		
		HashMap <String, String> details = new HashMap <String, String> ( );
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no Repository Details at all from the server");
			return null;
		}
		
		RestEntrySet res = rms.getListEntrySets ( ).get (0);
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			details.put (key, res.getValue (key));			
		}
		
		details.put ("ID", Long.toString ((Long) this.session.getAttribute ("repositoryItem")));
		
		return details;
	}
	
	/**
	 * @return
	 */
	
	public List <RepositoryItem> getRepositories ( ) {
		
		String result = this.prepareRestTransmission ("Repository/").GetData ( );
		List <RepositoryItem> repoList = new ArrayList <RepositoryItem> ( );
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no Repository Details at all from the server");
			return null;
		}
		
		for (RestEntrySet res : rms.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			RepositoryItem repo = new RepositoryItem ( );
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key: " + key + " value: " + res.getValue (key));
				
				if (key.equalsIgnoreCase ("name")) {
					
					repo.setName (res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("url")) {
					
					repo.setUrl (res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("repository_id")) {
					
					repo.setId (new Long (res.getValue (key)));
					
				} else
					continue;
			}
			
			repoList.add (repo);
		}
		
		return repoList;
	}
	
	public String g2p ( ) {
		
		return "ID";
	}
	
	/**
	 * @param resource
	 * @return
	 */
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
}
