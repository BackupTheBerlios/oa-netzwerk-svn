/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;


/**
 * @author Michael Kühn
 *
 */

public class HelperMethods {
	
	public static String stream2String (InputStream stream) {
		
		BufferedReader in = new BufferedReader (new InputStreamReader (stream));
		
		StringBuilder sb = new StringBuilder ( );
		String line = null;
		
		try {
			
			while ((line = in.readLine ( )) != null)				
				sb.append (line + "\n");
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
			
		} finally {
			
			try {
				
				in.close ( );
				
			} catch (IOException ex) {
				
				ex.printStackTrace ( );
			}
		}
		
		return sb.toString ( );
	}
	
	public static Properties loadPropertiesFromFile (String file) {
		
		Properties props = new Properties ( );
		
		try {
			
			props.loadFromXML (new FileInputStream (file));
			
		} catch (InvalidPropertiesFormatException ex) {
			
			ex.printStackTrace ( );
			
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			ex.printStackTrace ( );
		}
		
		return props;
	}
}
