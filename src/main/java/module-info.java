module com.paktc.flipnic_save_edit {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires maven.model;
    requires plexus.utils;
    requires java.desktop;

    opens com.paktc.flipnic_save_edit to javafx.fxml;
    exports com.paktc.flipnic_save_edit;
}