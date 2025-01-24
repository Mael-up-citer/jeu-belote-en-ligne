package main;

import java.io.*;
import java.net.Socket;

/**
 * Classe gérant la connexion client-serveur.
 * Cette classe est responsable d'établir, maintenir et gérer une connexion avec le serveur.
 * Elle utilise l'EventManager pour publier des événements en réponse à diverses actions (connexion, réception de messages, déconnexion, etc.).
 *
 * Singleton : Une seule instance de cette classe est autorisée pour garantir une gestion centralisée de la connexion.
 */
public class ServerConnection {

    // Port et adresse du serveur
    private static final int PORT = 12345;
    private static final String HOST = "127.0.0.1";

    // Instance unique pour le singleton
    private static ServerConnection instance;
    private ConnectionListener listener;  // Référence au listener

    // Socket et flux de communication
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isConnected = false; // Indique si une connexion est active

    // Gestionnaire d'événements
    private EventManager eventManager;
       // Ajout d'une interface pour le listener
       public interface ConnectionListener {
        void onServerMessage(String message);
        void onConnectionError(String error);
        void onDisconnected();
    }

    /**
     * Constructeur privé pour le pattern Singleton.
     *
     * @param eventManager L'EventManager utilisé pour publier les événements.
     */
    private ServerConnection(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Récupère l'instance unique de ServerConnection.
     * Si l'instance n'existe pas encore, elle est créée.
     *
     * @param eventManager L'EventManager utilisé pour gérer les événements (nécessaire lors de la première initialisation).
     * @return L'instance unique de ServerConnection.
     */
    public static ServerConnection getInstance(EventManager eventManager) {
        if (instance == null)
            instance = new ServerConnection(eventManager);

        return instance;
    }

    /**
     * Établit une connexion avec le serveur.
     * Si la connexion est déjà établie, elle n'est pas recréée.
     * En cas de succès, un événement "server:connected" est publié.
     * En cas d'échec, un événement "server:connection_error" est publié avec les détails de l'erreur.
     *
     * @return true si la connexion a réussi, false en cas d'erreur.
     */
    public boolean connect() {
        if (isConnected) {
            System.out.println("Déjà connecté.");
            return true;
        }

        try {
            // Initialisation de la connexion
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;

            System.out.println("Connecté au serveur : " + HOST + ":" + PORT);

            // Lancer un thread pour écouter les messages entrants du serveur
            new Thread(this::listenToServer).start();

            // Publier un événement de connexion réussie
            eventManager.publish("server:connected", null);
            return true;
        } catch (IOException e) {
            // Publier un événement d'erreur de connexion avec le message d'erreur
            eventManager.publish("server:connection_error", e.getMessage());
            return false;
        }
    }

    /**
     * Écoute en continu les messages provenant du serveur dans un thread séparé.
     * Chaque message reçu est publié sous l'événement "server:message_received".
     * En cas de déconnexion ou d'erreur, un événement "server:disconnected" est publié.
     */
    private void listenToServer() {
        try {
            String message;
            // Lecture des messages envoyés par le serveur
            while ((message = in.readLine()) != null) {
                eventManager.publish("server:message_received", message);
            }
        } catch (IOException e) {
            // Gérer la déconnexion
            eventManager.publish("server:disconnected", null);
            cleanupResources();
        }
    }

    /**
     * Envoie un message au serveur.
     * Si la connexion n'est pas établie, un message d'erreur est affiché.
     *
     * @param message Le message à envoyer au serveur.
     */
    public void sendToServer(String message) {
        if (isConnected && out != null) {
            out.println(message);
            System.out.println("Message envoyé : " + message);
        } else {
            System.out.println("Connexion non établie. Impossible d'envoyer le message.");
        }
    }

    /**
     * Déconnecte la connexion actuelle avec le serveur.
     * Ferme le socket et les flux, et publie un événement "server:disconnected".
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Déconnecté.");
        } catch (IOException e) {
            System.out.println("Erreur de déconnexion : " + e.getMessage());
        } finally {
            cleanupResources();
        }
    }

    /**
     * Libère les ressources utilisées par la connexion (flux et socket).
     * Met à jour l'état de la connexion et publie un événement "server:disconnected".
     */
    private void cleanupResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.out.println("Erreur de nettoyage des ressources : " + e.getMessage());
        } finally {
            isConnected = false;
            eventManager.publish("server:disconnected", null);
        }
    }

    /**
     * Indique si la connexion avec le serveur est active.
     *
     * @return true si la connexion est active, false sinon.
     */
    public boolean isConnected() {
        return isConnected;
    }

    // Méthode pour définir un listener externe
    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }
}