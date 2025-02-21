package GUI.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.border.Border;

import GUI.Gui;
import main.EventManager;
import javafx.scene.transform.Scale;
import main.GameManager;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WindowsGameController extends Gui {
    private final String NAMEPUBLISH = "GameGui:Gui_response";

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
    private FlowPane AtoutPane;  // FlowPane contenant les boutons pour choisir l'atout
    @FXML                       // Par défaut désactivé
    private Button Coeur;
    @FXML
    private Button Pique;
    @FXML
    private Button Carreau;
    @FXML
    private Button Trefle;
    @FXML
    private Button Passer;  // Le bouton Passer


    // Contenaire des cartes de chaque joueurs
    @FXML
    private FlowPane CadrePlayer1;
    @FXML
    private FlowPane CadrePlayer2;
    @FXML
    private FlowPane CadrePlayer3;
    @FXML
    private FlowPane CadrePlayer4;


    @FXML
    private StackPane CardDump; // Contenaire des cartes d'une partie
    // Contenaire des images des cartes joué (1 plis = 1-4 cartes)
    @FXML
    private ImageView CardDumpImg1; // Image des cartes joué en 1er
    @FXML
    private ImageView CardDumpImg2; // Image des cartes joué en 2eme
    @FXML
    private ImageView CardDumpImg3; // Image des cartes joué en 3eme
    @FXML
    private ImageView CardDumpImg4; // Image des cartes joué en 4eme


    // Stocke le fond sombre quand le jeu attend les joueurs
    private Pane dimmingPane;

    // Le contrôleur a l'ID de jeu
    private static String idGame;
    // Débutet fin du chemin des images des cartes
    private final String prefix = "/images/cartes/";
    private final String suffix = ".png";

    // Nom de la carte du milieu durant le choix de l'atout
    private String nameMiddleCarte;

    private HashMap<String, MyImageView> deck = new HashMap<>();  // Map(nom-> image) des images correspondant à la main du joueur

    // Table de dispatching pour associer les commandes à leurs méthodes
    private final Map<String, Consumer<String>> COMMANDMAP = new HashMap<>();


    @FXML
    public void initialize() {
        GameManager gameManager = new GameManager();
        initializeCOMMANDMAP();

        EventManager.getInstance().subscribe("GameManager:message_received", (eventType, data) -> {
            if (data instanceof String) {
                String[] serveurResponse = ((String) data).split(":");
                Consumer<String> handler = COMMANDMAP.get(serveurResponse[0]);

                if (handler != null)
                    Platform.runLater(() -> handler.accept(serveurResponse[1]));
            }
        });

        applyDimmingEffect();  // On applique l'effet sombre ici

        // Associe la même méthode à tous les boutons
        Coeur.setOnAction(e -> handleButtonClick(Coeur));
        Pique.setOnAction(e -> handleButtonClick(Pique));
        Carreau.setOnAction(e -> handleButtonClick(Carreau));
        Trefle.setOnAction(e -> handleButtonClick(Trefle));
        Passer.setOnAction(e -> handleButtonClick(Passer));

        // Définir un message sur le nombre de joueurs
        idGameLabel.setText("id de la partie: " + idGame);
        nbPlayer.setText("en attente de joueurs");
    }

    /**
     * Applique l'effet d'assombrissement et désactive les interactions avec mainPane
     */
    private void applyDimmingEffect() {
        dimmingPane = new Pane();
        dimmingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        dimmingPane.prefWidthProperty().bind(mainPane.widthProperty());
        dimmingPane.prefHeightProperty().bind(mainPane.heightProperty());
    
        mainPane.getChildren().add(dimmingPane);
    
        mainPane.getChildren().remove(dialogPane);  // Retirer temporairement dialogPane
        mainPane.getChildren().add(dialogPane);     // Réajouter dialogPane au-dessus
    }    

    @FXML
    private void handleButtonClick(Button button) {
        // Récupére l'ID du bouton cliqué et publie l'évènement
        EventManager.getInstance().publish(NAMEPUBLISH, "AtoutChoisi:"+button.getId());

        // Désactive les buttons Après un clique
        handleAtoutButton(true);
    }

    /**
     * Retire l'effet d'assombrissement et réactive l'intéraction avec mainPane
     */
    private void onGameStart() {
        Platform.runLater(() -> {
            if (mainPane.getChildren().contains(dimmingPane))
                mainPane.getChildren().remove(dimmingPane);  // Supprime le fond sombre
    
            // Cacher les éléments de la boite de dialogue
            dialogPane.setVisible(false);
            idGameLabel.setVisible(false);
            nbPlayer.setVisible(false);
            progressIndicator.setVisible(false);
    
            dialogPane.setEffect(null);  // Supprime l'ombre
            mainPane.setMouseTransparent(false);  // Réactive les interactions
        });
    }       

    /**
     * Méthode d'initialisation de la table de dispatching. 
     * Elle associe chaque commande à une méthode de traitement correspondante.
     */
    private void initializeCOMMANDMAP() {
        COMMANDMAP.put("PlayerJoin", this::onPlayerJoin);
        COMMANDMAP.put("GameStart", unused -> onGameStart());
        COMMANDMAP.put("PlayerHand", this::dispPlayerHand);
        COMMANDMAP.put("SetMiddleCard",  this::dispMiddleCard);
        COMMANDMAP.put("GetAtout1", unused -> askAtout1());
        COMMANDMAP.put("GetAtout2", unused -> askAtout2());
        COMMANDMAP.put("AtoutIsSet", unused -> atoutIsSet());
        COMMANDMAP.put("ClearHand", unused -> ClearHand());
        COMMANDMAP.put("Play", this::play);
    }

    // Met à jour le label indiquant le nombre de joueur présent / celui attendu
    private void onPlayerJoin(String message) {
        nbPlayer.setText("nombre de joueurs: "+message);
    }

    // Affiche les cartes du clients
    // Affiche les cartes du client
    private void dispPlayerHand(String hand) {
        String[] cartes = hand.split(";");

        for (String name : cartes) {
            // Créer une instance de MyImageView
            MyImageView imageView = new MyImageView(name, prefix + name + suffix);

            // Ajouter la carte à la collection
            deck.put(name, imageView);
        }

        // Ajouter toutes les cartes affichées au cadre du joueur
        CadrePlayer1.getChildren().addAll(deck.values());
    }

    // Affiche la carte du milieu
    private void dispMiddleCard(String carte) {
        nameMiddleCarte = carte;    // Garde en mémoir le nom de la carte
        CardDumpImg1.setImage(new Image(getClass().getResource(prefix + carte + suffix).toExternalForm()));
    }

    // Active tout les buttons atouts
    private void askAtout1() {
        // Récupère la couleur de la carte et active le boutton associé
        switch (nameMiddleCarte.split("De")[1]) {
            case "COEUR":
            Coeur.setDisable(false);
                break;
            case "PIQUE":
                Pique.setDisable(false);
                break;
            case "TREFLE":
                Trefle.setDisable(false);
                break;
            case "CARREAU":
                Carreau.setDisable(false);
                break;
        
            default:
                break;
        }
        Passer.setDisable(false);   // Active le button passer
    }

    // Active tout les buttons atouts
    private void askAtout2() {
        // Active tous les buttons du choix de l'atout
        handleAtoutButton(false);
    }

    // Inverse l'etat d'activation des buttons du choix de l'atout
    private void handleAtoutButton(Boolean state) {
        Coeur.setDisable(state);
        Pique.setDisable(state);
        Carreau.setDisable(state);
        Trefle.setDisable(state);
        Passer.setDisable(state);
    }

    // Quand l'atout est set on désactive la pane du choix de l'atout
    private void atoutIsSet() {
        // 1. Désactive la pane du choix de l'atout
        AtoutPane.setVisible(false);

        // 2. Marque qui à pris

        // 3. Set le label atout

    }

    // Si personne ne prend on désafiche toutes les cartes
    private void ClearHand() {
        nameMiddleCarte = null;

        // 1. Enlève la carte du milieu
        CardDumpImg1.setImage(null);

        // 2. Enlève la main du joueur
        for (ImageView iv : deck.values())
            iv.setImage(null);

        CadrePlayer1.getChildren().clear();
        deck.clear();
    }

    private void play(String card) {
        // 1. Rendre cliquable les cartes jouables
        card = card.substring(1, card.length()-1);
    
        String[] str = card.split(", ");
    
        // Pour chaque carte jouable
        for(String carte : str)
            if (deck.containsKey(carte))
                deck.get(carte).activate(); // Active la carte avec la nouvelle méthode
    }    

    public static void setIdGame(String idG) {
        idGame = idG;
    }
}