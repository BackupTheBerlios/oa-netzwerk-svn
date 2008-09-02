/**
 * 
 */
package de.dini.oanetzwerk.admin.utils;

import java.security.Principal;
import java.util.List;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

/**
 * @author Michael K&uuml;hn
 *
 */

public class OAdmiNRealm extends RealmBase {
	
	/**
	 * 
	 */
	
	public OAdmiNRealm ( ) {
		
		super ( );
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#getName()
	 */
	
	@Override
	protected String getName ( ) {
		
		return this.getClass ( ).getSimpleName ( );
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#getPassword(java.lang.String)
	 */
	
	@Override
	protected String getPassword (final String username) {
		
		//TODO: getting hashed pw from REST 
		return null;
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#getPrincipal(java.lang.String)
	 */
	
	@Override
	protected Principal getPrincipal (final String username) {
		
		return new GenericPrincipal (this, username, getPassword (username), this.getRoles (username));
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#authenticate(java.lang.String, java.lang.String)
	 */
	
	@Override
	public Principal authenticate (String username, String credentials) {
		
		if (username == null) {
			
			return null;
		}
		
		String restcredentials = getPassword (username);
		
		boolean validated = this.checkPassword (credentials, restcredentials);
		
		if (validated)
			return new GenericPrincipal (this, username, credentials, this.getRoles (username));
			
		return null;
	}

	/**
	 * @param username 
	 * @return
	 */
	
	private List <String> getRoles (String username ) {
		
		// TODO: get Roles from REST
		return null;
	}

	/**
	 * @param credentials
	 * @param restcredentials
	 * @return
	 */
	
	private boolean checkPassword (String credentials, String restcredentials) {

		// TODO: SHA-512 compare
		
		if (credentials.equals (restcredentials))
			return true;
		
		return false;
	}
}
