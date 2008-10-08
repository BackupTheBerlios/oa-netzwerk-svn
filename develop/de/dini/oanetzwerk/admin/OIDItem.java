/**
 * 
 */
package de.dini.oanetzwerk.admin;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * @author Michael K&uuml;jn
 *
 */

public class OIDItem {
	
	FacesContext ctx = FacesContext.getCurrentInstance ( );
	HttpSession session = (HttpSession) ctx.getExternalContext ( ).getSession (false);
	private Long oid;
	
	/**
	 * 
	 */
	
	public OIDItem ( ) {
		
		this.setOid (new Long (0));
	}
	
	/**
	 * 
	 */
	
	public String detail ( ) {
		
		this.session.setAttribute ("OIDItem", getLongOid ( ));
		
		return "go2dat";
	}
	
	/**
	 * @return the oid
	 */
	
	public final Long getLongOid ( ) {
		
		return this.oid;
	}
	
	/**
	 * @return
	 */
	
	public final String getOid ( ) {
	
		return Long.toString (this.oid);
	}
	
	/**
	 * @param oid the oid to set
	 */

	public final void setOid (Long oid) {
		
		this.oid = oid;
	}
	
	/**
	 * @return
	 */
	
	public final String getObjectdetail ( ) {
		
		if (this.session.getAttribute ("OIDItem") == null)
			return "details";
		
		if (this.oid.equals ((Long) this.session.getAttribute ("OIDItem"))) {
			
//			String result = this.prepareRestTransmission ("
			
			return "DETAIL";
			
		} else
			return "details";
	}
}
