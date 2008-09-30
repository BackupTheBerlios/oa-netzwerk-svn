/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DataView implements Serializable {
	
	FacesContext ctx = FacesContext.getCurrentInstance ( );
	HttpSession session = (HttpSession) ctx.getExternalContext ( ).getSession (false);
	private Properties props = null;
	private static Logger logger = Logger.getLogger (DataView.class);
	private int oid;
	
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
	
	public List <?> getData ( ) {
		
		String result = this.prepareRestTransmission ("AllOIDs/" + this.getOid ( )).GetData ( );
		
		return null;
	}
	
	
	/**
	 * @return the oid
	 */
	public final int getOid ( ) {
	
		return this.oid;
	}

	
	/**
	 * @param oid the oid to set
	 */
	public final void setOid (int oid) {
	
		this.oid = oid;
	}

	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}
}
