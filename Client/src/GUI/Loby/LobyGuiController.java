package GUI.Loby;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.io.IOException;

/**
 * Contrôleur pour l'interface graphique du lobby.
 * Gère l'affichage des messages du serveur, la connexion et les interactions des boutons.
 */
public class LobyGuiController {

    private static ServerConnection serverConnection;
    private static CreateGameController createGameController;
    private static Stage primaryStage;

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
        // Associer les actions aux boutons
        createGameButton.setOnAction(event -> startNewGame());
        joinGameButton.setOnAction(event -> joinExistingGame());
        quitButton.setOnAction(event -> quitApplication());

        // Initialiser l'état du label d'erreur : vide et invisible
        errorLabel.setVisible(false); // Caché au départ
    }

    /**
     * Méthode pour démarrer une nouvelle partie.
     */
    @FXML
    private void startNewGame() {
        FXMLLoader loader = null;
        AnchorPane root = null;
        try {
            // Charger le FXML et récupérer le contrôleur
            loader = new FXMLLoader(getClass().getResource("/GUI/Loby/createGame.fxml"));
            root = loader.load();
        } catch (IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR,
                        "impossible de charger la suite",
                        ButtonType.OK);
                alert.setTitle("Erreur de chargement");
                alert.setHeaderText(null); // Pas d'en-tête
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Quitter l'application si l'utilisateur clique sur OK
                        Platform.exit();
                        System.exit(0);
                    }
                });
            });
        }
        createGameController = loader.getController();
        CreateGameController.setServeurConnection(serverConnection);

        serverConnection.setListener(new ServerConnection.ConnectionListener() {
            @Override
            public void onServerMessage(String message) {
                Platform.runLater(() -> createGameController.displayServerMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                Platform.runLater(() -> createGameController.displayConnectionError(error));
            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> createGameController.displayServerMessage("Déconnecté du serveur."));
            }
        });
        // Créer la scène et l'afficher
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Création d'équipe");
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
        serverConnection.disconnect();
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
     * Affiche un message d'erreur de connexion dans le label et désactive les boutons.
     * 
     * @param error Le message d'erreur à afficher
     */
    public void displayConnectionError(String error) {
        // Afficher le message d'erreur dans le label
        errorLabel.setText(error);
        errorLabel.setVisible(true); // Rendre le label visible

        // Désactiver les boutons pour éviter toute interaction avec le serveur
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

        // Réactiver les boutons pour permettre à l'utilisateur d'interagir
        createGameButton.setDisable(false);
        joinGameButton.setDisable(false);
    }
}