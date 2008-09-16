/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;

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
	
	private Properties props = null;
	private static Logger logger = Logger.getLogger (RepositoryView.class);
	private int detail = 0;
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidPropertiesFormatException 
	 * 
	 */
	
	public RepositoryView ( ) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {

		this.props = HelperMethods.loadPropertiesFromFile ("webapps/adminservlet/WEB-INF/admingui.xml");
	}
	
	public List <RepositoriesBean> getRepositories ( ) {
		
		String result = this.prepareRestTransmission ("Repository/").GetData ( );
		List <RepositoriesBean> repoList = new LinkedList <RepositoriesBean> ( );
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no Repository Details at all from the server");
			System.exit (1);
		}
		
		for (RestEntrySet res : rms.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			RepositoriesBean repo = new RepositoriesBean ( );
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key: " + key + " value: " + res.getValue (key));
				
				if (key.equalsIgnoreCase ("name")) {
					
					repo.setName (res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("url")) {
					
					repo.setUrl (res.getValue (key));
					
				} else if (key.equalsIgnoreCase ("repository_id")) {
					
					repo.setId (new Integer (res.getValue (key)));
					
				} else
					continue;
			}
			
			repoList.add (repo);
		}
		return repoList;
	}
	
	public String showDetails ( ) {
		
		FacesContext context = FacesContext.getCurrentInstance(); 
		Map map = context.getExternalContext().getRequestParameterMap();
		
		logger.debug ("detail: " + (String) map.get ("id"));
		
		return "";
	}
	
	/**
	 * @param resource
	 * @return
	 */
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
	
	/**
	 * @return the detail
	 */
	
	public final int getDetail ( ) {
		
		return this.detail;
	}
	
	/**
	 * @param detail the detail to set
	 */
	
	public final void setDetail (int detail) {
		
		this.detail = detail;
	}
}
