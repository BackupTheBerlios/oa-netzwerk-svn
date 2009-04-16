package de.dini.oanetzwerk.utils.imf;

public class FullTextLink {
	
	int number = 0;
	String url;
	String language;
	String mimeformat;
	
	public FullTextLink() {
		
	}
	
	public FullTextLink(String url, String mimeformat, String language, int number) {
		this.url = url;
		this.mimeformat = mimeformat;
		this.language = language;
		this.number = number;
	}
	
	public String toString() {
		StringBuffer sbResult = new StringBuffer();
		sbResult.append("url=" + this.url)
		        .append("\n" + "mimeformat=" + this.mimeformat)
		        .append("\n" + "language=" + this.language)
		        .append("\n" + "number=" + this.number);
		return sbResult.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeformat() {
		return mimeformat;
	}

	public void setMimeformat(String mimeformat) {
		this.mimeformat = mimeformat;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
