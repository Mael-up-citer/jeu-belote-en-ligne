package main;

import java.io.*;
import java.net.Socket;

/**
 * La classe ServerConnection gère la connexion entre le client et le serveur.
 */
public class ServerConnection {

    // Constantes pour la connexion
    private static final int PORT = 12345; // Port du serveur
    private static final String HOST = "127.0.0.1"; // Adresse IP du serveur

    private Socket socket; // Socket pour la communication
    private BufferedReader in; // Flux d'entrée pour recevoir les messages
    private PrintWriter out; // Flux de sortie pour envoyer les messages
    private ConnectionListener listener; // Interface pour notifier des événements liés à la connexion
    private boolean isConnected = false; // Etat de la connexion

    // Interface pour écouter les événements de connexion
    public interface ConnectionListener {
        void onServerMessage(String message);
        void onConnectionError(String error);
        void onDisconnected();
    }

    public ServerConnection(ConnectionListener listener) {
        this.listener = listener;
    }

    /**
     * Établit la connexion au serveur.
     *
     * @return true si la connexion a réussi, false sinon
     */
    public boolean connect() {
        if (isConnected) return true; // Retourne true si déjà connecté

        try {
            socket = new Socket(HOST, PORT); // Établit une connexion au serveur
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Prépare le flux d'entrée
            out = new PrintWriter(socket.getOutputStream(), true); // Prépare le flux de sortie
            System.out.println("Connecté au serveur");

            isConnected = true;

            // Démarre un thread pour écouter les messages du serveur
            new Thread(this::listenToServer).start();

            return true;
        } catch (IOException e) {
            listener.onConnectionError("Impossible de se connecter au serveur.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Écoute en continu les messages du serveur.
     */
    private void listenToServer() {
        try {
            String message;

            while ((message = in.readLine()) != null)
                listener.onServerMessage(message); // Notifie le message au listener

        } catch (IOException e) {
            listener.onDisconnected(); // Notifie que la connexion a été interrompue
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message au serveur.
     *
     * @param message le message à envoyer
     */
    public void sendToServer(String message) {
        if (isConnected && out != null) {
            out.println(message); // Envoie le message au serveur
            System.out.println("Message envoyé au serveur : " + message);
        }
    }

    /**
     * Ferme la connexion avec le serveur.
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Ferme le socket
                System.out.println("Déconnexion du serveur.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isConnected = false;
            listener.onDisconnected(); // Notifie que la connexion a été interrompue
        }
    }

    /**
     * Retourne l'état de la connexion.
     *
     * @return true si connecté, false sinon
     */
    public boolean isConnected() {
        return isConnected;
    }
}