package de.dini.oanetzwerk.admin;

/**
 * @author Sammy David
 * sammy.david@cms.hu-berlin.de
 * 
 */
public class Repository {

	
//	private boolean deactivated = false;
//	private boolean deleted = false;
//	private boolean stored = false;

	private Long id = null;
	
//	@Size(min = 8, message = "Please enter the Email")
	private String name;
	private String owner;
	private String ownerEmail;
	
//	@NotNull
	private String url;
	private String oaiUrl;
	private String harvestAmount = "10";
	private String harvestPause = "5000";
	private String lastFullHarvestBegin;
	private String testData;
	private boolean listRecords;
	private boolean active = true;
	
	
//	public boolean success() {
//		return deactivated || deleted || stored;
//	}
	
	/***************************** Getter & Setter ****************************/
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOwner(String owner){
		this.owner = owner;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public void setOwnerEmail(String ownerEmail){
		this.ownerEmail = ownerEmail;
	}
	
	public String getOwnerEmail(){
		return ownerEmail;
	}
	
	public String getOaiUrl() {
		return oaiUrl;
	}

	public void setOaiUrl(String oaiUrl) {
		this.oaiUrl = oaiUrl;
	}

	public String getHarvestAmount() {
		return harvestAmount;
	}

	public void setHarvestAmount(String harvestAmount) {
		this.harvestAmount = harvestAmount;
	}

	public String getLastFullHarvestBegin() {
		return lastFullHarvestBegin;
	}

	public void setLastFullHarvestBegin(String lastFullHarvestBegin) {
		this.lastFullHarvestBegin = lastFullHarvestBegin;
	}

	public String getHarvestPause() {
		return harvestPause;
	}

	public void setHarvestPause(String harvestPause) {
		this.harvestPause = harvestPause;
	}

	public boolean isListRecords() {
		return listRecords;
	}

	public void setListRecords(boolean listRecords) {
		this.listRecords = listRecords;
	}

	public String getTestData() {
		return testData;
	}

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
