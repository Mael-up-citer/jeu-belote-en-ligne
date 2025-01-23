package src.GUI;

import javax.swing.Action;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class Gui extends Application {

    public Gui(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Belote");
        stage.show();

        initWindows(stage);
    }

    private void initWindows(Stage stage) {
        FlowPane pane = new FlowPane(Orientation.VERTICAL);

        Label rules = new Label("Choissez un nombre\nEntrez 2 nombres qui constituerons une borne pour la recherche");
        pane.getChildren().add(rules);

        FlowPane ssPane1 = new FlowPane();
        Label l1 = new Label("born inf");
        TextField tf1 = new TextField();
        ssPane1.getChildren().addAll(l1, tf1);

        FlowPane ssPane2 = new FlowPane();
        Label l2 = new Label("born sup");
        TextField tf2 = new TextField();
        ssPane2.getChildren().addAll(l2, tf2);

        Button b = new Button("Soummetre");
        pane.getChildren().addAll(ssPane1, ssPane2, b);

        Label error = new Label();

        b.setOnAction(e -> {
            int min = 0;
            int max = 0;
            try {
                min = Integer.parseInt(tf1.getText());
                max = Integer.parseInt(tf2.getText());

                if (min >= max) {
                    error.setText("erreur min doit etre plus grand que max");
                    error.setTextFill(Color.RED);
                }
                else {
                }
            } catch (Exception ex) {
                error.setText("erreur entrez 2 nombres");
                error.setTextFill(Color.RED);
                pane.getChildren().add(error);
            }
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.sizeToScene();
    }

    private void gameWindows(Stage stage) {
        FlowPane pane = new FlowPane(Orientation.VERTICAL);

        Label rules = new Label("Choissez un nombre\nEntrez 2 nombres qui constituerons une borne pour la recherche");
        pane.getChildren().add(rules);

        FlowPane ssPane1 = new FlowPane();
        Label l1 = new Label("born inf");
        TextField tf1 = new TextField();
        ssPane1.getChildren().addAll(l1, tf1);

        FlowPane ssPane2 = new FlowPane();
        Label l2 = new Label("born sup");
        TextField tf2 = new TextField();
        ssPane2.getChildren().addAll(l2, tf2);

        Button b = new Button("Soummetre");
        pane.getChildren().addAll(ssPane1, ssPane2, b);

        Label error = new Label();

        b.setOnAction(e -> {
            int min = 0;
            int max = 0;
            try {
                min = Integer.parseInt(tf1.getText());
                max = Integer.parseInt(tf2.getText());

                if (min >= max) {
                    error.setText("erreur min doit etre plus grand que max");
                    error.setTextFill(Color.RED);
                }
                else {
                }
            } catch (Exception ex) {
                error.setText("erreur entrez 2 nombres");
                error.setTextFill(Color.RED);
                pane.getChildren().add(error);
            }
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.sizeToScene();
    }

    private void WinWindows(Stage stage) {
        FlowPane pane = new FlowPane(Orientation.VERTICAL);

        Label rules = new Label("Choissez un nombre\nEntrez 2 nombres qui constituerons une borne pour la recherche");
        pane.getChildren().add(rules);

        FlowPane ssPane1 = new FlowPane();
        Label l1 = new Label("born inf");
        TextField tf1 = new TextField();
        ssPane1.getChildren().addAll(l1, tf1);

        FlowPane ssPane2 = new FlowPane();
        Label l2 = new Label("born sup");
        TextField tf2 = new TextField();
        ssPane2.getChildren().addAll(l2, tf2);

        Button b = new Button("Soummetre");
        pane.getChildren().addAll(ssPane1, ssPane2, b);

        Label error = new Label();

        b.setOnAction(e -> {
            int min = 0;
            int max = 0;
            try {
                min = Integer.parseInt(tf1.getText());
                max = Integer.parseInt(tf2.getText());

                if (min >= max) {
                    error.setText("erreur min doit etre plus grand que max");
                    error.setTextFill(Color.RED);
                }
                else {
                }
            } catch (Exception ex) {
                error.setText("erreur entrez 2 nombres");
                error.setTextFill(Color.RED);
                pane.getChildren().add(error);
            }
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.sizeToScene();
    }
}