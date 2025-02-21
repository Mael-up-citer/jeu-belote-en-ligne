package src.main;

import src.main.Paquet.Carte;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;

import java.io.IOException;


/**
 * Classe abstraite représentant un joueur, qu'il soit humain ou un bot.
 */
public abstract class Joueur {
    protected Equipe equipe; // L'équipe à laquelle appartient le joueur
    protected String nom; // Le nom du joueur
    protected HashMap<Paquet.Carte.Couleur, List<Paquet.Carte>> main; // La main du joueur, organisée par couleur

    /**
     * Constructeur par défaut de la classe Joueur.
     */
    public Joueur() {
        this.nom = "Joueur inconnu";
        initMain();
    }

    /**
     * Constructeur de la classe Joueur avec un nom.
     *
     * @param nom Le nom du joueur.
     */
    public Joueur(String nom) {
        this.nom = nom;
        initMain();
    }

    /**
     * Initialise la main du joueur avec les couleurs de cartes disponibles.
     */
    private void initMain() {
        this.main = new HashMap<>();
        for (Paquet.Carte.Couleur couleur : Paquet.Carte.Couleur.values())
            main.put(couleur, new ArrayList<>());
    }

    // Vide les listes de cartes
    public void clearMain() {
        for (List<Carte> list : main.values())
            list.clear();
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
        Carte.Couleur key = carte.getCouleur();
        if (main.get(carte.getCouleur()) == null)
            throw new IllegalStateException("Erreur la clef: "+key+"    N'est pas défini ici");

            main.get(key).add(carte);
    }

    /**
     * Trie les cartes de la main du joueur par ordre croissant de valeur.
     */
    public void sortCard() {
        main.values().forEach(cartes -> Collections.sort(cartes, Comparator.comparingInt(Paquet.Carte::getNbPoint)));
    }

    /**
     * Méthode abstraite définissant l'action de jouer un tour.
     */
    public abstract Paquet.Carte jouer(Plis plis);

    /**
     * Méthode définissant l'action à réaliser pour choisir l'atout.
     */
    public abstract Paquet.Carte.Couleur parler(int tour);

    public HashMap<Paquet.Carte.Couleur, List<Paquet.Carte>> getMain() {
        return main;
    }

    public void setMain(HashMap<Paquet.Carte.Couleur, List<Paquet.Carte>> main) {
        this.main = main;
    }
}

/**
 * Classe représentant un joueur humain.
 */
class Humain extends Joueur {
    private Socket socket; // Socket pour la communication avec le client (non sérialisé)
    private PrintWriter out;
    private BufferedReader in;


    /**
     * Constructeur d'un joueur humain avec une connexion réseau.
     *
     * @param socket La socket utilisée pour la communication réseau.
     */
    public Humain(Socket socket, BufferedReader in, PrintWriter out) {
        super("Joueur inconnu");
        this.socket = socket;
    }

