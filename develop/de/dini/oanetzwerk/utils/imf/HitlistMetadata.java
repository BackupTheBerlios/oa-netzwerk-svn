package de.dini.oanetzwerk.utils.imf;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( namespace = "http://oanetzwerk.dini.de/" ) 
public class HitlistMetadata {

    BigDecimal oid;
	List<Title> titleList;
	List<Author> authorList;
	List<Identifier> identifierList;
	List<FullTextLink> fullTextLinkList;
	List<DuplicateProbability> duplicateProbabilityList;
	
	public HitlistMetadata() {
        oid = new BigDecimal(-1);
		titleList = new LinkedList<Title>();
		authorList = new LinkedList<Author>();
		identifierList = new LinkedList<Identifier>();
		fullTextLinkList = new LinkedList<FullTextLink>();
		duplicateProbabilityList = new LinkedList<DuplicateProbability>();
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\nOID: " + oid);
				
		sb.append("\n-- titles:\n");
		for(Title title : titleList) {
			sb.append(title+"\n");
		}
		sb.append("\n-- authors:\n");
		for(Author author : authorList) {
			sb.append(author+"\n");
		}
		sb.append("\n-- identifierList:\n");
		for(Identifier ident : identifierList) {
			sb.append(ident+"\n");
		}
		sb.append("\n-- fullTextLinkList:\n");
		for(FullTextLink fullTextLink : fullTextLinkList) {
			sb.append(fullTextLink+"\n");
		}
		sb.append("\n-- duplicateProbabilityList:\n");
		for(DuplicateProbability duplicateProbability : duplicateProbabilityList) {
			sb.append(duplicateProbability+"\n");
		}
		return sb.toString();
	}
	
	public boolean isEmpty() {
		//TODO: stets überprüfen, ob neue Metadatenelemente hinzugekommen sind, die hier nicht überprüft werden
		if (!titleList.isEmpty()) return false; 
		if (!authorList.isEmpty()) return false; 
		if (!identifierList.isEmpty()) return false; 
		if (!fullTextLinkList.isEmpty()) return false;
		if (!duplicateProbabilityList.isEmpty()) return false;
		return true;
	}
	
	public int addAuthor(Author author) {
		int result = 0;
		if (author != null) {
			this.authorList.add(author);
		}
		return result;
	}
	
	public int addTitle(Title title) {
		int result = 0;
		if (title != null) {
			this.titleList.add(title);
		}
		return result;
	}
	
	public int addIdentifier(Identifier value) {
		int result = 0;
		if (value != null) {
			this.identifierList.add(value);
		}
		return result;
	}
	
	public int addFullTextLink(FullTextLink value) {
		int result = 0;
		if (value != null) {
			this.fullTextLinkList.add(value);
		}
		return result;
	}
	
	public int addDuplicateProbability(DuplicateProbability value) {
		int result = 0;
		if (value != null) {
			this.duplicateProbabilityList.add(value);
		}
		return result;
	}

	public List<FullTextLink> getFullTextLinkList() {
		return fullTextLinkList;
	}

	public void setFullTextLinkList(List<FullTextLink> fullTextLinkList) {
		this.fullTextLinkList = fullTextLinkList;
	}

	public List<DuplicateProbability> getDuplicateProbabilityList() {
		return duplicateProbabilityList;
	}

	public void setDuplicateProbabilityList(
			List<DuplicateProbability> duplicateProbabilityList) {
		this.duplicateProbabilityList = duplicateProbabilityList;
	}

	public BigDecimal getOid() {
		return oid;
	}

	public void setOid(BigDecimal oid) {
		this.oid = oid;
	}

	public List<Title> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<Title> titleList) {
		this.titleList = titleList;
	}

	public List<Author> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<Author> authorList) {
		this.authorList = authorList;
	}

	public List<Identifier> getIdentifierList() {
		return identifierList;
	}

	public void setIdentifierList(List<Identifier> identifierList) {
		this.identifierList = identifierList;
	}
		
}







