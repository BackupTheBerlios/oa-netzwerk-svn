package de.dini.oanetzwerk.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "fileStorage")
@ApplicationScoped
public class FileStorage {

	private String storageDirectory = "";
	
	private String validatorErrorsFilename = "validator_errors.oan";
	
	private Map<Integer, String> validatorErrors;
	
	public Map<Integer, String> loadValidatorInfo() {
		
		try {
	        ObjectInputStream is = new ObjectInputStream(new FileInputStream(storageDirectory + validatorErrorsFilename));
	        validatorErrors = (HashMap<Integer, String>) is.readObject();
	        
	        is.close();
	        return validatorErrors;
		} catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        return null;
	}
	
	public void storeValidatorInfo(Map<Integer, String> output) {
		
		try {
	        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(storageDirectory + validatorErrorsFilename, false));
	        os.writeObject(output);
	        
	        os.flush();
	        os.close();
		} catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	
	
	public Map<Integer, String> getValidatorErrors() {
    	if (validatorErrors == null) {
    		validatorErrors = loadValidatorInfo();
    	}
		return validatorErrors;
    }

	public String getStorageDirectory() {
    	return storageDirectory;
    }

	public void setStorageDirectory(String storageDirectory) {
    	this.storageDirectory = storageDirectory;
    }
	
}
