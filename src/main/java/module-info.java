module com.example.sqlinsertconv {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.hiprofession.sqlinsertconv to javafx.fxml;
    exports com.hiprofession.sqlinsertconv;
}