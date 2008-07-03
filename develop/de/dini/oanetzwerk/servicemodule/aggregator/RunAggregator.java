package de.dini.oanetzwerk.servicemodule.aggregator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.HelperMethods;




public class RunAggregator {

	
	
	private static Logger logger = Logger.getLogger (RunAggregator.class);

	/**
	 * ensures the right value for the repository ID.
	 * 
	 * @param optionValue
	 *            a string representing a number greater or equal zero.
	 * @return an integer which represents the repository ID
	 * @throws ParseException
	 *             when the repository ID is negative
	 */
	private static int filterId(String optionValue) throws ParseException {

		int i = new Integer(optionValue);

		if (i < 0)
			throw new ParseException("ItemID must not be negative!");

		if (logger.isDebugEnabled())
			logger.debug("filtered ItemID: " + i);

		return i;
	}

	
	
	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		Options options = new Options();

		options.addOption("h", false, "show help");

		options
				.addOption(OptionBuilder
						.withLongOpt("itemId")
						.withArgName("ID")
						.withDescription(
								"Id of the database object, that shall be extracted and converted")
						.withValueSeparator().hasArg().create('i'));
		options.addOption(OptionBuilder.withLongOpt("auto").withDescription(
				"Automatic mode, no given ID is necessary")

		.create('a'));

		if (args.length > 0) {

			try {

				CommandLine cmd = new GnuParser().parse(options, args);

				if (cmd.hasOption("h"))
					HelperMethods.printhelp("java "
							+ Aggregator.class.getCanonicalName(), options);

				else if (cmd.hasOption("itemId") || cmd.hasOption('i')
						|| cmd.hasOption('a') || cmd.hasOption("auto")) {

					int id = 0;

					// Bestimmen, ob nur eine einzelne ID übergeben wurde oder
					// der Auto-Modus genutzt werden soll
					if (cmd.getOptionValue('i') != null)
						id = filterId(cmd.getOptionValue('i'));
					if (cmd.getOptionValue("itemId") != null)
						id = filterId(cmd.getOptionValue("itemId"));

					// Here we go: create a new instance of the aggregator

					Aggregator aggregator = new Aggregator();

					// hier wird entweder die spezifische Objekt-ID übergeben
					// oder ein Auto-Durchlauf gestartet
					if (id > 0) {
						aggregator.startSingleRecord(id);
					} else {
						aggregator.startAutoMode();
					}

				} else {

					HelperMethods.printhelp("java "
							+ Aggregator.class.getCanonicalName(), options);
					System.exit(1);
				}

			} catch (ParseException parex) {

				logger.error(parex.getMessage());
				System.out.println(parex.getMessage());
				HelperMethods.printhelp("java "
						+ Aggregator.class.getCanonicalName(), options);
				System.exit(1);
			}

		} else {

			HelperMethods.printhelp("java "
					+ Aggregator.class.getCanonicalName(), options);
			System.exit(1);
		}
	}

}
