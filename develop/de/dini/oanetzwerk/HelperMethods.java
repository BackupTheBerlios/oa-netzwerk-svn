/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.*;


/**
 * @author Michael Kühn
 *
 */

public class HelperMethods {
	
	static String stream2String (InputStream stream) {
		
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
}
