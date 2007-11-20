/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.Connection;

/**
 * @author Michael Kühn
 *
 */

public interface DBAccessInterface {

	public abstract void createConnection ( );

	public abstract void closeConnection ( );

	public abstract Connection getConnetion ( );

}
