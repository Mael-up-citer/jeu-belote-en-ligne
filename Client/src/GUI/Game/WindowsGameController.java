package GUI.Game;

import GUI.Gui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.control.ProgressIndicator;

public class WindowsGameController extends Gui {
    @FXML
    private Pane mainPane;  // Pane contenant tout

    // Elements de la boite de dialoque
    @FXML
    private Pane dialogPane;  // Pane contenant la boite de dialogue
    @FXML
    private Label idGameLabel;   // Affiche l'id de la game
    @FXML
    private Label nbPlayer;  // Affiche x/nbPlayer
    @FXML
    private ProgressIndicator progressIndicator;   // Affiche l'id de la game


    @FXML
    private Label moi;  // Label "moi"
    @FXML
    private Label labelJoueur2;  // Label "Joueur 2"
    @FXML
    private Label labelJoueur3;  // Label "Joueur 3"
    @FXML
    private Label labelJoueur4;  // Label "Joueur 4"


    @FXML
    private Label labelScoreEux;  // Label "0/1000" pour Eux
    @FXML
    private Label labelScoreNous;  // Label "0/1000" pour Nous


    @FXML
    private Label labelAtoutEnCour;  // Label "Atout en cours"


    @FXML
    private FlowPane buttonPane;  // FlowPane contenant les boutons
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button passeButton;  // Le bouton Passe
    
    // Le contrôleur a un ID de jeu
    private static String idGame;

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        // Appliquer un effet de réduction de la luminosité sur tout le fond

        // Initialiser le FlowPane central avec un indicateur de chargement
        // Ajoute l'indicateur au FlowPane
        progressIndicator.setVisible(true);

        // Définir un message sur le nombre de joueurs
        nbPlayer.setText("1/4 joueurs connectés");

        
    }

    public static void setIdGame(String idG) {
        idGame = idG;
    }
}