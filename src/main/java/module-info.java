module editortexto {
    requires javafx.controls;
    requires javafx.fxml;

    opens editortexto to javafx.fxml;
    exports editortexto;
}
