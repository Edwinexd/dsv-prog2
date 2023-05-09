package com.gouswin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu8469
 */
public class PathFinder extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("start.fxml"));
        Parent fxml = loader.load();
        Controller controller = (Controller) loader.getController();
        controller.setup(stage);

        scene = new Scene(fxml, fxml.prefWidth(0), fxml.prefHeight(0));
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // private Parent loadFXML(String fxml) throws IOException {
    // }

    public static void main(String[] args) {
        launch();
    }

}