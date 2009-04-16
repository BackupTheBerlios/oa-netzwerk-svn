/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Michael K&uuml;hn
 *
 */

public class MainView implements Serializable {
	
	private static Logger logger = Logger.getLogger (MainView.class);
	
	/**
	 * @throws IOException 
	 * 
	 */
	
	public MainView ( ) throws IOException {
		
		logger.setLevel (Level.DEBUG);
		logger.addAppender (new FileAppender (new PatternLayout ("%d{ISO8601} %-5p [%t] %c: %m%n"), "/usr/local/www/logs/tomcat/adminservlet.log"));
	}
	
	public String jump2Repos ( ) {
		
		return "go2repos";
	}
	
	public String jump2Proc ( ) {
		
		return "go2proc";
	}
	
	public String jump2Serv ( ) {
		
		return "go2serv";
	}
	
	public String jump2Dat ( ) {
		
		return "go2dat";
	}
	
	public String jump2Harvester ( ) {
		
		return "go2harves";
	}
	
	public String jump2Aggregator ( ) {
		
		return "go2aggreg";
	}
	
	public String jump2Eraser ( ) {
		
		return "go2markeras";
	}
	
	public String jump2Manager ( ) {
		
		return "go2manag";
	}
	
	public String jump2Logout ( ) {
		
		return "logout";
	}
}
