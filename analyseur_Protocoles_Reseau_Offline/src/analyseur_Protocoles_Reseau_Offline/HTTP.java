package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class HTTP implements IProtocole {
	
	private TCP tcp;
	private List<String> octets;
	private String methode, url, version;
	private String code_statut, message;
	private List<Ligne> lignes;
	
	public HTTP(TCP tcp) {
		this.tcp=tcp;
		octets = tcp.getData();
		
		methode="";
		url="";
		version="";
		code_statut="";
		message="";
		
		int i=0;
		if(tcp.protocoleIsHttpRequest()) {
			for(; ! octets.get(i).equals("20") ; i++) {
				methode += Convert.ascii2char(octets.get(i));
			}
			i++;
			for(; ! octets.get(i).equals("20") ; i++) {
				url += Convert.ascii2char(octets.get(i));
			}
			i++;
			for(; ! (octets.get(i).equals("0d") && octets.get(i+1).equals("0a")) ; i++) {
				version += Convert.ascii2char(octets.get(i));
			}
			i+=2;
		}
		else { //protocol is response
			for(; ! octets.get(i).equals("20") ; i++) {
				version += Convert.ascii2char(octets.get(i));
			}
			i++;
			for(; ! octets.get(i).equals("20") ; i++) {
				code_statut += Convert.ascii2char(octets.get(i));
			}
			i++;
			for(; ! (octets.get(i).equals("0d") && octets.get(i+1).equals("0a")) ; i++) {
				message += Convert.ascii2char(octets.get(i));
			}
			i+=2;
		}
		
		lignes=new ArrayList<HTTP.Ligne>();
		//lignes d'entete
		while( ! ( (octets.get(i).equals("0d"))&&(octets.get(i+1).equals("0a")))){//&&(octets.get(i+2).equals("0d"))&&(octets.get(i+3).equals("0a")))) {
			//si fin des lignes
			String champ="";
			for(; ! octets.get(i).equals("20") ; i++) {
				champ+=Convert.ascii2char(octets.get(i));
			}
			i++;
			
			String val="";
			for(; ! (octets.get(i).equals("0d") && octets.get(i+1).equals("0a")) ; i++) {
				val+=Convert.ascii2char(octets.get(i));
			}
			i+=2;
			
			lignes.add(new Ligne(champ, val));
			
		}
		
	}
	
	private class Ligne{
		private String champ;
		private String val;
		public Ligne (String ch, String v) {
			champ=ch;
			val=v;
		}
		public String getChamp() {
			return champ;
		}
		public String getVal() {
			return val;
		}
		public String toString () {
			return getChamp()+" "+getVal()+"\n";
		}
	}
	
	public String toString() { ///// ameliorer ajouter statut code 404,200...
		
		String s="Hypertext Transfer Protocol (HTTP)\n\t";
		
		if(tcp.protocoleIsHttpRequest()) {
			s += "Request method: "+methode+"\n\t"
				+ "Request URI: "+url+"\n\t"
				+ "Request Version: "+version+"\n\t";
		}
		else {
			s += "Response method: "+methode+"\n\t"
				+ "Response Statut Code: "+code_statut+"\n\t"
				+ "[Status code Description: ]\n\t"
				+ "Response Phrase "+message+"\n\t";
		}
		
		
		for(Ligne line : lignes) {
			s+=line.toString()+"\t";
		}
		
		return s+="\n";
		
	}

}
