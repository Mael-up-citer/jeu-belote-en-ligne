package main;

import GUI.Loby.LobyGui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

    private static final int MAX_RETRIES = 5; // Nombre maximal de tentatives avant d'afficher l'erreur
    private static final int RETRY_INTERVAL_MS = 3000; // Intervalle de réessai toutes les 3 secondes
    private static int retryCount = 0;  // Compteur des tentatives

    private static ServerConnection serverConnection; // Référence Singleton
    private LobyGui lobyGui; // Déclare lobyGui comme variable d'instance

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        lobyGui = new LobyGui(primaryStage); // Initialiser lobyGui ici

        // Obtenir l'instance Singleton de ServerConnection
        serverConnection = ServerConnection.getInstance();

        // Configurer le ConnectionListener
        serverConnection.setListener(new ServerConnection.ConnectionListener() {
            @Override
            public void onServerMessage(String message) {
                Platform.runLater(() -> lobyGui.showServerMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                System.err.println("Erreur de connexion : " + error);
                Platform.runLater(() -> lobyGui.showConnectionError(error));
            }

            @Override
            public void onDisconnected() {
                System.out.println("Déconnecté du serveur.");
                Platform.runLater(() -> lobyGui.showServerMessage("Déconnecté du serveur."));
            }
        });

        primaryStage.setTitle("Lobby du jeu");

        // Tentative de connexion immédiate
        attemptConnection();

        // Créer et afficher la scène
        primaryStage.show();
    }

    /**
     * Tente de se connecter au serveur et redemande une tentative en cas d'échec.
     * Lorsque la connexion est réussie, elle passe la référence à la GUI.
     */
    private void attemptConnection() {
        // Tenter de se connecter
        if (serverConnection.connect()) {
            // Si la connexion réussie, passer la référence du serveur à la GUI
            Platform.runLater(() -> {
                LobyGui.setServeurConnection(serverConnection);
                // Réinitialiser la GUI (désactiver le label d'erreur et réactiver les composants)
                lobyGui.resetGui();
            });
        } else {
            retryCount++;
            // Si le nombre max d'essaie est atteint
            if (retryCount >= MAX_RETRIES)
                showConnectionFailureAlert();   // Abandonner et notifier
            else {
                // Si la connexion échoue, réessayer toutes les X secondes
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Tenter à nouveau de se connecter
                        attemptConnection();
                    }
                }, RETRY_INTERVAL_MS);
            }
        }
    }

    /**
     * Affiche une alerte d'échec de connexion après un certain nombre de tentatives.
     */
    private void showConnectionFailureAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR, 
                "Le nombre maximum de tentatives de connexion a été atteint. Abandon de la connexion.", 
                ButtonType.OK);
            alert.setTitle("Erreur de Connexion");
            alert.setHeaderText(null);  // Pas de texte d'en-tête
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Ferme l'application si l'utilisateur clique sur OK
                    Platform.exit();
                    System.exit(0);
                }
            });
        });
    }
}