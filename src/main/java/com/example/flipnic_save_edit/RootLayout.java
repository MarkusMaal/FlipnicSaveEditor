package com.example.flipnic_save_edit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class RootLayout {
    public CheckMenuItem regionCheck;
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
        alert.setContentText("""
        Allows you to edit, hack, and repair save files for the Flipnic video game.
        
        CRC-32 variant: JAMCRC
        Dark theme credit: Wesos de Queso""");
        alert.showAndWait();
    }

    @FXML
    private void ToggleRegion() {
        if (!mainApp.fs.isLoaded()) {
            regionCheck.setSelected(!regionCheck.isSelected());
            return;
        }
        mainApp.fs.ToggleJpNames(regionCheck.isSelected());
        mainApp.controller.stages[1] = regionCheck.isSelected() ? "Theology A" : "Evolution A";
        mainApp.controller.stages[3] = regionCheck.isSelected() ? "Theology B" : "Evolution B";
        mainApp.controller.stages[5] = regionCheck.isSelected() ? "Theology C" : "Evolution C";
        mainApp.controller.stages[10] = regionCheck.isSelected() ? "Theology D" : "Evolution D";
        MainApp.friendlyStageNames[1] = regionCheck.isSelected() ? "Theology A" : "Evolution A";
        MainApp.friendlyStageNames[3] = regionCheck.isSelected() ? "Theology B" : "Evolution B";
        MainApp.friendlyStageNames[5] = regionCheck.isSelected() ? "Theology C" : "Evolution C";
        MainApp.friendlyStageNames[10] = regionCheck.isSelected() ? "Theology D" : "Evolution D";
        mainApp.controller.RefreshRegion(regionCheck.isSelected());
        mainApp.controller.update(mainApp.fs);
    }

    @FXML
    private void ImportMsgFile() {
        if (!mainApp.fs.isLoaded()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import message file");
            alert.setHeaderText("Can't import");
            alert.setContentText("Please load a save file first!");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Flipnic message files", "*.MSG");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        try {
            if (!mainApp.fs.ImportMessageFile(file.getAbsolutePath())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Import message file");
                alert.setHeaderText("Not a valid message file");
                alert.setContentText("File path: " + file.getAbsolutePath());
                alert.showAndWait();
                return;
            }
            mainApp.controller.update(mainApp.fs);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Import message file");
            alert.setHeaderText("Message file imported successfully!");
            alert.setContentText("Note that you need to re-import it every time you load a save file.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import message file");
            alert.setHeaderText("Failed to import message file");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void ToggleDark() {
        try {
            mainApp.controller.ToggleDark();
        } catch (MalformedURLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Toggle theme");
            alert.setHeaderText("Failed to switch theme");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void ShowDisclaimer() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Flipnic save editor");
        alert.setHeaderText("Disclaimer");
        alert.setContentText("""
        This software is freeware and is provided AS IS without a warranty. This program is also provided free of charge. If you had to pay for it, please ask for a refund from the seller.
        
        In addition, the author of this program will not be held responsible for damage done to your Flipnic save file. Please remember to click the "Update checksums" button before saving to avoid the "Save file may be corrupt" error message.
        
        The features of this program should not be used to cheat in score or time attack competitions.""");
        alert.showAndWait();
    }

    @FXML
    public void ShowFaq() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.OK);
        alert.setTitle("Flipnic save editor");
        alert.setHeaderText("Frequently asked questions (1/3)");
        alert.setContentText(
        """
        Why are there 2 checksums and how they differ?
        
        The primary checksum (located at offset 0x0C) is calculated for data starting from 0x10. The backup checksum (located at offset 0x08) is calculated after the first one and includes data starting from 0x08 (including the primary checksum)
        
        
        Why does the game say my save file is corrupt after editing it with this program?
        
        There are 2 possible reasons. First reason, one of the checksums is invalid. To rectify this, simply click on the "Update checksums" button and that'll fix any checksum related issues. Second reason may be that the save file is the wrong size. To check for this, click on the "Diagnose save file" button, which will perform various checks on the save file and attempt to fix them. You must also ensure that there are no other files other than the ones put there by Flipnic on the game's save directory.
        """
        );
        alert.showAndWait();
        alert.setHeaderText("Frequently asked questions (2/3)");
        alert.setContentText(
        """
        When I go to the missions tab, the names for the missions are incorrect?
        
        This will occur if you have a save file from the original Japanese version of the game. You can fix this by extracting files from the game's RES.BIN container (using FlipnicBinExtract or FlipnicFs) and then importing the JA.MSG file to this save editor after opening a save file (Menu bar > Options > Import JA.MSG file).
        
        
        Why does "Last stage played" show up as "N/A"?
        
        The game will only save current stage being played if you have completed/started at least one mission and then quit from the stage. 2P stages are not stored in the save file and so they will also not show up as the last stage played.
        """);
        alert.showAndWait();
        alert.setHeaderText("Frequently asked questions (3/3)");
        alert.setContentText(
        """
        What does "Force score mode" option under "Unlocks" tab do?
        
        It allows you to hack the save file in a way where it sets the current stage in original game to whatever you want and as a side effect you can replay that stage with score in original mode.


        I want to analyze the save file format myself in a hex editor. How can I highlight sections of the save file we already know about?
        
        If you use ImHex, you can right click on the "Pattern editor" section of the window and select "Import Pattern File..." from the popup menu. Then import the "flipnicsave.hexpat" file from the root directory on FlipnicSaveEditor repository (git clone source tree first).
        """);
        alert.showAndWait();
    }
}
