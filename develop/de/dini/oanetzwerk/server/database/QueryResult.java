/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.sql.ResultSet;
import java.sql.SQLWarning;


/**
 * @author Michael K&uuml;hn
 *
 */

public class QueryResult {
	
	private ResultSet resultSet;
	private int updateCount;
	private SQLWarning warning;
	
	public QueryResult ( ) {

		this.resultSet = null;
		this.updateCount = -1;
		this.warning = null;
	}
	
	/**
	 * @return the resultSet
	 */
	public final ResultSet getResultSet ( ) {
	
		return this.resultSet;
	}
	
	/**
	 * @param resultSet the resultSet to set
	 */
	protected final void setResultSet (ResultSet resultSet) {
	
		this.resultSet = resultSet;
	}
	
	/**
	 * @return the updateCount
	 */
	public final int getUpdateCount ( ) {
	
		return this.updateCount;
	}
	
	/**
	 * @param updateCount the updateCount to set
	 */
	protected final void setUpdateCount (int updateCount) {
	
		this.updateCount = updateCount;
	}
	
	/**
	 * @return the warning
	 */
	public final SQLWarning getWarning ( ) {
	
		return this.warning;
	}
	
	/**
	 * @param warning the warning to set
	 */
	protected final void setWarning (SQLWarning warning) {
	
		this.warning = warning;
	} 
	
	public String toString() {
		return "resultSet="+resultSet+" updateCount="+updateCount+" warning="+warning;
	}
}