/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

public class DataView implements Serializable {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	FacesContext ctx = FacesContext.getCurrentInstance ( );
	HttpSession session = (HttpSession) ctx.getExternalContext ( ).getSession (false);
	private Properties props = null;
	private static Logger logger = Logger.getLogger (DataView.class);
	private Long repository = null;
	private RestMessage repoOIDList;
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidPropertiesFormatException 
	 */
	
	public DataView ( ) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/adminservlet/WEB-INF/admingui.xml");
	}
	
	/**
	 * @return
	 */
	
	public List <OIDItem> getShortRepoData ( ) {
		
		if (this.repository == null || !this.repository.equals ((Long) this.session.getAttribute ("repositoryItem"))) {
		
			String result = this.prepareRestTransmission ("AllOIDs/fromRepositoryID/" + Long.toString ((Long) this.session.getAttribute ("repositoryItem"))).GetData ( );
			this.repoOIDList = RestXmlCodec.decodeRestMessage (result);
		}	
				
		if (this.repoOIDList == null || this.repoOIDList.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no Repository Details at all from the server");
			return null;
		}
		
		List <OIDItem> oidList = new ArrayList <OIDItem> ( );
		
		for (RestEntrySet res : repoOIDList.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			
			OIDItem oid = new OIDItem ( );
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (key.equalsIgnoreCase ("oid")) {
					
					oid.setOid (new Long (res.getValue (key)));
					
				} else
					continue;
			}
			
			oidList.add (oid);
		}
		
		return oidList;
	}
	
	/**
	 * @param resource
	 * @return
	 */
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
}
