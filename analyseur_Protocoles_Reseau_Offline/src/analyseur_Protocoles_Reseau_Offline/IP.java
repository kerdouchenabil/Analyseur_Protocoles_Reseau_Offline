package analyseur_Protocoles_Reseau_Offline;

import java.util.List;

public class IP implements IProtocole {
	
	private char version, ihl, flag0, flag1_df, flag2_mf;
	private String tos, total_length;
	private String id, fragment_offset;
	private String ttl, protocol, header_checksum;
	private String source, dest;
	private String options, padding, data;
	
	private int tailleHeader, tailleOptions, tailleData; //header=entete+options=20+options
	
	private String fragment_offset_bin;

	public IP(List<String> o) { //a partir de l'octet 14 après Ethernet
		
		version=o.get(0).charAt(0);
		
		ihl=o.get(0).charAt(1);
		tailleHeader = 4*Convert.hex2dec(""+ihl);
		
		tailleOptions = tailleHeader-20;
		
		tos=o.get(1);
		
		total_length=o.get(2)+o.get(3);
		tailleData = Convert.hex2dec(total_length) - tailleHeader;
		
		id = o.get(4)+o.get(5);
		
		String flags_fo_hex = o.get(6)+o.get(7);
		String flags_fo_bin = Convert.hex2bin(flags_fo_hex);
		flags_fo_bin = Convert.adjustNumberLength(flags_fo_bin, 16);
				
		flag0 = flags_fo_bin.charAt(0);
		flag1_df = flags_fo_bin.charAt(1);
		flag2_mf = flags_fo_bin.charAt(2);
		
		String fo_bin = flags_fo_bin.substring(3);
		fragment_offset = Convert.bin2hex(fo_bin); //stocké en hexa
		fragment_offset_bin = fo_bin; //stocké en binaire juste pour voir sur 13bits
		
		ttl = o.get(8);
		
		protocol = o.get(9);
		
		header_checksum = o.get(10)+o.get(11);
		
		source = "";
		for(int i=12; i<16; i++) {
			source += Convert.hex2dec(o.get(i));
			if(i != 15) source += ".";
		}
		
		dest = "";
		for(int i=16; i<20; i++) {
			dest += Convert.hex2dec(o.get(i));
			if(i != 19) dest += ".";
		}
		
		/*fin de l'entete IP (20oct), Options à suivre (max 40oct)*/
		options = "";
		int i=20; //debut des options
		
		while(i<tailleHeader) {
			
			// codés sur 1 octet chacun
			String type = o.get(i);
			String longueur = o.get(i+1);
			String pointeur = o.get(i+2);
			
			// si longeur option==0 -> break
			if(Convert.hex2dec(longueur)==0) {
				break;
			}
			
			options += type+"; "; //ajouter le type de l'option
			
			i += Convert.hex2dec(longueur); //aller a l'option suivante
			
		}
		
		//nombre d'octets de bourrage en decimal
		padding = ""+(tailleHeader - i);
		
		//taille des data pour le moment
		data = ""+tailleData;
		
	}
	
	public String toString() {
		String s="Internet Protocol (IP)\n\t";
		
		s += "version: "+Convert.hex2dec(""+version)+"\n\t"
				+ "Header Length: "+Convert.hex2dec(""+ihl)+"\n\t"
				+ "TOS: "+Convert.hex2dec(tos)+"\n\t"
				+ "Total length: "+Convert.hex2dec(total_length)+"\n\t"
				+ "Identification: 0x"+id+" ("+Convert.hex2dec(id)+")\n\t"
				+ "Flag reserved bit: "+flag0+"\n\t"
				+ "Flag Don't fragment: "+flag1_df+"\n\t"
				+ "Flag More fragments: "+flag2_mf+"\n\t"
				+ "Fragment offset: "+Convert.hex2dec(fragment_offset)+"(0b "+fragment_offset_bin+")\n\t"
				+ "TTL: "+Convert.hex2dec(ttl)+"\n\t"
				+ "Protocol: 0x"+protocol+" ("+Convert.hex2dec(protocol)+")\n\t" // a ameliorer
				+ "Header checksum: "+Convert.hex2dec(header_checksum)+"\n\t"
				+ "Source: "+source+"\n\t"
				+ "Destination: "+dest+"\n\t";
				//facultatif:
				s+="Options: "+options+"\n\t"
				+ "Padding: "+padding+" octets\n\t"
				+ "Data: "+data+" octets\n\t";
		
		return s;
	}

}
