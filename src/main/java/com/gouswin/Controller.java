/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu
 */

package com.gouswin;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
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
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

class NodeEdge {
    
}

public class Controller {

    private Circle[] circlesSelected = new Circle[2]; // store selected circles (highlighted in red in the UI);

    private static final String MAP_FILE = "europa.gif";

    private boolean unsavedChanges = false;
    private ListGraph<Node> listGraph = null;
    private HashMap<Circle, Node> drawnNodes = new HashMap<>();
    private HashMap<Node, HashMap<Edge<Node>, Line>> drawnEdges = new HashMap<>();

    @FXML
    private ImageView map;
    @FXML
    private Pane mapPane;
    @FXML
    private Pane navPane;

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

    public void setup(Stage stage) {
        stage.setOnCloseRequest(e -> {
            if (unsavedChanges && !discardChanges()) {
                e.consume();
            }
        });
    }

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
        for (Node n : listGraph.getNodes()) {
            if (!drawnNodes.values().contains(n)) {
                drawNode(n);
            }
        }
    }

    private void drawEdge(Node origin, Edge<Node> edge) {
        Line line = new Line(origin.getCoordinate().getX(), origin.getCoordinate().getY(),
                edge.getDestination().getCoordinate().getX(), edge.getDestination().getCoordinate().getY());
        line.setStrokeWidth(2);
        line.setStroke(javafx.scene.paint.Color.BLACK);
        line.setMouseTransparent(true);
        HashMap<Edge<Node>, Line> map = drawnEdges.get(origin);
        if (map == null) {
            map = new HashMap<>();
            drawnEdges.put(origin, map);
        }
        map.put(edge, line);
        mapPane.getChildren().add(line);
    }

    private void drawEdges() {
        for (Node n : listGraph.getNodes()) {
            for (Edge<Node> e : listGraph.getEdgesFrom(n)) {
                if (drawnEdges.get(n) != null && drawnEdges.get(n).containsKey(e)) {
                    continue;
                }
                drawEdge(n, e);
            }
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
        this.listGraph = new ListGraph<>();
    }

    private void setImage() {
        Image image = new Image(MAP_FILE);

        Stage stage = (Stage) newMap.getParentPopup().getOwnerWindow();

        // System.out.println(navPane.getPrefHeight());
        // System.out.println(navPane.getHeight());
        System.out.println(stage.getHeight() - navPane.getHeight() - map.getFitHeight());
        if (stage.getHeight() - navPane.getHeight() < image.getHeight()) {
            // TODO: Figure out why this needs to be ~40
            stage.setHeight(navPane.getHeight() + image.getHeight() + 40);
            map.setFitHeight(image.getHeight());
            // TODO: Figure out why this needs to be ~40
            mapPane.setPrefHeight(image.getHeight() + 40);
        }
        // stage.setHeight(stage.getHeight() + image.getHeight());
        // map.setFitHeight(image.getHeight());
        // mapPane.setPrefHeight(image.getHeight());

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
        if (circlesSelected[1] == null || circlesSelected[0] == null) {
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
        List<Edge<Node>> path = listGraph.getPath(start, end);
        System.out.println(path);
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
            for (int i = 0; i < path.size(); i++) {
                Edge<Node> edge = path.get(i);
                total += edge.getWeight();
                String from = i == 0 ? start.getName() : path.get(i-1).getDestination().getName();
                trajectory += "%s to %s by %s takes %d \n".formatted(from, edge.getDestination().getName(),
                        edge.getName(), edge.getWeight());
            }
            trajectory += "Total %d".formatted(total);
            alert.setContentText(trajectory);
            alert.showAndWait();
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
        if (listGraph.hasConnection(one, two)) {
            Alert alert = new Alert(AlertType.ERROR, "Connection already exists", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from %s to %s".formatted(one.getName(), two.getName()));
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

        // System.out.println(name);
        // System.out.println(weight);


        listGraph.connect(one, two, name, weight);
        unsavedChanges = true;
        drawMap();

    }

    @FXML
    private void showConnectionAction() {
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
        Edge<Node> e = listGraph.getEdgeBetween(one, two);
        if (e == null) {
            Alert alert = new Alert(AlertType.ERROR, "No connection exists", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION, "" ,ButtonType.OK);
        alert.setTitle("Connection");
        alert.setHeaderText("Connection from %s to %s".formatted(one.getName(), two.getName()));

        Label nameLabel = new Label("Name:");
        TextField nameField  = new TextField();
        nameField.setText(e.getName());
        nameField.setDisable(true);

        Label weightLabel = new Label("Time:");
        TextField weightField  = new TextField();
        weightField.setText(String.valueOf(e.getWeight()));
        weightField.setDisable(true);

        GridPane grid = new GridPane();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(weightLabel, 0, 1);
        grid.add(weightField, 1, 1);

        alert.getDialogPane().setContent(grid);

        alert.showAndWait();

    }

    @FXML
    private void changeConnectionAction() {
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
        Edge<Node> edge = listGraph.getEdgeBetween(one, two);
        if (edge == null) {
            Alert alert = new Alert(AlertType.ERROR, "No connection exists", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from %s to %s".formatted(one.getName(), two.getName()));
        Label nameLabel = new Label("Name:");
        dialog.getEditor().setText(edge.getName());
        dialog.getEditor().setDisable(true);

        Label weightLabel = new Label("Time:");
        TextField weightField  = new TextField();
        weightField.setText(String.valueOf(edge.getWeight()));

        GridPane grid = new GridPane();
        grid.add(nameLabel, 0, 0);
        grid.add(dialog.getEditor(), 1, 0);
        grid.add(weightLabel, 0, 1);
        grid.add(weightField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();

        int weight;
        try {
            weight = Integer.parseInt(weightField.getText());
        } catch (NumberFormatException e) {
            // TODO Generalize displaying error
            Alert alert = new Alert(AlertType.ERROR, "", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        try {
            listGraph.setConnectionWeight(one, two, weight);
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(AlertType.ERROR, "", ButtonType.OK);
            alert.setTitle("Error!");
            alert.setHeaderText(e.getMessage());
            alert.initOwner(map.getScene().getWindow());
            alert.showAndWait();
            return;
        }

    }

    @FXML
    private void openMapAction() throws IOException {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        String lines = String.join("\n", Files.readAllLines(Paths.get("europa.graph"), StandardCharsets.UTF_8));
        listGraph = deserialize(lines); // TODO: Reimplement this here
        setImage();
        drawMap();
    }

    private ListGraph<Node> deserialize(String input) {
        ListGraph<Node> res = new ListGraph<>();
        String[] lines = input.split("\n");
        HashMap<String, Node> nameNode = new HashMap<>();
        
        String[] nodeTokens = lines[1].split(";");
        for (int i = 0; i < nodeTokens.length; i+=3) {
            // TODO Dont use replace
            Node node = new Node(nodeTokens[i], new Coordinate(Double.parseDouble(nodeTokens[i+1].replace(",", ".")), Double.parseDouble(nodeTokens[i+2].replace(",", "."))));
            nameNode.put(nodeTokens[i], node);
            res.add(node);
        }
        
        for (int i = 2; i < lines.length; i++) {
            String[] edgeData = lines[i].split(";");
            
            Node from = nameNode.get(edgeData[0]);
            Node to = nameNode.get(edgeData[1]);
            try {
                res.connect(from, to, edgeData[2], Integer.parseInt(edgeData[3]));
            } catch (IllegalStateException e) {
                // Connection already exists
            }
        }
        return res;
    }

    @FXML
    private void saveMapAction() throws IOException {
        // String output = "file:%s\n".formatted(MAP_FILE) + serialize(); // TODO: Reimplement this here
        String output = serialize();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("europa.graph"),
                StandardCharsets.UTF_8)) {
            writer.write(output);
        }
        unsavedChanges = false;

    }

    private String serialize() {

        StringBuilder res = new StringBuilder();

        res.append("file:%s\n".formatted(MAP_FILE));
        res.append("name;lat;lon\n");
        res.append(listGraph.getNodes().stream().map(node -> "%s;%f;%f".formatted(node.getName(), node.getCoordinate().getX(), node.getCoordinate().getY())).collect(Collectors.joining(";")));
        res.append("\n");
        for (Node n : listGraph.getNodes()) {
            for (Edge<Node> edge : listGraph.getEdgesFrom(n)) {
                res.append("%s;%s;%s;%d\n".formatted(n.getName(), edge.getDestination().getName(), edge.getName(), edge.getWeight()));
            }
        }
        return res.toString();
    }

    @FXML
    private void saveImageAction() {
        // Take a screenshot of the window
        // WritableImage image = mapPane.snapshot(new SnapshotParameters(), null);
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
                // Check if name already exists
                if (listGraph.getNodes().stream().anyMatch(node -> node.getName().equals(res))) {
                    // TODO Generalize displaying error
                    Alert alert = new Alert(AlertType.ERROR, "", ButtonType.OK);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Name already in use!");
                    alert.initOwner(map.getScene().getWindow());
                    alert.showAndWait();
                    return;
                }
                listGraph.add(new Node(res, coordinate));
                unsavedChanges = true;
                drawMap();
            }
        });
    }

}
