package analyseur_Protocoles_Reseau_Offline;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Analyseur extends Application {

	// chemins par defaut
	static String inputFileName = "data/trace_test.txt";
	static String outputFileName = "data/analyse.txt";

	private boolean bouton1_is_set = false;
	private boolean bouton2_is_set = false;

	static String analyse;// = analyser();

	public static void main(String[] args) {

		launch(args);

	}

	private void configuringFileChooser(FileChooser fileChooser) {
		// Set title for FileChooser
		fileChooser.setTitle("Select Trame Files (.txt)");

		// Set Initial Directory
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		// Add Extension Filters
		fileChooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("TXT", "*.txt"));
	}

	private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
		// Set title for FileChooser
		directoryChooser.setTitle("Select the destination folder to save (analyse.txt)");

		// Set Initial Directory
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		final int width = 1280;
		final int height = 720;

		VBox root = new VBox();

		// label analyse
		Label label = new Label("please select a file to analyse");
		label.setPadding(new Insets(10));
		label.setStyle("-fx-font-size: 1.5em; -fx-text-fill : #0000ff; ");
		// label du titre
		Label titleLabel = new Label("Network Protocols Analyzer");
		// titleLabel.setPadding(new Insets(5, 5, 5, 5));

		HBox title = new HBox(titleLabel);
		title.setStyle(
				"-fx-font-size: 2.5em; -fx-background-color: grey; -fx-font-weight: bold; -fx-text-fill : green;");
		title.setAlignment(Pos.CENTER);

		// label analyse
		Label analyseLabel = new Label(analyse);
		analyseLabel.setPadding(new Insets(10, 5, 5, 5));
		analyseLabel.setStyle("-fx-text-fill : #000000; -fx-font-size: 1.2em;");

		// ScrollPane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(analyseLabel);

		// Pannable.
		scrollPane.setPannable(true);
		scrollPane.setPadding(new Insets(5));

		// Button 1
		Button button1 = new Button("Select file");
		button1.setPrefSize(200, 50);

		// event bouton selection file
		button1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				// filechooser pour button1
				final FileChooser fileChooser = new FileChooser();
				configuringFileChooser(fileChooser);

				try {
					Path path = Paths.get(fileChooser.showOpenDialog(new Stage()).toURI());
					if (path != null) {
						inputFileName = path.toString();
						System.out.println("input file= " + inputFileName);
						label.setText("Input file: " + inputFileName);
					}
					bouton1_is_set = true; // fichier ouvert
					bouton2_is_set = false; // nouveau fichier non analysé
				}
				// si clique sur le bouton mais ne selectionne rien
				catch (NullPointerException npe) {
					// System.out.println("Error button1: "+npe.getMessage());
				}

			}
		});

		// Button 2
		Button button2 = new Button("start analyse");
		button2.setPrefSize(200, 50);

		// event bouton start analyse
		button2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (bouton1_is_set) {
					analyse = analyser(inputFileName);
					System.out.println(analyse);
					analyseLabel.setText(analyse);
					bouton2_is_set = true;
				} else {
					System.out.println("please select a file to analyse !");
					label.setText("Please select a file to analyse !");
				}
			}
		});

		// Button 3
		Button button3 = new Button("Save analyse");
		button3.setPrefSize(200, 50);

		// event bouton selection file destination sauvegarde
		button3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (bouton2_is_set == false) { // si il n y a pas d'analyse
					System.out.println("please analyse before saving !");
					label.setText("Please analyse before saving !");
					return;
				}
				final DirectoryChooser dirChooser = new DirectoryChooser();
				File selectedDirectory = dirChooser.showDialog(primaryStage);
				configuringDirectoryChooser(dirChooser);

				if (selectedDirectory == null) {
					// No Directory selected
				} else {
					// System.out.println("dest folder= "+selectedDirectory.getAbsolutePath());
					outputFileName = selectedDirectory.getAbsolutePath() + "/analyse.txt";
					System.out.println("saved file= " + outputFileName);
					label.setText("saved file: " + outputFileName);

					// redirection vers fichier et ecriture sur ce fichier
					File outputFile = new File(outputFileName);
					PrintStream console = System.out; // sauvegarde console (facultatif)
					PrintStream printStream;

					try {
						printStream = new PrintStream(outputFile);
						System.setOut(printStream);
						System.out.println(analyse);

					} catch (FileNotFoundException e) {
						System.out.println("main: erreur lors de la sauvegarde du fichier !");
						label.setText("main: erreur lors de la sauvegarde du fichier !");
					}
				}

			}
		});

		// hbox pour les boutons
		HBox buttons = new HBox();
		buttons.getChildren().addAll(button1, button2, button3);
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(5);
		buttons.setStyle("-fx-font-size: 1.5em; -fx-background-color: grey; -fx-text-fill : green;");
		buttons.setPadding(new Insets(5));

		// mettre le tout dans la vbox principale
		root.getChildren().addAll(title, buttons, label, scrollPane);

		// window
		primaryStage.setTitle("Network Protocols Analyzer");
		Scene scene = new Scene(root, width, height);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static String analyser(String ifn) {

		inputFileName = ifn;
		Trace trace;
		StringBuilder res = new StringBuilder();// sortie

		List<Trame> trames = null;
		
		
			try {
				trace = new Trace(inputFileName);
				
				// creation des trames
				trames = Filtre.filtrer(trace);
				
				// affichage des trames (mettre en comment)
				/*
				 * for(Trame t : trames) { System.out.println(t); }
				 */
				
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Error when reading file");
			}




			
		
			

			// analyse des trames
			int cpt = 0;
			for (Trame t : trames) {
				
				try {
				
					// System.out.println("---------- TRAME " + (++cpt) + " ----------");
					res.append("\n*** TRAME " + (++cpt) + " ***");
					res.append("\n");
	
					Ethernet eth = new Ethernet(t);
					// System.out.println(eth);
					res.append(eth.toString());
					res.append("\n");
	
					/* faire une vérification si protocole == ip */
					if (eth.protocoleIsIP()) {
	
						IP ip = new IP(eth);
						// System.out.println(ip);
						res.append(ip);
						res.append("\n");
	
						/* faire une vérification si protocole == tcp */
						if (ip.protocoleIsTcp()) {
	
							TCP tcp = new TCP(ip);
							// System.out.println(tcp);
							res.append(tcp);
							res.append("\n");
	
							/* faire une vérification si protocole == http */
							if (tcp.protocoleIsHttpRequest() || tcp.protocoleIsHttpResponse()) {
	
								HTTP http = new HTTP(tcp);
								// System.out.println(http);
								res.append(http);
								res.append("\n");
							}
						}
					}
				
				
				} catch (InvalidTrameException ite) { 
					//ite.printStackTrace();
					System.out.println(ite.getMessage());
					res.append(ite.getMessage());
					res.append("\n");
	
				} catch (FileNotFoundException fnf) {
					fnf.printStackTrace();
					System.out.println("Fichier introuvable !");
					res.append(fnf.getMessage());
					res.append("\n");
	
				} catch (IOException io) {
					io.printStackTrace();
					System.out.println("Erreur lors de la lecture du fichier !");
					res.append(io.getMessage());
					res.append("\n");
	
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Erreur inconnue !");
					res.append(e.getMessage());
					res.append("\n");
	
				} finally {
					res.append("\n");
					//return res.toString();
				}
					
				
			}/////fin for

			return res.toString();

	}

}
