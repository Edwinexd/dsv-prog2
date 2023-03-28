package com.gouswin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class StartController {

    private static final String MAP_FILE = "europa.gif";

    private boolean unsavedChanges = false;

    @FXML
    private ImageView map;


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
            // TODO confirm to discard current map
            return;
        }
        Image image = new Image(MAP_FILE);

        Stage stage = (Stage) newMap.getParentPopup().getOwnerWindow();
        
        stage.setHeight(stage.getHeight() + image.getHeight());
        map.setFitHeight(image.getHeight());
        
        if (stage.getWidth() < image.getWidth()) {
            stage.setWidth(image.getWidth());
            map.setFitWidth(image.getWidth());
        }

        map.setImage(image);
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

}
