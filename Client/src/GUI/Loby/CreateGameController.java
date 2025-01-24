package GUI.Loby;

import main.ServerConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CreateGameController {

    private static ServerConnection serverConnection;

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
}