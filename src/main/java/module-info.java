module com.example.flipnic_save_edit {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.flipnic_save_edit to javafx.fxml;
    exports com.example.flipnic_save_edit;
}