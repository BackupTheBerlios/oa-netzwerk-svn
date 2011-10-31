package de.dini.oanetzwerk.servicemodule;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
/**
 * This class should be used when running Runtime.exec() command, because according to the javadocs 
 * streams by a process need to be cleared in order to prevent blocks or deadlocks due to limited 
 * stream buffer size (system-dependent)
 * 
 * Read javadocs: http://download.oracle.com/javase/6/docs/api/java/lang/Process.html
 * 
 * @author Sammy David
 *
 */
public class ProcessStreamHandler extends Thread {

	private final static Logger logger = Logger.getLogger(ProcessStreamHandler.class);
	
	InputStream inputStream;
	String streamType;

	public ProcessStreamHandler(InputStream inputStream, String streamType) {
		this.inputStream = inputStream;
		this.streamType = streamType;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				logger.debug(streamType + " —> " + line);
			}
			bufferedReader.close();

		} catch (Exception e) {
			logger.debug(e);
		}

	}
}
