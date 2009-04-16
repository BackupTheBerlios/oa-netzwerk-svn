package de.dini.oanetzwerk.utils;

import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

public class TestEncoding {

	public static String convertBAtoSTring(byte[] ba) {
		BigInteger bi = new BigInteger(ba);
	    String s = bi.toString(16);           
	    if (s.length() % 2 != 0) {
	        // Pad with 0
	        s = "0"+s;
	    }	    
	    return s;
	}
	
	@Test
	public void testFixEnconding() throws Exception {
		String strOriginal = " ä ö ü ß § ";
		
		byte[] baUTF_8 = strOriginal.getBytes("UTF-8");
		byte[] baISO_8859_1 = strOriginal.getBytes("ISO-8859-1");
//		byte[] baUS_ASCII = strOriginal.getBytes("US-ASCII");
		
		String strISO_8859_1 = new String(baISO_8859_1, "UTF-8");
		String strUTF_8 = new String(baUTF_8, "UTF-8");
		
		System.out.println("default charset: " + Charset.defaultCharset());
		
		/*
		System.out.println("UTF8  -> UTF8: " + new String(baUTF_8,"UTF-8") + " byte array length: " + baUTF_8.length);
		System.out.println("ISO   -> UTF8: " + new String(baISO_8859_1,"UTF-8") + " byte array length: " + baISO_8859_1.length);
		System.out.println("ASCII -> UTF8: " + new String(baUS_ASCII,"UTF-8") + " byte array length: " + baUS_ASCII.length);
		
		System.out.println("length ISO   -> ISO   -> UTF-8: " + (new String(baISO_8859_1,"ISO-8859-1")).getBytes("UTF-8").length);
		System.out.println("length ISO   -> UTF-8 -> UTF-8: " + (new String(baISO_8859_1,"UTF-8")).getBytes("UTF-8").length);		
		System.out.println("length ISO   -> UTF-8 -> ISO  : " + (new String(baISO_8859_1,"UTF-8")).getBytes("ISO-8859-1").length);		
		
		System.out.println("length UTF-8 -> ISO   -> UTF-8: " + (new String(baUTF_8,"ISO-8859-1")).getBytes("UTF-8").length);
		System.out.println("length UTF-8 -> UTF-8 -> UTF-8: " + (new String(baUTF_8,"UTF-8")).getBytes("UTF-8").length);		
		System.out.println("length UTF-8 -> UTF-8 -> ISO  : " + (new String(baUTF_8,"UTF-8")).getBytes("ISO-8859-1").length);		
		*/
		
		System.out.println("strUTF_8 - original length: " + strUTF_8.length() + " --- " + strUTF_8);
		String reUTF8 = new String(strUTF_8.getBytes("UTF-8"),"UTF-8");
		System.out.println("         - re UTF-8 length: " + reUTF8.length() + " --- " + reUTF8);
		String reISO = new String(strUTF_8.getBytes("UTF-8"),"ISO-8859-1");
		System.out.println("         - re ISO   length: " + reISO.length() + " --- " + reISO);
	
		System.out.println("strISO_8859_1 - original length: " + strISO_8859_1.length() + " --- " + strISO_8859_1);
		reUTF8 = new String(strISO_8859_1.getBytes("UTF-8"),"UTF-8");
		System.out.println("              - re UTF-8 length: " + reUTF8.length() + " --- " + reUTF8);
		reISO = new String(strISO_8859_1.getBytes("UTF-8"),"ISO-8859-1");
		System.out.println("              - re ISO   length: " + reISO.length() + " --- " + reISO);
		System.out.println("              - ISO            : " + new String(baISO_8859_1,"ISO-8859-1"));
		reISO = new String(strISO_8859_1.getBytes("ISO-8859-1"),"UTF-8");
		System.out.println("              - re ISO   length: " + reISO.length() + " --- " + reISO);
		reISO = new String(strISO_8859_1.getBytes("ISO-8859-1"),"ISO-8859-1");
		System.out.println("              - re ISO   length: " + reISO.length() + " --- " + reISO);
		
		System.out.println("unconverted ISO: " + convertBAtoSTring(baISO_8859_1));
		System.out.println("converted ISO  : " + convertBAtoSTring(new String(baISO_8859_1,"UTF-8").getBytes("UTF-8")));
		
		CharBuffer cb = CharBuffer.wrap(strISO_8859_1);
		Charset csUTF8 = Charset.forName("UTF-8");
        ByteBuffer bb = csUTF8.encode(strISO_8859_1);		
		System.out.println("encoded ISO  : " + convertBAtoSTring(bb.array()));		
        bb = csUTF8.encode(cb);		
		System.out.println("encoded ISO  : " + convertBAtoSTring(bb.array()));
		
		OutputStreamWriter osw = new OutputStreamWriter(System.out, "ISO-8859-1"); 
        osw.write(strISO_8859_1);
        osw.flush();
        
        System.out.println(csUTF8.decode(ByteBuffer.wrap(baISO_8859_1)));
	}
	
}
