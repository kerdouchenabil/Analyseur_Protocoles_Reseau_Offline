package analyseur_Protocoles_Reseau_Offline;

import java.util.List;
import java.util.ArrayList;

public class Ethernet {
	
	private String dest, source, type, proto_name;
	//private IProtocole protocole;
	private Trame trame;

	public Ethernet(Trame trame) throws Exception {
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
		
		switch (type) { //////////// numeros a verifier http et tcp
			case "0800": proto_name="IPv4"; /*protocole=new IP(trame.getOctets().subList(14, trame.size()));*/ break;
			case "8080": proto_name="TCP"; /*protocole=new TCP(trame.getOctets().subList(14, trame.size()));*/ break;
			case "0000": proto_name="HTTP"; /*protocole=new HTTP(trame.getOctets().subList(14, trame.size()));*/ break;
			
			case "0806": proto_name="ARP"; break;
			//autres facultatifs...
			
			default:
				throw new Exception("type protocole invalide !");
		}
		
	}
	
	/*retourne les octets restants à envoter au protocole, (a partir de 14)*/
	public List<String> getRemainingOctets(){
		return new ArrayList<>(trame.getOctets().subList(14, trame.size()));
	}
	
	public String toString() {
		String s = "Ethernet\n\t"
				+ "Destination: "+dest+ "\n\t"
				+ "Source: "+source+ "\n\t"
				+ "Type: "+proto_name+" (0x"+type+ ")\n";
		return s;
				
	}

}