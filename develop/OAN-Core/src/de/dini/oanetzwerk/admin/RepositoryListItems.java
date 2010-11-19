/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.util.ArrayList;

/**
 * @author kuehnmic
 *
 */
@Deprecated
public class RepositoryListItems extends ArrayList <RepositoryListItems> {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Long selectedID;
	
	/**
	 * @return the selectedID
	 */
	@Deprecated
	public final Long getSelectedID ( ) {
	
		return this.selectedID;
	}
	
	/**
	 * @param selectedID the selectedID to set
	 */
	@Deprecated
	public final void setSelectedID (Long selectedID) {
	
		this.selectedID = selectedID;
	}
}
