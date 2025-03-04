package src.main;

import src.main.Paquet.Carte;
import src.main.Paquet.Carte.*;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


/**
 * Classe abstraite implémentant les algorithmes de règles du jeu.
 */
public abstract class Rules {

   /**
     * Détermine les cartes jouables par un joueur dans un pli donné en appliquant les règles de la Belote.
     *
     * @param contexte Le pli en cours.
     * @param player   Le joueur dont on vérifie les cartes jouables.
     * @return Une liste des cartes jouables par le joueur.
     */
    public static List<Carte> playable(Plis contexte, Joueur player) {
        // Si le joueur est le premier à jouer, il peut jouer n’importe quelle carte.
        if (contexte.getPlis()[0] == null) return getAllCards(player);

        Carte carteDemande = contexte.getPlis()[0];
        boolean atoutDejaPresent = contexte.getPowerfullCard().getCouleur().getIsAtout();

        // Si la couleur demandée est un atout
        if (carteDemande.getCouleur().getIsAtout()) return playAtout(contexte, player);

        // Sinon, la couleur demandée n'est pas un atout
        else return playNonAtout(contexte, player, carteDemande, atoutDejaPresent);
    }

    /**
     * Gestion du cas où la carte demandée est un atout.
     */
    private static List<Carte> playAtout(Plis contexte, Joueur player) {
        List<Carte> atoutCards = getCardsOfColor(player, contexte.getPlis()[0].getCouleur());
        if (atoutCards == null || atoutCards.isEmpty())
            // Si le joueur ne possède pas d'atouts, il peut jouer n'importe quelle carte.
            return getAllCards(player);

        // On recherche les atouts qui permettent de surcouper le pli
        List<Carte> overcutCards = getOvercutCards(atoutCards, contexte.getPowerfullCard());
        // S'il en existe, le joueur doit les jouer
        if (!overcutCards.isEmpty()) return overcutCards;

        // Sinon, il doit jouer un atout, même s'il ne peut pas surcouper
        return atoutCards;
    }

    /**
     * Gestion du cas où la carte demandée n'est pas un atout.
     */
    private static List<Carte> playNonAtout(Plis contexte, Joueur player, Carte carteDemande, boolean atoutDejaPresent) {
        // Si le joueur peut suivre la couleur demandée, il doit le faire.
        List<Carte> cardsOfColor = getCardsOfColor(player, carteDemande.getCouleur());
        if (cardsOfColor != null && !cardsOfColor.isEmpty()) return cardsOfColor;

        // Le joueur n'a pas la couleur demandée, il doit alors couper s'il possède des atouts.
        List<Carte> atoutCards = getCardsOfColor(player, Joueur.colorAtout);
        if (atoutCards != null && !atoutCards.isEmpty()) {
            if (atoutDejaPresent) {
                // Si quelqu'un a déjà coupé, il faut tenter de surcouper.
                List<Carte> overcutCards = getOvercutCards(atoutCards, contexte.getPowerfullCard());
                // S'il ne peut pas surcouper, il est quand même obligé de couper.
                if (!overcutCards.isEmpty()) return overcutCards;
                else  return atoutCards;
            } 
            else
                // Si personne n'a encore coupé, jouer un atout.
                return atoutCards;
        }

        // Si le joueur ne possède ni la couleur demandée ni d'atout, il peut jouer n'importe quelle carte.
        return getAllCards(player);
    }

    /**
     * Renvoie toutes les cartes de la main du joueur.
     */
    private static List<Carte> getAllCards(Joueur player) {
        return player.getMain().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Renvoie la liste des cartes de la main du joueur pour une couleur donnée.
     */
    private static List<Carte> getCardsOfColor(Joueur player, Couleur couleur) {
        return player.getMain().get(couleur);
    }

    /**
     * Renvoie la liste des atouts permettant de surcouper l'atout actuellement le plus fort dans le pli.
     */
    private static List<Carte> getOvercutCards(List<Carte> atoutCards, Carte currentPowerful) {
        List<Carte> res = new ArrayList<>();
        for (Carte carte : atoutCards)
            if (carte.compareTo(currentPowerful) > 0)
                res.add(carte);

        return res;
    }


    ////////////////////////////////////////////////////////////////////////////////////
    // Méthode pour l'IA : calcul des successeurs (cartes non jouées) en fonction du contexte //
    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retourne l'ensemble des cartes encore jouables dans le jeu en fonction des cartes déjà jouées.
     * Cette méthode est utile pour les simulations et l'IA afin de déterminer les coups possibles.
     *
     * Règles appliquées :
     * - Si aucun coup n'a été joué dans le pli, toutes les cartes non jouées sont disponibles.
     * - Si la couleur demandée est toujours présente dans le jeu, seules ces cartes sont considérées.
     * - En cas de demande d'atout, et si un atout a déjà été joué, il faut surcouper si possible.
     *
     * @param contexte      Le pli en cours.
     * @param cartesJouees  Les cartes déjà jouées, organisées par couleur.
     * @param paquet        Le paquet complet des cartes du jeu.
     * @return La liste des cartes non jouées filtrées selon la règle du suivi.
     */
    public static List<Carte> successeur(Plis contexte, HashMap<Couleur, List<Carte>> cartesJouees, Paquet paquet) {
        Carte carteDemandee = contexte.getPlis()[0];
        List<Carte> nonJouees = cardNotPlayed(cartesJouees, paquet);

        if (carteDemandee == null) return nonJouees;

        if (carteDemandee.getCouleur().getIsAtout()) {
            // Cas où la couleur demandée est un atout.
            if (contexte.getPowerfullCard() != null && contexte.getPowerfullCard().getCouleur().getIsAtout()) {
                List<Carte> atouts = nonJouees.stream()
                        .filter(c -> c.getCouleur().getIsAtout())
                        .collect(Collectors.toList());
                List<Carte> surcoups = getOvercutCards(atouts, contexte.getPowerfullCard());
                if (!surcoups.isEmpty()) return surcoups;
                else return atouts;
            }
            else {
                // Aucun atout joué : seules les cartes atout non jouées sont autorisées.
                return nonJouees.stream()
                        .filter(c -> c.getCouleur().getIsAtout())
                        .collect(Collectors.toList());
            }
        }
        else {
            // Pour une couleur non-atout, vérifier s'il reste des cartes de cette couleur.
            List<Carte> cartesCouleur = nonJouees.stream()
                    .filter(c -> c.getCouleur() == carteDemandee.getCouleur())
                    .collect(Collectors.toList());
            if (!cartesCouleur.isEmpty()) return cartesCouleur;
            else return nonJouees;
        }
    }

    /**
     * Renvoie l'ensemble des cartes du paquet qui n'ont pas encore été jouées.
     *
     * @param cartesJouees Les cartes déjà jouées, organisées par couleur.
     * @param paquet       Le paquet complet.
     * @return La liste des cartes non jouées.
     */
    private static List<Carte> cardNotPlayed(HashMap<Couleur, List<Carte>> cartesJouees, Paquet paquet) {
        List<Carte> allCards = new ArrayList<>(paquet.getCartes());
        cartesJouees.values().forEach(allCards::removeAll);
        return allCards;
    }
}