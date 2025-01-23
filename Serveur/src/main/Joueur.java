package src.main;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Classe abstraite représentant un joueur, qu'il soit humain ou un bot.
 */
public abstract class Joueur {
    private Equipe equipe; // L'équipe à laquelle appartient le joueur
    private String nom; // Le nom du joueur
    private HashMap<Paquet.Carte.Couleur, ArrayList<Paquet.Carte>> main; // La main du joueur, organisée par couleur

    /**
     * Constructeur de la classe Joueur.
     * 
     */
    public Joueur() {
        initMain();
    }

    /**
     * Constructeur de la classe Joueur.
     * 
     * @param nom Le nom du joueur.
     */
    public Joueur(String nom) {
        this.nom = nom;
        initMain();
    }

    private void initMain() {
        this.main = new HashMap<>();

        // Initialise la main
        for (Paquet.Carte.Couleur couleur : Paquet.Carte.Couleur.values())
            main.put(couleur, new ArrayList<>());
    }

    /**
     * Retourne le nom du joueur.
     * 
     * @return Le nom du joueur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne l'équipe du joueur.
     * 
     * @return L'équipe du joueur.
     */
    public Equipe getEquipe() {
        return equipe;
    }

    /**
     * Définit l'équipe du joueur.
     * 
     * @param equipe L'équipe à assigner au joueur.
     */
    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    /**
     * Ajoute une carte à la main du joueur.
     * 
     * @param carte La carte à ajouter.
     */
    public void addCard(Paquet.Carte carte) {
        main.get(carte.getCouleur()).add(carte);
    }

    /**
     * Trie les cartes de la main du joueur par ordre croissant de valeur.
     */
    public void sortCard() {
        for (ArrayList<Paquet.Carte> cartes : main.values())
            cartes.sort(Comparator.comparingInt(Paquet.Carte::getNbPoint)); // Trier par valeur croissante
    }

    /**
     * Méthode abstraite définissant l'action de jouer un tour.
     */
    public abstract void jouer();
}

/**
 * Classe représentant un joueur humain.
 */
class Humain extends Joueur {
    transient private Socket socket; // Socket pour la communication avec le client qu'on ne séréalise pas

    /**
     * Constructeur de la classe Humain.
     * 
     * @param nom    Le nom du joueur.
     * @param socket La socket pour la communication réseau.
     */
    public Humain(Socket socket) {
        this.socket = socket;
    }

    /**
     * Constructeur de la classe Humain.
     * 
     * @param nom    Le nom du joueur.
     * @param socket La socket pour la communication réseau.
     */
    public Humain(String nom, Socket socket) {
        super(nom);
        this.socket = socket;
    }

    /**
     * Joue un tour en interagissant avec le client via le réseau.
     */
    @Override
    public void jouer() {
        System.out.println("Humain " + getNom() + " joue son tour.");
        // Logique pour recevoir une action via le socket
    }

    public void notifier(String s){
    }

    public void endConnection(){
    }

    /**
     * Retourne la socket associée au joueur humain.
     * 
     * @return La socket du joueur.
     */
    public Socket getSocket() {
        return socket;
    }
}

/**
 * Classe représentant un bot.
 */
class Bot extends Joueur {

    /**
     * Constructeur de la classe Bot.
     * 
     * @param nom Le nom du bot.
     */
    public Bot(String nom) {
        super(nom);
    }

    /**
     * Joue un tour automatiquement.
     */
    @Override
    public void jouer() {
        System.out.println("Bot " + getNom() + " joue automatiquement.");
        // Implémenter une logique automatisée ici
    }
}