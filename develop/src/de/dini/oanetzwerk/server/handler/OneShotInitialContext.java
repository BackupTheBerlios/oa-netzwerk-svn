package de.dini.oanetzwerk.server.handler;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.sybase.jdbc3.jdbc.SybDataSource;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * 
 * @author malitzro
 *
 */
public class OneShotInitialContext implements Context {

	private SybDataSource ds = null;
	
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {return null;}
	public void bind(Name name, Object obj) throws NamingException {}
	public void bind(String name, Object obj) throws NamingException {}
	public void close() throws NamingException {}
	public Name composeName(Name name, Name prefix) throws NamingException {return null;}
	public String composeName(String name, String prefix) throws NamingException {return null;}
	public Context createSubcontext(Name name) throws NamingException {return null;}
	public Context createSubcontext(String name) throws NamingException {return null;}
	public void destroySubcontext(Name name) throws NamingException {}
	public void destroySubcontext(String name) throws NamingException {}
	public Hashtable<?, ?> getEnvironment() throws NamingException {return null;}
	public String getNameInNamespace() throws NamingException {return null;}
	public NameParser getNameParser(Name name) throws NamingException {return null;}
	public NameParser getNameParser(String name) throws NamingException {return null;}
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {return null;}
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {return null;}
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {return null;}
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {return null;}
	public Object lookup(Name name) throws NamingException {return null;}

	public Object lookup(String name) throws NamingException {
//		System.out.println("OneShotInitialContext.lookup()");		
		
		if("java:comp/env".equals(name)) return this;
		
		if(ds == null) {
			ds = new SybDataSource();


			Properties props = new Properties();
			try {
				props = HelperMethods.loadPropertiesFromFile("dbprop.xml");		
			} catch(Exception ex) {
				//schade!
			}

			ds.setServerName(props.getProperty("ServerName"));
			ds.setPortNumber(Integer.parseInt(props.getProperty("Port")));
			ds.setUser(props.getProperty("user"));
			ds.setPassword(props.getProperty("password"));

		}
		
		return ds;
	}

	public Object lookupLink(Name name) throws NamingException {return null;}
	public Object lookupLink(String name) throws NamingException {return null;}
	public void rebind(Name name, Object obj) throws NamingException {}
	public void rebind(String name, Object obj) throws NamingException {}
	public Object removeFromEnvironment(String propName) throws NamingException {return null;}
	public void rename(Name oldName, Name newName) throws NamingException {}
	public void rename(String oldName, String newName) throws NamingException {}
	public void unbind(Name name) throws NamingException {}
	public void unbind(String name) throws NamingException {}


}
