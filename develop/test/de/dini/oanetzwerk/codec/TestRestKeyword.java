package de.dini.oanetzwerk.codec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class TestRestKeyword {

	@Test
	public void printRegisteredAllKeywords() {
		System.out.println("The following keywords may be currently used:\n");
		ArrayList<RestKeyword> listKeywords = new ArrayList<RestKeyword>();
		for(RestKeyword k : RestKeyword.values()) listKeywords.add(k);
		Collections.sort(listKeywords);
		for(RestKeyword name : listKeywords) {
			if(name != RestKeyword.UNKNOWN) {
				System.out.println("  - " + name.toString());
			}
		}
		RestKeyword[] rkws = RestKeyword.values(); 
		Arrays.sort(rkws);
		for(RestKeyword name : rkws) {
			if(name != RestKeyword.UNKNOWN) {
				System.out.println("  - " + name.toString());
			}
		}
	}
	
}
