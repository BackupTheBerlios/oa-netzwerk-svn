package de.dini.oanetzwerk.utils;

import org.apache.log4j.RollingFileAppender;

public class CustomRollingFileAppender extends RollingFileAppender {

	@Override
	public void setFile(String file) {
		
		super.setFile(file);
	}

	
	
}
