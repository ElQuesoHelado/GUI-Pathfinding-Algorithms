module com.jjac.pathfindinggui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.jjac.pathfindinggui to javafx.fxml;
    exports com.jjac.pathfindinggui;
}