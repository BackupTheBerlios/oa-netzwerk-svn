package de.dini.oanetzwerk.bibexport;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;

public class TestJavabib {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BibtexFile bibtexFile = new BibtexFile();
		
		BibtexEntry bibtexEntry = bibtexFile.makeEntry("article", "ref001");
		bibtexEntry.setField("author", bibtexFile.makeString("Hans Tester"));
		bibtexEntry.setField("title", bibtexFile.makeString("Testtitel"));
		bibtexFile.addEntry(bibtexEntry);
		
		bibtexEntry = bibtexFile.makeEntry("phdthesis", "ref002");
		bibtexEntry.setField("author", bibtexFile.makeString("Hans Toaster"));
		bibtexEntry.setField("title", bibtexFile.makeString("Testtitel2"));
		bibtexFile.addEntry(bibtexEntry);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bos);
		bibtexFile.printBibtex(writer);
		writer.flush();
		
		try {
		  System.out.println(bos.toString("UTF-8"));
		} catch(Exception ex) {
			System.err.println(ex);
		}
		  
		System.out.println("TEST ENDE.");
	}

}
