package GUI.Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import main.EventManager;
import java.util.ArrayList;
import java.util.List;


public class MyImageView extends ImageView {
    private final String cardName;
    private static final List<MyImageView> activeCards = new ArrayList<>(); // Liste des cartes activées


    public MyImageView(String cardName, String imagePath) {
        super(new Image(MyImageView.class.getResource(imagePath).toExternalForm()));
        this.cardName = cardName;

        // Configuration de base
        this.setFitWidth(60);
        this.setFitHeight(125);
        this.setPreserveRatio(true);

        // Effet de grossissement au survol
        Scale scale = new Scale(1, 1);
        this.getTransforms().add(scale);

        this.setOnMouseEntered(event -> {
            scale.setX(1.3);
            scale.setY(1.3);
        });

        this.setOnMouseExited(event -> {
            scale.setX(1);
            scale.setY(1);
        });

        // Désactiver l'interaction au départ
        this.setMouseTransparent(true);

        // Gestion du clic sur la carte
        this.setOnMouseClicked(event -> {
            if (!this.isMouseTransparent()) {
                // Désactiver toutes les cartes activées
                disableAllActiveCards();

                // Envoyer l'événement
                EventManager.getInstance().publish("GameGui:Gui_response", "CardPlay:" + cardName);
            }
        });
    }

    // Désactiver toutes les cartes activées
    private void disableAllActiveCards() {
        for (MyImageView card : activeCards) {
            card.setMouseTransparent(true);
        }
        activeCards.clear(); // Vider la liste après désactivation
    }

    // Activer une carte et l'ajouter à la liste des cartes activées
    public void activate() {
        this.setMouseTransparent(false);
        activeCards.add(this);
    }
}