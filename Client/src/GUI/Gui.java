package GUI;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

/**
 * Classe abstraite Gui qui gère le chargement et la navigation entre les scènes de l'interface utilisateur.
 */
public abstract class Gui {
    private static Stage primaryStage; // Fenêtre principale de l'application

    /**
     * Charge une nouvelle scène en fonction du fichier FXML donné et retourne son contrôleur.
     *
     * @param file     Nom du fichier FXML à charger.
     * @param <T>      Type du contrôleur qui doit étendre Gui.
     * @return         Instance du contrôleur correspondant au fichier FXML.
     */
    public static <T extends Gui> T loadScene(String file) {
        FXMLLoader loader = new FXMLLoader(Gui.class.getResource(file));
        Pane root = null;
        T controller = null;

        try {
            root = loader.load(); // Charge le FXML
            controller = loader.getController(); // Récupère le contrôleur du FXML
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAndExit("Impossible de charger la scène : " + file);
        }

        if (primaryStage != null) {
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        }

        return controller;
    }

    /**
     * Définit la fenêtre principale de l'application.
     *
     * @param stage Stage principal
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Affiche une alerte en cas d'erreur et ferme l'application.
     *
     * @param message Message d'erreur
     */
    private static void showErrorAndExit(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Platform.exit();
                    System.exit(0);
                }
            });
        });
    }
}