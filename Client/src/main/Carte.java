package main;



/**
 * La classe Carte représente une carte de jeu avec une couleur et un type (valeur).
 * Elle implémente l'interface Comparable pour permettre la comparaison des cartes par leur valeur.
 */
public class Carte implements Comparable<Carte> {
    /**
     * Enumération représentant les couleurs possibles des cartes.
     * Chaque couleur peut être définie comme "atout" ou non.
     */
    public enum Couleur {
        COEUR,
        CARREAU,
        PIQUE,
        TREFLE;

        private Boolean isAtout;

        // Constructeur par défaut : La couleur n'est pas un atout au départ
        Couleur() {
            isAtout = false;
        }

        /**
         * Indique si la couleur est l'atout.
         * 
         * @return true si la couleur est l'atout, false sinon.
         */
        public Boolean getIsAtout() {
            return isAtout;
        }

        /**
         * Définit si la couleur est un atout.
         * 
         * @param isAtout true si la couleur doit être un atout, false sinon.
         */
        public void setIsAtout(Boolean isAtout) {
            this.isAtout = isAtout;
        }
    }

    /**
     * Enumération représentant les types (ou valeurs) des cartes.
     * Chaque type de carte est associé à un certain nombre de points.
     */
    public enum Type {
        AS(11), SEPT(0), HUIT(0), NEUF(0), DIX(0), VALLET(2), DAMME(3), ROI(4);

        private final int value;  // Valeur en points d'une carte

        /**
         * Constructeur de Type qui définit la valeur d'une carte en fonction de son type.
         * 
         * @param val La valeur de la carte en points.
         */
        Type(int val) {
            value = val;
        }

        /**
         * Retourne la valeur en points associée au type de la carte.
         * 
         * @return La valeur en points de la carte.
         */
        public int getValue() {
            return value;
        }
    }

    protected Couleur couleur;  // La couleur de la carte
    protected Type type;        // Le type (valeur) de la carte

    /**
     * Constructeur de la classe Carte.
     * 
     * @param c La couleur de la carte.
     * @param t Le type (valeur) de la carte.
     */
    Carte(Couleur c, Type t) {
        couleur = c;
        type = t;
    }

    /**
     * Compare deux cartes en fonction de leur valeur en points.
     * Cette méthode est utilisée par les collections pour trier les cartes.
     * 
     * @param c La carte à comparer.
     * @return Un entier négatif, zéro ou positif si cette carte est respectivement plus petite, égale ou plus grande que l'autre.
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
        if (couleur.getIsAtout()) {
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

    /**
     * Récupère la couleur de la carte.
     * 
     * @return La couleur de la carte.
     */
    public Couleur getCouleur() {
        return couleur;
    }

    /**
     * Récupère le type de la carte.
     * 
     * @return Le type (valeur) de la carte.
     */
    public Type getType() {
        return type;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de la carte.
     * Cette méthode combine le nom du type et de la couleur pour fournir une description lisible de la carte.
     * 
     * @return Une chaîne de caractères représentant la carte, par exemple "AS de COEUR".
     */
    @Override
    public String toString() {
        return type.name() + "De" + couleur.name();
    }
}