package de.dini.oanetzwerk.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.Constants;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.util.StringManager;
import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ResourceRealm extends RealmBase {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ResourceRealm.class);
	
	/**
	 * 
	 */
	
	private Properties props = new Properties ( );
	
	/**
	 * 
	 */
	
	private static StringManager sm = StringManager.getManager (Constants.Package);
	
	/**
	 * 
	 */
	
	private String pathname = "conf/resource.conf";
	
	/**
	 * 
	 */

	private File configFile;
	
	
	public ResourceRealm ( ) {
		
		super ( );
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#start()
	 */
	
	public synchronized void start ( ) throws LifecycleException {
		
		super.start ( );
		configFile = new File (this.pathname);
		
		if (!configFile.isAbsolute ( ))
			configFile = new File (System.getProperty ("catalina.base"), this.pathname);
		
		if (!configFile.exists ( ) || !configFile.canRead ( ))
			throw new LifecycleException (sm.getString ("ResourceRealm.loadExist", configFile.getAbsolutePath ( )));
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#stop()
	 */
	
	public synchronized void stop ( ) throws LifecycleException {
		
		super.stop ( );
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
		
		try {
			
			System.out.println(pathname);
			this.props.loadFromXML (new FileInputStream (configFile));
			
		} catch (InvalidPropertiesFormatException ex1) {
			
			logger.fatal (ex1.getLocalizedMessage ( ), ex1);
			return null;
			
		} catch (FileNotFoundException ex1) {
			
			logger.fatal (ex1.getLocalizedMessage ( ), ex1);
			return null;
			
		} catch (IOException ex1) {
			
			logger.fatal (ex1.getLocalizedMessage ( ), ex1);
			return null;
		}
		
		return this.props.getProperty (username);
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#getPrincipal(java.lang.String)
	 */
	
	@Override
	protected Principal getPrincipal (final String username) {

		return new GenericPrincipal (this, username, getPassword (username), this.getRoles (username));
	}
	
	/**
	 * @param username 
	 * @return
	 */
	
	private List <String> getRoles (String username) {
		
		List <String> rolelist = new LinkedList <String> ( );
		
		rolelist.add ("oanetzwerk");
		return rolelist;
	}
}
