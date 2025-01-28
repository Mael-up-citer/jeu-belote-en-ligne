package GUI.Loby;

import main.EventManager; // Assurez-vous d'importer EventManager

import main.ServerConnection;
import GUI.Game.WindowsGameController;
import GUI.Loby.LobyGuiController;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.IOException;

public class JoinGameController {
    private static ServerConnection serverConnection;
    private static WindowsGameController windowsGameController;
    private static Stage primaryStage;

    @FXML
    private TextField idGameText; // Zone de saisie de l'id d'une partie

    @FXML
    private Button validateButton;

    @FXML
    private Button returnButton;

    @FXML
    private Label errorLabel;   // Affiche la réponse du serveur en cas de problème

    @FXML
    private ProgressIndicator loadIndicator; // Icone de chargement

    // Utilisation du singleton EventManager
    private static EventManager eventManager = EventManager.getInstance();

    // Task pour attendre la réponse du serveur
    private Task<Void> attendreReponseTask;

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

    @FXML
    public void initialize() {
        loadIndicator.setVisible(false);

        // S'abonner à l'événement "server_response" pour recevoir la réponse du serveur
        eventManager.subscribe("server:message_received", (eventType, data) -> {
            if (data instanceof String) {
                String serveurResponse = (String) data;

                // Signaler la réponse dans l'UI (appeler un update UI via Platform.runLater)
                Platform.runLater(() -> {
                    loadIndicator.setVisible(false);
                    if (serveurResponse.startsWith("Erreur"))
                        errorLabel.setText(serveurResponse);
                    else
                        loadGameWindows();
                });
            }
        });

        validateButton.setOnAction(e -> {
            if (idGameText.getText() == null || idGameText.getText().isEmpty())
                errorLabel.setText("Erreur : le champ est vide");
            else {
                serverConnection.sendToServer("join_game, " + idGameText.getText());
                loadIndicator.setVisible(true);

                // Utilisation d'un Task pour attendre la réponse du serveur
                attendreReponseTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        // Attente active pour que la réponse arrive (bloquer ce thread)
                        while (true) {
                            Thread.sleep(100);  // Attendre un peu avant de vérifier la réponse
                        }
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(() -> {
                            loadIndicator.setVisible(false);
                        });
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        Platform.runLater(() -> {
                            loadIndicator.setVisible(false);
                            errorLabel.setText("Une erreur est survenue lors de la connexion au serveur.");
                        });
                    }
                };

                // Lancer le task dans un thread séparé
                Thread thread = new Thread(attendreReponseTask);
                thread.setDaemon(true);
                thread.start();
            }
        });

        returnButton.setOnAction(e -> goBack());
    }

    private void loadGameWindows() {
        FXMLLoader loader = null;
        AnchorPane root = null;
        try {
            // Charger le FXML et récupérer le contrôleur
            loader = new FXMLLoader(getClass().getResource("/GUI/Game/game.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR,
                        "impossible de charger la suite",
                        ButtonType.OK);
                alert.setTitle("Erreur de chargement");
                alert.setHeaderText(null); // Pas d'en-tête
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Quitte l'application si l'utilisateur clique sur OK
                        Platform.exit();
                        System.exit(0);
                    }
                });
            });
        }
        windowsGameController = loader.getController();
        WindowsGameController.setServeurConnection(serverConnection);
        WindowsGameController.setStage(primaryStage);

        serverConnection.setListener(new ServerConnection.ConnectionListener() {
            @Override
            public void onServerMessage(String message) {
                Platform.runLater(() -> windowsGameController.handleServeurMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                Platform.runLater(() -> windowsGameController.displayConnectionError(error));
            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> windowsGameController.handleServeurMessage("Déconnecté du serveur."));
            }
        });
        // Créer la scène et l'afficher
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rejoindre une équipe");
    }

    /**
     * Affiche un message provenant du serveur.
     * 
     * @param message Le message à afficher
     */
    public void handleServeurMessage(String message) {
        System.out.println(message);
    }

    /**
     * Affiche un message d'erreur de connexion.
     * 
     * @param error Le message d'erreur à afficher
     */
    public void displayConnectionError(String error) {
        // Vous pouvez traiter les erreurs de connexion ici si nécessaire
    }

    /**
     * Réinitialise l'interface graphique après une connexion réussie.
     * Masque le label d'erreur et réactive les boutons.
     */
    public void resetGui() {
        // Réinitialisez l'interface graphique si nécessaire
    }

    private void goBack() {
        // Récupérer l'instance unique de EventManager via le singleton
        eventManager = EventManager.getInstance(); 
        try {
            // Charge le FXML et récupérer le contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Loby/lobyGui.fxml"));
            AnchorPane root = loader.load();

            // Obtenir l'instance de ServerConnection avec l'EventManager
            serverConnection = ServerConnection.getInstance(eventManager);

            // Crée la scène et l'afficher
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Lobby du jeu");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}