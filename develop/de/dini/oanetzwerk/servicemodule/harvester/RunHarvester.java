/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;


/**
 * @author Michael Kühn
 *
 */

public class RunHarvester {
	
	private static Logger logger = Logger.getLogger (RunHarvester.class);
	
	/**
	 * Main class which have to be called.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	
	@SuppressWarnings("static-access")
	public static void main (String [ ] args) {
		
		Harvester harvester;
		Options options = new Options ( );
		
		options.addOption ("h", false, "show help");
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("repositoryUrl")
										.withArgName ("URL")
										.withDescription ("URL of the repository which need to be harvested")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('u'));
		
		options.addOption (OptionBuilder.isRequired ( )
										.withLongOpt ("repositoryId")
										.withArgName ("ID")
										.withDescription ("Id for the repositoryURL see database for details")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('i'));
		
		options.addOption (OptionBuilder.isRequired ( )
										.withType (new String ( ))
										.withLongOpt ("harvesttype")
										.withArgName ("full|update")
										.withDescription ("harvesting-type can be 'full' for a full harvest or 'update' for an updating harvest")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('t'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("updateDate")
										.withArgName ("yyyy-mm-dd")
										.withDescription ("Date from which on the Records are harvested, when update-harvest-type is specified")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('d'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("harvestAmount")
										.withArgName ("amount")
										.withDescription ("amount of Objects which will be requested at once")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('a'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("harvestInterval")
										.withArgName ("milliseconds")
										.withDescription ("time between the requests")
										.withValueSeparator ( )
										.hasArg ( )
										.create ('I'));
		
		options.addOption (OptionBuilder.withType (new String ( ))
										.withLongOpt ("testData")
										.withDescription ("URL of the repository which need to be harvested")
										.create ('T'));
		
		if (args.length > 0) {
			
			try {
				
				CommandLine cmd = new GnuParser ( ).parse (options, args);
				
				// if -h was chosen, print the help and exit
				if (cmd.hasOption ("h")) {
					
					HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
					System.exit (0);
					
				} else if ((cmd.hasOption ("repositoryId") || cmd.hasOption ('i')) &&
						(cmd.hasOption ("harvesttype") || cmd.hasOption ('t'))) {
					
					int id = Harvester.filterId (cmd.getOptionValue ('i'));
					
					// Here we go: create a new instance of the harvester
					harvester = Harvester.getHarvester ( );
					harvester.prepareHarvester (id);
					
					harvester.filterUrl (cmd.getOptionValue ('u'));
					harvester.filterBool (cmd.getOptionValue ('t'), cmd);
					
					if (!harvester.isFullharvest ( ))
						harvester.filterDate (cmd.getOptionValue ('d'));
					
					harvester.filterAmount (cmd.getOptionValue ('a'));
					harvester.filterInterval (cmd.getOptionValue ('I'));
					
					if (cmd.hasOption ('T'))
						harvester.setTestData (cmd.hasOption ('T'));
					
					if (cmd.hasOption ('r'))
						harvester.setListRecords (true);
					
					/* 
					 * firstly we have to collect some data from the repository, which have to be processed
					 * when we finished processing all these data, we have the IDs form all records we have
					 * to collect. This is the next step: we catch the records for all the IDs and put the
					 * raw datas into the database, if they don't exist or newer than in the database.
					 */
					
					if (logger.isDebugEnabled ( )) {
						
						logger.debug ("Data after processing the CommandLine:");
						logger.debug ("oai_url: " + harvester.getRepositoryURL ( ));
						logger.debug ("test_data: " + harvester.isTestData ( ));
						logger.debug ("harvest_amount: " + harvester.getAmount ( ));
						logger.debug ("harvest_pause: " + harvester.getInterval ( ));
					}
					
					harvester.processRepository ( );
					
				} else {
				
					HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
					harvester = null;
					System.exit (1);
				}
				
			} catch (ParseException parex) {
				
				logger.error (parex.getLocalizedMessage ( ));
				HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
				parex.printStackTrace ( );
				System.exit (1);
				
			} finally {
				
				harvester = null;
			}
				
		} else {
			
			HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
			harvester = null;
			System.exit (1);
		}
	}
}
