/**
 * 
 */
package de.dini.oanetzwerk.admin_old;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


/**
 * @author Michael K&uuml;hn
 *
 */

public class RepositoryItem {
	
	FacesContext ctx = FacesContext.getCurrentInstance ( );
	HttpSession session = (HttpSession) ctx.getExternalContext ( ).getSession (false);
//	private static Logger logger = Logger.getLogger (RepositoryItem.class);
	private String name = "", url = "";
	private Long id = new Long (0);
	
	/**
	 * 
	 */
	
	public RepositoryItem ( ) { }
	
	/**
	 * @param name
	 * @param url
	 * @param id
	 */
	
	public RepositoryItem (String name, String url, Long id) {
		
		this.setName (name);
		this.setUrl (url);
		this.setId (id);
	}
	
	public String detail ( ) {
		
//		logger.debug ("repoID: " + Long.toString (id));
		this.session.setAttribute ("repositoryItem", getId ( ));
		
		return "ID";
	}
	
	/**
	 * @return the name
	 */
	
	public String getName ( ) {
	
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	
	public void setName (String name) {
	
		this.name = name;
	}
	
	/**
	 * @return the url
	 */
	
	public String getUrl ( ) {
	
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	
	public void setUrl (String url) {
	
		this.url = url;
	}
	
	/**
	 * @return the id
	 */
	
	public Long getId ( ) {
	
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	
	public void setId (Long id) {
	
		this.id = id;
	}
}
