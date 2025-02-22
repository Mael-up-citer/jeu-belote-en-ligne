package src.main;

/**
 * Représente un pli dans une partie de Belote.
 * Un pli est une collection de 4 cartes jouées par les joueurs.
 */
public class Plis {
    
    private Paquet.Carte[] plis = new Paquet.Carte[4]; // Tableau des cartes du pli
    private int index = 0;  // Position d'insertion dans le tableau
    private Paquet.Carte powerfullCard = null; // Carte la plus forte du pli
    private Joueur maitre; // Joueur qui possède le pli
    private int winner; // Index de la carte qui fait gagné le plis

    /**
     * Ajoute une carte au pli en respectant les règles de prise de pli.
     * @param j Le joueur qui joue la carte.
     * @param carte La carte jouée.
     * @throws IndexOutOfBoundsException si plus de 4 cartes sont ajoutées.
     */
    public void addCard(Joueur j, Paquet.Carte carte) throws IndexOutOfBoundsException {
        // Si c'est la première carte, elle définit la couleur demandée
        if (index == 0) {
            powerfullCard = carte;
            maitre = j;
            winner = 0;
        }
        else {
            Paquet.Carte.Couleur couleurDemandee = plis[0].getCouleur(); // Première carte jouée

            boolean carteActuelleEstAtout = carte.getCouleur().getIsAtout();
            boolean cartePowerfullEstAtout = powerfullCard.getCouleur().getIsAtout();

            // Cas 1 : Si la carte actuelle est un atout et que la plus forte n'est pas un atout, elle prend le pli
            if (carteActuelleEstAtout && !cartePowerfullEstAtout) {
                powerfullCard = carte;
                maitre = j;
                winner = index;
            }
            // Cas 2 : Si les deux sont atouts, on garde le plus fort
            else if (carteActuelleEstAtout && cartePowerfullEstAtout) {
                if (carte.compareTo(powerfullCard) > 0) {
                    powerfullCard = carte;
                    maitre = j;
                    winner = index;
                }
            }
            // Cas 3 : Si la carte actuelle est de la couleur demandée et que powerfullCard n'est pas un atout
            else if (carte.getCouleur() == couleurDemandee && !cartePowerfullEstAtout) {
                if (carte.compareTo(powerfullCard) > 0) {
                    powerfullCard = carte;
                    maitre = j;
                    winner = index;
                }
            }
        }

        // Ajouter la carte au pli
        plis[index] = carte;
        index++;
    }    

    /**
     * Calcule la valeur totale du pli en fonction des points des cartes.
     * @return La somme des points des cartes dans le pli.
     */
    public int getValue() {
        int sum = 0;
        for(int i = 0; i < plis.length; i++) {
            sum += plis[i].getNbPoint();
        }
        return sum;
    }

    /**
     * Réinitialise le pli pour une nouvelle manche.
     */
    public void reset() {
        for(int i = 0; i < plis.length; i++) {
            plis[i] = null;
        }
        index = 0;
        maitre = null;
        powerfullCard = null;
    }

    /**
     * Retourne les cartes jouées dans ce pli.
     * @return Un tableau contenant les cartes du pli.
     */
    public Paquet.Carte[] getPlis() {
        return plis;
    }

    /**
     * Retourne la carte la plus forte du pli.
     * @return La carte la plus forte.
     */
    public Paquet.Carte getPowerfullCard() {
        return powerfullCard;
    }

    /**
     * Retourne le joueur maître du pli.
     * @return Le joueur ayant gagné le pli.
     */
    public Joueur getMaitre() {
        return maitre;
    }

    /**
     * Retourne l'index du joueur qui a gagné le pli.
     * @return L'index du joueur gagnant ou -1 si aucun gagnant n'est défini.
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Retourne l'équipe qui a remporté le pli.
     * @return L'équipe du joueur maître du pli.
     */
    public Equipe getEquipe() {
        return maitre.getEquipe();
    }
}