/*
* PROG2 VT2023, Inlämningsuppgift, del 2
* Grupp 069
* Erik Lind Gou-Said - erli1872
* Edwin Sundberg - edsu8469
*/

package com.gouswin;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javafx.scene.image.WritableImage;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

class NodeEdge {

}

public class Controller {

    private static final String DEFAULT_MAP_FILE = "europa.gif";
    private static final int EDGE_WIDTH = 2;
    // TODO: Figure out why this needs to be ~40 + better var name
    private static final int MENU_HEIGHT = 40;

    private ListGraph<Node> listGraph = null;

    private String mapFile = DEFAULT_MAP_FILE;

    // (highlighted in red in the UI);
    private Node[] nodesSelected = new Node[2];

    private boolean unsavedChanges = false;
    private Set<Node> drawnNodes = new HashSet<>();
    private HashMap<Node, HashMap<Edge<Node>, Line>> drawnEdges = new HashMap<>();

    // FXML containers
    @FXML
    private BorderPane mainPane; // Wrapper around the entire UI

    @FXML
    private Pane outputArea; // Container for the map image along with nodes and edges

    @FXML
    private Pane navPane; // Wrapper around the navigation UI

    // Elements
    @FXML
    private ImageView map;

    // Menu items
    @FXML
    private MenuItem menuNewMap;
    @FXML
    private MenuItem menuOpenFile;
    @FXML
    private MenuItem menuSaveFile;
    @FXML
    private MenuItem menuSaveImage;
    @FXML
    private MenuItem menuExit;

    // Buttons
    @FXML
    private Button btnNewPlace;

    // Helper methods
    public void setup(Stage stage) {
        stage.setOnCloseRequest(e -> {
            if (unsavedChanges && !discardChanges()) {
                e.consume();
            }
        });
    }

    private void clearState() {
        unsavedChanges = false;
        listGraph = null;
        // Remove elem if in drawnNodes or drawnEdges.anyKey.anyKey.value
        List<Line> lines = new ArrayList<>();
        for (HashMap<Edge<Node>, Line> edges : drawnEdges.values()) {
            lines.addAll(edges.values());
        }
        outputArea.getChildren().removeIf(elem -> drawnNodes.contains(elem) || lines.contains(elem));
        drawnNodes.clear();
        drawnEdges.clear();
        Arrays.fill(nodesSelected, null);
        clearImage();
    }

