package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class TCP implements IProtocole {
	
	private String src_port,dst_port,sq_Number,acknow_Number,data_offset,reserved;
	private char urg,ack,psh,rst,syn,fin;
	private String window,checksum,urgent_pointer,padding,data;
	private int tailleHeader;
	
	private List<String> options; 
	private List<String> octets; //données de IP
	public static int nb = 1;
	
	public TCP(IP ip) throws InvalidTrameException {
		octets = ip.getData();
		
		if(octets.size()<20) {
			throw new InvalidTrameException("TCP: Insufficient number of bytes ("+octets.size()+") !");
		}
		
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
		tailleHeader = Convert.hex2dec(data_offset)*4;
		
		if (!headerLengthValid()) {
			throw new InvalidTrameException("TCP: Header length invalid ("+tailleHeader+"), number of bytes invalid !");
		}
		
		
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
		int fin_options = 4*Convert.hex2dec(data_offset);
		
		options = new ArrayList<String>();
		/*on a minimun un octet d'options*/
		if(fin_options>20) {
			options = new Options(octets.subList(20, fin_options), fin_options).getOptionsString();
		}
		else {
			
			options.add("No options");
		}
		
		//options = "";
		data = "";
		//options += (fin_options -20); //a ameliorer
		padding = ""; //a ameliorer
		data += (octets.size()-fin_options); // http ?
	}
	
	//en nombre d'octets
	public int getDataOffset() {
		return 4*Convert.hex2dec(data_offset);
	}
	
	
	/**
	 * @return données de TCP
	 */
	public List<String> getData(){
		return new ArrayList<>(octets.subList(getDataOffset(), octets.size()));
	}
	
	public boolean protocoleIsHttpRequest() {
		return Convert.hex2dec(dst_port) == 80;
	}
	
	public boolean protocoleIsHttpResponse() {
		return Convert.hex2dec(src_port) == 80;
	}
	public String afficheOptions() {
		String s = "";
		for(String p : options) {
			s+="\n\t\t";
			s+=p;
			
		}
		return s ; 
	}
	
	public boolean headerLengthValid() {
		return tailleHeader >=20 && tailleHeader<=60;
	}
	
	public String relativeSequenceNumber(){
		if (syn=='0') {
			nb=1;
			return "n°"+nb+" New connexion segment";
		}else {
			nb++;
			return "n°"+nb+" Current session segment";
		}
	}
	
	private String relativeACKnb(){
		return "Waiting for segment n°"+(nb+1)+" ";
	}
	
	public String toString () {
		String s = "Transmission Control Protocol (TCP)\n\t";
		
		s +=  	"Source: "+Convert.hex2dec(src_port)+"\n\t"
				+ "Destination: "+Convert.hex2dec(dst_port)+"\n\t"
				+ "Sequence number: 0x"+sq_Number+"\n\t"
				+ "Relative Sequence Number: "+relativeSequenceNumber()+"\n\t"//Convert.hex2dec(""+sq_Number)+"\n\t"
				+ "Acknowledgement number: 0x"+(acknow_Number)+"\n\t"
				+ "Relative ACK number: "+relativeACKnb()+"\n\t"//+Convert.hex2dec(acknow_Number)+"\n\t"
				+ "Data offset(THL): "+4*Convert.hex2dec(data_offset)+" octects\n\t"
				+ "Reserved: "+reserved+"\n\t"
				+ "URG: "+urg+" Urgent:"+Convert.Flag(urg)+"\n\t"
				+ "ACK: "+ack+" Acknowledgment:"+Convert.Flag(ack)+"\n\t"
				+ "PSH: "+psh+" Push:"+Convert.Flag(psh)+"\n\t"
				+ "RST: "+rst+" Reset:"+Convert.Flag(rst)+"\n\t"
				+ "SYN: "+syn+" Syn:"+Convert.Flag(syn)+"\n\t"
				+ "FIN: "+fin+" Fin:"+Convert.Flag(fin)+"\n\t"
				+ "Window: "+Convert.hex2dec(window)+"\n\t"
				+ "Checksum: 0x"+checksum+"\n\t"
				+ "Urgent pointer: "+Convert.hex2dec(urgent_pointer)+"\n\t";
				//facultatif:
				s+="Options: "+afficheOptions()+"\n\t"
				+ "Data: "+data+" octets\n\t";
		
		return s;
		
	}
	
	
}
