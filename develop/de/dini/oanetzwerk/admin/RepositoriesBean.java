/**
 * 
 */
package de.dini.oanetzwerk.admin;

/**
 * @author Michael K&uuml;hn
 *
 */

public class RepositoriesBean {
	
	private String name = "", url = "";
	private int id = 0;
	
	/**
	 * 
	 */
	
	public RepositoriesBean ( ) { }
	
	/**
	 * @param name
	 * @param url
	 * @param id
	 */
	
	public RepositoriesBean (String name, String url, int id) {
		
		this.setName (name);
		this.setUrl (url);
		this.setId (id);
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
	
	public int getId ( ) {
	
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	
	public void setId (int id) {
	
		this.id = id;
	}
}
