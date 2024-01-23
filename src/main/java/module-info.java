module editortexto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens editortexto to javafx.fxml;
    exports editortexto;
}
