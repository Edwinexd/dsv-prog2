package com.gouswin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class PathFinder extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent fxml = loadFXML("start");
        scene = new Scene(fxml, fxml.prefWidth(0), fxml.prefHeight(0));
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.setResizable(true); // TODO: Make this false
        stage.show();
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}