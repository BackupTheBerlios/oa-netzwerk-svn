package de.dini.oanetzwerk.utils;

public class DJ { 
	
  private String djName; 
 
  public DJ() {}
  
  @javax.xml.bind.annotation.XmlElement( name = "name" ) 
  public String getDjName() { 
    return djName; 
  } 
 
  public void setDjName( String name ) { 
    this.djName = name; 
  } 
  
}