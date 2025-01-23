package src.main;

import src.main.Paquet;

public class Plis {
    // Plis regroupe 4 cartes
    private Paquet.Carte[] plis = new Paquet.Carte[4];
    private int bigestValue = 0;    // nb point de la plus grosse carte
    private int index = 0;  // Position d'insertion dans le tableau
    private Joueur maitre;  // Personne à qui appartient le plis

    // Ajoute une carte avec une valeur percu example si la couleur n'est pas celle demandé la carte ne compte pas mais ses points après si
    public void addCard(Joueur j, Paquet.Carte carte, int perceptValue) throws IndexOutOfBoundsException {
        // Si la nouvelle carte est la plus forte
        if (bigestValue < perceptValue) {
            bigestValue = perceptValue;
            maitre = j;
        }
        // Add la carte au plis
        plis[index] = carte;
        index++;
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

    public void setPlis(Paquet.Carte[] plis) {
        this.plis = plis;
    }

    public void reset() {
        for(int i = 0; i < plis.length; i++)
            plis[i] = null;
        
        index = 0;
        maitre = null;
        bigestValue = 0;
    }
}