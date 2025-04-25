package com.paktc.flipnic_save_edit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MainApp extends Application {
    public Stage primaryStage;
    private BorderPane rootLayout;

    public byte[] rawData = new byte[0];
    public FlipnicSave fs = new FlipnicSave(new byte[0]);
    public FirstForm mainWindow;

    public Scene rootScene;
    public FirstForm controller;
    public RootLayout rootController;

    public String version;
    public String savePath = "";
    private static String args;
    public static String[] friendlyStageNames = "Biology A,Evolution A,Metallurgy A,Evolution B,Optics A,Evolution C,Biology B,Metallurgy B,Optics B,Geometry A,Evolution D".split(",");
    public static boolean darkMode = false;


    @Override
    public void start(Stage stage) throws IOException, XmlPullParserException {
        GetVersion();

        this.primaryStage = stage;
        this.primaryStage.setMinWidth(665);
        this.primaryStage.setMinHeight(450);
        this.primaryStage.setTitle("Flipnic Save Editor " + this.version);
        initRootLayout();
        showFirstForm();
    }

    private void GetVersion() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        version = model.getVersion();
    }

    private FXMLLoader initLayout(String resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(resource));
        return loader;
    }

    public void initRootLayout() throws IOException {
        FXMLLoader loader = initLayout("RootLayout.fxml");
        rootLayout = (BorderPane) loader.load();
        rootScene = new Scene(rootLayout);
        primaryStage.setScene(rootScene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));
        rootController = loader.getController();
        rootController.setMainApp(this);
        primaryStage.show();
    }

    public void showFirstForm() throws IOException {
        FXMLLoader loader = initLayout("FirstForm.fxml");
        AnchorPane firstForm = (AnchorPane) loader.load();

        controller = loader.getController();
        this.mainWindow = controller;
        controller.setMainApp(this);
        rootLayout.setCenter(firstForm);
        controller.SetParameters(getParameters());
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void SetAlertIcon(Alert a) {
        Stage s = (Stage)a.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));
    }

    public void SetAlertTheme(Alert a) {
        if (darkMode) {
            a.getDialogPane().getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void loadFile(File saveFile) {
        try {
            Path path = saveFile.toPath();
            savePath = path.toAbsolutePath().toString();
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
            /*
            if (!fs.isValidHeader()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Header mismatch");
                alert.setHeaderText("File header is not correct, but nobody cares");
                alert.showAndWait();
            }*/
            primaryStage.setTitle("Flipnic Save Editor " + this.version + " - " + saveFile.getName());
            mainWindow.update(this.fs, true);

        } catch (IOException ie) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IOException");
            alert.setHeaderText("Runtime error has occurred");
            alert.setContentText(ie.getMessage());
            alert.showAndWait();
        }
    }
}