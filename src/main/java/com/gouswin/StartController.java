package com.gouswin;

import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class StartController {

    private Circle[] circlesSelected = new Circle[2]; // store selected circles (highlighted in red in the UI);

    private static final String MAP_FILE = "europa.gif";

    private boolean unsavedChanges = false;
    private ListGraph listGraph = null;
    private HashMap<Circle, Node> drawnNodes = new HashMap<>();
    private HashMap<Line, Edge> drawnEdges = new HashMap<>();

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

    private boolean selectCircle(Circle circle) {
        for (int i = 0; i < circlesSelected.length; i++) {
            if (circlesSelected[i] == circle) {
                return false;
            } else if (circlesSelected[i] == null) {
                circlesSelected[i] = circle;
                return true;
            }
        }
        return false;
    }

    private boolean unselectCircle(Circle circle) {
        for (int i = 0; i < circlesSelected.length; i++) {
            if (circlesSelected[i] == circle) {
                circlesSelected[i] = null;
                return true;
            }
        }
        return false;
    }

    private void drawNode(Node n) {
        Circle circle = new Circle(n.getCoordinate().getX(), n.getCoordinate().getY(), 10);
        // change color of circle to a random color
        circle.setCursor(Cursor.HAND);
        circle.setFill(javafx.scene.paint.Color.BLUE);
        circle.onMouseClickedProperty().set(e -> {
            Circle clickedCircle = (Circle) e.getTarget();
            if (clickedCircle.getFill() == javafx.scene.paint.Color.BLUE) {
                if (selectCircle(clickedCircle)) {
                    clickedCircle.setFill(javafx.scene.paint.Color.RED);
                }
                ;
            } else {
                if (unselectCircle(clickedCircle)) {
                    clickedCircle.setFill(javafx.scene.paint.Color.BLUE);
                }
            }

        });
        drawnNodes.put(circle, n);
        mapPane.getChildren().add(circle);

    }

    private void drawNodes() {
        HashSet<Node> targetNodes = listGraph.getNodes();
        targetNodes.removeIf(node -> drawnNodes.values().contains(node));
        for (Node n : targetNodes) {
            drawNode(n);
        }
    }

    private void drawEdge(Edge e) {
        Line line = new Line(e.getOrigin().getCoordinate().getX(), e.getOrigin().getCoordinate().getY(),
                e.getDestination().getCoordinate().getX(), e.getDestination().getCoordinate().getY());
        line.setStrokeWidth(2);
        line.setStroke(javafx.scene.paint.Color.BLACK);
        drawnEdges.put(line, e);
        mapPane.getChildren().add(line);
    }

    private void drawEdges() {
        Set<Edge> targetEdges = listGraph.getEdges();
        targetEdges.removeIf(edge -> drawnEdges.values().contains(edge));
        for (Edge e : targetEdges) {
            System.out.println(e);
            drawEdge(e);
        }
    }

    private void drawMap() {
        drawNodes();
        drawEdges();
    }

    private boolean discardChanges() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Unsaved changes, continue anyway?", ButtonType.OK,
                ButtonType.CANCEL);
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
        setImage();
        this.listGraph = new ListGraph();
    }

    private void setImage() {
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
    }

    @FXML
    private void findPathAction() {
        // TODO This doesnt actually ensure we have two circles selected
        if (circlesSelected[1] == null) {
            // if one or less circles are selected, then prompt the user to select two
            // circles
            Alert alert = new Alert(AlertType.ERROR, "Please select two places", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        Node start = drawnNodes.get(circlesSelected[0]);
        Node end = drawnNodes.get(circlesSelected[1]);
        ArrayList<Edge> path = listGraph.getPath(start, end);
        if (path == null) {
            Alert alert = new Alert(AlertType.ERROR, "No path found", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Path found", ButtonType.OK);
            alert.setTitle("%s to %s".formatted(start.getName(), end.getName()));
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            String trajectory = "";
            int total = 0;
            for (Edge edge : path) {
                total += edge.getWeight();
                trajectory += " to %s by %s takes %i \n".formatted(edge.getDestination().getName(),
                        edge.getTravelType(), edge.getWeight());

            }
            trajectory += "Total %i".formatted(total);
            alert.setContentText(trajectory);
        }

    }

    @FXML
    private void newConnectionAction() {
        // TODO This doesnt actually ensure we have two circles selected
        if (circlesSelected[1] == null) {
            // if one or less circles are selected, then prompt the user to select two
            // circles
            Alert alert = new Alert(AlertType.ERROR, "Please select two places", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        Node one = drawnNodes.get(circlesSelected[0]);
        Node two = drawnNodes.get(circlesSelected[1]);
        if (listGraph.getPath(one, two) != null) {
            Alert alert = new Alert(AlertType.ERROR, "Connection already exists", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection form %s to %s".formatted(one.getName(), two.getName()));
        Label nameLabel = new Label("Name:");

        Label weightLabel = new Label("Time:");
        TextField weightField  = new TextField();

        GridPane grid = new GridPane();
        grid.add(nameLabel, 0, 0);
        grid.add(dialog.getEditor(), 1, 0);
        grid.add(weightLabel, 0, 1);
        grid.add(weightField, 1, 1);



        dialog.getDialogPane().setContent(grid);

        
        dialog.showAndWait();

        String name = dialog.getEditor().getText();
        int weight = Integer.parseInt(weightField.getText());

        System.out.println(name);
        System.out.println(weight);


        // TODO Fix this funkyness
        listGraph.connect(one, two, weight, name);
        listGraph.connect(two, one, weight, name);
        unsavedChanges = true;
        drawMap();
        
    }

    @FXML
    private void changeConnectionAction() {
        // TODO
    }

    @FXML
    private void openMapAction() throws IOException {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        String lines = String.join("\n", Files.readAllLines(Paths.get("europa.graph"), StandardCharsets.UTF_8));
        listGraph = ListGraph.desterialise(lines);
        setImage();
        drawMap();
    }

    @FXML
    private void saveMapAction() throws IOException {
        String output = "file:%s\n".formatted(MAP_FILE) + listGraph.seraliaze();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("europa.graph"),
                StandardCharsets.UTF_8)) {
            writer.write(output);
        }

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
            String res = dialog.getResult();
            if (res != null) {
                listGraph.add(new Node(res, coordinate));
                unsavedChanges = true;
                drawMap();
            }
        });
    }

}
