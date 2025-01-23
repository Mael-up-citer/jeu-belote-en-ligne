package GUI.Loby;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

public class LobyGui {

    public LobyGui(Stage primaryStage) {
        try {
            // Charger le fichier FXML et lier le contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Loby/LobyGui.fxml"));

            // Charger la scène
            Pane root = loader.load(); // Ce qui charge le FXML et retourne le root node
            
            // Créer la scène avec le root node (ici un Pane)
            Scene scene = new Scene(root);
            
            // Définir la scène et le titre de la fenêtre
            primaryStage.setTitle("Lobby");
            primaryStage.setScene(scene);
            
            // Afficher la fenêtre
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}