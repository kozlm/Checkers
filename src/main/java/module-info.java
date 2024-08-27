module com.example.checkers {
    requires javafx.controls;
    requires javafx.fxml;


    opens gui to javafx.fxml;
    exports gui;

    opens gamelogic;
    exports gamelogic;
    exports gamelogic.pieces;
    opens gamelogic.pieces;
}