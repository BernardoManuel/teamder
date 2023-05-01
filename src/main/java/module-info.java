module login.formlogin {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;


    opens model to org.hibernate.orm.core;
    opens app to javafx.fxml;
    exports app;

}