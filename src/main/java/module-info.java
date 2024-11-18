module proj.teachers_proj {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens proj.teachers_proj to javafx.fxml;
    exports proj.teachers_proj;
}