package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class Options {
	
	private List<Option> options;
	private List<String> o;
	private int tailleOptions;
	
	public Options(List<String> ls, int tailleHeader) throws InvalidTrameException {
		o=ls;
		this.tailleOptions=tailleHeader-20;
		
		/*fin de l'entete IP (20oct), Options à suivre (max 40oct)*/
		options = new ArrayList<Option>();
		int i=0; //debut des options
		
		try {
			while(i<tailleOptions) {
				
				// codés sur 1 octet chacun
				String type = o.get(i);
				
				// si type==0 (bourrage) -> break
				if(Convert.hex2dec(type)==0) {
					options.add(new Option(type, ""+(tailleOptions-i), "00")); //option padding
					i=tailleOptions;
					break;
				}
				
				//si type==1 (no operation, alignement de la prochaine option a la ligne)
				if(Convert.hex2dec(type)==1) {
					//int jump = 4-(i%4); //compter le nombre d'octets restants = 4-(i%4)
					options.add(new Option(type, "1", "01")); //option padding
					i++;
					continue;
				}
				
				
				/*----------------------- en bas a verifier ---------------------*/
				
				String longueur = o.get(i+1);
				String pointeur = o.get(i+2);
				
				
				
				
				options.add(new Option(type, longueur, pointeur)); //ajouter le type de l'option
				
				i += Convert.hex2dec(longueur); //aller a l'option suivante
				
			}
		}catch(ArrayIndexOutOfBoundsException ae) {
			throw new InvalidTrameException("IP: Option invalide"); 
		}
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	

	public List<String> getOptionsString(){
		
		
		
		return null;
	}
	
	
	
	private class Option {
		
		private int type ;
		private int longueur ;
		private int valeur ;
		/*ajouter uen liste d'octets si on veut ameliorer et traiter l'option*/
	
		public Option(String t, String l, String v) {
			type=Convert.hex2dec(t);
			longueur=Convert.hex2dec(l);
			valeur =Convert.hex2dec(v);
			/*init la liste si amelioration*/
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
	
			case 7:
				s = "(7) Record route";
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
				s = "Unknown option !";
				break;
			}
			
			return s;
		}
		
		public int getLongueur() {
			return longueur;
		}
		
		public int getValeur() {
			return valeur;
		}
		
		public String toString() {
			String s="";
			s+=getType()+"; "+"Length="+getLongueur()+"; Value="+getValeur();
			return s;
		}
	}
	
	

}
