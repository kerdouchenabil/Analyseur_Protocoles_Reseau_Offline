package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class Options {

	private List<Option> options;
	private List<String> o;
	private int tailleOptions;

	public Options(List<String> ls, int tailleHeader) throws InvalidTrameException {
		o = ls;
		this.tailleOptions = tailleHeader - 20;

		/* fin de l'entete IP (20oct), Options à suivre (max 40oct) */
		options = new ArrayList<Option>();
		int i = 0; // debut des options

		try {
			while (i < tailleOptions) {

				// codés sur 1 octet chacun
				String type = o.get(i);

				// si type==0 (bourrage) -> break
				if (Convert.hex2dec(type) == 0) {
					options.add(new Option(type, "" + (tailleOptions - i), "00")); // option padding
					i = tailleOptions;
					break;
				}

				// si type==1 (no operation, alignement de la prochaine option a la ligne)
				if (Convert.hex2dec(type) == 1) {
					// int jump = 4-(i%4); //compter le nombre d'octets restants = 4-(i%4)
					options.add(new Option(type, "1", "01")); // option padding
					i++;
					continue;
				}

				String longueur = o.get(i + 1);
				
				String pointeur = o.get(i + 2);
				
				if(Convert.hex2dec(longueur)>3 ) {
					List<String> list = o.subList(i+3, i+Convert.hex2dec(longueur)); ///
					options.add(new Option(type, longueur, pointeur, list)); // ajouter des octets pour le traitement
				}
				else {
					options.add(new Option(type, longueur, pointeur)); // ajouter le type de l'option sans la liste
				}
				
				if(Convert.hex2dec(longueur) > 40) {
					
					break;//important 
				}
				
				i += Convert.hex2dec(longueur); // aller a l'option suivante

			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			throw new InvalidTrameException("IP: Invalid option");
		}

	}

	public List<String> getOptionsString() {
		ArrayList<String> los = new ArrayList<String>();
		for (Option p : options) {
			los.add(p.toString());
		}
		return los;

	}

	private class Option {

		private int type;
		private int longueur;
		private int pointeur;
		private String p ;
		private List<String> list;
		/* ajouter uen liste d'octets si on veut ameliorer et traiter l'option */

		public Option(String t, String l, String v) {
			type = Convert.hex2dec(t);
			longueur = Convert.hex2dec(l);
			pointeur = Convert.hex2dec(v);
			p = v;
			/* init la liste si amelioration */
		}
		
		public Option(String t, String l, String v, List<String> lis) {
			type = Convert.hex2dec(t);
			longueur = Convert.hex2dec(l);
			pointeur = Convert.hex2dec(v);
			list = lis;
			p = v;
		}

		/**
		 * return
		 */
		public String getType() {
			String s = "";

			switch (type) {
			case 0:
				s = "(0) End of options list";
				break;

			case 1:
				s = "(1) No operation";
				break;

			case 2:
				s = "(2) Maximum Segment Size(MSS)";
				break;

			case 3:
				s = "(3) WSOPT-Window Scale";
				break;

			case 4:
				s = "(4) SACK Permitted";
				break;

			case 5:
				s = "(5) SACK (Selective ACK)";
				break;

			case 6:
				s = "(6) Echo ";
				break;

			case 7:
				s = "(7) Record route";
				break;

			case 8:
				s = "(8) TSOPT - Time Stamp Option";
				break;

			case 9:
				s = "(9) Partial Order Connection Permitted";
				break;

			case 10:
				s = "(10) Partial Order Service Profile ";
				break;

			case 11:
				s = "(11) CC ";
				break;

			case 12:
				s = "(12) CC.NEW ";
				break;

			case 13:
				s = "(13) CC.ECHO ";
				break;

			case 14:
				s = "(14) TCP Alternate Checksum Request ";
				break;

			case 15:
				s = "(15) TCP Alternate Checksum Data  ";
				break;

			case 68:
				s = "(68) Time stamp";
				break;

			case 131:
				s = "(131) Loose routing";
				break;

			case 137:
				s = "(137) Strict routing";
				break;

			default:
				s = "Unknown option";
				break;
			}

			return s;
		}

		public int getLongueur() {
			return longueur;
		}

		public int getValeur() {
			return pointeur;
		}
		
		public String getRecordRoute() {
			if(list==null) return "";
			
			String r = "\t";
			
			if((longueur-3)%4 == 0) {
				for (int i = 0; i < list.size(); i+=4) {
					for (int j = 0; j < 4; j++) {
						r += Convert.hex2dec(list.get(i+j));
						if (j!=3) r += ".";
					}
					r+="\n\t\t\t";
				}
			}
			else {
				r+="\tIncomplete option IP adresses";
			}
			
			return r;
		}
		
		public String getTimeStamp() {
			if(list==null) return "";
			list.add(0, p);
			String r = "";
			
			if(longueur==10) {
				r+="\tTSV: 0x"+list.get(0)+list.get(1)+list.get(2)+list.get(3)
				+ "\n\t\t\tTERV: 0x"+list.get(4)+list.get(5)+list.get(6)+list.get(7);
			}
			else {
				r+="\tIncomplete Time Stamp";
			}
			
			return r;
		}
		
		public String getMSS() {
			if(list==null) return "";
			//list.add(0, p);
			String r = "";
			
			if(longueur==4) {
				r+="\tMSS: 0x"+p+list.get(0) + " ("+Convert.hex2dec(p+list.get(0))+")";
			}
			else {
				r+="\tIvalid MSS length";
			}
			
			return r;
		}

		public String toString() {
			String s = "";
			if(type ==  1) {
				s += getType() + ": " + "Length=" + getLongueur() ;
				return s ;
			}
			if(longueur > 40) {
				s += getType() + ": " + "Length=" + "invalid length ("+longueur + "); Pointer=" + getValeur();
				return s;
			}
			
			/*traitement options*/
			if(type ==  7) { //record route
				s += getType() + ": " + "Length=" + getLongueur() + "; Pointer=" + getValeur() + "\n\t\t" + getRecordRoute();
				return s ;
			}
			
			if(type ==  8) { // TS
				s += getType() + ": " + "Length=" + getLongueur() + "\n\t\t" + getTimeStamp();
				return s ;
			}
			
			if(type ==  2) { // MSS
				s += getType() + ": " + "Length=" + getLongueur() + "\n\t\t" + getMSS();
				return s ;
			}
			
			if(type ==  3) { // Window
				s += getType() + ": " + "Length=" + getLongueur() + "; Shift=" + getValeur();
				return s ;
			}
			
			s += getType() + ": " + "Length=" + getLongueur() + "; Pointer=" + getValeur();
			return s;
		}
	}

}
