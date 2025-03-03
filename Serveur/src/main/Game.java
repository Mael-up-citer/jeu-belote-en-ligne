package src.main;


import src.main.Paquet.Carte.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


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

    private Plis[] plis;  // Represente les plis du jeu
    private int premierJoueur; // Index du joueur qui commence le tour
    private int indexDonne; // Index du joueur qui donne
    private int indexJoueurApris;   // Index du joueur qui  à pris dans le tableau joueurs
    private Paquet.Carte middleCard;  // Carte du milieu qui dirige l'atout

    // Contient toutes les cartes joué durant la partie
    private HashMap<Couleur, List<Paquet.Carte>> cartePlay = new HashMap<>();


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

        this.joueurs = new Joueur[] { 
            equipe1.getJ1(), 
            equipe2.getJ1(),
            equipe1.getJ2(), 
            equipe2.getJ2() 
        };

        this.paquet = new Paquet();
        premierJoueur = 1;
        indexDonne = 0;

        // Init la map
        for(Couleur c : Couleur.values()) cartePlay.put(c, new ArrayList<>());

        int nbPlis = paquet.getCartes().size() / NB_PLAYERS;
        plis = new Plis[nbPlis];
        // Init le tab avec des plis vide
        for (int i = 0; i < nbPlis; i++) plis[i] = new Plis();

        // Attends le chargement des UI Clients
        try{Thread.sleep(100);} catch(InterruptedException e) {}

        // Previens les humains que le jeu commence et leur envoie leur numero
        for (int i = 0; i < joueurs.length; i++){
            if (joueurs[i] instanceof Humain)
                ((Humain) joueurs[i]).notifier("GameStart:"+i);
        }
    }

    /**
     * Méthode principale pour gérer la partie.
     * Gère les tours et détermine les conditions de victoire.
     */
    @Override
    public void run() {
        try {
            while (!partieTerminee()) {
                huitPlis();
                indexDonne = (indexDonne+1) % joueurs.length; // Après chaque 8 plis on avance dans la donne
            }
        } catch (Exception e) {
            e.printStackTrace();
            endConnection();
        }
    }

    private void huitPlis() {
        int nbTour = 7;    // Chaque joueur va jouer 8 fois pour chaque plis

        // Distribuer les cartes au début de la partie
        distribuerNCartes(3, null);
        distribuerNCartes(2, null);
        for (Joueur joueur : joueurs) joueur.sortCard();

        transmiteClientHand();

        // 1. Définir l'atout
        Paquet.Carte.Couleur atout = chooseAtout();

        // Si on ne prend pas d'atout
        if (atout == null) return; // Quitte et recommence

        // Distribuer les cartes restantes
        distribuerNCartes(3, joueurs[indexJoueurApris]);
        for (Joueur joueur : joueurs) joueur.sortCard();

        shareLastCard();


        // 2. Jouer
        while (nbTour >= 0) {
            for (int i = premierJoueur; i < premierJoueur+NB_PLAYERS; i++) {
                Paquet.Carte carteJouee = joueurs[i%NB_PLAYERS].jouer(plis[plis.length - nbTour - 1]);
                // Met à jour l'affichage du millieu
                majAllClients("AddCardOnGame:"+carteJouee.toString());
                // Ajoute la carte joué à la map des cartes joué
                cartePlay.get(carteJouee.getCouleur()).add(carteJouee);
            }
            premierJoueur = (premierJoueur +plis[plis.length - nbTour].getWinner()) % joueurs.length;
            nbTour--;
        }

        // 3. Partage les scores avec les UI

        // 4.RAZ les variables de jeu
        // Reconstruit le paquet avec les plis
        paquet.addPlis(plis, equipes);
        // Reset les plis
        for (int i = 0; i < plis.length; i++)
            plis[i].reset();

        // 5. RAZ les GUI (la pile de cartes, ...)

    }

    // Détermine qu'elle sera l'atout du ces 8plis
    private Paquet.Carte.Couleur chooseAtout() {
        Paquet.Carte.Couleur atout = null;

        atout = tourAtout(1);

        if (atout != null) return atout;

        atout = tourAtout(2);

        if (atout != null) return atout;

        // 3. Personne ne prends
        resetParty();   // Recommence la partie

        return null;
    }

    private Paquet.Carte.Couleur tourAtout(int tour) {
        Paquet.Carte.Couleur atout = null;

        for (int i = indexDonne+1; i <= indexDonne+NB_PLAYERS; i++) {
            atout = joueurs[i%joueurs.length].parler(tour);
            // Dès qu'un joueur prend on quitte la boucle
            if (atout != null) {
                // Previens tout le monde que l'atout est définie
                majAllClients("AtoutIsSet:"+atout);
                indexJoueurApris = i%joueurs.length;
                return atout;
            }
        }
        return null;
    }

    /**
     * Distribue un certains nombres de cartes à chaque joueur.
     * 
     * @param n Le nombre de cartes à distribuer.
     * @param exception donne n-1 carte au joueur exception
     */
    private void distribuerNCartes(int n, Joueur exception) {
        int nbCarte;

        // Pour tout les joueurs
        for (int i = indexDonne+1; i <= indexDonne+NB_PLAYERS; i++) {
            if (joueurs[i%joueurs.length].equals(exception)) nbCarte = n-1;
            else nbCarte = n;

            // Donne nbCarte cartes à un joueur
            for (int j = 0; j < nbCarte; j++) {
                Paquet.Carte carte = paquet.getNext();
                joueurs[i%joueurs.length].addCard(carte);
            }
        }

        // Donne la carte du milieu au joueur qui a pris
        if (exception != null) exception.addCard(middleCard);
    }

    // Donne au clients leurs main et la carte du milieu
    private void transmiteClientHand() {
        middleCard = paquet.getNext();

        // Previens tout les humains en leur envoyant leur main
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain) {
                // Envoie au client sa main
                ((Humain) joueur).notifier("SetMain:"+joueur.getMain().toString());
                // Envoie au client la carte du milieu
                ((Humain) joueur).notifier("SetMiddleCard:"+middleCard.toString());
            }
        }
    }

    // Donne au clients leurs main et la carte du milieu
    private void shareLastCard() {
        // Previens tout les humains en leur envoyant leur main
        for (Joueur joueur : joueurs) {
            if (joueur instanceof Humain) {
                // Envoie au client sa main
                ((Humain) joueur).notifier("SetMain:null");
                // Envoie au client la carte du milieu
                ((Humain) joueur).notifier("SetMain:"+joueur.getMain().toString());
            }
        }
    }

    private void resetParty() {
        // 1. Reset toutes les mains
        for (Joueur joueur : joueurs)
            joueur.clearMain();

        // 2. Previens les humains
        for (Joueur joueur : joueurs)
            if (joueur instanceof Humain)
                ((Humain) joueur).notifier("SetMain:null");

        // 3. Coupe le paquet et remet l'index à 0
        paquet.coupe();
        paquet.RAZCurrentAcessIndex();

        // 4. Vider toutes les listes de cartes jouées mais garder les couleurs
        for (List<Paquet.Carte> cartes : cartePlay.values()) cartes.clear();
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