package main;

import java.io.*;
import java.net.Socket;

/**
 * La classe ServerConnection gère la connexion client-serveur via un modèle Singleton.
 * Elle offre une interface simple pour établir, envoyer et recevoir des messages,
 * tout en notifiant les événements grâce à un ConnectionListener.
 */
public class ServerConnection {

    // Constantes pour la connexion
    private static final int PORT = 12345;  // Port sur lequel le serveur écoute
    private static final String HOST = "127.0.0.1";  // Adresse IP du serveur (localhost pour ce cas)

    private static ServerConnection instance; // Instance unique (Singleton)

    private Socket socket;  // Socket utilisé pour communiquer avec le serveur
    private BufferedReader in;  // Flux entrant
    private PrintWriter out;  // Flux sortant
    private ConnectionListener listener;  // Listener pour les événements liés à la connexion
    private boolean isConnected = false;  // Indicateur d'état de connexion

    /**
     * Interface permettant de notifier des événements liés à la connexion.
     */
    public interface ConnectionListener {
        void onServerMessage(String message); // Message reçu
        void onConnectionError(String error); // Erreur de connexion
        void onDisconnected();               // Déconnexion
    }

    /**
     * Constructeur privé (Singleton).
     */
    private ServerConnection() {
        // Constructeur privé pour éviter l'instanciation directe
    }

    /**
     * Retourne l'instance unique de ServerConnection.
     * 
     * @return L'instance unique de ServerConnection
     */
    public static ServerConnection getInstance() {
        if (instance == null)
            instance = new ServerConnection();

        return instance;
    }

    /**
     * Initialise le listener pour recevoir les notifications d'événements.
     * 
     * @param listener Un objet implémentant ConnectionListener
     */
    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    /**
     * Établit la connexion avec le serveur.
     * 
     * @return true si la connexion a réussi, false sinon
     */
    public boolean connect() {
        if (isConnected) {
            log("Déjà connecté au serveur.");
            return true;
        }

        try {
            // Connexion au serveur
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;

            log("Connecté au serveur : " + HOST + ":" + PORT);

            // Lancer un thread pour écouter les messages du serveur
            new Thread(this::listenToServer).start();
            return true;
        } catch (IOException e) {
            notifyConnectionError("Erreur de connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * Écoute les messages entrants du serveur et les transmet au listener.
     */
    private void listenToServer() {
        try {
            String message; // Message du serveur

            // Tq la connexion est effective
            while ((message = in.readLine()) != null) {
                if (listener != null)
                    listener.onServerMessage(message);
            }
        } catch (IOException e) {
            notifyDisconnection();
        }
    }

    /**
     * Envoie un message au serveur.
     * 
     * @param message Le message à envoyer
     */
    public void sendToServer(String message) {
        if (isConnected && out != null) {
            out.println(message);
            log("Message envoyé : " + message);
        }
        else
            logError("Impossible d'envoyer le message, connexion non établie.");
    }

    /**
     * Ferme la connexion avec le serveur et libère les ressources.
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            log("Déconnexion réussie.");
        } catch (IOException e) {
            logError("Erreur lors de la déconnexion : " + e.getMessage());
        } finally {
            cleanupResources();
        }
    }

    /**
     * Tente une reconnexion au serveur.
     * 
     * @return true si la reconnexion a réussi, false sinon
     */
    public boolean reconnect() {
        log("Tentative de reconnexion...");
        disconnect();
        return connect();
    }

    /**
     * Nettoie les ressources utilisées et met à jour l'état de connexion.
     */
    private void cleanupResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            logError("Erreur lors du nettoyage des ressources : " + e.getMessage());
        } finally {
            isConnected = false;
            if (listener != null)
                listener.onDisconnected();
        }
    }

    /**
     * Notifie une erreur de connexion.
     * 
     * @param errorMessage Le message d'erreur
     */
    private void notifyConnectionError(String errorMessage) {
        logError(errorMessage);
        if (listener != null)
            listener.onConnectionError(errorMessage);
    }

    /**
     * Notifie que la connexion a été perdue.
     */
    private void notifyDisconnection() {
        log("Connexion perdue.");
        cleanupResources();
    }

    /**
     * Retourne l'état actuel de la connexion.
     * 
     * @return true si connecté, false sinon
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Méthode utilitaire pour les logs.
     * 
     * @param message Le message à afficher
     */
    private void log(String message) {
        System.out.println("[ServerConnection] " + message);
    }

    /**
     * Méthode utilitaire pour les erreurs.
     * 
     * @param errorMessage Le message d'erreur à afficher
     */
    private void logError(String errorMessage) {
        System.err.println("[ServerConnection] " + errorMessage);
    }
}