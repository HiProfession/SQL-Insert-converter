module com.example.gldb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.gldb to javafx.fxml;
    exports com.example.gldb;
}