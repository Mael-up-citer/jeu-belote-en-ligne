package GUI.Loby;

import main.ServerConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LobyGuiController {
    private static ServerConnection serverConnection;  // Connection avec le client

    @FXML
    private Label titleLabel; // Le label affichant "Bonjour choisissez entre :"

    @FXML
    private Label errorLabel; // Le label affichant "" pour les erreurs de connection (disable par défaut)

    @FXML
    private Button createGameButton; // Le bouton "Créer une partie"

    @FXML
    private Button joinGameButton; // Le bouton "Rejoindre une partie"

    @FXML
    private Button quitButton; // Le bouton "Quitter"


    public static void setServeurConnection(ServerConnection s) {
        serverConnection = s;
    }

    // Méthode d'initialisation, appelée quand le contrôleur est chargé
    @FXML
    public void initialize() {
        // Ajouter des gestionnaires d'événements pour chaque bouton
        createGameButton.setOnAction(event -> startNewGame());
        joinGameButton.setOnAction(event -> joinExistingGame());
        quitButton.setOnAction(event -> quitApplication());
    }

    // Méthode pour démarrer une nouvelle partie
    @FXML
    private void startNewGame() {
    }

    // Méthode pour rejoindre une partie existante
    @FXML
    private void joinExistingGame() {
    }

    // Méthode pour quitter l'application
    @FXML
    private void quitApplication() {
        System.out.println("cliquuuue");
        System.exit(0); // Cette ligne ferme l'application
    }
}