    /**
     * Constructeur d'un joueur humain avec un nom et une connexion réseau.
     *
     * @param nom    Le nom du joueur.
     * @param socket La socket utilisée pour la communication réseau.
     */
    public Humain(String nom, Socket socket, BufferedReader in, PrintWriter out) {
        super(nom);
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
 
    /**
     * Joue un tour en interagissant avec le client via le réseau.
     */
    @Override
    public Paquet.Carte.Couleur parler(int tour) {
        // Previens le clients qu'on attend qu'il donne un atout
        notifier("GetAtout"+tour+":$");

        // Récupère sa réponse sous forme d'une des valeurs de l'enum couleur ou 'Passer'
        String atout = waitForClient();

        if (atout.equals("Passer")) return null;    // Si le joueur ne prend pas

        try {   // Récupère la couleur
            Paquet.Carte.Couleur res = Paquet.Carte.Couleur.valueOf(atout.toUpperCase());
            res.setIsAtout(true);   // Défini que l'atout est cette couleur
            return res;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Joue un tour en interagissant avec le client via le réseau.
     */
    @Override
    public Paquet.Carte jouer(Plis plis) {
        // Previens le clients qu'on attend qu'il pose une carte
        notifier("Play:"+Rules.playable(plis, this));

        // Récupère sa réponse sous forme: "TypeDeCouleur"
        String cartePlaye = waitForClient();

       // Récupère la carte joué
        Paquet.Carte carte = Paquet.Carte.parseCarte(cartePlaye);

        System.out.println("Le joueur "+nom+"  a joué la carte "+carte);

        // L'enlève de la main
        removeCarte(carte);

        return carte;
    }

    // Supprime la carte c de la main du joueur
    private void removeCarte(Carte c) throws IllegalArgumentException {
        if (main.get(c.getCouleur()) == null)
            throw new IllegalArgumentException("Aucun carte de cette couleur n'hésiste");

        main.get(c.getCouleur()).remove(c);
    }

    public String waitForClient() {
        // Code bloquant pour attendre un message entrant du client
        try {
            // Lire une ligne du flux d'entrée
            String message = in.readLine();
            // Si le message reçu est nul, cela signifie que la connexion a été fermée par le client
            if (message == null) {
                System.err.println("Le client a fermé la connexion.");
                return null;  // Retourne null si le client a déconnecté
            }

            // Si un message est reçu, le retourner
            return message;
        } catch (IOException e) {
            // Capture l'exception d'entrée/sortie
            System.err.println("Erreur lors de la lecture du message du client.");
            e.printStackTrace();
        } catch (Exception e) {
            // Capture d'autres erreurs générales
            System.err.println("Une erreur inattendue s'est produite.");
            e.printStackTrace();
        }
        return null;  // Retourne null en cas d'erreur
    }    

    /**
     * Notifie le joueur avec un message.
     *
     * @param message Message à envoyer au joueur.
     */
    public void notifier(String message) {
        if (out != null)
            out.println(message);  // Envoie le message au client
        else
            System.err.println("Erreur: le flux de sortie est null.");
    }

    /**
     * Termine la connexion du joueur humain en fermant les flux et la socket.
     */
    public void endConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la fermeture de la connexion pour " + getNom());
        }
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

class BotFactory {
    private static final Map<String, Function<String, Joueur>> BOT_OF = new HashMap<>();

    static {
        BOT_OF.put("débutant", BotDebutant::new);
        BOT_OF.put("intermédiaire", BotMoyen::new);
        BOT_OF.put("expert", BotExpert::new);
    }

    /**
     * Crée un bot avec un nom personnalisé et un niveau spécifié.
     *
     * @param nom    Le nom du bot.
     * @param niveau Le niveau du bot.
     * @return Une instance du bot correspondant.
     */
    public static Joueur creeBot(String nom, String niveau) {
        // Extrait la fonction associé
        Function<String, Joueur> botSupplier = BOT_OF.get(niveau.toLowerCase());
        if (botSupplier == null)    // Si aucune ne correspond
            throw new IllegalArgumentException("Erreur, la difficulté: " + niveau + " n'est pas connue");

        return botSupplier.apply(nom);  // Retourne une nouvelle instance
    }
}


/**
 * Classe représentant un bot débutant.
 */
class BotDebutant extends Joueur {
    public BotDebutant(String nom) {
        super(nom);
    }

    @Override
    public Paquet.Carte.Couleur parler(int tour) {
        System.out.println(getNom() + " (Expert) parle.");
        return null;
    }

    @Override
    public Paquet.Carte jouer(Plis plis) {
        System.out.println(getNom() + " (Débutant) joue.");
        return Rules.playable(plis, this).get(0);
    }
}

/**
 * Classe représentant un bot intermédiaire.
 */
class BotMoyen extends Joueur {
    public BotMoyen(String nom) {
        super(nom);
    }

    @Override
    public Paquet.Carte jouer(Plis plis) {
        System.out.println(getNom() + " (Intermédiaire) joue.");
        return Rules.playable(plis, this).get(0);
    }

    @Override
    public Paquet.Carte.Couleur parler(int tour) {
        System.out.println(getNom() + " (Expert) parle.");
        return null;
    }
}

/**
 * Classe représentant un bot expert.
 */
class BotExpert extends Joueur {
    public BotExpert(String nom) {
        super(nom);
    }

    @Override
    public Paquet.Carte jouer(Plis plis) {
        System.out.println(getNom() + " (Expert) joue.");
        return Rules.playable(plis, this).get(0);
    }

    @Override
    public Paquet.Carte.Couleur parler(int tour) {
        System.out.println(getNom() + " (Expert) parle.");
        return null;
    }
}