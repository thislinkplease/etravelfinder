module DoAnCoSo01
{
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires jbcrypt;

    exports src to javafx.graphics;
    opens src.Controller to javafx.fxml;
    opens Entity to org.hibernate.orm.core, javafx.base;
}