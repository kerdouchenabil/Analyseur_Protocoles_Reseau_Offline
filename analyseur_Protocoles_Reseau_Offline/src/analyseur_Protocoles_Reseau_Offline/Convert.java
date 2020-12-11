package analyseur_Protocoles_Reseau_Offline;

public class Convert {

	public static int hex2dec(String hex) { //throws NumberFormatException
		return Integer.parseInt(hex, 16);
	}
	
	public static String hex2bin(String hex) { //throws ?
		Integer i = Convert.hex2dec(hex);
	    String bin=Integer.toBinaryString(i);
	    return bin;
	}
	
	public static String bin2hex(String bin) { //throws NumberFormatException
		int dec = Integer.parseInt(bin, 2);
		String hex = Integer.toString(dec,16);
		return hex;
	}
	public static int bin2dec(String bin) {
		String i = Convert.bin2hex(bin);
		return Convert.hex2dec(i);
		
	}
	
	public static String adjustNumberLength(String nb, int length) {
		String res=nb;
		while(res.length()<length) {
			res="0"+res;
		}
		return res;
	}
	
}
