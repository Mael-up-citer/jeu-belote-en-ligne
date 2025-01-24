package main;

import GUI.Loby.LobyGuiController;
import main.EventManager; // Assurez-vous d'importer EventManager
import main.ServerConnection; // Assurez-vous d'importer EventManager

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;


public class App extends Application {

    private static final int MAX_RETRIES = 5; // Nombre maximal de tentatives de connexion
    private static final int RETRY_INTERVAL_MS = 3000; // Intervalle de réessai (en ms)
    private static int retryCount = 0; // Compteur des tentatives de connexion

    private static ServerConnection serverConnection; // Singleton de la connexion au serveur
    private static EventManager eventManager; // EventManager nécessaire pour la connexion
    private LobyGuiController lobyGuiController; // Contrôleur du lobby

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Créer l'instance d'EventManager
        eventManager = new EventManager(); 

        // Charger le FXML et récupérer le contrôleur
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Loby/lobyGui.fxml"));
        AnchorPane root = loader.load();
        lobyGuiController = loader.getController();

        // Obtenir l'instance de ServerConnection avec l'EventManager
        serverConnection = ServerConnection.getInstance(eventManager);

        LobyGuiController.setServeurConnection(serverConnection);
        LobyGuiController.setStage(primaryStage);

        // Configurer le listener pour recevoir des événements du serveur
        serverConnection.setListener(new ServerConnection.ConnectionListener() {
            @Override
            public void onServerMessage(String message) {
                Platform.runLater(() -> lobyGuiController.displayServerMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                Platform.runLater(() -> lobyGuiController.displayConnectionError(error));
            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> lobyGuiController.displayServerMessage("Déconnecté du serveur."));
            }
        });

        // Créer la scène et l'afficher
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lobby du jeu");
        primaryStage.show();

        // Tenter de se connecter au serveur
        attemptConnection();
    }

    /**
     * Tente de se connecter au serveur avec des réessais en cas d'échec.
     */
    private void attemptConnection() {
        if (serverConnection.connect())
            // Si la connexion réussit, réinitialiser la GUI
            Platform.runLater(() -> lobyGuiController.resetGui());
        else {
            // Prévient la gui que la connexion a échoué
            Platform.runLater(() -> lobyGuiController.displayConnectionError("impossible de ce connecter au serveur"));
            retryCount++;
            if (retryCount >= MAX_RETRIES)
                // Si le nombre maximal de tentatives est atteint, afficher une alerte
                showConnectionFailureAlert();
            else {
                // Réessayer après un intervalle défini
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        attemptConnection();
                    }
                }, RETRY_INTERVAL_MS);
            }
        }
    }

    /**
     * Affiche une alerte si la connexion échoue après plusieurs tentatives.
     */
    private void showConnectionFailureAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR,
                    "Le nombre maximum de tentatives de connexion a été atteint. Abandon de la connexion.",
                    ButtonType.OK);
            alert.setTitle("Erreur de Connexion");
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
}