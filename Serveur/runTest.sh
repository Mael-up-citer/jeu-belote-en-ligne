#!/bin/bash

# Répertoire des bibliothèques
LIB_DIR="./lib"
# Dossier de compilation
BIN_DIR="./bin"
# Chemin du fichier des tests
TEST_DIR="src/tests"
# Chemin des fichiers sources principaux
SRC_DIR="src/main"

# JUnit et JavaFX SDK
JUNIT_JAR="$LIB_DIR/junit-platform-console-standalone-1.11.3.jar"
JAVAFX_SDK="$LIB_DIR/javafx-sdk-21.0.5/lib"
JAVAFX_MODULES="javafx.controls,javafx.fxml"

# Vérifier que les fichiers de tests existent
if [[ ! -d "$TEST_DIR" ]]; then
  echo "Le répertoire $TEST_DIR n'existe pas."
  exit 1
fi

# Créer le dossier bin s'il n'existe pas
mkdir -p "$BIN_DIR"

# Compilation des fichiers sources
echo "Compilation des fichiers sources..."
javac -d "$BIN_DIR" --module-path "$JAVAFX_SDK" --add-modules $JAVAFX_MODULES -cp "$LIB_DIR/junit-platform-console-standalone-1.11.3.jar" $(find "$SRC_DIR" -name "*.java") $(find "$TEST_DIR" -name "*.java")

# Vérifier si la compilation a réussi
if [[ $? -eq 0 ]]; then
  echo "Compilation réussie."

  # Exécution des tests avec JUnit
  echo "Exécution des tests..."
  java -jar "$JUNIT_JAR" --class-path "$BIN_DIR" --scan-class-path

  # Supprimer les fichiers .class générés après les tests (optionnel)
  rm -r "$BIN_DIR"/* &> /dev/null
else
  echo "Erreur lors de la compilation."
fi