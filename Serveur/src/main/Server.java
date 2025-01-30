package src.main;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Serveur de jeu permettant à plusieurs joueurs de se connecter, de créer des parties,
 * et de jouer en équipe. Gère les connexions des joueurs, la création de parties, et
 * la répartition des joueurs entre équipes.
 */
public class Server {
    private static final int PORT = 12345; // Port d'écoute
    private static final int MAX_GAMES = 4; // Nombre maximum de parties simultanées
    private static final int MAX_PLAYERS = 4; // Nombre maximum de joueurs par partie
    private static final Map<String, Paire<Integer, List<Socket>>> gameQueue = new HashMap<>(); // File d'attente des joueurs par partie

    private static Map<String, Game> games = new HashMap<>(); // Liste des parties en cours
    private static int gameCounter = 0; // Compteur pour générer des identifiants de partie

    /**
     * Méthode principale lançant le serveur pour écouter les connexions des clients.
     *
     * @param args Arguments de la ligne de commande.
     * @throws IOException Si une erreur réseau survient.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Serveur lancé sur le port " + PORT);

        // Boucle principale pour accepter les connexions des clients
        while (true) {
            Socket clientSocket = serverSocket.accept();
            // Démarrer un thread pour gérer chaque client connecté
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    /**
     * Classe interne pour gérer la communication avec un client.
     */
    static class ClientHandler implements Runnable {
        private Socket socket;  // Connexion socket avec le client
        private String playerName; // Nom du joueur (éventuellement utilisé pour l'identité)
        private String gameId; // ID de la partie à laquelle le joueur appartient
        private PrintWriter out; // Sortie pour envoyer des messages au client

