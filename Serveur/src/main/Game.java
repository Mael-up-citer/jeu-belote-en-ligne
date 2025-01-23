package src.main;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe représentant une partie de jeu.
 * Implémente Runnable pour permettre l'exécution en parallèle.
 */
public class Game implements Runnable {
    private final String id; // Identifiant unique de la partie
    private final Equipe[] equipes; // Tableau des équipes participant à la partie
    private final Joueur[] joueurs; // Tableau des joueurs (taille fixe : 4)
    private final Paquet paquet; // Le paquet de cartes

    private int premierJoueur; // Index du joueur qui commence le tour
    private boolean enCours; // Indique si la partie est en cours

    /**
     * Constructeur de la classe Game.
     * 
     * @param id      Identifiant unique de la partie.
     * @param equipe1 La première équipe.
     * @param equipe2 La deuxième équipe.
     */
    public Game(String id, Equipe equipe1, Equipe equipe2) {
        this.id = id;
        this.equipes = new Equipe[] { equipe1, equipe2 };

        // Créer un tableau fixe de joueurs (ordre important pour les règles)
        this.joueurs = new Joueur[] { 
            equipe1.getJ1(), 
            equipe1.getJ2(), 
            equipe2.getJ1(), 
            equipe2.getJ2() 
        };

        this.paquet = new Paquet();
        this.premierJoueur = 0;
    }

    /**
     * Distribue un certain nombre de cartes à chaque joueur.
     * 
     * @param n Le nombre de cartes à distribuer.
     */
    public void distribuerCartes(int n) {
        int index = 0; // Index de la carte actuelle dans le paquet

        for (Joueur joueur : joueurs) {
            for (int i = 0; i < n; i++) {
                if (index < paquet.size()) {
                    Paquet.Carte carte = paquet.get(index++);
                    joueur.addCard(carte);
                }
            }
        }
    }

    /**
     * Méthode principale pour gérer la partie.
     * Gère les tours et détermine les conditions de victoire.
     */
    @Override
    public void run() {
        // Distribuer les cartes au début de la partie
        distribuerCartes(3);
        distribuerCartes(2);

        for (Joueur joueur : joueurs)
            joueur.sortCard();

        while (!partieTerminee()) {
            for (Joueur joueur : joueurs) {
                joueur.jouer();
            }
        }

        // Fin de la partie
        endConnection();
    }

    /**
     * Vérifie si la partie est terminée.
     * 
     * @return true si une équipe atteint le score cible.
     */
    private boolean partieTerminee() {
        return Arrays.stream(equipes)
                .anyMatch(equipe -> equipe.getScore() >= 1000);
    }

    /**
     * Notifie tous les joueurs humains.
     */
    private void MajAllClients() {
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain) {
                ((Humain) joueur).notifier("Mise à jour de votre situation.");
            }
        }
    }

    /**
     * Notifie tous les joueurs humains sauf un.
     * 
     * @param exclu Joueur à exclure.
     */
    private void MajAllExceptOneClient(Joueur exclu) {
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain && joueur != exclu) {
                ((Humain) joueur).notifier("Mise à jour de votre situation.");
            }
        }
    }

    /**
     * Ferme les connexions des joueurs humains.
     */
    private void endConnection() {
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain) {
                ((Humain) joueur).endConnection();
            }
        }
    }
}