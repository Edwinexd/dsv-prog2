module com.gouswin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires transitive javafx.graphics;

    opens com.gouswin to javafx.fxml, javafx.swing;

    exports com.gouswin;
}
