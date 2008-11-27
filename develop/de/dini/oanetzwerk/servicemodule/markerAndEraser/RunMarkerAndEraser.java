package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author MIchael K&uuml;hn
 *
 */

public class RunMarkerAndEraser {

	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (RunMarkerAndEraser.class);
	
	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main (String[] args) {
		
		Options options = new Options ( );
		
		options.addOption ("h", false, "show help");
		
		options.addOption (OptionBuilder.isRequired ( )
				.withLongOpt ("repositoryId")
				.withArgName ("ID")
				.withDescription ("Id for the repository see database for details")
				.withValueSeparator ( )
				.hasArg ( )
				.create ('i'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
				.withLongOpt ("eraseOnlyTestData")
				.withDescription ("only data marked as test will be erased when existent for the given repository")
				.create ('t'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
				.withLongOpt ("markAndErase")
				.withDescription ("peculiar data are marked and old data marked as test will be removed")
				.create ('m'));
		
		if (args.length > 0) {
			
			try {
				
				CommandLine cmd = new GnuParser ( ).parse (options, args);
				
				// if -h was chosen, print the help and exit
				if (cmd.hasOption ("h")) {
					
					HelperMethods.printhelp ("java " + RunMarkerAndEraser.class.getCanonicalName ( ), options);
					System.exit (0);
					
				} else if (cmd.hasOption ('m')) {
					
					new MarkerAndEraser (HelperMethods.getBigDecimalFromCmdLine (cmd.getOptionValue ('i'))).markAndErase ( );
					
				} else if (cmd.hasOption ('t')) {
					
					new MarkerAndEraser (HelperMethods.getBigDecimalFromCmdLine (cmd.getOptionValue ('i'))).eraseTestOnlyData ( );
				}
				
			} catch (ParseException parex) {
				
				logger.error (parex.getLocalizedMessage ( ), parex);
				HelperMethods.printhelp ("java " + RunMarkerAndEraser.class.getCanonicalName ( ), options);
				parex.printStackTrace ( );
				System.exit (1);
				
			} catch (NumberFormatException nufoex) {
				
				logger.error (nufoex.getLocalizedMessage ( ), nufoex);
				HelperMethods.printhelp ("java " + RunMarkerAndEraser.class.getCanonicalName ( ), options);
				nufoex.printStackTrace ( );
				System.exit (1);
			}
			
		} else {
			
			HelperMethods.printhelp ("java " + RunMarkerAndEraser.class.getCanonicalName ( ), options);
			System.exit (1);
		}
	}
}
