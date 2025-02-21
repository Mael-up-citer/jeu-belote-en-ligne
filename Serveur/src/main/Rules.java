package src.main;

import src.main.Paquet.Carte;
import src.main.Paquet.Carte.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe abstraite implémentant les algorithmes de règles du jeu.
 */
public abstract class Rules {

    /**
     * Détermine les cartes jouables par un joueur dans un pli donné.
     *
     * @param contexte Le pli en cours.
     * @param player   Le joueur dont on vérifie les cartes jouables.
     * @return Une liste des cartes jouables par le joueur.
     */
    public static List<Carte> playable(Plis contexte, Joueur player) {
        List<Carte> res = new ArrayList<>();
        HashMap<Couleur, List<Carte>> playerMain = player.getMain();

        Carte asked = contexte.getPlis()[0]; // Première carte jouée du pli

        // Si aucune carte n'a été joué
        if (asked == null) {    // Joue ce qu'on veut
            return player.getMain().values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        }

        // Vérifie si la couleur demandée est un atout
        if (asked.getCouleur().getIsAtout()) {
            List<Carte> cartes = playerMain.get(asked.getCouleur());
            if (cartes != null) {
                // Parcourt les cartes pour trouver celles qui battent la carte demandée
                int i = cartes.size() - 1;
                while (i >= 0 && cartes.get(i).compareTo(asked) > 0) {
                    res.add(cartes.get(i));
                    i--;
                }
                // Si aucune carte ne bat l'atout demandé, renvoie toutes les cartes de la couleur
                if (res.isEmpty())
                    return playerMain.get(asked.getCouleur());
            }
            else {
                // Si le joueur n'a pas la couleur demandée, il peut jouer n'importe quelle carte
                return player.getMain().values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }
        else {
            // Si ce n'est pas un atout, applique les mêmes règles
            List<Carte> cartes = playerMain.get(asked.getCouleur());

            if (cartes != null)
                return playerMain.get(asked.getCouleur());
            else {
                return player.getMain().values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>(); // Renvoie une liste vide si un problème survient
    }

    /**
     * Retourne toutes les cartes jouables dans un pli en fonction des cartes déjà jouées.
     * Utile pour les IA afin de déterminer les coups possibles.
     *
     * @param contexte   Le pli en cours.
     * @param cartePlay  Les cartes déjà jouées dans la partie.
     * @param paquet     Le paquet complet des cartes du jeu.
     * @return Une liste des cartes encore jouables.
     */
    public static List<Carte> successeur(Plis contexte, HashMap<Couleur, List<Carte>> cartePlay, Paquet paquet) {
        Carte asked = contexte.getPlis()[0];
        List<Carte> cartes = cartePlay.get(asked.getCouleur());

        if (cartes != null)
            return cardByColorNotPlayed(asked.getCouleur(), cartePlay, paquet);
        else
            return cardNotPlayed(cartePlay, paquet);
    }

    /**
     * Retourne les cartes encore jouables d'une couleur donnée.
     *
     * @param couleur   La couleur des cartes recherchées.
     * @param cartePlay Les cartes déjà jouées.
     * @param paquet    Le paquet complet des cartes du jeu.
     * @return Une liste des cartes de la couleur donnée qui n'ont pas encore été jouées.
     */
    private static List<Carte> cardByColorNotPlayed(Couleur couleur, HashMap<Couleur, List<Carte>> cartePlay, Paquet paquet) {
        List<Carte> allCards = paquet.getCartes().stream()
                .filter(c -> c.getCouleur() == couleur)
                .collect(Collectors.toList());
        
        List<Carte> playedCards = cartePlay.getOrDefault(couleur, new ArrayList<>());
        
        return allCards.stream()
                .filter(carte -> !playedCards.contains(carte))
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les cartes encore disponibles dans le jeu.
     *
     * @param cartePlay Les cartes déjà jouées.
     * @param paquet    Le paquet complet des cartes du jeu.
     * @return Une liste des cartes non jouées.
     */
    private static List<Carte> cardNotPlayed(HashMap<Couleur, List<Carte>> cartePlay, Paquet paquet) {
        List<Carte> allCards = paquet.getCartes().stream().collect(Collectors.toList());

        for (List<Carte> played : cartePlay.values())
            allCards.removeAll(played);

        return List.copyOf(allCards); // Renvoie une liste immuable
    }
}