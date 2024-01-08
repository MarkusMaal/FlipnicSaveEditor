package com.example.flipnic_save_edit;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    public byte[] rawData = new byte[0];
    public FlipnicSave fs = new FlipnicSave(new byte[0]);
    public FirstForm mainWindow;

    public String version = "0.3";


    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Flipnic Save Editor " + this.version);
        initRootLayout();
        showFirstForm();
    }

    private FXMLLoader initLayout(String resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(resource));
        return loader;
    }

    public void initRootLayout() throws IOException {
        FXMLLoader loader = initLayout("RootLayout.fxml");
        rootLayout = (BorderPane) loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        RootLayout controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.show();
    }

    public void showFirstForm() throws IOException {
        FXMLLoader loader = initLayout("FirstForm.fxml");
        AnchorPane firstForm = (AnchorPane) loader.load();

        FirstForm controller = loader.getController();
        this.mainWindow = controller;
        controller.setMainApp(this);
        rootLayout.setCenter(firstForm);
    }

    public static void main(String[] args) {
        launch();
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void loadFile(File saveFile) {
        try {
            Path path = saveFile.toPath();
            long size = Files.size(path);
            if (size > 1048576) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File size error");
                alert.setHeaderText("File is too big");
                alert.setContentText("The selected file is too big to open with this program.");
                alert.showAndWait();
                return;
            }
            this.rawData = Files.readAllBytes(path);
            this.fs = new FlipnicSave(this.rawData);
            if (!fs.isValidSave()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Header mismatch");
                alert.setHeaderText("File header is not correct");
                alert.setContentText("The save file may be unsupported by this program or corrupted.");
                alert.showAndWait();
            }
            primaryStage.setTitle("Flipnic Save Editor " + this.version + " - " + saveFile.getName());
            int totalMissions = 0;
            for (int i = 0; i < 11; i++) {
                totalMissions += this.fs.GetMissions(i).length;
            }
            if (totalMissions != 115) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Stage status invalid");
                alert.setHeaderText("Mission data may be corrupted");
                alert.setContentText("Abnormal amount of missions detected: expected 115, actual " + totalMissions + "\nThis save file may be modified");
                alert.showAndWait();
            }
            mainWindow.update(this.fs);

        } catch (IOException ie) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IOException");
            alert.setHeaderText("Runtime error has occurred");
            alert.setContentText(ie.getMessage());
            alert.showAndWait();
        }
    }
}