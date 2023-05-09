module login.formlogin {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.jackson2;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;

    opens app to javafx.fxml;
    opens model to javafx.base, org.hibernate.orm.core;
    exports app;
}
