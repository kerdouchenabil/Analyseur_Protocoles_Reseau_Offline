package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Checksum;

public class IP implements IProtocole {

	private char version, ihl, flag0, flag1_df, flag2_mf;
	private String tos, total_length;
	private String id, fragment_offset;
	private String ttl, protocol, header_checksum;
	private String source, dest;
	private String data;
	private List<String> octets;
	private int tailleHeader, tailleOptions, tailleData; // header=entete+options=20+options

	private String fragment_offset_bin;

	private List<String> options;

	public IP(Ethernet eth) throws Exception { // a partir de l'octet 14 après Ethernet

		octets = eth.getRemainingOctets();
		List<String> o = octets;
		version = o.get(0).charAt(0);

		ihl = o.get(0).charAt(1);
		tailleHeader = 4 * Convert.hex2dec("" + ihl);
		if (!headerLengthValid()) {
			throw new InvalidTrameException("IP: Header length invalid, nombre d'octets insuffisant!");
		}
		tailleOptions = tailleHeader - 20;

		tos = o.get(1);

		total_length = o.get(2) + o.get(3);
		tailleData = Convert.hex2dec(total_length) - tailleHeader;

		id = o.get(4) + o.get(5);

		String flags_fo_hex = o.get(6) + o.get(7);
		String flags_fo_bin = Convert.hex2bin(flags_fo_hex);
		flags_fo_bin = Convert.adjustNumberLength(flags_fo_bin, 16);

		flag0 = flags_fo_bin.charAt(0);
		flag1_df = flags_fo_bin.charAt(1);
		flag2_mf = flags_fo_bin.charAt(2);

		String fo_bin = flags_fo_bin.substring(3);
		fragment_offset = Convert.bin2hex(fo_bin); // stocké en hexa
		fragment_offset_bin = fo_bin; // stocké en binaire juste pour voir sur 13bits

		ttl = o.get(8);

		protocol = o.get(9);

		header_checksum = o.get(10) + o.get(11);

		source = "";
		for (int i = 12; i < 16; i++) {
			source += Convert.hex2dec(o.get(i));
			if (i != 15)
				source += ".";
		}

		dest = "";
		for (int i = 16; i < 20; i++) {
			dest += Convert.hex2dec(o.get(i));
			if (i != 19)
				dest += ".";
		}

		options = new ArrayList<String>();
		/* on a minimun un octet d'options */
		if (tailleHeader > 20) {
			options = new Options(octets.subList(20, tailleHeader), tailleHeader).getOptionsString();
		} else {

			options.add("No options");
		}

		// taille des data pour le moment
		data = "" + tailleData;

	}

	// en nb d'Octects
	public int getHeaderLength() {
		return tailleHeader;
	}

	public boolean verifyCheckSum() {
		int sum = 0;
		for (int i = 0; i < getHeaderLength(); i += 2) {
			int cour = Convert.hex2dec(octets.get(i) + octets.get(i + 1));
			sum += cour;

		}
		String sum_hex = Integer.toHexString(sum);
		Convert.adjustNumberLength(sum_hex, 4);

		if (sum_hex.length() > 4) {
			String depassement = "" + sum_hex.charAt(0);
			sum_hex = sum_hex.substring(1);

			int d = Convert.hex2dec(depassement);
			int s = Convert.hex2dec(sum_hex);
			s += d;
			sum_hex = Integer.toHexString(s);
			Convert.adjustNumberLength(sum_hex, 4);
		}

		if (sum_hex.equals("FFFF") || sum_hex.equals("ffff")) {
			return true;
		}
		return false;
	}

	public boolean headerLengthValid() {
		return octets.size() >= tailleHeader;
	}

	public List<String> getData() {
		return new ArrayList<>(octets.subList(4 * Convert.hex2dec("" + ihl), octets.size()));
	}

	public boolean protocoleIsTcp() {
		return Convert.hex2dec(protocol) == 6;
	}

	public String getProtocole() {
		if (protocoleIsTcp()) {
			return "6 (TCP) ";
		}
		if (Convert.hex2dec(protocol) == 1) {
			return "1 (ICMP)";
		}
		if (Convert.hex2dec(protocol) == 17) {
			return "17 (UDP)";
		}
		return "unknown protocol!";
	}

	public String afficheOptions() {
		String s = "";
		for (String p : options) {
			s += "\n\t\t";
			s += p;

		}
		return s;
	}
	
	public boolean verifyVersion() {
		return ((Convert.hex2dec(""+version))==4);
	}

	public String toString() {
		String s = "Internet Protocol (IP)\n\t";

		s += "version: " + Convert.hex2dec("" + version) + "\n\t" + "Header Length: " + 4 * Convert.hex2dec("" + ihl)
				+ "\n\t" + "TOS: " + Convert.hex2dec(tos) + "\n\t" + "Total length: " + Convert.hex2dec(total_length)
				+ "\n\t" + "Identification: 0x" + id + " (" + Convert.hex2dec(id) + ")\n\t" + "Flag reserved bit: "
				+ flag0 + "\n\t" + "Flag Don't fragment: " + flag1_df + "\n\t" + "Flag More fragments: " + flag2_mf
				+ "\n\t" + "Fragment offset: " + Convert.hex2dec(fragment_offset) + "(0b " + fragment_offset_bin
				+ ")\n\t" + "TTL: " + Convert.hex2dec(ttl) + "\n\t" + "Protocol: " + getProtocole() + "\n\t" // a
																												// ameliorer
				+ "Header checksum: 0x" + header_checksum + " (" + Convert.hex2dec(header_checksum) + ")\n\t"
				+ "Source: " + source + "\n\t" + "Destination: " + dest + "\n\t";
		// facultatif:
		s += "Options: " + afficheOptions() + "\n\t" + "Data: " + data + " octets\n\t"
				+ "_______verification_______\n\t"
				+ "IP version validity: "+verifyVersion()+"\n\t" 
				+ "checksum validity: " + verifyCheckSum() + " \n\t"
				+ "";

		return s;
	}

}
