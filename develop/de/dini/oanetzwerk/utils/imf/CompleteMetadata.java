package de.dini.oanetzwerk.utils.imf;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( namespace = "http://oanetzwerk.dini.de/" ) 
public class CompleteMetadata extends InternalMetadata {

	List<FullTextLink> fullTextLinkList;

	List<DuplicateProbability> duplicateProbabilityList;
	
	public CompleteMetadata() {
		super();
		fullTextLinkList = new LinkedList<FullTextLink>();
		duplicateProbabilityList = new LinkedList<DuplicateProbability>();
	}
			
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append("\n-- fullTextLinkList:");
		for(FullTextLink fullTextLink : fullTextLinkList) {
			sb.append(fullTextLink+"\n");
		}
		sb.append("\n-- duplicateProbabilityList:");
		for(DuplicateProbability duplicateProbability : duplicateProbabilityList) {
			sb.append(duplicateProbability+"\n");
		}
		return sb.toString();
	}
	
	public boolean isEmpty() {
		boolean result = super.isEmpty();
		if(!fullTextLinkList.isEmpty()) return false;
		if(!duplicateProbabilityList.isEmpty()) return false;
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
		
}







