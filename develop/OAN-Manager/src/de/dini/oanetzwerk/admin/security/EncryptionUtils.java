package de.dini.oanetzwerk.admin.security;

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

}
