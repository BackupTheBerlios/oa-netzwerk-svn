/**
 * 
 */
package src.de.dini.oanetzwerk.admin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.Constants;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.util.StringManager;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;

/**
 * @author Michael K&uuml;hn
 *
 */

public class OAdmiNRealm extends RealmBase {
	
	private static Logger logger = Logger.getLogger (OAdmiNRealm.class);
	private String pathname = "conf/db-users.conf";
	private static StringManager sm = StringManager.getManager (Constants.Package);
	private Properties props = new Properties ( );
	
	/**
	 * 
	 */
	
	public OAdmiNRealm ( ) {
		
		super ( );
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#start()
	 */
	@Override
	public synchronized void start ( ) throws LifecycleException {
		
		super.start ( );
		File file = new File (this.pathname);
		
		if (!file.isAbsolute ( ))
			file = new File (System.getProperty ("catalina.base"), this.pathname);
		
		if (!file.exists ( ) || !file.canRead ( ))
			throw new LifecycleException (sm.getString ("OAdmiNRealm.loadExist", file.getAbsolutePath ( )));
	}
	
	/**
	 * @see org.apache.catalina.realm.RealmBase#stop()
	 */
	@Override
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
		
		return getUserData (username) [1];
	}
	
	/**
	 * @return
	 */
	
	private String [ ] getUserData (String username) {
		
		HttpClient newclient = new HttpClient ( );
		String userData [ ] = {"", "", "", ""};
		
		try {
			
			this.props.loadFromXML (new FileInputStream (this.pathname));
			
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
		
		logger.debug ("user: " + this.props.getProperty ("username"));
		logger.debug ("pw: " + this.props.getProperty ("password"));
		logger.debug ("truststore: " + System.getProperty ("catalina.base") + this.props.getProperty ("trustStore"));
		System.setProperty ("javax.net.ssl.trustStore", this.props.getProperty ("trustStore"));
		System.setProperty ("javax.net.ssl.keyStorePassword", this.props.getProperty ("keystorepassword"));
		newclient.getParams ( ).setAuthenticationPreemptive (true);
		Credentials defaultcreds = new UsernamePasswordCredentials (this.props.getProperty ("username"), this.props.getProperty ("password"));
		newclient.getState ( ).setCredentials (new AuthScope (this.props.getProperty ("url"), new Integer (this.props.getProperty ("port")), AuthScope.ANY_REALM), defaultcreds);
		newclient.getParams ( ).setParameter ("http.protocol.content-charset", "UTF-8");
		String url = "https://" + this.props.getProperty ("url") + ":" + this.props.getProperty ("port") + "/" + this.props.getProperty ("servletPath") + "/LoginData/" + username;
		logger.debug ("URL: " + url);
		GetMethod method = new GetMethod (url);
		int statusCode = 403;
		
		try {
			
			statusCode = newclient.executeMethod (method);
			logger.debug ("StatusCode: " + statusCode);
			
		} catch (HttpException ex) {
			
			logger.fatal (ex.getLocalizedMessage ( ), ex);
			return null;
			
		} catch (IOException ex) {
			
			logger.fatal (ex.getLocalizedMessage ( ), ex);
			return null;
		}
		
		if (statusCode != HttpStatus.SC_OK)
			return null;
		
		else {
			
			try {
				
				RestMessage rms = RestXmlCodec.decodeRestMessage (new String (method.getResponseBody ( )));
				
				if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
					
					logger.fatal ("Received no data!");
					return null;
					
				} else {
					
					RestEntrySet res = rms.getListEntrySets ( ).get (0);
					Iterator <String> it = res.getKeyIterator ( );
					String key = "";
					
					while (it.hasNext ( )) {
						
						key = it.next ( );
						logger.debug ("Key: " + key);
						logger.debug ("Value: " + res.getValue (key));
						
						
						if (key.equalsIgnoreCase ("name"))
							userData [0] = res.getValue (key);
						
						else if (key.equalsIgnoreCase ("password"))
							userData [1] = res.getValue (key);
						
						else if (key.equalsIgnoreCase ("email"))
							userData [2] = res.getValue (key);
						
						else if (key.equalsIgnoreCase ("superuser"))
							userData [3] = res.getValue (key);
					}
				
					rms = null;
					res = null;
					
					logger.debug (userData [0] + " : " + userData [1] + " : " + userData [2] + " : " + userData [3]);
					
					return userData;
				}
				
			} catch (IOException ex) {

				logger.fatal (ex.getLocalizedMessage ( ), ex);
				return null;
			}
		}
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
		
		//byte [ ] restcredentials;
		
//		try {
//			
//			restcredentials = getPassword (username).getBytes ("UTF-8");
//			
//		} catch (UnsupportedEncodingException ex) {
//			
//			logger.fatal (ex.getLocalizedMessage ( ), ex);
//			return null;
//		}
		
//		boolean validated = this.checkPassword (credentials, restcredentials);
		boolean validated = this.checkPassword (credentials, getPassword (username));
		
		if (validated)
			return new GenericPrincipal (this, username, credentials, this.getRoles (username));
			
		return null;
	}

	/**
	 * @param username 
	 * @return
	 */
	
	private List <String> getRoles (String username) {
		
		List <String> rolelist = new LinkedList <String> ( );
		
		if (getUserData (username) [3].equals ("false")) {
			
			rolelist.add ("oadmin");
			return rolelist;
		
		} else if (getUserData (username) [3].equals ("true")) {
			
			rolelist.add ("oadmin");
			rolelist.add ("oasysop");
			return rolelist;
			
		} else {
		
			return null;
		}
	}

	/**
	 * @param credentials
	 * @param restcredentials
	 * @return
	 */
	
	private final boolean checkPassword (String credentials, String restcredentials) {
		
//		MessageDigest sha = null;
//		byte credByte [ ] = null;
//		String encoding = this.getDigestEncoding ( );
		
//		try {
//			
//			sha = MessageDigest.getInstance (this.getDigest ( ));
//			
//			if (encoding != null)
//				credByte = credentials.getBytes (encoding);
//			
//			else
//				credByte = credentials.getBytes ( );
//			
//		} catch (NoSuchAlgorithmException ex) {
//			
//			logger.fatal (ex.getLocalizedMessage ( ), ex);
//			return false;
//			
//		} catch (UnsupportedEncodingException ex) {
//			
//			logger.fatal (ex.getLocalizedMessage ( ), ex);
//			return false;
//		}
		
//		byte shacred [ ] = sha.digest (credByte);
		logger.debug ("entered pw: " + credentials);
		logger.debug ("restpw: " + restcredentials);
//		credByte = "There is no password!!!".getBytes ( );
		
//		if (credByte.length != restcredentials.length)
		if (credentials.length ( ) != restcredentials.length ( ))
			return false;
		
		if (!credentials.equalsIgnoreCase (restcredentials))
			return false;
//		for (int i = 0; i < restcredentials.length; i++) {
//			
//			if (restcredentials [i] != credByte [i])
//				return false;
//		}
		
//		credByte = "There is no password!!!".getBytes ( );
		
		return true;
	}
}
