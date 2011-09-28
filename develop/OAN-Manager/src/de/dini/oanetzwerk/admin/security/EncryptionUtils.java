package de.dini.oanetzwerk.admin.security;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;


public class EncryptionUtils {

	private static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	
	static {
		
		textEncryptor.setPassword("oanetzwerk");
		
	}
	
	public static String encrypt(String text) {
		return textEncryptor.encrypt(text);
	}
	
	public static String decrypt(String encryptedText) {
		return textEncryptor.decrypt(encryptedText);
	}
	
	public static String encryptAndEncode(String text) {
		
		return Base64.encodeBase64URLSafeString(encrypt(text).getBytes());
	}

	public static String decryptAndDecode(String encryptedAndEncodedText) {
		
		return decrypt(new String(Base64.decodeBase64(encryptedAndEncodedText.getBytes())));
	}
}
