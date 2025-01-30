package GUI.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import GUI.Gui;
import main.EventManager;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.control.ProgressIndicator;

public class WindowsGameController extends Gui {
    @FXML
    private Pane mainPane;  // Pane contenant tout

    // Elements de la boite de dialoque
    @FXML
    private Pane dialogPane;  // Pane contenant la boite de dialogue
    @FXML
    private Label idGameLabel;   // Affiche l'id de la game
    @FXML
    private Label nbPlayer;  // Affiche x/nbPlayer
    @FXML
    private ProgressIndicator progressIndicator;   // Affiche l'id de la game


    @FXML
    private Label moi;  // Label "moi"
    @FXML
    private Label labelJoueur2;  // Label "Joueur 2"
    @FXML
    private Label labelJoueur3;  // Label "Joueur 3"
    @FXML
    private Label labelJoueur4;  // Label "Joueur 4"


    @FXML
    private Label labelScoreEux;  // Label "0/1000" pour Eux
    @FXML
    private Label labelScoreNous;  // Label "0/1000" pour Nous


    @FXML
    private Label labelAtoutEnCour;  // Label "Atout en cours"


    @FXML
    private FlowPane buttonPane;  // FlowPane contenant les boutons
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button passeButton;  // Le bouton Passe
    
    // Le contrôleur a un ID de jeu
    private static String idGame;

    // Table de dispatching pour associer les commandes à leurs méthodes
    private final Map<String, Consumer<String>> COMMANDMAP = new HashMap<>();


    @FXML
    public void initialize() {
        initializeCOMMANDMAP();

        // S'abonner à l'événement "server_response" pour recevoir la réponse du serveur
        EventManager.getInstance().subscribe("server:message_received", (eventType, data) -> {
            if (data instanceof String) {
                // Diviser la chaîne en un tableau de mots
                String[] serveurResponse = ((String) data).split(" ");
                Consumer<String> handler = COMMANDMAP.get(serveurResponse[0]);

                // Si la commande est trouvée, l'exécuter
                if (handler != null)
                    Platform.runLater( () -> handler.accept(serveurResponse[1]));  // Appel de la méthode associée à la commande
            }
        });

        // Créer un fond assombri (un Pane transparent noir) qui couvre tout sauf dialogPane
        Pane dimmingPane = new Pane();
        dimmingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");  // Fond sombre mais transparent
        dimmingPane.prefWidthProperty().bind(mainPane.widthProperty());  // Ajuste la largeur du dimmingPane à la largeur de mainPane
        dimmingPane.prefHeightProperty().bind(mainPane.heightProperty()); // Ajuste la hauteur du dimmingPane à la hauteur de mainPane

        // Ajouter le dimmingPane à mainPane
        mainPane.getChildren().add(dimmingPane);

        // Réajouter dialogPane au-dessus du dimmingPane
        mainPane.getChildren().remove(dialogPane);  // Retirer dialogPane temporairement
        mainPane.getChildren().add(dialogPane);    // Réajouter dialogPane après le dimmingPane

        // Appliquer un fond semi-transparent à dialogPane
        dialogPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");  // Blanc opaque à 80%
    
        // Appliquer une ombre sur dialogPane pour le faire ressortir
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);  // Rayon de l'ombre
        dropShadow.setOffsetX(5);  // Décalage de l'ombre sur l'axe X
        dropShadow.setOffsetY(5);  // Décalage de l'ombre sur l'axe Y
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));  // Couleur de l'ombre (noir avec une opacité de 0.5)
        dialogPane.setEffect(dropShadow);  // Applique l'ombre sur dialogPane
    
        mainPane.setMouseTransparent(true);  // Désactive toute interaction avec mainPane

        // Définir un message sur le nombre de joueurs
        idGameLabel.setText("id de la partie: " + idGame);
        nbPlayer.setText("en attente de joueurs");
    }

    /**
     * Méthode d'initialisation de la table de dispatching. 
     * Elle associe chaque commande à une méthode de traitement correspondante.
     */
    public void initializeCOMMANDMAP() {
        COMMANDMAP.put("PlayerJoin", this::onPlayerJoin);
        //COMMANDMAP.put("DROP DATABASES", unused -> processDROPDATABASESCommand());
    }

    private void onPlayerJoin(String message) {
        nbPlayer.setText("nombre de joueurs: "+message);
    }

    public static void setIdGame(String idG) {
        idGame = idG;
    }
}