package de.dini.oanetzwerk.utils.imf;

public class Title {
	String title;
	int number;
	String qualifier;
	String lang;
	
	public Title() {
		title = new String();
		lang = new String();
		qualifier = new String();
		number = 0;
	}

	public String toString() {
		String result = "title=" +this.title;
		result = result + "\n" + "qualifier=" + this.qualifier;
		result = result + "\n" + "number=" + this.number;
		result = result + "\n" + "language=" + this.lang;
		return result;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null) this.title = "";
		else
			this.title = title;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		if (qualifier == null) this.qualifier = "";
		else
			this.qualifier = qualifier;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		if (lang == null) this.lang = "";
		else
			this.lang = lang;
	}
	
}

