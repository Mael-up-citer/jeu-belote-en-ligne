package GUI.Loby;

import main.ServerConnection;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class LobyGui {

    private static ServerConnection serverConnection;
    private LobyGuiController controller;

    public LobyGui(Stage primaryStage) {
        try {
            // Charger le fichier FXML et lier le contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Loby/LobyGui.fxml"));
            
            // Charger le root pane et obtenir le contrôleur
            Pane root = loader.load();
            controller = loader.getController();  // Obtenir l'instance du contrôleur
            
            // Créer la scène avec le root node
            Scene scene = new Scene(root);
            primaryStage.setTitle("Lobby");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setServeurConnection(ServerConnection s) {
        serverConnection = s;
        LobyGuiController.setServeurConnection(serverConnection);
    }

    // Méthodes qui manipulent la GUI via le contrôleur

    public void showServerMessage(String message) {
        // Appeler le contrôleur pour afficher le message du serveur
        controller.displayServerMessage(message);
    }
    
    public void showConnectionError(String error) {
        // Appeler le contrôleur pour afficher l'erreur de connexion
        controller.displayConnectionError(error);
    }

    public void resetGui() {
        // Réinitialiser l'interface en cas de connexion réussie
        controller.resetGui();
    }
}