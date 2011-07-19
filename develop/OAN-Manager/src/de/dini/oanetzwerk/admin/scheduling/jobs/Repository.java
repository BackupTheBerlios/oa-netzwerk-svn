package de.dini.oanetzwerk.admin.scheduling.jobs;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
class Repository {

	
	private int id;
	private String baseUrl;
	private int interval;
	private int amount;
	private boolean listRecords;
	private boolean testData;
	
	
	

	public int getId() {
    	return id;
    }
	public void setId(int id) {
    	this.id = id;
    }
	public String getBaseUrl() {
    	return baseUrl;
    }
	public void setBaseUrl(String baseUrl) {
    	this.baseUrl = baseUrl;
    }
	public int getInterval() {
    	return interval;
    }
	public void setInterval(int interval) {
    	this.interval = interval;
    }
	public int getAmount() {
    	return amount;
    }
	public void setAmount(int amount) {
    	this.amount = amount;
    }
	public boolean isListRecords() {
    	return listRecords;
    }
	public void setListRecords(boolean listRecords) {
    	this.listRecords = listRecords;
    }
	public boolean isTestData() {
    	return testData;
    }
	public void setTestData(boolean testData) {
    	this.testData = testData;
    }
	

}
