package GUI.Game;

import main.ServerConnection;

import java.util.ArrayList;

import main.ServerConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import GUI.Loby.CreateGameController;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;


public class WindowsGameController {
    private static ServerConnection serverConnection;
    private static Stage primaryStage;

    private String serveurResponse;

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
        
    }

    private void loadGameWindows() {
        
    }
    /**
     * Affiche un message provenant du serveur.
     * 
     * @param message Le message à afficher
     */
    public void handleServeurMessage(String message) {
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
}