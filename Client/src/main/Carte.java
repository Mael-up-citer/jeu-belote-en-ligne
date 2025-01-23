package main;

import java.util.ArrayList;
import java.util.Random;

public class Carte implements Comparable<Carte> {
    public enum Couleur {
        COEUR,
        CARREAU,
        PICK,
        TREFLE;

        private Boolean isAtout;

        Couleur() {
            isAtout = false;
        }

        public Boolean getIsAtout() {
            return isAtout;
        }

        public void setIsAtout(Boolean isAtout) {
            this.isAtout = isAtout;
        }
    }// Fin de Couleur
    public enum Type {
        AS(11), SEPT(0), HUIT(0), NEUF(0), DIX(0), VALLET(2), DAMME(3), ROI(4);

        private final int value;    // Nombre de point d'une carte

        Type(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }
    }// Fin de Type

    Couleur couleur;
    Type type;
    String path;    // Chemin de l'image

    public Carte(Couleur c, Type t) {
        couleur = c;
        type = t;
        path = "image/"+type+"De"+couleur;
    }

    @Override
    public int compareTo(Carte c) {
        return this.getNbPoint() - c.getNbPoint();
    }

    // Donne le nombre de point de cette carte selon si on est a l'atout ou non
    public int getNbPoint() {
        if (couleur.isAtout) {
            switch (type) {
                case VALLET:
                    return 20;
                case NEUF:
                    return 14;
                default:
                    break;
            }
        }
        return type.getValue(); 
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "";
    }
}