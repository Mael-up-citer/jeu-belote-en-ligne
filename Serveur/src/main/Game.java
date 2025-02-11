package src.main;

import java.util.Arrays;

/**
 * Classe représentant une partie de jeu.
 * Implémente Runnable pour permettre l'exécution en parallèle.
 * Attention le caractère séparateur est le ':'
 */
public class Game implements Runnable {
    private static final int NB_PLAYERS = 4;
    private final String gameId; // Identifiant unique de la partie
    private final Equipe[] equipes; // Tableau des équipes participant à la partie (taille fixe : 2)
    private final Joueur[] joueurs; // Tableau des joueurs (taille fixe : 4)
    private final Paquet paquet; // Le paquet de cartes

    private int premierJoueur; // Index du joueur qui commence le tour
    private int nextCarte;   // Index de la prochaine carte du paquet

    /**
     * Constructeur de la classe Game.
     * 
     * @param id      Identifiant unique de la partie.
     * @param equipe1 La première équipe.
     * @param equipe2 La deuxième équipe.
     */
    public Game(String id, Equipe equipe1, Equipe equipe2) {
        this.gameId = id;
        this.equipes = new Equipe[] { equipe1, equipe2 };

        // Créer un tableau fixe de joueurs
        this.joueurs = new Joueur[] { 
            equipe1.getJ1(), 
            equipe1.getJ2(), 
            equipe2.getJ1(), 
            equipe2.getJ2() 
        };

        this.paquet = new Paquet();
        premierJoueur = 0;
        nextCarte = 0;

        try{Thread.sleep(100);} catch(InterruptedException e) {}
        majAllClients("GameStart:$");
    }

    /**
     * Méthode principale pour gérer la partie.
     * Gère les tours et détermine les conditions de victoire.
     */
    @Override
    public void run() {
        while (!partieTerminee())
            huitPlis();

        // Fin de la partie
        endConnection();
    }

    private void huitPlis() {
        int taillePli = 8;    // Chaque joueur va jouer 8 fois pour chaque plis

        // Distribuer les cartes au début de la partie
        distribuerNCartes(3);
        distribuerNCartes(2);

        for (Joueur joueur : joueurs)
            joueur.sortCard();
  

        // Previens tout les humains en leur envoyant leur main
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain) {
                System.out.println(joueur.getMain().toString());
                // Envoie au client sa main
                ((Humain) joueur).notifier("SetMain:"+joueur.getMain().toString());
                // ENvoie au client la carte du milieu
                ((Humain) joueur).notifier("SetMiddleCard:"+getFirstCard());
            }
        }

        // 1. Définir l'atout
        Paquet.Carte.Couleur atout = chooseAtout();

        // Si on ne prend pas d'atout
        if (atout == null)
            return; // Quitte et recommence

        // 2. Jouer
        while (taillePli-- > 0) {
            for (int i = premierJoueur; i < premierJoueur+NB_PLAYERS; i++) {
                joueurs[i%NB_PLAYERS].jouer();
            }
        }
    }

    // Détermine qu'elle sera l'atout du ces 8plis
    private Paquet.Carte.Couleur chooseAtout() {
        Paquet.Carte.Couleur atout = null;

        // 1. tour 1
        for (int i = premierJoueur; i < premierJoueur+NB_PLAYERS; i++) {
            atout = joueurs[i].parler(1);
            // Dès qu'un joueur prend on quitte la boucle
            if (atout != null)
                break;
        }

        if (atout == null) {
            // 2. tour 2
            for (int i = premierJoueur; i < premierJoueur+NB_PLAYERS; i++) {
                atout = joueurs[i].parler(2);
                // Dès qu'un joueur prend on quitte la boucle
                if (atout != null)
                    break;
            }
     
            if (atout == null)
                // 3. Personne ne prends
                resetParty();
        }
        return atout;
    }

    /**
     * Distribue un certains nombres de cartes à chaque joueur.
     * 
     * @param n Le nombre de cartes à distribuer.
     */
    private void distribuerNCartes(int n) {
        // Pour tout les joueurs
        for (Joueur joueur : joueurs) {
            // Donne n cartes à un joueur
            for (int i = 0; i < n; i++) {
                if (nextCarte < paquet.size()) {
                    Paquet.Carte carte = paquet.get(nextCarte++);
                    joueur.addCard(carte);
                }
                else
                    throw new IndexOutOfBoundsException("Erreur: carte "+nextCarte+"/"+paquet.size()+" inexistente");
            }
        }
    }

    // Retourne la carte qui sera placé au centre pour choisir l'atout
    private String getFirstCard() {
        return paquet.get(nextCarte).toString();
    }

    private void resetParty() {
        // 1. Reset toutes les mains
        for (Joueur joueur : joueurs)
                joueur.setMain(null);

        // 2. Previens les humains
        for (Joueur joueur : joueurs)
            if (joueur instanceof Humain)
                ((Humain) joueur).notifier("SetMain:null");
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
     * Envoie un meme message à tous les joueurs humains.
     */
    private void majAllClients(String message) {
        for (Joueur joueur : joueurs)
            if (joueur instanceof Humain)
                ((Humain) joueur).notifier(message);
    }

    /**
     * Notifie tous les joueurs humains sauf un.
     * 
     * @param exclu Joueur à exclure.
     */
    private void majAllExceptOneClient(Joueur exclu, String message) {
        for (Joueur joueur : joueurs)
            if (joueur instanceof Humain && !joueur.equals(exclu))
                ((Humain) joueur).notifier(message);
    }

    /**
     * Ferme les connexions des joueurs humains.
     */
    private void endConnection() {
        for (Joueur joueur : joueurs)
            if (joueur instanceof Humain)
                ((Humain) joueur).endConnection();
    }
}