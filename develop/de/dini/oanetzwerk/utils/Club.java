package de.dini.oanetzwerk.utils;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( namespace = "http://tutego.com/" ) 
public class Club { 
	
  private DJ  dj; 
  private int numberOfPersons; 
 
   
  private List<DJ> staff;
  
  public Club() {}
  
  public DJ getDj() { 
    return dj; 
  } 
 
  public void setDj( DJ dj ) { 
    this.dj = dj; 
  } 
 
  public int getNumberOfPersons() { 
    return numberOfPersons; 
  } 
 
  public void setNumberOfPersons( int numberOfPersons ) { 
    this.numberOfPersons = numberOfPersons; 
  } 
  
  @javax.xml.bind.annotation.XmlElement( name = "djstaff" )
  public List<DJ> getStaff() {
	  return this.staff;
  }

  //@javax.xml.bind.annotation.XmlElement( name = "djstaff" )
  public void setStaff(List<DJ> staff) {
	this.staff = staff;
  }
  
  
}
