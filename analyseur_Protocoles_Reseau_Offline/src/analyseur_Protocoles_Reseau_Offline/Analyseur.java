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
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Analyseur extends Application {
	
	//chemins par defaut
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
    }
    
    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        // Set title for FileChooser
    	directoryChooser.setTitle("Select the destination folder to save (analyse.txt)");
 
        // Set Initial Directory
    	directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		final int width = 675;
		final int height = 720;
		final int layoutYButtons = 100;
		final int layoutYAnalyse = 155;
		
        final Pane container = new Pane();
        //container.setPadding(new Insets(20,20,20,20));
        container.setStyle("-fx-background-color: #cccccc; ");
        
        //label analyse
        Label analyseLabel = new Label(analyse);
        analyseLabel.setPadding(new Insets(layoutYAnalyse, 5, 5, 5));
        analyseLabel.setStyle("-fx-text-fill : #000000;");
        container.getChildren().add(analyseLabel);
        
        //label analyse
        Label label = new Label("please select a file to analyse");
        label.setPadding(new Insets(60, 5, 5, 5));
        label.setStyle("-fx-font-size: 1.5em; -fx-text-fill : #0000ff;");
        container.getChildren().add(label);
        
        Label titleLabel = new Label("Network Protocols Analyzer");
        titleLabel.setPadding(new Insets(5, 5, 5, 5));
        titleLabel.setMinWidth(width);
        titleLabel.setMinHeight(50);
        titleLabel.setStyle("-fx-font-size: 2.5em; -fx-background-color: black; -fx-font-weight: bold; -fx-text-fill : white;");
        titleLabel.setAlignment(Pos.CENTER);
        
        container.getChildren().add(titleLabel);
      
        // ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(container);
        
        // Pannable.
        scrollPane.setPannable(true);
        scrollPane.setPadding(new Insets(0));
        
		final FileChooser fileChooser = new FileChooser();
		configuringFileChooser(fileChooser);
		
        // Button 1
        Button button1= new Button("select file");
        button1.setLayoutX(5);
        button1.setLayoutY(layoutYButtons);
        button1.setPrefSize(200, 50);
        container.getChildren().add(button1);
        
        // event bouton selection file 
        button1.setOnAction(new EventHandler<ActionEvent>() {
        	 
        	@Override
            public void handle(ActionEvent event) {
        		
        		try {
        			Path path = Paths.get(fileChooser.showOpenDialog(new Stage()).toURI());
        			if(path != null) {
                    	inputFileName = path.toString();
                    	System.out.println("input file= "+inputFileName);
                    	label.setText("input file= "+inputFileName);
                    }
                    bouton1_is_set = true; //fichier ouvert
                	bouton2_is_set = false; //nouveau fichier non analysé
        		}
        		// si clique sur le bouton mais ne selectionne rien
        		catch(NullPointerException npe) {
        			//System.out.println("Error button1: "+npe.getMessage());
        		}
                
            }
        });
        
        
        // Button 2
        Button button2= new Button("start analyse");
        button2.setLayoutX(210);
        button2.setLayoutY(layoutYButtons);
        button2.setPrefSize(200, 50);
        container.getChildren().add(button2);
        
        // event bouton start analyse
        button2.setOnAction(new EventHandler<ActionEvent>() {
        	 
        	@Override
            public void handle(ActionEvent event) {
            	if(bouton1_is_set) {
            		analyse = analyser(inputFileName);
            		System.out.println(analyse);
            		analyseLabel.setText(analyse);
            		bouton2_is_set = true;
            	} else {
            		System.out.println("please select a file to analyse !");
            		label.setText("please select a file to analyse !");
            	}
            }
        });
        
		
        // Button 3
        Button button3= new Button("save analyse");
        button3.setLayoutX(415);
        button3.setLayoutY(layoutYButtons);
        button3.setPrefSize(200, 50);
        container.getChildren().add(button3);
        
        // event bouton selection file destination sauvegarde
        button3.setOnAction(new EventHandler<ActionEvent>() {
        	 
        	@Override
            public void handle(ActionEvent event) {
        		if(bouton2_is_set==false) { //si il n y a pas d'analyse
        			System.out.println("please analyse before saving !");
        			label.setText("please analyse before saving !");
        			return;
        		}
        		final DirectoryChooser dirChooser = new DirectoryChooser();
        		File selectedDirectory = dirChooser.showDialog(primaryStage);
        		
        		if(selectedDirectory == null){
        		     //No Directory selected
        		}else{
					//System.out.println("dest folder= "+selectedDirectory.getAbsolutePath());
					outputFileName = selectedDirectory.getAbsolutePath()+"/analyse.txt";
					System.out.println("dest file= "+outputFileName);
					label.setText("dest file= "+outputFileName);
					// redirection vers fichier et ecriture sur ce fichier
					File outputFile = new File(outputFileName);
					PrintStream console = System.out; //sauvegarde console
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
        
        //primaryStage.setResizable(false);
        primaryStage.setTitle("Network Protocols Analyzer");
        Scene scene = new Scene(scrollPane, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
		
	}

	public static String analyser(String ifn) {

		inputFileName = ifn;
		//String outputFileName = "data/analyse.txt";
		Trace trace;
		StringBuilder res = new StringBuilder();//sortie

		try {

			//File outputFile = new File(outputFileName);
			trace = new Trace(inputFileName);

			// creation des trames
			List<Trame> trames = Filtre.filtrer(trace);

			// affichage des trames (mettre en comment)
			/*
			 * for(Trame t : trames) { System.out.println(t); }
			 */
			
			
			// analyse des trames
			int cpt = 0;
			for (Trame t : trames) {
				//System.out.println("---------- TRAME " + (++cpt) + " ----------");
				res.append("\n*** TRAME " + (++cpt) + " ***");
				res.append("\n");

				Ethernet eth = new Ethernet(t);
				//System.out.println(eth);
				res.append(eth.toString());
				res.append("\n");

				/* faire une vérification si protocole == ip */
				if (eth.protocoleIsIP()) {
					
					IP ip = new IP(eth);
					//System.out.println(ip);
					res.append(ip);
					res.append("\n");

					/* faire une vérification si protocole == tcp */
					if (ip.protocoleIsTcp()) {
						
						TCP tcp = new TCP(ip);
						//System.out.println(tcp);
						res.append(tcp);
						res.append("\n");

						/* faire une vérification si protocole == http */
						if (tcp.protocoleIsHttpRequest() || tcp.protocoleIsHttpResponse()) {

							HTTP http = new HTTP(tcp);
							//System.out.println(http);
							res.append(http);
							res.append("\n");
						}
					}
				}
			}
			
		} catch (InvalidTrameException ite) {
			//System.out.println(ite.getMessage());
			res.append(ite.getMessage());
			res.append("\n");
			
		} catch (FileNotFoundException fnf) {
			System.out.println("Fichier introuvable !");
			res.append(fnf.getMessage());
			res.append("\n");

		} catch (IOException io) {
			System.out.println("Erreur lors de la lecture du fichier !");
			res.append(io.getMessage());
			res.append("\n");

		} catch (Exception e) {
			System.out.println("Erreur inconnue !");
			res.append(e.getMessage());
			res.append("\n");

		}
		finally {
			res.append("\n");
			return res.toString();
		}

	}

}
