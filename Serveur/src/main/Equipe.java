package src.main;

import src.main.Joueur;
import src.main.Paquet;
import src.main.Paquet.Carte;
import src.main.Plis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Equipe {
    private Joueur j1;
    private Joueur j2;
    private int score = 0;
    private boolean aPris = false;

    private ArrayList<Plis> plis = new ArrayList<>();   // Liste des plis de l'equipe pour un tour

    Equipe() {
    }

    Equipe(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
    }

    // Retourne true si un litige apparait
    public boolean calculerScore(boolean dixDeDer) {
        int sum = 0;

        for(int i = 0; i < plis.size(); i++)
            sum += plis.get(i).getValue();

        if(dixDeDer)
            sum+=10;

        // Si litige prsn ne marque
        if (sum == 81)
            return true;
        // Si on met plus ou moins mais qu'on a pas pris marque les pts
        else if ((sum > 81) || (sum < 81 && !aPris)) {
            sum = sum/10 *10;
            score += sum;   // Ajoute le resultat final
        }
        // Pas de litige et pas de points marqué
        return false;
    }

    public Joueur getJ1() {
        return j1;
    }

    public Joueur getJ2() {
        return j2;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addPlie(Plis p) {
        plis.add(p);
    }
    
    public void addJoueur(Joueur j) throws IllegalStateException {
        if (j1 == null)
            j1 = j;
        else if (j2 == null)
            j2 = j;
        else
            throw new IllegalStateException("L'équipe est pleine !");
    }

    public List<Joueur> getJoueurs() {
        return Arrays.asList(j1, j2);
    }
}