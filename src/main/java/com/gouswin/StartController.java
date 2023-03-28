package com.gouswin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class StartController {

    private static final String MAP_FILE = "europa.gif";

    private boolean unsavedChanges = false;
    private ListGraph listGraph = null;
    private HashMap<Node, Circle> drawnNodes = new HashMap<>();

    @FXML
    private ImageView map;
    @FXML
    private Pane mapPane;



    // Menu items
    @FXML
    private MenuItem newMap;
    @FXML
    private MenuItem openMap;
    @FXML
    private MenuItem saveMap;
    @FXML
    private MenuItem saveImage;
    @FXML
    private MenuItem exit;

    // Buttons
    @FXML
    private Button newPlace;


    private void drawMap() {
        // Delete all drawn nodes
        drawnNodes.clear();
        for (Node node : listGraph.getNodes()) {
            Circle circle = new Circle(node.getCoordinate().getX(), node.getCoordinate().getY(), 10);
            // change color of circle to a random color
            circle.setCursor(Cursor.HAND);
            circle.setFill(javafx.scene.paint.Color.RED);
            drawnNodes.put(node, circle);
            mapPane.getChildren().add(circle);
        }

    }

    private boolean discardChanges() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Unsaved changes, continue anyway?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Warning!");
        alert.setHeaderText("");
        alert.initOwner(map.getScene().getWindow());
        alert.showAndWait();

        return alert.getResult() == ButtonType.OK;
    }

    @FXML
    private void newMapAction() {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        Image image = new Image(MAP_FILE);

        Stage stage = (Stage) newMap.getParentPopup().getOwnerWindow();
        
        stage.setHeight(stage.getHeight() + image.getHeight());
        map.setFitHeight(image.getHeight());
        mapPane.setPrefHeight(image.getHeight());
        
        if (stage.getWidth() < image.getWidth()) {
            stage.setWidth(image.getWidth());
            map.setFitWidth(image.getWidth());
            mapPane.setPrefWidth(image.getWidth());
        }

        map.setImage(image);
        this.listGraph = new ListGraph();
    }


    @FXML
    private void openMapAction() {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        // TODO open map
    }

    @FXML
    private void saveMapAction() {
        // TODO save map
    }

    @FXML
    private void saveImageAction() {
        // TODO save image of map
    }

    @FXML
    private void exitAction() {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        System.exit(0);
    }

    @FXML
    private void newPlaceAction() {
        newPlace.setDisable(true);
        map.setCursor(Cursor.CROSSHAIR);
        map.setOnMouseClicked(e -> {
            Coordinate coordinate = new Coordinate(e.getX(), e.getY());
            map.setOnMouseClicked(null);
            map.setCursor(Cursor.DEFAULT);
            newPlace.setDisable(false);
            // Have user input location name
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Name");
            dialog.setHeaderText("");
            dialog.setContentText("Name of place");
            dialog.showAndWait();

            listGraph.add(new Node(dialog.getResult(), coordinate));
            unsavedChanges = true;
            // TODO Redraw map nodes
            drawMap();
        });
    }

}
