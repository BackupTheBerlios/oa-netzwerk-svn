package de.dini.oanetzwerk.userfrontend;

public class Utils {

	private final static String WEBAPP_DIR 			= "/webapps";
	private final static String DEFAULT_CONTEXT 	= "/OAN-Search";
	
	
	public static void loadResource() {
		
	}
	
	public static String getWebappPath()
	{
		return WEBAPP_DIR + DEFAULT_CONTEXT;
	}
}
