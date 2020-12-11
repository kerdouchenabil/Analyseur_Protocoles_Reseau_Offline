package analyseur_Protocoles_Reseau_Offline;

import java.util.List;


public class TCP implements IProtocole {
	private String src_port,dst_port,sq_Number,acknow_Number,data_offset,reserved;
	private char urg,ack,psh,rst,syn,fin;
	private String window,checksum,urgent_pointer,options,padding,data;
	
	public TCP(List<String> octets) {
		src_port=octets.get(0)+octets.get(1);
		dst_port=octets.get(2)+octets.get(3);
		sq_Number="";
		for(int i=4; i<8; i++) {
			sq_Number += octets.get(i);
		}
		acknow_Number="";
		for(int i=8; i<12; i++) {
			acknow_Number+= octets.get(i);
		}
		
		data_offset="";
		data_offset+=octets.get(12).charAt(0);
		
		String reserved_flags=octets.get(12).charAt(1)+octets.get(13);
		String reserved_flag_sbin =Convert.hex2bin(reserved_flags) ;
		reserved_flag_sbin = Convert.adjustNumberLength(reserved_flag_sbin, 12);
		reserved="";
		reserved += Convert.bin2dec(reserved_flag_sbin.substring(0, 6));
		reserved_flag_sbin = reserved_flag_sbin.substring(6);
		urg=reserved_flag_sbin.charAt(0);
		ack=reserved_flag_sbin.charAt(1);
		psh=reserved_flag_sbin.charAt(2);
		rst=reserved_flag_sbin.charAt(3);
		syn=reserved_flag_sbin.charAt(4);
		fin=reserved_flag_sbin.charAt(5);
		window = octets.get(14)+octets.get(15);
		checksum = octets.get(16)+octets.get(17);
		urgent_pointer = octets.get(18)+octets.get(19);
		// options a ameliorer  
		int fin_options = Convert.hex2dec(data_offset);
		options = "";
		data = "";
		options += (fin_options -20); //a ameliorer
		padding = ""; //a ameliorer
		data += (octets.size()-fin_options); // http ?
	}
	
	
	public String toString () {
		String s = "TCP\n\t";
		
		s +=  	"Source: "+Convert.hex2dec(src_port)+"\n\t"
				+ "Destination: "+Convert.hex2dec(dst_port)+"\n\t"
				+ "Sequence number: "+Convert.hex2dec(""+sq_Number)+"\n\t"
				+ "Acknowledgement number: "+Convert.hex2dec(acknow_Number)+"\n\t"
				+ "Data offset: "+Convert.hex2dec(data_offset)+"\n\t"
				+ "Reserved: "+reserved+"\n\t"
				+ "URG: "+urg+"\n\t"
				+ "ACK: "+ack+"\n\t"
				+ "PSH: "+psh+"\n\t"
				+ "RST: "+rst+"\n\t"
				+ "SYN: "+syn+"\n\t"
				+ "FIN: "+fin+"\n\t"
				+ "Window: "+window+"\n\t"
				+ "Checksum: "+checksum+"\n\t"
				+ "Urgent pointer: "+Convert.hex2dec(urgent_pointer)+"\n\t";
				//facultatif:
				s+="Options: "+options+"\n\t"
				+ "Padding: "+padding+" octets\n\t"
				+ "Data: "+data+" octets\n\t";
		
		return s;
		
	}
	
	
}
