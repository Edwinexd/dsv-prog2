module com.gouswin {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gouswin to javafx.fxml;
    exports com.gouswin;
}
