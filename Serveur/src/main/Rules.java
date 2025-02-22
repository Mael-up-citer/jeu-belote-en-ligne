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
        List<Carte> res = new ArrayList<>();
        HashMap<Couleur, List<Carte>> playerMain = player.getMain();
        Paquet.Carte[] cartesPlis = contexte.getPlis();
        Carte asked = cartesPlis[0]; // Première carte jouée du pli
    
        // Cas où le joueur est le premier à jouer -> il peut jouer ce qu'il veut
        if (asked == null) {
            return playerMain.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    
        boolean atoutDéjàPrésent = (contexte.getPowerfullCard() != null) && contexte.getPowerfullCard().getCouleur().getIsAtout();
    
        // Vérifie si la couleur demandée est un atout
        if (asked.getCouleur().getIsAtout()) {
            List<Carte> cartesAtout = playerMain.get(asked.getCouleur());
    
            if (cartesAtout != null && !cartesAtout.isEmpty()) {
                // Si l'atout est déjà présent dans le pli, il faut monter si possible
                for (Carte carte : cartesAtout)
                    if (!atoutDéjàPrésent || carte.compareTo(contexte.getPowerfullCard()) > 0)
                        res.add(carte);

                // Si aucune carte ne bat la plus forte, jouer un atout quelconque
                if (res.isEmpty())
                    res.addAll(cartesAtout);

            } else {
                // Pas d'atout -> peut jouer n'importe quoi
                return playerMain.values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        } else {
            // Si ce n'est pas un atout, on regarde si on peut suivre la couleur demandée
            List<Carte> cartesDemandées = playerMain.get(asked.getCouleur());
    
            if (cartesDemandées != null && !cartesDemandées.isEmpty())
                return cartesDemandées;
            else {
                // Si le joueur n'a pas cette couleur, il peut jouer ce qu'il veut
                return playerMain.values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }
    
        return res.isEmpty() ? playerMain.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()) : res;
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
        Carte asked = contexte.getPlis()[0]; // Première carte jouée dans le pli

        // Si aucune carte n'a été jouée, toutes les cartes non jouées sont disponibles
        if (asked == null) {
            return cardNotPlayed(cartePlay, paquet);
        }

        // Vérifier si la couleur demandée a encore des cartes non jouées
        List<Carte> cartesRestantes = cardByColorNotPlayed(asked.getCouleur(), cartePlay, paquet);
        
        return !cartesRestantes.isEmpty() ? cartesRestantes : cardNotPlayed(cartePlay, paquet);
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

        List<Carte> playedCards = cartePlay.getOrDefault(couleur, Collections.emptyList());

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
        List<Carte> allCards = new ArrayList<>(paquet.getCartes());

        // Supprime toutes les cartes déjà jouées
        cartePlay.values().forEach(allCards::removeAll);

        return allCards.isEmpty() ? Collections.emptyList() : allCards;
    }
}