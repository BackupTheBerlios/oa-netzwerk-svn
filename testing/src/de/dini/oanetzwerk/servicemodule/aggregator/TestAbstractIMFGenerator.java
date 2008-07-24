package de.dini.oanetzwerk.servicemodule.aggregator;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;

public class TestAbstractIMFGenerator {

	static AbstractIMFGenerator myIMFGen = null;
	
	@BeforeClass
	public static void init() {
		myIMFGen = new IMFGeneratorDCSimple();
	}
	
	@Test
	public void testExtractAuthor() {
		String strInput = null;
		Author extractedAuthor = null;
		
		strInput = "Max Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Mustermann, Max";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "\t \nMax Mustermann\t \n";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Max \t   \n Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Mustermann \t   \n ,  \t   \n Max";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Max Marius Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max Marius", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Mustermann, Max Marius";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max Marius", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Mustermann-Schnarrenberger, Max Marius";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max Marius", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann-Schnarrenberger", extractedAuthor.getLastname());
		
		strInput = "Max Marius Mustermann-Schnarrenberger";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max Marius", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann-Schnarrenberger", extractedAuthor.getLastname());
		
		strInput = "M. M. Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("M. M.", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());

		strInput = "Max M. Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max M.", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());

		strInput = "Mustermann, Max M.";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Max M.", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
		
		strInput = "Hans-Peter Mustermann";
		extractedAuthor = myIMFGen.extractAuthor(strInput);
		Assert.assertEquals("Hans-Peter", extractedAuthor.getFirstname());
		Assert.assertEquals("Mustermann", extractedAuthor.getLastname());
	}
	
	@Test
	public void testExtractPublisher() {
		String strInput = null;
		Publisher extractedPublisher = null;
		
		strInput = " \t   \n Muster-Herausgeber \t   \n ";
		extractedPublisher = myIMFGen.extractPublisher(strInput);
		Assert.assertEquals("Muster-Herausgeber", extractedPublisher.getName());

	}
	
	@Test
	public void testExtractTitleInformaton() {
		String strInput = null;
		Title extractedTitle = null;
		
		strInput = " \t   \n Dies ist der Haupt-Titel einer Arbeit. \t   \n ";
		extractedTitle = myIMFGen.extractTitleInformation(strInput);
		Assert.assertEquals("Dies ist der Haupt-Titel einer Arbeit.", extractedTitle.getTitle());
		Assert.assertEquals("main", extractedTitle.getQualifier());

		strInput = " \t   \n Dies ist der Untertitel einer Arbeit. \t   \n ";
		extractedTitle = myIMFGen.extractTitleInformation(strInput);
		Assert.assertEquals("Dies ist der Untertitel einer Arbeit.", extractedTitle.getTitle());
		Assert.assertEquals("subtitle", extractedTitle.getQualifier());

	}
	
	@Test
	public void testExtractClassification() {
		String strInput = null;
		Classification extractedClassification = null;
		
		strInput = " \t   \n ddc:fooWert \t   \n ";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertEquals("fooWert", extractedClassification.getValue());

		strInput = "ddc:fooWert";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertTrue(extractedClassification instanceof DDCClassification);

		strInput = "pub-type:fooWert";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertTrue(extractedClassification instanceof DINISetClassification);

		strInput = "dnb:fooWert";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertTrue(extractedClassification instanceof DNBClassification);
		
		strInput = " \t   \n PuB-tYPe:fooWert \t   \n ";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertEquals("fooWert", extractedClassification.getValue());
		Assert.assertTrue(extractedClassification instanceof DINISetClassification);

		strInput = "fooKuerzel:fooWert";
		extractedClassification = myIMFGen.extractClassification(strInput);
		Assert.assertEquals("fooKuerzel:fooWert", extractedClassification.getValue());
		Assert.assertTrue(extractedClassification instanceof OtherClassification);
		
		
	}
	
}
