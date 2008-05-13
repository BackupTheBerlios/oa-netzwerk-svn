package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.StringUtils;

//@XmlType( name="", propOrder={"number","firstname","lastname","email","institution","title"})
public class Contributor {
	
	String firstname;
	String lastname;
	String email;
	String institution;
	String title;
	int number;

	public Contributor() {
		
	}
	
	public int init(String original, int number) {
		int result = 0;

		String[] temp = null;
		// Splitten zwischen Nach- und Vornamen (separiert durch ein Komma
		temp = original.split(",");
		if (temp.length >= 2) {
			this.lastname = temp[0];
			this.firstname = temp[1];
		} else {
			// wenn es nicht genug Elemente gibt, konnte nicht gesplittet werden, neuer Versuch am Leerzeichen
			temp = original.split(" ");
			if (temp.length >= 2) {
				this.lastname = temp[1];
				this.firstname = temp[0];
			}
		}

		if (temp.length < 2) {
			temp[0] = original;
		}

		firstname = StringUtils.trim(firstname);
		lastname = StringUtils.trim(lastname);
		
		this.number = number;

		return result;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@XmlAttribute()
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String toString() {
		String result = "lastname=" + this.lastname;
		result = result + "\n" + "firstname=" + this.firstname;
		result = result + "\n" + "e-mail=" + this.email;
		result = result + "\n" + "institution=" + this.institution;
		result = result + "\n" + "title=" + this.title;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}
