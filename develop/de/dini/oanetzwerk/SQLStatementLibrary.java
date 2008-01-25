package de.dini.oanetzwerk;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class SQLStatementLibrary {

	final static int DB_TYPE_SYBASE = 0;

	final static int used_db_type = DB_TYPE_SYBASE;

	final static String filenameSybaseSQL = "statementsSybaseSQL.txt";
	
	static Logger logger = Logger.getLogger (SQLStatementLibrary.class);
	
	private Properties propsSQLStatements;
	
	private static SQLStatementLibrary instance = null;
    
	private SQLStatementLibrary() {
		String strFilename = null;
    	switch(used_db_type) {
    		case DB_TYPE_SYBASE: 
    			strFilename = filenameSybaseSQL;
    			break;
    		default:
    			logger.error("unsupported db type ("+used_db_type+") - use \"sybase\"");
    			strFilename = filenameSybaseSQL;
    	}
    	propsSQLStatements = new Properties();
    	loadStatements(strFilename);    	
    }

	private void loadStatements(String strFilename) {
		Reader reader = null;
		try {
			// absoluter Weg, das File zu finden:
//			reader = new FileReader("C:\\Dokumente und Einstellungen\\malitzro\\EclipseWorkspace\\OANetzwerkDevelop\\de\\dini\\oanetzwerk\\" + strFilename );
			// File, relativ, wo die Klasse liegt
			reader = new InputStreamReader(SQLStatementLibrary.class.getResourceAsStream(strFilename));
			propsSQLStatements.load( reader ); 
		} 
		catch ( IOException e ) { 
			logger.error("error while reading prepared sql statements from file \"" + strFilename + "\" - " + e); 
		} 
		finally { 
			try { if(reader != null) reader.close(); } catch ( IOException e ) { } 
		}
	}
	
	public String getSQLStatementFor(String key) {
		return propsSQLStatements.getProperty(key);
	}
	
	public Set<String> getStatementKeys() {
		return propsSQLStatements.stringPropertyNames();
	}
	
	public static SQLStatementLibrary getInstance() {
		if (instance == null) {
           instance = new SQLStatementLibrary();
        }
        return instance;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SQLStatementLibrary sqlLib = SQLStatementLibrary.getInstance();
		
		System.out.println("Folgende SQL prepared statements kann man abrufen:");
		/* oder Liste verwenden, was es gibt */
		Set<String> keys = sqlLib.getStatementKeys();		
		for(String key : keys) {
			System.out.println("key \"" + key + "\" liefert: ");
			System.out.println("---> " + sqlLib.getSQLStatementFor(key));			
		}
	}

}
