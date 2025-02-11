package main;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameManager {
    private final String NAMEPUBLISH = "GameManager:message_received";

    // Carte de la main du joueur
    private static Map<Carte.Couleur, List<Carte>> playerHand = new HashMap<>();

    // Table de dispatching pour associer les commandes à leurs méthodes pour la com server->GameManager
    private final Map<String, Consumer<String>> COMMANDMAPSERVER = new HashMap<>();
    // Table de dispatching pour associer les commandes à leurs méthodes pour la com GameGUI->GameManager
    private final Map<String, Consumer<String>> COMMANDMAPClIENT = new HashMap<>();


    public GameManager() {
        System.out.println("GameManager existe enfin");
        // S'abonne à l'événement "server_response" pour recevoir la réponse du serveur
        EventManager.getInstance().subscribe("server:message_received", (eventType, data) -> {
            if (data instanceof String) {
                // Diviser la chaîne en un tableau de mots
                String[] serveurResponse = ((String) data).split(":");
                Consumer<String> handler = COMMANDMAPSERVER.get(serveurResponse[0]);

                // Si la commande est trouvée, l'exécuter
                if (handler != null)
                    handler.accept(serveurResponse[1]);  // Appel de la méthode associée à la commande
            }
        });

        // S'abonne à l'événement "Gui_response" pour recevoir la réponse du serveur
        EventManager.getInstance().subscribe("GameGui:Gui_response", (eventType, data) -> {
            if (data instanceof String) {
                // Diviser la chaîne en un tableau de mots
                String[] serveurResponse = ((String) data).split(":");
                Consumer<String> handler = COMMANDMAPClIENT.get(serveurResponse[0]);

                // Si la commande est trouvée, l'exécuter
                if (handler != null)
                    handler.accept(serveurResponse[1]);  // Appel de la méthode associée à la commande
            }
        });

        initializeCOMMANDMAPServer();
        initializeCOMMANDMAPClient();
    }

    /**
     * Méthode d'initialisation de la table de dispatching. 
     * Elle associe chaque commande à une méthode de traitement correspondante.
     */
    private void initializeCOMMANDMAPServer() {
        // Commande pour la comunication arrivant du serveur
        COMMANDMAPSERVER.put("GameStart", unused -> onGameStart());
        COMMANDMAPSERVER.put("SetMain", this::onReceiveHand);
        COMMANDMAPSERVER.put("SetMiddleCard", this::SetMiddleCard);
        COMMANDMAPSERVER.put("GetAtout1", unused -> askAtout1());
        COMMANDMAPSERVER.put("GetAtout1", unused -> askAtout2());
        COMMANDMAPSERVER.put("AtoutIsSet", this::atoutIsSet);
    }

    /**
     * Méthode d'initialisation de la table de dispatching. 
     * Elle associe chaque commande à une méthode de traitement correspondante.
     */
    private void initializeCOMMANDMAPClient() {
        // Commande pour la comunication arrivant de la GUI
        COMMANDMAPClIENT.put("CardPlay", this::onCardPlay);
        COMMANDMAPClIENT.put("ParleAtout", this::SetAtout);
    }


    /* #################################
     * Méthodes de COMMANDMAPSERVER
     * ###############################
     */

    // Publie un event pour prévenir la GUI que la partie commence
    private void onGameStart() {
        System.out.println("je vais dire a la Gui que la game start");
        EventManager.getInstance().publish(NAMEPUBLISH, "GameStart:$");
    }

    // Quand le serveur partage la main du joueur si elle est vide on reset la main
    // Cette méthode est dite cumulative càd la main ce voit ajouter les cartes sans supprimer ce qu'il y avait avant
    // Sauf si message est vide
    // Pour reset la main voir @ref
    private void onReceiveHand(String message) {
        if (message.equals("null"))
            resetPlayerHand();  // Si le message est "null", on réinitialise la main
        else {
            playerHand = parseHand(message);
            // Envoie à la GUI les cartes à afficher sous forme de leur nom (type + "De" + couleur)
            EventManager.getInstance().publish(NAMEPUBLISH, "PlayerHand:"+formatForGui());
        }
    }

    private Map<Carte.Couleur, List<Carte>> parseHand(String message) {
        // Supprimer les accolades
        message = message.substring(1, message.length() - 1);

        // Expression régulière pour capturer "COULEUR=[...]" sans couper l'intérieur des crochets
        Pattern pattern = Pattern.compile("(\\w+)=\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String couleurStr = matcher.group(1);  // Récupère la couleur
            String cartesStr = matcher.group(2);   // Récupère [liste de cartes]

            try {
                Carte.Couleur couleur = Carte.Couleur.valueOf(couleurStr);
                List<Carte> cartes = new ArrayList<>();

                if (!cartesStr.isEmpty()) {
                    // Séparer chaque carte
                    String[] cartesArray = cartesStr.split(", ");

                    for (String carteStr : cartesArray) {
                        String[] parts = carteStr.split("De");
                        Carte.Type type = Carte.Type.valueOf(parts[0]);
                        cartes.add(new Carte(couleur, type));
                    }
                }
                playerHand.put(couleur, cartes);
            } catch (IllegalArgumentException e) {
                System.err.println("Erreur: Couleur ou type inconnu -> " + couleurStr);
            }
        }
        return playerHand;
    }

    // Dit à la GUI de retourner la carte du milieu
    private void SetMiddleCard(String carte) {
        EventManager.getInstance().publish(NAMEPUBLISH, "SetMiddleCard:"+carte);
    }

    // Transmet à la GUI qu'on attend un atout
    private void askAtout1() {
        EventManager.getInstance().publish(NAMEPUBLISH, "askAtout1:$");
    }

    // Transmet à la GUI qu'on attend un atout
    private void askAtout2() {
        EventManager.getInstance().publish(NAMEPUBLISH, "askAtout2:$");
    }

    // Quand l'atout a été définie
    private void atoutIsSet(String atout) {
        EventManager.getInstance().publish(NAMEPUBLISH, "AtoutIsSet:$");

        // Marque dans le code local que l'atout est 'atout'
        try {   // Récupère la couleur
            Carte.Couleur res = Carte.Couleur.valueOf(atout);
            res.setIsAtout(true);   // Défini que l'atout est cette couleur
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Vide la main du joueur et previens la gui que la main a changé
    private void resetPlayerHand() {
        EventManager.getInstance().publish(NAMEPUBLISH, "ClearHand:$");
    }


    /* #################################
     * Méthodes de COMMANDMAPClIENT
     * ###############################
     */

    // Tansmet au serveur la carte joué
    private void onCardPlay(String carte) {
        ServerConnection.getInstance().sendToServer("UpdateHand:" + carte); // Envoie au serveur sous forme de chaîne
    }

    // Transmet au serveur l'atout choisie
    private void SetAtout(String atout) {
        if (atout.isEmpty())
            ServerConnection.getInstance().sendToServer("SetAtout:" + true); // Envoie au serveur sous forme de chaîne
        else
            ServerConnection.getInstance().sendToServer("SetAtout:" + false); // Envoie au serveur sous forme de chaîne
    }


    /* #################################
     * Méthodes de auxiliaires
     * ###############################
     */

    // Format la main pour que la GUI puisse afficher la carte associé
    // Format à suivre: (type + "De" + couleur)
    private String formatForGui() {
        StringBuilder res = new StringBuilder();

        for (List<Carte> cartes : playerHand.values())
            for (Carte carte : cartes)
                res.append(carte.toString()+";");

        res.deleteCharAt(res.length()-1); // Enlève le dernier ';'

        return res.toString();
    }
}