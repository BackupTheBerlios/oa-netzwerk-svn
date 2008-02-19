package de.dini.oanetzwerk.utils.imf;

public class Description {
	String description;
	String language;
	int number = 0;
	
	public Description() {
		
	}
	
	private void init(String description, String language, int number) {
		this.description = description;
		this.language = language;
		this.number = number;
	}
	
	public Description(String description, String language, int number) {
		this.init(description, language, number);
	}
	
	public Description(String description, int number) {
		this.init(description, null, number);
	}
	
	public String toString() {
		String result = "description=" +this.description;
		result = result + "\n" + "language=" + this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
