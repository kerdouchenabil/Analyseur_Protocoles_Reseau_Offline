package analyseur_Protocoles_Reseau_Offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trace {

	private String trace = "";

	public Trace(String filename) throws IOException {
		
		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = br.readLine()) != null) {
				trace += line;
				trace += " \n";
			}

		} catch (Exception e) {
			throw new IOException("Erreur lors de la lecture du fichier !");
		} finally {
			br.close();
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
		t.trace = this.trace;
		return t;
	}

}
