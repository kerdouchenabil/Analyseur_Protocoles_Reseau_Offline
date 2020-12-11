package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class Trame {
	
	private List<String> octets;
	
	public Trame() {
		octets = new ArrayList<>();
	}
	
	public String toString() {
		return octets.toString();
	}
	
	public void addOctet(String s) {
		octets.add(s);
	}
	
	public int size() {
		return octets.size();
	}
	
	public List<String> getOctets(){
		return octets;
	}
}
