package GUI.Loby;

import main.ServerConnection;

import java.util.ArrayList;

import main.ServerConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import GUI.Loby.LobyGuiController;
import main.EventManager; // Assurez-vous d'importer EventManager

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;


import java.io.IOException;


public class CreateGameController {
    private static final int NBPLAYER = 4; // Nombre joueurs
    private static ServerConnection serverConnection;
    private static Stage primaryStage;

    @FXML
    private ComboBox selectNbHumain; // La combo box pour choisir le nombre d'humains

    @FXML
    private Button joueurButton; // Button représentant le joueur

    @FXML
    private FlowPane poolPane;  // Pane qui doit contenir tout les Joueurs des equipes qui seront a répartir

    @FXML
    private VBox Equipe1;  // Pane qui doit contenir les Joueurs de l'equipe 1

    @FXML
    private VBox Equipe2;  // Pane qui doit contenir les Joueurs de l'equipe 2

    @FXML
    private Label errorLabel; // Le label affichant les erreurs

    @FXML
    private Button validateButton; // Le bouton pour valider la configuration

    @FXML
    private Button retournButton; // Le bouton "Retour"

    private ArrayList<ComboBox> botBox = new ArrayList<>(); // Les combos box pour choisir le niveau des IA
    private ArrayList<Button> humainButton = new ArrayList<>(); // Les combos box pour choisir le niveau des IA

    /**
     * Définit la connexion au serveur pour le contrôleur.
     * 
     * @param s Une instance de ServerConnection
     */
    public static void setServeurConnection(ServerConnection s) {
        serverConnection = s;
    }

    /**
     * Définit la connexion au serveur pour le contrôleur.
     * 
     * @param s Une instance de ServerConnection
     */
    public static void setStage(Stage s) {
        primaryStage = s;
    }

    /**
     * Méthode d'initialisation, appelée automatiquement lorsque le contrôleur est chargé.
     * Initialise l'état des composants graphiques.
     */
    @FXML
    public void initialize() {
        // Ajout des choix à la ComboBox
        selectNbHumain.getItems().addAll("1", "2", "3");
        retournButton.setOnAction(event -> goBack());
        selectNbHumain.setOnAction(event -> displayAll());

        //TODO maquer les composants
    }

    /**
     * Affiche un message provenant du serveur.
     * 
     * @param message Le message à afficher
     */
    public void displayServerMessage(String message) {
        System.out.println("Message du serveur : " + message);
    }

    /**
     * Affiche un message d'erreur de connexion.
     * 
     * @param error Le message d'erreur à afficher
     */
    public void displayConnectionError(String error) {
    }

    /**
     * Réinitialise l'interface graphique après une connexion réussie.
     * Masque le label d'erreur et réactive les boutons.
     */
    public void resetGui() {
    }

    private void goBack() {
        System.out.println("retour a l'ancienne scene");
    }

    private void displayAll() {
        int nbHumains = Integer.parseInt((String)selectNbHumain.getValue());
        System.out.println("nb humains = "+nbHumains);
        // TODO Rendre affichable tout le reste

        // Crée les Bouttons des humains
        for (int i = 1; i < nbHumains; i++) {
            Button btn = new Button("Joueur"+i);
            //btn.setOnMouseClick(e -> makeDragAble());
            humainButton.add(btn);
            poolPane.getChildren().add(btn);
        }
        // Crée les comboBox des Bots
        for (int i = nbHumains; i < NBPLAYER; i++) {
            ComboBox CBox = new ComboBox();
            CBox.getItems().addAll("Débutant", "Intermédiaire", "Expert");
            CBox.setValue("bot"+i);
            //CBox.setOnMouseClick(e -> makeDragAble());
            botBox.add(CBox);
            poolPane.getChildren().add(CBox);
        }
        // Class pour rendre dragAble les composants
    }
}