package de.dini.oanetzwerk.utils;

import java.math.BigDecimal;

public class RepositoryConfig {
	
	BigDecimal referToOID;
	int repositoryID; 
	String repositoryName;
	String repositoryURL;
	String repositoryOAI_BASEURL;
	boolean repositoryTEST_DATA;
	String repositoryHARVEST_PAUSE;
	String repositoryHARVEST_AMOUNT;
	
	public RepositoryConfig() {
		
	}
	
    public RepositoryConfig(BigDecimal referToOID) {
    	this.referToOID = referToOID;
	}

	public String toString() {
		StringBuffer sbResult = new StringBuffer();
		sbResult.append("referToOID=" + this.referToOID)
		        .append("\n  repositoryID=" + this.repositoryID)
		        .append("\n  repositoryName=" + this.repositoryName)
		        .append("\n  repositoryURL=" + this.repositoryURL)
		        .append("\n  repositoryOAI_BASEURL=" + this.repositoryOAI_BASEURL)
		        .append("\n  repositoryTEST_DATA=" + this.repositoryTEST_DATA)
		        .append("\n  repositoryHARVEST_AMOUNT=" + this.repositoryHARVEST_AMOUNT)
		        .append("\n  repositoryHARVEST_PAUSE=" + this.repositoryHARVEST_PAUSE);
		return sbResult.toString();
	}
    
	public BigDecimal getReferToOID() {
		return referToOID;
	}

	public void setReferToOID(BigDecimal referToOID) {
		this.referToOID = referToOID;
	}

	public int getRepositoryID() {
		return repositoryID;
	}

	public void setRepositoryID(int repositoryID) {
		this.repositoryID = repositoryID;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryURL() {
		return repositoryURL;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public String getRepositoryOAI_BASEURL() {
		return repositoryOAI_BASEURL;
	}

	public void setRepositoryOAI_BASEURL(String repositoryOAI_BASEURL) {
		this.repositoryOAI_BASEURL = repositoryOAI_BASEURL;
	}

	public boolean isRepositoryTEST_DATA() {
		return repositoryTEST_DATA;
	}

	public void setRepositoryTEST_DATA(boolean repositoryTEST_DATA) {
		this.repositoryTEST_DATA = repositoryTEST_DATA;
	}

	public String getRepositoryHARVEST_PAUSE() {
		return repositoryHARVEST_PAUSE;
	}

	public void setRepositoryHARVEST_PAUSE(String repositoryHARVEST_PAUSE) {
		this.repositoryHARVEST_PAUSE = repositoryHARVEST_PAUSE;
	}

	public String getRepositoryHARVEST_AMOUNT() {
		return repositoryHARVEST_AMOUNT;
	}

	public void setRepositoryHARVEST_AMOUNT(String repositoryHARVEST_AMOUNT) {
		this.repositoryHARVEST_AMOUNT = repositoryHARVEST_AMOUNT;
	}
    
}
