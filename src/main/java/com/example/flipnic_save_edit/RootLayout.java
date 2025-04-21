package com.example.flipnic_save_edit;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class RootLayout {
    @FXML
    private BorderPane borderPane;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    private void initialize() {

    }

    @FXML
    private void onCloseApp() {
        System.exit(0);
    }

    @FXML
    private void onOpenFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Flipnic save data", "*");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadFile(file);
        }
    }

    @FXML
    private void onSaveAsFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Flipnic save data", "*");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            try {
                mainApp.fs.Save(file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File saved successfully");
                alert.setContentText("Location: " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error saving file");
                alert.setContentText("Details: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onSaveFile() {
        if (mainApp.savePath.isEmpty()) {
            onSaveAsFile();
            return;
        }
        String file = mainApp.savePath;
        try {
            mainApp.fs.Save(file);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("File saved successfully");
            alert.setContentText("Location: " + file);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving file");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onAboutClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Flipnic Save Editor " + mainApp.version);
        alert.setContentText("Allows you to edit, hack, and repair save files for the Flipnic video game.");
        alert.showAndWait();
    }
}
