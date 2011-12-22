package OapsAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.axis.AxisFault;
import org.apache.commons.codec.binary.Base64;

import OapsAPI_pkg.Login;
import OapsAPI_pkg.OapsAPI;
import OapsAPI_pkg.OapsAPIBindingStub;
import OapsAPI_pkg.OapsAPILocator;
import OapsAPI_pkg.ReportResult;

public class Test {

	public static void main(String[] args) {
		try {
			OapsAPI srv = new OapsAPILocator();
			URL url = new URL(srv.getOapsAPIPortAddress());
			OapsAPIBindingStub mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);

			String pw = "g7w5a%a5";
			
            String hash = null; 
	        try {
	            hash = hash(pw);
            } catch (Exception e) {
	            e.printStackTrace();
            }
			
            // example request
			//String jobId = mb.startJob(new Login("sammy.david@cms.hu-berlin.de", hash), getBase64(new File("gradmann.pdf")), "de");
            ReportResult rr = mb.getNextReport(new Login("sammy.david@cms.hu-berlin.de", hash));
            String jobId = rr.getJobid();
            String status = rr.getStatus();
            int rating = rr.getRating();
  
			
			
			System.out.println("Job-ID: " + jobId);
			System.out.println("Status: "+status);
			String output = Base64.encodeBase64URLSafeString("258,imrael@arcor.de".getBytes());
			String reverse = new String(Base64.decodeBase64(output));
			System.out.println(reverse);
			
			
//			if (status.equals("OK")) {
//				File dir = new File("/home/imrael/"+jobId+".html");
//				//File outfile = File.createTempFile(jobId, ".html", dir);
//				FileOutputStream fos = new FileOutputStream(dir);
//				fos.write(rr.getFile());
//				fos.close();
//			}
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

    public static String hash(String str) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(str.getBytes());
        byte[] result = md5.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<result.length; i++) {
            hexString.append(Integer.toHexString(0xFF & result[i]));
        }
        System.out.println("MD5: " + hexString.toString());
        return hexString.toString();
    }

	public static byte[] getBase64(File file) {
	
		try {
			byte[] bytes = getBytesFromFile(file);
			ByteArrayOutputStream baos = new
			ByteArrayOutputStream();
			ObjectOutputStream stream = new
			ObjectOutputStream(baos);
			stream.write(bytes);
			stream.flush();
			stream.close();
			return Base64.encodeBase64(baos.toByteArray());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null; 
	}
	
	public static byte[] getBytesFromFile(File file) throws
	IOException {
	
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File exceeds max value: "
			+ Integer.MAX_VALUE);
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
	
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
		&& (numRead = is.read(
		bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file" + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
		
	}
}
