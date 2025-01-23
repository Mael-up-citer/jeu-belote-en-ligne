package src.test;

import src.main.Paquet.Carte;
import src.main.Joueur;
import src.main.Paquet;
import src.main.Paquet.Carte;
import src.main.Paquet.Carte.Couleur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JoueurTest {

    private Joueur humain;
    private Joueur bot;

    @BeforeEach
    void setUp() {
        humain = new Humain();
        bot = new Bot();
    }

    @Test
    void testInitialisationCarte() {
        // Vérifie que la map cartes est bien initialisée pour chaque couleur
        HashMap<Paquet.Carte.Couleur, ArrayList<Paquet.Carte>> jeuHumain = humain.getJeu();
        HashMap<Paquet.Carte.Couleur, ArrayList<Paquet.Carte>> jeuBot = bot.getJeu();

        assertNotNull(jeuHumain, "La map de cartes pour l'humain ne devrait pas être nulle.");
        assertNotNull(jeuBot, "La map de cartes pour le bot ne devrait pas être nulle.");

        for (Paquet.Carte.Couleur couleur : Paquet.Carte.Couleur.values()) {
            assertNotNull(jeuHumain.get(couleur), "La liste des cartes pour la couleur " + couleur + " ne devrait pas être nulle.");
            assertNotNull(jeuBot.get(couleur), "La liste des cartes pour la couleur " + couleur + " ne devrait pas être nulle.");
            assertTrue(jeuHumain.get(couleur).isEmpty(), "La liste des cartes pour la couleur " + couleur + " devrait être vide au départ.");
            assertTrue(jeuBot.get(couleur).isEmpty(), "La liste des cartes pour la couleur " + couleur + " devrait être vide au départ.");
        }
    }

    @Test
    void testNom() {
        // Vérifie que le nom est correctement initialisé et récupéré
        assertNull(humain.getNom(), "Le nom du joueur humain devrait être nul par défaut.");
        assertNull(bot.getNom(), "Le nom du joueur bot devrait être nul par défaut.");
    }

    @Test
    void testAddCard() {
        // Vérifie que l'ajout d'une carte fonctionne correctement
        Paquet.Carte carte = new Paquet.Carte(Paquet.Carte.Couleur.ROUGE, 5);
        humain.addCard(carte);

        ArrayList<Paquet.Carte> cartesRouges = humain.getColorCard(Paquet.Carte.Couleur.ROUGE);
        assertNotNull(cartesRouges, "La liste des cartes rouges ne devrait pas être nulle.");
        assertEquals(1, cartesRouges.size(), "La liste des cartes rouges devrait contenir une carte.");
        assertEquals(carte, cartesRouges.get(0), "La carte ajoutée devrait être présente dans la liste.");
    }

    @Test
    void testGetColorCard() {
        // Vérifie que la méthode getColorCard retourne la bonne liste
        ArrayList<Paquet.Carte> cartesBleues = humain.getColorCard(Paquet.Carte.Couleur.BLEU);
        assertNotNull(cartesBleues, "La liste des cartes bleues ne devrait pas être nulle.");
        assertTrue(cartesBleues.isEmpty(), "La liste des cartes bleues devrait être vide par défaut.");
    }
}