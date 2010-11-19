package de.dini.oanetzwerk.utils.imf;

import java.math.BigDecimal;

public class DuplicateProbability {
	
	BigDecimal referToOID;
    double probability;
    double reverseProbability;
	int number;

	public DuplicateProbability() {
		
	}
	
    public DuplicateProbability(BigDecimal referToOID, double probability, double reverseProbability, int number) {
    	this.referToOID = referToOID;
        this.probability = probability;
        this.reverseProbability = reverseProbability;
    	this.number = number;
	}

	public String toString() {
		StringBuffer sbResult = new StringBuffer();
		sbResult.append("referToOID=" + this.referToOID)
		        .append("\n" + "probability=" + this.probability)
		        .append("\n" + "number=" + this.number);
		return sbResult.toString();
	}
    
	public BigDecimal getReferToOID() {
		return referToOID;
	}

	public void setReferToOID(BigDecimal referToOID) {
		this.referToOID = referToOID;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getReverseProbability() {
		return reverseProbability;
	}

	public void setReverseProbability(double reverseProbability) {
		this.reverseProbability = reverseProbability;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
    
    
}