    private void displayError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error!");
        alert.setHeaderText("");
        alert.showAndWait();
    }

    private boolean selectNode(Node node) {
        for (int i = 0; i < nodesSelected.length; i++) {
            if (nodesSelected[i] == node) {
                return false;
            } else if (nodesSelected[i] == null) {
                nodesSelected[i] = node;
                return true;
            }
        }
        return false;
    }

    private boolean unselectNode(Node node) {
        for (int i = 0; i < nodesSelected.length; i++) {
            if (nodesSelected[i] == node) {
                nodesSelected[i] = null;
                return true;
            }
        }
        return false;
    }

    private boolean selectionIsComplete() {
        for (int i = 0; i < nodesSelected.length; i++) {
            if (nodesSelected[i] == null) {
                return false;
            }
        }
        return true;
    }

    private void drawNode(Node n) {
        n.setOnMouseClicked(e -> {
            Node clickedNode = (Node) e.getTarget();
            if (!clickedNode.isSelected()) {
                if (selectNode(clickedNode)) {
                    clickedNode.setSelected(true);
                }
            } else {
                if (unselectNode(clickedNode)) {
                    clickedNode.setSelected(false);
                }
            }

        });
        drawnNodes.add(n);
        outputArea.getChildren().add(n);

    }

    private void drawNodes() {
        for (Node n : listGraph.getNodes()) {
            if (!drawnNodes.contains(n)) {
                drawNode(n);
            }
        }
    }

    private void drawEdge(Node origin, Edge<Node> edge) {
        Line line = new Line(origin.getCoordinate().getX(), origin.getCoordinate().getY(),
                edge.getDestination().getCoordinate().getX(), edge.getDestination().getCoordinate().getY());
        line.setStrokeWidth(EDGE_WIDTH);
        line.setStroke(javafx.scene.paint.Color.BLACK);
        line.setMouseTransparent(true);
        line.setDisable(true);
        HashMap<Edge<Node>, Line> map = drawnEdges.get(origin);
        if (map == null) {
            map = new HashMap<>();
            drawnEdges.put(origin, map);
        }
        map.put(edge, line);
        outputArea.getChildren().add(line);
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
        alert.showAndWait();

        return alert.getResult() == ButtonType.OK;
    }

    private ConnectionDialogResult getConnectionProperties(Node one, Node two, Edge<Node> edge, boolean editableName,
            boolean editableWeight) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "");
        alert.setTitle("Connection");
        alert.setHeaderText("Connection from %s to %s".formatted(one.getName(), two.getName()));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setDisable(!editableName);

        Label weightLabel = new Label("Time:");
        TextField weightField = new TextField();
        weightField.setDisable(!editableWeight);

        if (edge != null) {
            nameField.setText(edge.getName());
            weightField.setText(String.valueOf(edge.getWeight()));

        }

        GridPane grid = new GridPane();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(weightLabel, 0, 1);
        grid.add(weightField, 1, 1);

        alert.getDialogPane().setContent(grid);

        alert.showAndWait();

        if (alert.getResult() != ButtonType.OK) {
            return null;
        }

        int weight;
        try {
            weight = Integer.parseInt(weightField.getText());
        } catch (NumberFormatException e) {
            displayError("Weight must be an integer!");
            return null;
        }

        return new ConnectionDialogResult(nameField.getText(), weight);

    }

    // Image handling
    private void clearImage() {
        mapFile = null;
        map.setImage(null);
        map.setFitHeight(0);
        map.setFitWidth(0);
        outputArea.setPrefHeight(0);
        outputArea.setPrefWidth(0);

        Stage stage = (Stage) menuNewMap.getParentPopup().getOwnerWindow();

        stage.setHeight(navPane.getHeight() + MENU_HEIGHT);
    }

    private void setImage(String mapFile) {
        if (mapFile == null) {
            throw new IllegalArgumentException("mapFile cannot be null");
        }
        this.mapFile = mapFile;

        Image image = new Image(mapFile);

        Stage stage = (Stage) menuNewMap.getParentPopup().getOwnerWindow();

        if (stage.getHeight() - navPane.getHeight() < image.getHeight()) {
            stage.setHeight(navPane.getHeight() + image.getHeight() + MENU_HEIGHT);
            map.setFitHeight(image.getHeight());
            outputArea.setPrefHeight(image.getHeight() + MENU_HEIGHT);
        }

        if (stage.getWidth() < image.getWidth()) {
            stage.setWidth(image.getWidth());
            map.setFitWidth(image.getWidth());
            outputArea.setPrefWidth(image.getWidth());
        }

        map.setImage(image);
    }

    // Seralization
    private String serialize() {
        StringBuilder res = new StringBuilder();

        res.append("file:%s\n".formatted(mapFile));
        res.append(listGraph.getNodes().stream().map(
                node -> "%s;%f;%f".formatted(node.getName(), node.getCoordinate().getX(), node.getCoordinate().getY()))
                .collect(Collectors.joining(";")));
        res.append("\n");
        for (Node n : listGraph.getNodes()) {
            for (Edge<Node> edge : listGraph.getEdgesFrom(n)) {
                res.append("%s;%s;%s;%d\n".formatted(n.getName(), edge.getDestination().getName(), edge.getName(),
                        edge.getWeight()));
            }
        }
        return res.toString();
    }

    private ListGraph<Node> deserialize(String input) {
        ListGraph<Node> res = new ListGraph<>();
        String[] lines = input.split("\n");
        HashMap<String, Node> nameNode = new HashMap<>();

        String[] nodeTokens = lines[1].split(";");
        for (int i = 0; i < nodeTokens.length; i += 3) {
            // TODO Dont use replace
            Node node = new Node(nodeTokens[i], new Coordinate(Double.parseDouble(nodeTokens[i + 1].replace(",", ".")),
                    Double.parseDouble(nodeTokens[i + 2].replace(",", "."))));
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
                // Connection may already exist because ListGraph creates both directions while
                // both directions are stored individually in the file
            }
        }
        return res;
    }

    // Event handlers
    @FXML
    private void newMapAction() {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        clearState();
        setImage(DEFAULT_MAP_FILE);
        this.listGraph = new ListGraph<>();
    }

    @FXML
    private void findPathAction() {
        if (!selectionIsComplete()) {
            displayError("Please select two places");
            return;
        }
        Node start = nodesSelected[0];
        Node end = nodesSelected[1];
        List<Edge<Node>> path = listGraph.getPath(start, end);
        if (path == null) {
            displayError("No path found");
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION, "Path found", ButtonType.OK);
        alert.setTitle("%s to %s".formatted(start.getName(), end.getName()));
        alert.setHeaderText("");
        String trajectory = "";
        int total = 0;
        for (int i = 0; i < path.size(); i++) {
            Edge<Node> edge = path.get(i);
            total += edge.getWeight();
            String from = i == 0 ? start.getName() : path.get(i - 1).getDestination().getName();
            trajectory += "%s to %s by %s takes %d \n".formatted(from, edge.getDestination().getName(),
                    edge.getName(), edge.getWeight());
        }
        trajectory += "Total %d".formatted(total);
        alert.setContentText(trajectory);
        alert.showAndWait();

    }

    @FXML
    private void newConnectionAction() {
        if (!selectionIsComplete()) {
            displayError("Please select two places");
            return;
        }
        Node one = nodesSelected[0];
        Node two = nodesSelected[1];
        if (listGraph.hasConnection(one, two)) {
            displayError("Connection already exists");
            return;
        }
        ConnectionDialogResult result = getConnectionProperties(one, two, null, true, true);
        if (result == null) {
            return;
        }

        listGraph.connect(one, two, result.getName(), result.getWeight());
        unsavedChanges = true;
        drawMap();

    }

    @FXML
    private void showConnectionAction() {
        if (!selectionIsComplete()) {
            displayError("Please select two places");
            return;
        }
        Node one = nodesSelected[0];
        Node two = nodesSelected[1];
        Edge<Node> e = listGraph.getEdgeBetween(one, two);
        if (e == null) {
            displayError("No connection exists");
            return;
        }
        getConnectionProperties(one, two, e, false, false);
    }

    @FXML
    private void changeConnectionAction() {
        if (!selectionIsComplete()) {
            displayError("Please select two places");
            return;
        }
        Node one = nodesSelected[0];
        Node two = nodesSelected[1];
        Edge<Node> edge = listGraph.getEdgeBetween(one, two);
        if (edge == null) {
            displayError("No connection exists");
            return;
        }
        ConnectionDialogResult result = getConnectionProperties(one, two, edge, false, true);
        if (result == null) {
            return;
        }

        try {
            listGraph.setConnectionWeight(one, two, result.getWeight());
        } catch (IllegalArgumentException e) {
            displayError(e.getMessage());
            return;
        }

    }

    @FXML
    private void openMapAction() throws IOException {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        clearState();
        String lines = String.join("\n", Files.readAllLines(Paths.get("europa.graph"), StandardCharsets.UTF_8));
        this.listGraph = deserialize(lines);
        try {
            setImage(lines.split("\n")[0].split(":")[1]);
        } catch (IllegalArgumentException e) {
            displayError("IllegalArgumentException: " + e.getMessage());
            return;
        }
        drawMap();
    }

    @FXML
    private void saveMapAction() throws IOException {
        if (mapFile == null) {
            displayError("No map is open!");
            return;
        }
        String output = serialize();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("europa.graph"),
                StandardCharsets.UTF_8)) {
            writer.write(output);
        }
        unsavedChanges = false;

    }

    @FXML
    private void saveImageAction() {
        try {
            WritableImage image = mainPane.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "png", new File("capture.png"));
        } catch (IOException e) {
            displayError("IOException: " + e.getMessage());
            return;
        }
    }

    @FXML
    private void exitAction() {
        if (unsavedChanges && !discardChanges()) {
            return;
        }
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void newPlaceAction() {
        btnNewPlace.setDisable(true);
        map.setCursor(Cursor.CROSSHAIR);
        map.setOnMouseClicked(e -> {
            Coordinate coordinate = new Coordinate(e.getX(), e.getY());
            map.setOnMouseClicked(null);
            map.setCursor(Cursor.DEFAULT);
            btnNewPlace.setDisable(false);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Name");
            dialog.setHeaderText("");
            dialog.setContentText("Name of place");
            dialog.showAndWait();
            String res = dialog.getResult();
            if (res != null) {
                if (listGraph.getNodes().stream().anyMatch(node -> node.getName().equals(res))) {
                    displayError("The name " + res + " is already in use!");
                    return;
                }
                listGraph.add(new Node(res, coordinate));
                unsavedChanges = true;
                drawMap();
            }
        });
    }

}
