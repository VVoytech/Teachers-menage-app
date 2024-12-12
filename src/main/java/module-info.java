module proj.teachers_proj {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;

    opens proj.teachers_proj to javafx.fxml, org.hibernate.orm.core;
    exports proj.teachers_proj;
}