package analyseur_Protocoles_Reseau_Offline;

import java.util.ArrayList;
import java.util.List;

public class Filtre {
	
	public static List<Trame> filtrer(Trace t) {
		
		List<Trame> trames = new ArrayList<>();
		
		//tableau de lignes
		String[] lines = t.getTrace().split("\n");
		
		//parcours des lignes
		for(String line : lines) {
			
			//System.out.println("line "+line); //juste pour debug
			
			//decouper la ligne
			String[] octets = line.split(" ");
			
			//si l'offset n'est pas codé sur un octet ou + -> ignorer ligne
			if( ! (octets[0].length()>1 && octets[0].length()%2==0) ) {
				continue; //passe a la ligne suivante
			}
			
			try {
				//si le premier mot de la ligne == offset 0
				if(Integer.parseInt(octets[0], 16) == 0x00) {
					//creation nouvelle trame
					Trame trame = new Trame();
					
					//ajouter cette trame a la liste des trames
					trames.add(trame);
				}
			}
			catch(NumberFormatException e) {
				//System.out.println("erreur format");
				continue;
			}
			
			Trame lastTrame;
			
			try {
				lastTrame = trames.get(trames.size()-1); //derniere trame
				//si l'offset ne correspond pas -> ignorer la ligne
				if(Integer.parseInt(octets[0], 16) != lastTrame.size()) {
					System.out.println("continue: octet= "+Integer.parseInt(octets[0], 16)+" last trame size= "+lastTrame.size());
					continue;
				}
			}
			catch(IndexOutOfBoundsException e) {
				//si la liste des trames est encore vide et l'offset 0 n'est pas trouvé
				continue;
			}
			
			//parcours de la ligne
			for(int i=1; i<octets.length; i++) {
				
				try {
					int hex = Integer.parseInt(octets[i], 16);
					
					//si n'est pas codé sur un octet -> ignorer
					if(octets[i].length() != 2) throw new NumberFormatException();
					
					//si ce n'est pas un octet -> ignorer // FACULTATIF //
					//if(hex<0 || hex>255) throw new NumberFormatException();
					
					//on ajoute l'octet à la derniere trame
					lastTrame.addOctet(octets[i]);
					
				}
				catch(NumberFormatException e) {
					//ignorer (ne rien faire)
					//System.out.println("mot "+octets[i]+" numero "+i+" ignoré"); //pour debug
				}
				
			}
			
		}
		
		return trames;
	}

}
