/**
 * 
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael K&uuml;hn
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
		
		Option help = new Option ("help", "print this message");
		Option repositoryURL = OptionBuilder.withType (new String ( ))
											.withLongOpt ("repositoryUrl")
											.withArgName ("URL")
											.withDescription ("URL of the repository which need to be harvested")
											.hasArg ( )
											.create ('u');
		Option repositoryID = OptionBuilder	.withType (new Integer (0))
											.withLongOpt ("repositoryID")
											.withArgName ("ID")
											.withDescription ("ID for the repositoryURL see database for details")
											.hasArg ( )
											.create ('i');
		Option harvesttype = OptionBuilder	.withType (new String ( ))
											.withLongOpt ("harvesttype")
											.withArgName ("full|update")
											.withDescription ("harvesting-type MUST be 'full' for a full harvest or 'update' for an updating harvest")
											.hasArg ( )
											.create ('t');
		Option updateDate = OptionBuilder	.withType (new String ( ))
											.withLongOpt ("updateDate")
											.withArgName ("yyyy-mm-dd")
											.withDescription ("Date from which on the Records are harvested. Works only when harvest-type 'update' is set")
											.hasArg ( )
											.create ('d');
		Option harvestAmount = OptionBuilder.withType (new String ( ))
											.withLongOpt ("harvestAmount")
											.withArgName ("amount")
											.withDescription ("amount of Objects which will be requested at once")
											.hasArg ( )
											.create ('a');
		Option harvestInt = OptionBuilder	.withType (new String ( ))
											.withLongOpt ("harvestInterval")
											.withArgName ("milliseconds")
											.withDescription ("time between the requests")
											.hasArg ( )
											.create ('I');
		Option testData = new Option ("testData", "harvested Data will be marked as 'test' and not used for productional use");
		Option listRecords = new Option ("listRecords", "listRecods-OAI-Method is used instead of listIdentifiers + getRecord");
		
		Options options = new Options ( );
		
		options.addOption (help);
		options.addOption (repositoryURL);
		options.addOption (repositoryID);
		options.addOption (harvesttype);
		options.addOption (updateDate);
		options.addOption (harvestAmount);
		options.addOption (harvestInt);
		options.addOption (testData);
		options.addOption (listRecords);
		
		if (args.length > 0) {
			
			try {
				
				CommandLine cmd = new GnuParser ( ).parse (options, args);
				
				// if -h was chosen, print the help and exit
				if (cmd.hasOption ("help")) {
					
					HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
					System.exit (0);
					
				} else if (cmd.hasOption ('i') && cmd.hasOption ('t')) {
					
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
					
					if (cmd.hasOption ("testData"))
						harvester.setTestData (true);
					
					if (cmd.hasOption ("listRecords"))
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
				
			} catch (MissingArgumentException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
				HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
				System.exit (1);
				
			} catch (ParseException parex) {
				
				logger.error (parex.getLocalizedMessage ( ), parex);
				HelperMethods.printhelp ("java " + RunHarvester.class.getCanonicalName ( ), options);
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
