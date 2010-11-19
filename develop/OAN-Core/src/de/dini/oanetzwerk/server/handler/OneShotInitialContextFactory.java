package de.dini.oanetzwerk.server.handler;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * 
 * @author malitzro
 *
 */
public class OneShotInitialContextFactory implements InitialContextFactory {

	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
//		System.out.println("OneShotInitialContextFactory.getInitialContext()");		
		return new OneShotInitialContext();
	}

}
