module login.formlogin {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.persistence;
    requires java.sql;
    requires java.desktop;


    opens app to javafx.fxml;
    exports app;

}