        /**
         * Constructeur initialisant le gestionnaire avec le socket du client.
         *
         * @param socket Socket représentant la connexion au client.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Méthode exécutée lorsque le thread démarre. Gère les messages reçus du client.
         */
        @Override
        public void run() {
            // Crée les flux d'entrées et de sortie du client
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                this.out = out; // Initialiser le flux de sortie
                String input;

                // Boucle pour traiter les commandes envoyées par le client
                while ((input = in.readLine()) != null) {
                    if (input.startsWith("create_game")) {
                        String[] str = input.split(";");
                        ArrayList<String> equipe2 = new ArrayList<>();
                        ArrayList<String> equipe1 = new ArrayList<>();
                        int numberOfHumans = 0;

                        // Remplie l'equipe 1
                        for (int i = 1; i < 3; i++) {
                            String joueur;
                            if (str[i].startsWith("humain")) {
                                joueur = str[i];
                                numberOfHumans++;
                            }
                            else {
                                joueur = str[i].split(",")[1];
                            }
                            equipe1.add(joueur);
                        }
                        // Remplie l'equipe 2
                        for (int i = 3; i < str.length; i++) {
                            String joueur;
                            if (str[i].startsWith("humain")) {
                                joueur = str[i];
                                numberOfHumans++;
                            }
                            else {
                                joueur = str[i].split(",")[1];
                            }
                            equipe2.add(joueur);
                        }
                        createGame(numberOfHumans, equipe1, equipe2, out);
                    }
                    else if (input.startsWith("join_game")) {
                        // Commande pour rejoindre une partie existante
                        String[] parts = input.split(" ");

                        // Regarde si la commande est bien formée
                        if (parts.length == 2)
                            joinGame(parts[1], out);
                        else
                            out.println("Erreur: Mauvais format de commande.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Crée une nouvelle partie avec un certain nombre de joueurs humains et met en attente
         * les connexions jusqu'à ce que tous les joueurs soient prêts.
         *
         * @param numberOfHumans Nombre de joueurs humains dans la partie.
         * @param out Flux de sortie pour envoyer des messages au client.
         */
        private void createGame(int numberOfHumans, ArrayList<String> equipe1,  ArrayList<String> equipe2, PrintWriter out) {
            System.out.println("create game\n");
            // Vérifie que le nombre de partie maximal n'est pas atteint
            if (gameQueue.size() >= MAX_GAMES) {
                out.println("Erreur: Limite de parties atteinte.");
                return;
            }

            // Générer un identifiant unique pour la partie
            String gameId = "game_" + (++gameCounter);

            // Initialiser la file d'attente pour cette partie
            gameQueue.put(gameId, new Paire<>(numberOfHumans, new ArrayList<>()));
            out.println(gameId);    // ENvoie le gameId au client

            // Attendre que le nombre de joueurs requis soit atteint
            synchronized (gameQueue) {
                while (gameQueue.get(gameId).getSecond().size() < numberOfHumans) {
                    try {
                        gameQueue.wait(); // Mettre en pause jusqu'à ce que d'autres joueurs rejoignent
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Une fois le nombre atteint, créer la partie
                createGameInstance(gameId, numberOfHumans, equipe1, equipe2, out);
            }
        }

        /**
         * Crée une instance de partie avec des équipes équilibrées et démarre la partie.
         *
         * @param gameId ID de la partie à créer.
         * @param numberOfHumans Nombre de joueurs humains.
         * @param out Flux de sortie pour informer le créateur de la partie.
         */
        private void createGameInstance(String gameId, int numberOfHumans, ArrayList<String> eq1, ArrayList<String> eq2, PrintWriter out) {
            System.out.println("ciiiiiiiiiiiiiiiii\n\n");
            // Récupère le socket des clients de cette partie
            List<Socket> players = gameQueue.get(gameId).getSecond();

            // Création des deux équipes
            Equipe equipe1 = new Equipe();
            Equipe equipe2 = new Equipe();

            // Ajouter les joueurs humains dans les équipes de manière alternée
            for (int i = 0; i < numberOfHumans; i++) {
                if (i % 2 == 0)
                    equipe1.addJoueur(new Humain("Joueur"+i, players.get(i)));
                else
                    equipe2.addJoueur(new Humain("Joueur"+i, players.get(i)));
            }

            // Compléter avec des bots si nécessaire
            while (equipe1.getJoueurs().size() < 2)
                equipe1.addJoueur(new Bot(null));

            while (equipe2.getJoueurs().size() < 2)
                equipe2.addJoueur(new Bot(null));

            // Instancier la partie et l'ajouter à la liste des parties actives
            Game game = new Game(gameId, equipe1, equipe2);
            games.put(gameId, game);

            out.println("La partie " + gameId + " commence avec " + numberOfHumans + " joueurs humains");

            // Lancer la partie dans un thread séparé
            new Thread(game).start();
        }

        /**
         * Permet à un joueur de rejoindre une partie existante.
         *
         * @param gameId ID de la partie à rejoindre.
         * @param out Flux de sortie pour informer le joueur.
         */
        private void joinGame(String gameId, PrintWriter out) {
            synchronized (gameQueue) {
                if (gameQueue.isEmpty()) {
                    out.println("Erreur: Partie inexistante.");
                    return;
                }
                // Regarde si la partie existe
                if (!gameQueue.containsKey(gameId)) {
                    out.println("Erreur: Partie inexistante.");
                    return;
                }
                int numberOfHumans = gameQueue.get(gameId).getFirst();
                // Regarde si il reste de la place
                if (gameQueue.get(gameId).getSecond().size() >= numberOfHumans) {
                    out.println("Erreur: La partie est déjà complète.");
                    return;
                }

                // Ajouter le joueur à la file d'attente
                gameQueue.get(gameId).getSecond().add(socket);
                out.println(gameId);    // Envoie le gameId au client

                // Notifier si le nombre de joueurs requis est atteint
                if (gameQueue.get(gameId).getSecond().size() == numberOfHumans)
                    gameQueue.notifyAll();
            }
        }
    }
}