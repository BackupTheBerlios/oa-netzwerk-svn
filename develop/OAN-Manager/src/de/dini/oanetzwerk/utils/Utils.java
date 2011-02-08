package de.dini.oanetzwerk.utils;

public class Utils {

	private final static String WEBAPP_DIR 			= "/webapps";
	private final static String DEFAULT_CONTEXT 	= "OAN-Manager";
	
	
	public static void loadResource() {
		
	}
	
	public static String getWebappPath()
	{
		return WEBAPP_DIR + DEFAULT_CONTEXT;
	}

	public static String getDefaultContext() {
    	return DEFAULT_CONTEXT;
    }
	
	
}
