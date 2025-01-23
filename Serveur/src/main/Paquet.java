package src.main;

import java.util.ArrayList;
import java.util.Random;

public class Paquet extends ArrayList<Paquet.Carte> {
    public static class Carte implements Comparable<Carte>{
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

        private final Couleur couleur;
        private final Type type;
        private int currentAcessIndex = 0;  // Indique quelle carte doit etre séléctionné

        public Carte(Couleur c, Type t) {
            couleur = c;
            type = t;
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

        public int getCurrentAcessIndex() {
            return currentAcessIndex;
        }

        public void setCurrentAcessIndex(int currentAcessIndex) {
            this.currentAcessIndex = currentAcessIndex;
        }

        public void RAZCurrentAcessIndex() {
            currentAcessIndex = 0;
        }
    }// Fin de Carte

    public Paquet() {
        createPaquet();
    }

    // Cree les 32 cartes
    public void createPaquet() {
        // cree toutes les combinaisons
        for (int i = 0; i < Carte.Couleur.values().length; i++)
            for (int j = 0; j < Carte.Type.values().length; j++)
                this.add(new Carte(Carte.Couleur.values()[i], Carte.Type.values()[j]));
        // Melange la collection
        shufle();
    }

    private void shufle() {
        Random ran = new Random();
        int indx1, indx2;

        // Itere n^3 fois
        for(int i = 0; i < this.size()*this.size()*this.size(); i++) {
            // Tire 2 index distincts
            do {
                indx1 = ran.nextInt(this.size());
                indx2 = ran.nextInt(this.size());
            } while (indx1 == indx2);

            // Effectue le swap
            Paquet.Carte tmp = this.get(indx1);
            this.set(indx1, this.get(indx2));
            this.set(indx2, tmp);
        }
    }

    // deverse le plis dans le paquet et supprime le plis
    public void addPlis(Plis plis) {
        // Deverse le plit
        for(int i = 0; i < plis.getPlis().length; i++)
            this.add(plis.getPlis()[i]);

        plis = null;    // Supprime
    }

    @Override
    public String toString() {
        return "";
    }
}