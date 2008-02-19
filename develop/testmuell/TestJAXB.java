package technotest;

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
		
	public static final String xmlDataWithEntities = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
		"<ns2:club xmlns:ns2=\"http://tutego.com/\">\n" +
		"    <dj>\n" +
		"        <name>John Peel</name>\n" +
		"    </dj>\n" +
		"    <numberOfPersons>1234</numberOfPersons>\n" +
		"    <djstaff>\n" +
		"        <name>\"Test&amp;quot; &quot; &gt; \\rth{}</name>\n" +
		"    </djstaff>\n" +
		"    <djstaff>\n" +
		"        <name>K&amp;uuml;.. &#176;&#5677;&#1766;&#1476;</name>\n" +
		"    </djstaff>\n" +
		"    <djstaff>\n" +
		"        <name>ABC &lt;&#60; &amp;cent;&apos;&amp;nbsp;&amp;copy;&amp;divide;</name>\n" +
		"    </djstaff>\n" +
		"</ns2:club>\n";
	
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


	@Test
	public void testUnmarshallEntities() {
		System.out.println("\n" + xmlDataWithEntities+"\n");
		try {
			Unmarshaller um = context.createUnmarshaller(); 
			Club club2 = (Club) um.unmarshal( new StringReader(xmlDataWithEntities) );
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
