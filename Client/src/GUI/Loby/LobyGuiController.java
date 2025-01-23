package GUI.Loby;

import main.ServerConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LobyGuiController {

    private static ServerConnection serverConnection;

    @FXML
    private Label titleLabel; // Le label affichant "Bonjour, choisissez entre :"

    @FXML
    private Label errorLabel; // Le label affichant les erreurs de connexion

    @FXML
    private Button createGameButton; // Le bouton "Créer une partie"

    @FXML
    private Button joinGameButton; // Le bouton "Rejoindre une partie"

    @FXML
    private Button quitButton; // Le bouton "Quitter"

    /**
     * Définit la connexion au serveur pour le contrôleur.
     * 
     * @param s Une instance de ServerConnection
     */
    public static void setServeurConnection(ServerConnection s) {
        serverConnection = s;
    }

    /**
     * Méthode d'initialisation, appelée automatiquement lorsque le contrôleur est chargé.
     * Initialise l'état des composants graphiques.
     */
    @FXML
    public void initialize() {
        // Associer les actions aux boutons
        createGameButton.setOnAction(event -> startNewGame());
        joinGameButton.setOnAction(event -> joinExistingGame());
        quitButton.setOnAction(event -> quitApplication());

        // Initialiser l'état du label d'erreur : vide et invisible
        errorLabel.setVisible(false); // Caché
    }

    /**
     * Méthode pour démarrer une nouvelle partie.
     */
    @FXML
    private void startNewGame() {
        System.out.println("cree partie");
        serverConnection.sendToServer("hello world");
        // Ajouter la logique pour démarrer une nouvelle partie
    }

    /**
     * Méthode pour rejoindre une partie existante.
     */
    @FXML
    private void joinExistingGame() {
        System.out.println("Tentative de rejoindre une partie existante...");
        // Ajouter la logique pour rejoindre une partie
    }

    /**
     * Méthode pour quitter l'application.
     */
    @FXML
    private void quitApplication() {
        System.out.println("Fermeture de l'application...");
        System.exit(0); // Fermer l'application
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
        // Afficher le message d'erreur
        errorLabel.setText(error);
        errorLabel.setVisible(true); // Rendre le label visible

        // Désactiver les boutons
        createGameButton.setDisable(true);
        joinGameButton.setDisable(true);
    }

    /**
     * Réinitialise l'interface graphique après une connexion réussie.
     * Masque le label d'erreur et réactive les boutons.
     */
    public void resetGui() {
        // Masquer et effacer le label d'erreur
        errorLabel.setText(""); // Réinitialiser le texte
        errorLabel.setVisible(false); // Masquer le label

        // Réactiver les boutons
        createGameButton.setDisable(false);
        joinGameButton.setDisable(false);
    }
}