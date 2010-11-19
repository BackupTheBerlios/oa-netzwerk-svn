package technotest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRestXmlCodec {

	final String KEY_FOO1 = "foo1";
	final String KEY_FOO2 = "foo2";
	final String KEY_NULL = "non-existent";
	final String KEY_NOTNULL = "existent";
	
	@BeforeClass
	public static void doThisOnce() {
//		System.out.println("\n\nDas mache ich VOR der GESAMTHEIT aller Testfälle.\n\n");
	}
	
	@AfterClass
	public static void doThatOnce() {
//		System.out.println("\n\nDas mache ich NACH der GESAMTHEIT aller Testfälle.\n\n");
	}
	
	@Before
	public void doThisEveryTime() {
//		System.out.println("\n\nDas mache ich VOR JEDEM Testfall.\n\n");
	}
	
	@After
	public void doThatEveryTime() {
//		System.out.println("\n\nDas mache ich NACH JEDEM Testfall.\n\n");
	}
	
	
	@SuppressWarnings("unchecked")
	public void printDiff(List<HashMap<String,String>> input, List<HashMap<String,String>> output) {
		for(int i = 0; i < input.size() && i < output.size(); i++) {
			System.out.println((i+1) + ". Eintrag");
			HashMap<String,String> iEntry = input.get(i);
			HashMap<String,String> oEntry = output.get(i);
			Iterator iIt = iEntry.keySet().iterator();
			Iterator oIt = iEntry.keySet().iterator();
			while(iIt.hasNext() && oIt.hasNext()) {
				String iKey = (String)iIt.next();
				String oKey = (String)oIt.next();
				String iValue = iEntry.get(iKey);
				String oValue = oEntry.get(oKey);
				System.out.println("   " + iKey + "=" + 
						           (iValue==null ? "<NULL>" : ("\"" + iValue + "\"")) + 
						           " <---> " + oKey + "=" +
						           (oValue==null ? "<NULL>" : ("\"" + oValue + "\"")) );
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void tunnelKVpair(String key, String value) {
		List<HashMap<String,String>> input = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> entry = new HashMap<String,String>();
		entry.put(key,value);
		input.add(entry);
		
		List<HashMap<String,String>> output = RestXmlCodec.decodeEntrySet((RestXmlCodec.encodeEntrySetRequestBody(input)));

		printDiff(input, output);
	}
	
	public List<HashMap<String,String>> getFooInput() {

		Random zuffi = new Random();
		
		List<HashMap<String,String>> input = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put(KEY_FOO1, "abc" + zuffi.nextInt(10000));
		mapTestEntry.put(KEY_FOO2, "" + zuffi.nextInt(10000));
		input.add(mapTestEntry);
		mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put(KEY_NULL, null);
		mapTestEntry.put(KEY_NOTNULL, "null");
		input.add(mapTestEntry);
		
		return input;
	}
	
	@Test
	public void injectXMLintoValue() {
		tunnelKVpair("test1", "<");
		tunnelKVpair("test2", ">");
		tunnelKVpair("test3", "<br>");
		tunnelKVpair("test4", "<foo></foo>");
		tunnelKVpair("test5", "&nonsense;");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testCodecFunction() {
		List<HashMap<String,String>> input = getFooInput();	
		List<HashMap<String,String>> output = RestXmlCodec.decodeEntrySet((RestXmlCodec.encodeEntrySetRequestBody(input)));

		Assert.assertEquals(output.get(0).get(KEY_FOO1),input.get(0).get(KEY_FOO1));
		Assert.assertEquals(output.get(0).get(KEY_FOO2),input.get(0).get(KEY_FOO2));
		Assert.assertNull(output.get(1).get(KEY_NULL));
		Assert.assertNotNull(output.get(1).get(KEY_NOTNULL));
		
		printDiff(input, output);		
	}
	
	@SuppressWarnings({ "deprecation", "deprecation", "deprecation", "unchecked" })
	@Test
	public void printCompleteTestrun() {
		
		List<HashMap<String,String>> listTestEntrySet = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put("foo", "bar");
		mapTestEntry.put("baz", "1234");
		listTestEntrySet.add(mapTestEntry);
		mapTestEntry = new HashMap<String,String>();
		mapTestEntry.put("non-existent", null);
		mapTestEntry.put("existent", "null");
		listTestEntrySet.add(mapTestEntry);
		
		String strXMLRequest = RestXmlCodec.encodeEntrySetRequestBody(listTestEntrySet);
		System.out.println("MessageType == " + RestXmlCodec.fetchMessageType(strXMLRequest));
		System.out.println(strXMLRequest);
		
		String strXMLResponse = RestXmlCodec.encodeEntrySetResponseBody(listTestEntrySet,"TestKeyword");
		System.out.println("MessageType == " + RestXmlCodec.fetchMessageType(strXMLResponse));
		System.out.println(strXMLResponse);
	
		List<HashMap<String,String>> listTestEntrySet2 = RestXmlCodec.decodeEntrySet(strXMLRequest);
		System.out.println("");
		for(int i = 0; i < listTestEntrySet2.size(); i++) {
			System.out.println("EINTRAG " + i);
			HashMap<String,String> entry = listTestEntrySet2.get(i);
			Iterator it = entry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				System.out.println("key == \"" + key + "\"");
				String value = entry.get(key);
				System.out.println("value == " + (value==null ? "<NULL>" : ("\"" + value + "\"")));
			}
		}
		
		System.out.println("");
		
		List<HashMap<String,String>> listTestErrors = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> mapTestError = new HashMap<String,String>();
		mapTestError.put("ServerError", "SQLException... ");
		mapTestError.put("ServerError", "IOException..");
		listTestErrors.add(mapTestError);
		mapTestError = new HashMap<String,String>();
		mapTestError.put("ServerError", "OutOfIdeasException... ");
		mapTestError.put("NOObject", "Object referenced by EXT_OID=1234 and REP_ID=0815 does not exist in database.");
		listTestErrors.add(mapTestError);
		
		String strXMLErrors = RestXmlCodec.encodeErrors(listTestErrors);	
		System.out.println("MessageType == " + RestXmlCodec.fetchMessageType(strXMLErrors));
		System.out.println(strXMLErrors);
		
		List<HashMap<String,String>> listTestErrors2 = RestXmlCodec.decodeErrors(strXMLErrors);
		System.out.println("");
		System.out.println("ERRORS");
		for(int i = 0; i < listTestErrors2.size(); i++) {
			HashMap<String,String> entry = listTestErrors2.get(i);
			Iterator it = entry.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				System.out.println("key == \"" + key + "\"");
				String value = entry.get(key);
				System.out.println("description == " + (value==null ? "<NULL>" : ("\"" + value + "\"")));
			}
		}
		
	}

	
}
