package de.dini.oanetzwerk.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestJAXB {

	public static JAXBContext context;
		
	@BeforeClass
	public static void init() {
		try {
			context = JAXBContext.newInstance( Club.class ); 
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
	}

	@Test 
	public void marshall() {		
		DJ dj;
		
		List<DJ> myStaff = new ArrayList<DJ>();
		dj = new DJ(); 
		dj.setDjName( "Manuel" );
		myStaff.add(dj);
		dj = new DJ(); 
		dj.setDjName( "Michael" );
		myStaff.add(dj);
		dj = new DJ(); 
		dj.setDjName( "Robin" );
		myStaff.add(dj);
		
		dj = new DJ(); 
		dj.setDjName( "John Peel" ); 
		
		Club club = new Club(); 
		club.setDj( dj ); 
		club.setNumberOfPersons( 1234 );
		club.setStaff(myStaff);
		
		StringWriter sw = new StringWriter();
		
		try {
			Marshaller m = context.createMarshaller(); 
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.marshal( club, System.out );
			m.marshal( club, sw);
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
		
		try {
			Unmarshaller um = context.createUnmarshaller(); 
			Club club2 = (Club) um.unmarshal( new StringReader(sw.toString()) ); 
			System.out.println( club2.getDj().getDjName() ); 
			System.out.println( club2.getNumberOfPersons() );
			for(DJ staffmember : club2.getStaff()) {
				System.out.println( staffmember.getDjName() );
			}
		} catch(JAXBException jex) {
			System.err.println(jex);
		}
	}


}
