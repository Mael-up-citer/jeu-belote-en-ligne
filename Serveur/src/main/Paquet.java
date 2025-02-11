package src.main;

import java.util.Random;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * La classe Paquet représente un paquet de 32 cartes de jeu.
 * Elle contient une liste de cartes et fournit des méthodes pour créer le paquet, 
 * mélanger les cartes, et ajouter des plis.
 * La classe Paquet hérite de ArrayList et contient une classe interne Carte.
 */
public class Paquet extends ArrayList<Paquet.Carte> {
    /**
     * La classe Carte représente une carte de jeu avec une couleur et un type (valeur).
     * Elle est comparable en fonction de sa valeur, et elle est sérialisable pour la transmission réseau.
     */
    public static class Carte implements Comparable<Carte>, Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * Enumération représentant les couleurs possibles des cartes.
         */
        public enum Couleur {
            COEUR,
            CARREAU,
            PIQUE,
            TREFLE;

            private Boolean isAtout;

            // Constructeur privé : Par défaut, la couleur n'est pas atout
            Couleur() {
                isAtout = false;
            }

            // Accesseur pour savoir si cette couleur est l'atout
            public Boolean getIsAtout() {
                return isAtout;
            }

            // Modificateur pour définir si cette couleur est l'atout
            public void setIsAtout(Boolean isAtout) {
                this.isAtout = isAtout;
            }
        }

        /**
         * Enumération représentant les types (ou valeurs) des cartes.
         */
        public enum Type {
            AS(11), SEPT(0), HUIT(0), NEUF(0), DIX(0), VALLET(2), DAMME(3), ROI(4);

            private final int value;  // Valeur en points d'une carte

            Type(int val) {
                value = val;
            }

            // Récupère la valeur de la carte
            public int getValue() {
                return value;
            }
        }

        private final Couleur couleur;  // La couleur de la carte
        private final Type type;        // Le type (valeur) de la carte

        /**
         * Constructeur pour créer une carte avec une couleur et un type donnés.
         * 
         * @param c La couleur de la carte.
         * @param t Le type (valeur) de la carte.
         */
        public Carte(Couleur c, Type t) {
            couleur = c;
            type = t;
        }

        /**
         * Compare deux cartes en fonction de leur valeur en points.
         * 
         * @param c La carte à comparer.
         * @return Un entier indiquant si cette carte est plus petite, égale ou plus grande que l'autre.
         */
        @Override
        public int compareTo(Carte c) {
            return this.getNbPoint() - c.getNbPoint();
        }

        /**
         * Calcule et retourne la valeur en points de la carte, en tenant compte de la couleur (atout ou non).
         * 
         * @return La valeur en points de la carte.
         */
        public int getNbPoint() {
            if (couleur.isAtout) {
                switch (type) {
                    case VALLET:
                        return 20;  // Le valet d'atout vaut 20 points
                    case NEUF:
                        return 14;  // Le neuf d'atout vaut 14 points
                    default:
                        break;
                }
            }
            return type.getValue();  // Sinon, la valeur de la carte est simplement la valeur associée au type
        }

        // Accesseurs pour obtenir la couleur et le type de la carte
        public Couleur getCouleur() {
            return couleur;
        }

        public Type getType() {
            return type;
        }

        /**
         * Retourne une représentation sous forme de chaîne de caractères de la carte.
         * Cette méthode combine le nom de la couleur et du type pour fournir une description de la carte.
         * Exemple: "AS de COEUR"
         * 
         * @return Une chaîne de caractères représentant la carte.
         */
        @Override
        public String toString() {
            // Retourne une représentation lisible sous forme de chaîne, par exemple "AS de COEUR"
            return type.name() + "De" + couleur.name();
        }
    }

    private int currentAcessIndex = 0;  // Indicateur pour savoir quelle carte doit être sélectionnée

    /**
     * Constructeur qui crée un paquet de cartes en générant toutes les combinaisons possibles de couleurs et de types,
     * puis les mélange de manière aléatoire.
     */
    public Paquet() {
        createPaquet();
    }

    /**
     * Crée un paquet de cartes en générant toutes les combinaisons de couleurs et de types,
     * puis les mélange de manière aléatoire.
     */
    public void createPaquet() {
        // Crée toutes les combinaisons de couleurs et de types
        for (int i = 0; i < Carte.Couleur.values().length; i++) {
            for (int j = 0; j < Carte.Type.values().length; j++) {
                this.add(new Carte(Carte.Couleur.values()[i], Carte.Type.values()[j]));
            }
        }
        shufle();  // Mélange les cartes
    }

    /**
     * Mélange les cartes du paquet de manière aléatoire en utilisant un algorithme de permutation.
     */
    private void shufle() {
        Random ran = new Random();
        int indx1, indx2;

        // Mélange les cartes n^3 fois (approximativement)
        for (int i = 0; i < this.size() * this.size() * this.size(); i++) {
            do {
                indx1 = ran.nextInt(this.size());
                indx2 = ran.nextInt(this.size());
            } while (indx1 == indx2);  // Assure que les index ne sont pas identiques

            // Échange les cartes
            Paquet.Carte tmp = this.get(indx1);
            this.set(indx1, this.get(indx2));
            this.set(indx2, tmp);
        }
    }

    /**
     * Ajoute un plis (ensemble de cartes) au paquet.
     * Les cartes du plis sont ajoutées à la fin du paquet.
     * 
     * @param plis Le plis à ajouter.
     */
    public void addPlis(Plis plis) {
        for (int i = 0; i < plis.getPlis().length; i++)
            this.add(plis.getPlis()[i]);

        plis = null;  // Supprime le plis une fois qu'il a été ajouté
    }

    // Accesseurs pour l'indicateur de sélection de la carte
    public int getCurrentAcessIndex() {
        return currentAcessIndex;
    }

    public void setCurrentAcessIndex(int currentAcessIndex) {
        this.currentAcessIndex = currentAcessIndex;
    }

    public void RAZCurrentAcessIndex() {
        currentAcessIndex = 0;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du paquet.
     * Cette méthode génère une liste de toutes les cartes du paquet sous forme de chaîne.
     * 
     * @return Une chaîne de caractères représentant toutes les cartes du paquet.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Carte carte : this)
            sb.append(carte.toString()).append("\n");  // Ajoute chaque carte au StringBuilder

        return sb.toString();  // Retourne la liste complète des cartes
    }
}