module com.gouswin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens com.gouswin to javafx.fxml;

    exports com.gouswin;
}
