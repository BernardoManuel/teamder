module login.formlogin {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;

    opens app to javafx.fxml;
    opens model to javafx.base, org.hibernate.orm.core;
    exports app;
}
