package analyseur_Protocoles_Reseau_Offline;

import java.util.List;
import java.util.ArrayList;

public class Ethernet {
	
	private String dest, source, type, proto_name;
	//private IProtocole protocole;
	private Trame trame;

	public Ethernet(Trame trame) throws InvalidTrameException {
		
		if(trame.size()<14) {
			throw new InvalidTrameException("Ethernet: Insufficient number of bytes ("+trame.size()+") !");
		}
		
		this.trame = trame;
		
		dest = "";
		source = "";
		type = "";
		proto_name = "";
		
		for(int i=0; i<6; i++) {
			dest += trame.getOctets().get(i);
			if(i != 5) dest += ":";
		}
		
		for(int i=6; i<12; i++) {
			source += trame.getOctets().get(i);
			if(i != 11) source += ":";
		}
		
		//type
		type = trame.getOctets().get(12) + trame.getOctets().get(13);
		
		switch (type) { 
			case "0800": proto_name="IPv4"; break;
			
			case "0806": proto_name="ARP"; break;
			//autres facultatifs...
			
			default:
				proto_name = "Unknown Protocol";
		}
		
	}
	
	public boolean protocoleIsIP () {
		return type.equals("0800");
	}
	
	/*retourne les octets restants à envoter au protocole, (a partir de 14)*/
	public List<String> getRemainingOctets(){
		return new ArrayList<>(trame.getOctets().subList(14, trame.size()));
	}
	
	public String toString() {
		String s = "Ethernet\n\t"
				+ "Destination: "+dest+ "\n\t"
				+ "Source: "+source+ "\n\t"
				+ "Type: "+proto_name+" (0x"+type+ ")\n"
				+ "Data: "+getRemainingOctets().size()+ " octets\n\t";
		return s;
				
	}

}