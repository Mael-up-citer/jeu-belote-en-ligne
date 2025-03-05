package GUI.Game;


import GUI.Gui;
import main.GameManager;
import main.EventManager;


import javafx.fxml.FXML;

import javafx.application.Platform;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;

import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import javafx.scene.input.MouseEvent;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.transform.Scale;
import javafx.geometry.Point2D;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.util.Duration;



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
    private FlowPane Mycadre;
    @FXML
    private FlowPane leftCadre;
    @FXML
    private FlowPane frontCadre;
    @FXML
    private FlowPane rightCadre;
    // Map qui lie le pane d'un joueur à son numéro pour rendre le jeu rélaliste
    private Map<Integer, FlowPane> joueurDispatch;


    @FXML
    private Pane CardDump; // Contenaire des cartes d'une partie
    // Contenaire des images des cartes joué (1 plis = 1-4 cartes)
    @FXML
    private ImageView CardDumpImg1; // Image des cartes joué en 1er
    @FXML
    private ImageView CardDumpImg2; // Image des cartes joué en 2eme
    @FXML
    private ImageView CardDumpImg3; // Image des cartes joué en 3eme
    @FXML
    private ImageView CardDumpImg4; // Image des cartes joué en 4eme

    private ArrayList<ImageView> cardDumpImg;   // List de toutes les cardDump
    private int indexCardDump = 0;

    // Stocke le fond sombre quand le jeu attend les joueurs
    private Pane dimmingPane;

    // Le contrôleur a l'ID de jeu
    private static String idGame;
    // Débutet fin du chemin des images des cartes
    private final String prefix = "/images/cartes/";
    private final String suffix = ".png";

    // Nom de la carte du milieu durant le choix de l'atout
    private String nameMiddleCarte;

    Map<String, MyImageView> deck = new LinkedHashMap<>();

    // Table de dispatching pour associer les commandes à leurs méthodes
    private final Map<String, Consumer<String>> COMMANDMAP = new HashMap<>();

    private int noPlayer = 0; // Numéro du joueur
    private int noFirstPlayer;  // Numéro du 1er joueur a jouer dans ce tour


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

        // Initialise la map de dispatch
        joueurDispatch = new HashMap<>();
        joueurDispatch.put(noPlayer, Mycadre);
        joueurDispatch.put((noPlayer+1)%4, leftCadre);
        joueurDispatch.put((noPlayer+2)%4, frontCadre);
        joueurDispatch.put((noPlayer+3)%4, rightCadre);
        noFirstPlayer = 1; // Le tout 1er joueur est le 1er

        // Initialisation de la liste pour contenir toutes les images des cartes jouer au fils des tours
        cardDumpImg = new ArrayList<>();
        cardDumpImg.add(CardDumpImg1);
        cardDumpImg.add(CardDumpImg2);
        cardDumpImg.add(CardDumpImg3);
        cardDumpImg.add(CardDumpImg4);

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

        EventManager.getInstance().publish(NAMEPUBLISH, "RESUME:$");
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
    private void onGameStart(String noPlayer) {
        try {
            this.noPlayer = Integer.parseInt(noPlayer);
        } catch (Exception e) {
        }

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
        COMMANDMAP.put("GameStart", this::onGameStart);
        COMMANDMAP.put("PlayerHand", this::dispPlayerHand);
        COMMANDMAP.put("SetMiddleCard",  this::dispMiddleCard);
        COMMANDMAP.put("GetAtout1", unused -> askAtout1());
        COMMANDMAP.put("GetAtout2", unused -> askAtout2());
        COMMANDMAP.put("AtoutIsSet",  this::atoutIsSet);
        COMMANDMAP.put("ClearHand", unused -> ClearHand());
        COMMANDMAP.put("Play", this::play);
        COMMANDMAP.put("AddCardOnGame", this::addCardOnGame);
    }

    // Met à jour le label indiquant le nombre de joueur présent / celui attendu
    private void onPlayerJoin(String message) {
        nbPlayer.setText("nombre de joueurs: "+message);
    }

    // Affiche les cartes du clients
    private void dispPlayerHand(String hand) {
        String[] cartes = hand.split(";");

        for (String name : cartes) {
            // Créer une instance de MyImageView
            MyImageView imageView = new MyImageView(name, prefix + name + suffix);
            imageView.setParentPane(Mycadre);

            // Ajouter la carte à la collection
            deck.put(name, imageView);
        }

        // Ajouter toutes les cartes affichées au cadre du joueur
        Mycadre.getChildren().addAll(deck.values());
    }

    // Affiche la carte du milieu
    private void dispMiddleCard(String carte) {
        nameMiddleCarte = carte.split("De")[1];    // Garde en mémoir le nom de la carte
        CardDumpImg2.setImage(new Image(getClass().getResource(prefix + carte + suffix).toExternalForm()));
    }

    // Active tout les buttons atouts
    private void askAtout1() {
        handleOneAtoutButton(nameMiddleCarte, false);
        Passer.setDisable(false);   // Active le button passer
    }

    // Active tout les buttons atouts
    private void askAtout2() {
        // Active tous les buttons du choix de l'atout
        handleAtoutButton(false);
        handleOneAtoutButton(nameMiddleCarte, true);
    }

    // Inverse l'etat d'activation des buttons du choix de l'atout
    private void handleAtoutButton(Boolean state) {
        Coeur.setDisable(state);
        Pique.setDisable(state);
        Carreau.setDisable(state);
        Trefle.setDisable(state);
        Passer.setDisable(state);
    }

    private void handleOneAtoutButton(String name, Boolean state) {
        // Agit sur le boutton name
        switch (nameMiddleCarte) {
            case "COEUR":
            Coeur.setDisable(state);
                break;
            case "PIQUE":
                Pique.setDisable(state);
                break;
            case "TREFLE":
                Trefle.setDisable(state);
                break;
            case "CARREAU":
                Carreau.setDisable(state);
                break;
        
            default:
                break;
        }
    }

    // Quand l'atout est set on désactive la pane du choix de l'atout
    private void atoutIsSet(String noPlayer) {
        // 1. Désactive la pane du choix de l'atout
        AtoutPane.setVisible(false);

        // 2. Affiche les cartes de dos des ennemis
        Image imageDos = new Image(getClass().getResource(prefix + "dos" + suffix).toExternalForm()); // Chargement unique
        int nombreCartes = deck.size() + 3;

        for (Pane pane : joueurDispatch.values()) {
            List<ImageView> images = new ArrayList<>(nombreCartes);
            for (int i = 0; i < nombreCartes; i++) {
                images.add(creerImageView(imageDos));
            }
            pane.getChildren().addAll(images); // Ajout en lot (meilleur perf)
        }

        // 3. Marque qui à pris

        // 4. Set le label atout
        
    }

    // Factory method pour éviter la duplication du code
    private ImageView creerImageView(Image image) {
        ImageView img = new ImageView(image);
        img.setFitWidth(60);
        img.setFitHeight(125);
        img.setPreserveRatio(true);
        return img;
    }

    // Si personne ne prend on désafiche toutes les cartes
    private void ClearHand() {
        nameMiddleCarte = null;

        // 1. Enlève la carte du milieu
        CardDumpImg2.setImage(null);

        // 2. Enlève la main du joueur
        for (ImageView iv : deck.values())
            iv.setImage(null);

        Mycadre.getChildren().clear();
        deck.clear();
    }

    private void play(String card) {
        // 1. Rendre cliquable les cartes jouables
        card = card.substring(1, card.length()-1);
    
        String[] str = card.split(", ");
    
        // Pour chaque carte jouable
        for(String carte : str)
            if (deck.containsKey(carte))
                deck.get(carte).activate(); // Active la carte
    }   

    /**
     * Affiche et anime la carte jouée en la déplaçant depuis la main du joueur
     * vers la position de l'image correspondante dans le dépôt.
     *
     * La conversion des coordonnées est effectuée pour que la carte aille exactement
     * vers cardDumpImg.get(indexCardDump).
     *
     * @param carteJouer le nom (ou identifiant) de la carte jouée
     */
    private void addCardOnGame(String carteJouer) {
        // Récupérer le FlowPane correspondant au joueur actuel
        FlowPane mainJoueur = joueurDispatch.get(noFirstPlayer);

        // Création d'une ImageView temporaire pour l'animation
        ImageView carteAnimee = new ImageView(new Image(getClass().getResource(prefix + carteJouer + suffix).toExternalForm()));
        carteAnimee.setFitWidth(60);
        carteAnimee.setFitHeight(125);
        carteAnimee.setPreserveRatio(true);

        // Positionner la carte au niveau du joueur (en supposant que mainJoueur et mainPane sont dans le même système de coordonnées)
        double startX = mainJoueur.getLayoutX() + mainJoueur.getWidth() / 2;
        double startY = mainJoueur.getLayoutY() + mainJoueur.getHeight() / 2;
        carteAnimee.setLayoutX(startX);
        carteAnimee.setLayoutY(startY);
        mainPane.getChildren().add(carteAnimee);

        // Convertir la position de la cible (cardDumpImg) dans le système de coordonnées de mainPane
        Point2D targetScene = cardDumpImg.get(indexCardDump).localToScene(0, 0);
        Point2D targetInMainPane = mainPane.sceneToLocal(targetScene);

        double targetX = targetInMainPane.getX();
        double targetY = targetInMainPane.getY();

        // Calculer le décalage entre la position de départ et la position cible
        double deltaX = targetX - carteAnimee.getLayoutX();
        double deltaY = targetY - carteAnimee.getLayoutY();

        // Animation de déplacement vers la position cible
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), carteAnimee);
        transition.setToX(deltaX);
        transition.setToY(deltaY);

        // Une fois l'animation terminée, mettre à jour l'affichage définitif et envoyer le message RESUME après une pause éventuelle
        transition.setOnFinished(event -> {
            // Met à jour l'image définitive dans le dépôt
            cardDumpImg.get(indexCardDump).setImage(
                new Image(getClass().getResource(prefix + carteJouer + suffix).toExternalForm())
            );
            mainPane.getChildren().remove(carteAnimee); // Supprime l'animation temporaire
            indexCardDump++;

            if (indexCardDump == 4) {
                // Si c'est la 4ème carte, attendre 800ms avant de vider le dépôt et publier RESUME
                PauseTransition pause = new PauseTransition(Duration.millis(700));
                pause.setOnFinished(e -> {
                    clearCardDump();
                    EventManager.getInstance().publish(NAMEPUBLISH, "RESUME:$");
                });
                pause.play();
            }
            else {
                // Pour les autres cartes, une courte pause de 50ms avant de publier RESUME
                PauseTransition courtePause = new PauseTransition(Duration.millis(50));
                courtePause.setOnFinished(e -> {
                    EventManager.getInstance().publish(NAMEPUBLISH, "RESUME:$");
                });
                courtePause.play();
            }
        });

        // Met à jour le numéro du prochain joueur
        noFirstPlayer = (noFirstPlayer + 1) % 4;
        transition.play();
    }

    // Nettoie le dépôt de carte
    private void clearCardDump() {
        for (int i = 0; i < cardDumpImg.size(); i++)
            cardDumpImg.get(i).setImage(null);

        indexCardDump = 0;
    }



    public static void setIdGame(String idG) {
        idGame = idG;
    }
}