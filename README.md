
# Projet Belote en ligne avec IA - Java

## Description

Ce projet est un jeu de **Belote en ligne** développé en Java dans le cadre d'un projet d'intelligence artificielle. Il propose une interface utilisateur intuitive, un jeu en réseau permettant de jouer avec d'autres joueurs en ligne, et une IA capable de jouer à trois niveaux de difficulté.

## Fonctionnalités

- **UI (Interface utilisateur)** : 
  - Interface graphique conviviale pour jouer à la Belote.
  - Affichage des cartes, des scores, et de l'état de la partie en temps réel.
  
- **Jeu en réseau** : 
  - Possibilité de jouer à la Belote en ligne avec d'autres joueurs.
  - Gestion des connexions et des échanges de données entre les joueurs via un serveur.
  
- **IA avec 3 niveaux de difficulté** : 
  - **Facile** : L'IA fait des choix simples basés sur des règles de base.
  - **Moyenne** : L'IA optimise ses choix en fonction des cartes jouées et de la phase du jeu.
  - **Difficile** : L'IA utilise des techniques avancées d'analyse pour prédire les meilleurs coups et stratégies.

## Technologies utilisées

- **Langage** : Java
- **Bibliothèque pour l'UI** : JavaFX (ou Swing si préféré)
- **Communication réseau** : Sockets TCP/IP
- **Logique de jeu** : Implémentation des règles de la Belote avec gestion des enchères et des tours de jeu.
- **IA** : Algorithmes d'intelligence artificielle pour la prise de décision basée sur les cartes disponibles et les stratégies possibles.

## Installation

1. Clonez ce repository :
   ```bash
   git clone https://github.com/username/projet-belote-en-ligne.git
   ```

2. Assurez-vous d'avoir **Java 8 ou supérieur** installé sur votre machine.

3. Compilez le projet en utilisant Maven (ou Gradle si vous préférez) :
   ```bash
   mvn install
   ```

4. Exécutez l'application avec la commande suivante :
   ```bash
   mvn exec:java
   ```

## Utilisation

### Lancer le serveur

Avant de jouer en ligne, vous devez démarrer un serveur pour gérer les connexions des joueurs. 

1. Exécutez la classe `Serveur` pour démarrer le serveur en écoute des connexions des joueurs :
   ```bash
   java Serveur
   ```

2. Le serveur gérera les connexions des joueurs, les échanges de données et la synchronisation des parties.

### Lancer un client (joueur)

Pour démarrer une session de jeu en tant que joueur, exécutez la classe `Client` :
```bash
java Client
```

L'application vous demandera de vous connecter à un serveur existant (indiquez l'adresse IP et le port).

Vous pouvez également choisir de jouer contre l'IA en sélectionnant l'option correspondante dans le menu.

### Choisir un niveau de difficulté pour l'IA

Lors de la configuration de la partie, vous pourrez choisir entre trois niveaux de difficulté pour l'IA :

- Facile
- Moyen
- Difficile

L'IA choisira ses actions en fonction du niveau sélectionné.

## Structure du projet

```
.
├── src/
│   ├── com/
│   │   ├── projetbelote/
│   │   │   ├── serveur/
│   │   │   ├── client/
│   │   │   ├── jeu/
│   │   │   └── ia/
│   │   ├── ui/
│   └── Main.java
├── pom.xml
└── README.md
```

## Contribuer

Les contributions sont les bienvenues ! Si vous souhaitez améliorer le projet, veuillez suivre les étapes suivantes :

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalité`)
3. Commitez vos modifications (`git commit -am 'Ajout d'une nouvelle fonctionnalité'`)
4. Poussez sur la branche (`git push origin feature/nouvelle-fonctionnalité`)
5. Ouvrez une pull request

## Auteurs

- [Votre nom] - Développeur principal
- [Autres contributeurs] - Contributeurs

## License

Ce projet est sous **licence MIT**. Voir le fichier [LICENSE](LICENSE) pour plus de détails.
