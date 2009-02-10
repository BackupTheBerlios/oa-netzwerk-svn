package de.dini.oanetzwerk.userfrontend;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestDDCDataSingleton {

	public static DDCDataSingleton ddcData = null;
	
	@BeforeClass
	public static void prepare() {
		ddcData = DDCDataSingleton.getInstance();
	}
	
	@Test
	public void printSimpleDDC() {
		System.out.println(ddcData.getSimpleDDCCategorySums());
	}
	
	@Test
	public void printDDCTree() {
		for(DDCNaviNode node : ddcData.getListDDCNaviNodes()) {
			System.out.println("----- " + node.getStrDDCValue() + " " + node.getStrNameDE() + " " + node.getLongItemCount());
			for(DDCNaviNode node2 : node.getListSubnodes()) {
				System.out.println("  --- " + node2.getStrDDCValue() + " " + node2.getStrNameDE() + " " + node2.getLongItemCount());
				for(DDCNaviNode node3 : node2.getListSubnodes()) {
					System.out.println("    - " + node3.getStrDDCValue() + " " + node3.getStrNameDE() + " " + node3.getLongItemCount());
				}
			}
		}
	}
	
}
