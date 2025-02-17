package src.main;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * La classe Paquet représente un paquet de 32 cartes de jeu.
 * Elle contient une liste de cartes et fournit des méthodes pour créer le paquet, 
 * mélanger les cartes, et ajouter des plis.
 * La classe Paquet hérite de ArrayList et contient une classe interne Carte.
 */
public class Paquet {
    /**
     * La classe Carte représente une carte de jeu avec une couleur et un type (valeur).
     * Elle est comparable en fonction de sa valeur, et elle est sérialisable pour la transmission réseau.
     */
    public static class Carte implements Comparable<Carte> {

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
         * Utilise une regex pour récupérer la Couleur et le Type dans la chaîne
         * et retourne un objet carte correspondant.
         * 
         * @param str La chaîne de caractères représentant une carte.
         * @return L'objet carte correspondant, ou null si la chaîne ne correspond pas.
         */
        public static Paquet.Carte parseCarte(String str) {
            str = str.toUpperCase();    // Normalise la chaîne
            String regex = "\\b(COEUR|CARREAU|PIQUE|TREFLE)\\b.*?\\b(AS|SEPT|HUIT|NEUF|DIX|VALLET|DAMME|ROI)\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);

            if (matcher.find()) {
                try {   
                    Paquet.Carte.Couleur couleur = Paquet.Carte.Couleur.valueOf(matcher.group(1));
                    Paquet.Carte.Type type = Paquet.Carte.Type.valueOf(matcher.group(2));
                    return new Carte(couleur, type);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        /**
         * Compare deux cartes en fonction de leur valeur en points.
         * 
         * @param c La carte à comparer.
         * @return Un entier indiquant si cette carte est plus petite, égale ou plus grande que l'autre.
         */
        @Override
        public int compareTo(Carte c) {
            // Si this est un atout et pas c, this est forcement supérieur
            if (this.getCouleur().getIsAtout() && !c.getCouleur().getIsAtout())
                return 1;
            // Même logique sauf qu'ici c est atout
            else if (!this.getCouleur().getIsAtout() && c.getCouleur().getIsAtout())
                return 0;

            // Si les 2 cartes sont atout ou aucune d'elles ne l'est, on compare leur nb de points
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
    private List<Carte> cartes = new ArrayList<Paquet.Carte>(); // Liste de toutes les cartes


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
        for (int i = 0; i < Carte.Couleur.values().length; i++)
            for (int j = 0; j < Carte.Type.values().length; j++)
                cartes.add(new Carte(Carte.Couleur.values()[i], Carte.Type.values()[j]));

        shufle();  // Mélange les cartes
    }

    /**
     * Mélange les cartes du paquet de manière aléatoire en utilisant un algorithme de permutation.
     */
    private void shufle() {
        Random ran = new Random();
        int indx1, indx2;

        // Mélange les cartes n^3 fois
        for (int i = 0; i < cartes.size() * cartes.size() * cartes.size(); i++) {
            do {
                indx1 = ran.nextInt(cartes.size());
                indx2 = ran.nextInt(cartes.size());
            } while (indx1 == indx2);  // Assure que les index ne sont pas identiques

            // Échange les cartes
            Paquet.Carte tmp = cartes.get(indx1);
            cartes.set(indx1, cartes.get(indx2));
            cartes.set(indx2, tmp);
        }
    }

    /**
     * Coupe le paquet en simulant une coupe de belote.  
     * L'index de coupe est choisi selon une loi normale centrée sur la moitié du paquet avec un écart-type de 8.  
     * Réorganise les cartes en effectuant une rotation.
     */
    public void coupe() {
        if (cartes.isEmpty()) return; // Vérifie si le paquet est vide

        int taille = cartes.size();
        Random rand = new Random();

        // Génère un index de coupe avec une distribution normale (moyenne = taille/2, écart-type = 8)
        int indexCoupe;
        do {
            indexCoupe = (int) Math.round(rand.nextGaussian() * 8 + taille / 2);
        } while (indexCoupe < 1 || indexCoupe >= taille); // S'assure que l'index est valide

        // Applique la coupe en décalant les cartes
        Collections.rotate(cartes, taille - indexCoupe);
    }

    /**
     * Retourne la carte suivante à partir de l'indice courant.
     * 
     * @return La carte suivante dans le paquet.
     */
    public Paquet.Carte getNext() {
        return cartes.get(currentAcessIndex++);
    }

    /**
     * Ajoute un plis (ensemble de cartes) au paquet.
     * Les cartes du plis sont ajoutées à la fin du paquet.
     * 
     * @param plis Le plis à ajouter.
     */
    public void addPlis(Plis plis) {
        for (int i = 0; i < plis.getPlis().length; i++)
            cartes.add(plis.getPlis()[i]);

        plis = null;  // Supprime le plis une fois qu'il a été ajouté
    }

    /**
     * Réinitialise l'indice d'accès à 0, ce qui permet de recommencer à parcourir
     * le paquet de cartes depuis le début.
     */
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
        for (Carte carte : cartes)
            sb.append(carte.toString()).append("\n");  // Ajoute chaque carte au StringBuilder

        return sb.toString();  // Retourne la liste complète des cartes
    }

    public List<Carte> getCartes() {
        return cartes;
    }
}