package analyseur_Protocoles_Reseau_Offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trace {
	
	private String trace="" ; 
	
	public Trace(String filename) throws IOException {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line="" ;
			while ((line = br.readLine())!=null) {
				trace +=line;
				trace+=" \n";
			}
			br.close(); 
			} catch (Exception e) {
				throw new IOException("erreur lors de la lecture du fichier "); 
			}
	}
	
	public Trace() {
		trace = "";
	}
	
	public String getTrace() {
		return trace;
	}
	
	public String toString() {
		return trace;
	}
	
	public Trace copy() {
		Trace t = new Trace();
		t.trace = this.trace; // = this.trace possible
		return t;
	}
	
}

