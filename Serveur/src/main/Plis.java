package src.main;


public class Plis {
    // Plis regroupe 4 cartes
    private Paquet.Carte[] plis = new Paquet.Carte[4];
    private int index = 0;  // Position d'insertion dans le tableau
    private Paquet.Carte powerfullCard = null;    // Carte la plus forte du plis
    private Joueur maitre;  // Personne à qui appartient le plis


    // Ajoute une carte avec une valeur percu example si la couleur n'est pas celle demandé la carte ne compte pas mais ses points après si
    public void addCard(Joueur j, Paquet.Carte carte, int perceptValue) throws IndexOutOfBoundsException {
        // Si la nouvelle carte est la plus forte
        if (carte.compareTo(powerfullCard) > 0) {
            powerfullCard = carte;
            maitre = j;
        }
        // Add la carte au plis
        plis[index] = carte;
        index++;
    }

    // Reset le pli pour un faire un nouveau
    public void reset() {
        // Enlève les cartes du pli
        for(int i = 0; i < plis.length; i++)
            plis[i] = null;

        index = 0;
        maitre = null;
        powerfullCard = null;
    }

    // Calcule le nombre de point d'un plis
    public int getValue() {
        int sum = 0;

        for(int i = 0; i < plis.length; i++)
            sum += plis[i].getNbPoint();

        return sum;
    }

    public Paquet.Carte[] getPlis() {
        return plis;
    }

    public Paquet.Carte getPowerfullCard() {
        return powerfullCard;
    }

    public Joueur getMaitre() {
        return maitre;
    }

    public Equipe getEquipe() {
        return maitre.getEquipe();
    }
}