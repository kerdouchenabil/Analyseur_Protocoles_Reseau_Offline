package analyseur_Protocoles_Reseau_Offline;

import java.io.IOException;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		
		String file = "data/trace_test.txt";
		Trace trace;
		
		try {
			
			/*
            System.out.println("bin= "+Convert.hex2bin("8"));
            System.out.println("hex= "+Convert.bin2hex("1111"));
			*/
			/*String reserved_flag_sbin="01234567";
			String reserved = reserved_flag_sbin.substring(0, 6);
			System.out.println(reserved);*/
			
			trace = new Trace(file);
			
			List<Trame> trames = Filtre.filtrer(trace);
			System.out.println(trames.get(0)); //afficher la 1ere trame
			System.out.println(trames.get(1)); //afficher la 2eme trame
			
			try {
				Ethernet eth = new Ethernet (trames.get(0));
				System.out.println(eth);
				
				/*
				IP ip = new IP(eth.getRemainingOctets());
				System.out.println(ip);
				*/
				
				TCP tcp = new TCP(eth.getRemainingOctets());
				System.out.println(tcp);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
