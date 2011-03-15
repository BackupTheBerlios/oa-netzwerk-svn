package de.dini.oanetzwerk.admin;

import java.io.Serializable;

import de.dini.oanetzwerk.admin.utils.AbstractBean;

public class ValidationResultBean extends AbstractBean implements Serializable {

	private String timestamp;
	
	private int 	testOffset;
	private String serverUrl = "";
	private String serverAlias = "";
	private String serverPassword = "";
	private String serverUsername = "";
	private String[] parameters;
	private String[] validationResults;
	private String[] errorMessages;
	
	public int getTestOffset() {
		return testOffset;
	}
	public void setTestOffset(int testOffset) {
		this.testOffset = testOffset;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getServerAlias() {
		return serverAlias;
	}
	public void setServerAlias(String serverAlias) {
		this.serverAlias = serverAlias;
	}
	public String getServerPassword() {
		return serverPassword;
	}
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	public String getServerUsername() {
		return serverUsername;
	}
	public void setServerUsername(String serverUsername) {
		this.serverUsername = serverUsername;
	}
	public String[] getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	public String[] getValidationResults() {
		return validationResults;
	}
	public void setValidationResults(String[] validationResults) {
		this.validationResults = validationResults;
	}
	public String[] getErrorMessages() {
		return errorMessages;
	}
	public void setErrorMessages(String[] errorMessages) {
		this.errorMessages = errorMessages;
	}
	
}
