package analyseur_Protocoles_Reseau_Offline;

import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Test {

	public static void main(String[] args) throws IOException {
		
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
			
			//System.out.println(Convert.ascii2char("41"));
			
			
			File file2 = new File("data/analyse.txt");
			trace = new Trace(file);
			
			List<Trame> trames = Filtre.filtrer(trace);
			//System.out.println(trames.get(0)); //afficher la 1ere trame
			//System.out.println(trames.get(1)); //afficher la 2eme trame
			PrintStream printStream = new PrintStream(file2);
            System.setOut(printStream);
 
				try {
					Ethernet eth = new Ethernet (trames.get(1));
					System.out.println(eth);
					
					
					IP ip = new IP(eth);
					System.out.println(ip);
					
					/* faire une vérification si protocole == tcp */
					if(ip.protocoleIsTcp()) {
						TCP tcp = new TCP(ip);
						System.out.println(tcp);
		
						/* faire une vérification si protocole == http */
						if(tcp.protocoleIsHttpRequest() || tcp.protocoleIsHttpResponse()) {
						
							try {
								HTTP http = new HTTP(tcp);
								System.out.println(http);
							}catch (InvalidTrameException ae) {
								System.out.println("HTTP INVALIDE !");
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			
			}catch (FileNotFoundException f) {
				f.printStackTrace();
			}	
		
	
		
		}
	}
