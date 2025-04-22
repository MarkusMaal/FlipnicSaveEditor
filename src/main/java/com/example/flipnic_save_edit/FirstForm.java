package com.example.flipnic_save_edit;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FirstForm {

    // information
    @FXML
    private Button updateSumsButton;
    @FXML
    private Label checkSumLabel;
    @FXML
    private Label redundantChecksumLabel;

    @FXML
    private Label currentScoreLabel;

    @FXML
    private Label currentStageLabel;

    // options
    @FXML
    private ComboBox<String> bgmVolumeLabel;
    @FXML
    private ComboBox<String> sfxVolumeLabel;
    @FXML
    private ComboBox<String> soundModeLabel;
    @FXML
    private CheckBox vibrationLabel;
    @FXML
    private ComboBox<String> leftFlipperLabel;
    @FXML
    private ComboBox<String> rightFlipperLabel;
    @FXML
    private ComboBox<String> leftNudgeLabel;
    @FXML
    private ComboBox<String> rightNudgeLabel;

    // ranking

    @FXML
    private ComboBox<String> gameModeSelector;

    @FXML
    private TableView<ScoreRow> rankingTable;

    @FXML
    private TableColumn<ScoreRow, String> rankColumn;

    @FXML
    private TableColumn<ScoreRow, String> scoreColumn;

    @FXML
    private TableColumn<ScoreRow, String> initialsColumn;

    @FXML
    private TableColumn<ScoreRow, String> combosColumn;

    // unlocks
    private final ToggleGroup grp = new ToggleGroup();
    @FXML
    private RadioButton freeRad;
    @FXML
    private RadioButton originalRad;

    @FXML
    private CheckBox bioACheck;
    @FXML
    private CheckBox bioBCheck;
    @FXML
    private CheckBox metACheck;
    @FXML
    private CheckBox metBCheck;
    @FXML
    private CheckBox optACheck;
    @FXML
    private CheckBox optBCheck;
    @FXML
    private CheckBox geoACheck;
    @FXML
    private CheckBox evoACheck;
    @FXML
    private CheckBox evoBCheck;
    @FXML
    private CheckBox evoCCheck;
    @FXML
    private CheckBox evoDCheck;
    @FXML
    private CheckBox creditsCheck;

    // missions
    @FXML
    private ComboBox missionsComboBox;
    @FXML
    private Label missionsLabel;
    @FXML
    private TableView stageStatusTable;

    private FlipnicSave fs;

    private String[] gameModes = {"Original game", "Biology A", "Biology B", "Metallurgy A", "Metallurgy B", "Optics A", "Optics B", "Geometry A",
            "Biology A (Time Attack)", "Biology B (Time Attack)", "Metallurgy A (Time Attack)", "Metallurgy B (Time Attack)", "Optics A (Time Attack)", "Optics B (Time Attack)", "Geometry A (Time Attack)"};
    private String[] stages = {"Biology A", "Evolution A", "Metallurgy A", "Evolution B", "Optics A", "Evolution C", "Biology B", "Metallurgy B", "Optics B", "Geometry A", "Evolution D"};
    private String[] soundModes = {"Mono", "Stereo"};
    private ArrayList<String[]> missions = new ArrayList<>();
    boolean locked = false;

    @FXML
    private void initialize() {
        List<String> numbers = new ArrayList<>();
        List<String> strings = new ArrayList<>(Arrays.asList(this.gameModes));
        for (int i = 0; i < 255; i++) {
            numbers.add(String.valueOf(i));
        }
        gameModeSelector.setItems(FXCollections.observableList(strings));
        strings = new ArrayList<>(Arrays.asList(this.stages));

        missionsComboBox.setItems(FXCollections.observableList(strings));
        bgmVolumeLabel.setItems(FXCollections.observableList(numbers));
        sfxVolumeLabel.setItems(FXCollections.observableList(numbers));
        soundModeLabel.setItems(FXCollections.observableList(List.of(soundModes)));
        leftNudgeLabel.setItems(FXCollections.observableList(List.of(FlipnicSave.GetAllInputs())));
        leftFlipperLabel.setItems(FXCollections.observableList(List.of(FlipnicSave.GetAllInputs())));
        rightNudgeLabel.setItems(FXCollections.observableList(List.of(FlipnicSave.GetAllInputs())));
        rightFlipperLabel.setItems(FXCollections.observableList(List.of(FlipnicSave.GetAllInputs())));
        freeRad.setToggleGroup(grp);
        originalRad.setToggleGroup(grp);
    }

    private static MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.fs = mainApp.fs;
        update(this.fs);
    }

    private void updateMissions() {
        this.missions = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            this.missions.add(mainApp.fs.GetMissions(i));
        }
    }

    public void update(FlipnicSave fs) {
        locked = true;
        if (fs.isLoaded()) {
            // information
            redundantChecksumLabel.setText(fs.GetChecksum(false) + " (" + (fs.ConfirmChecksums(false) ? "Valid" : "Invalid") + ")");
            checkSumLabel.setText(fs.GetChecksum(true) + " (" + (fs.ConfirmChecksums(true) ? "Valid" : "Invalid") + ")");
            currentScoreLabel.setText(String.valueOf(fs.GetCurrentScore()));
            currentStageLabel.setText(fs.GetCurrentStage());
            // options
            sfxVolumeLabel.getSelectionModel().select(fs.getVolumeSfx());
            bgmVolumeLabel.getSelectionModel().select(fs.getVolumeBgm());
            soundModeLabel.getSelectionModel().select(fs.getSoundMode().equals("Mono") ? 0 : 1);
            vibrationLabel.setSelected(fs.getVibration());
            // inputs
            leftNudgeLabel.getSelectionModel().select(fs.getLeftNudge());
            rightNudgeLabel.getSelectionModel().select(fs.getRightNudge());
            leftFlipperLabel.getSelectionModel().select(fs.getLeftFlipper());
            rightFlipperLabel.getSelectionModel().select(fs.getRightFlipper());
            onGameModeChanged();
            // missions
            updateMissions();
        } else {
            // information
            checkSumLabel.setText("Not loaded");
            currentScoreLabel.setText("0");
            currentStageLabel.setText("Biology A");
            // options
            bgmVolumeLabel.getSelectionModel().select(0);
            sfxVolumeLabel.getSelectionModel().select(0);
            soundModeLabel.getSelectionModel().select(0);
            vibrationLabel.setSelected(false);
            // inputs
            leftNudgeLabel.getSelectionModel().select(0);
            rightNudgeLabel.getSelectionModel().select(0);
            leftFlipperLabel.getSelectionModel().select(0);
            rightFlipperLabel.getSelectionModel().select(0);
        }
        locked = false;
    }

    @SuppressWarnings("unchecked")
    @FXML
    private void onMissionStageChanged() {
        if (mainApp.fs.isLoaded()) {
            stageStatusTable.getItems().clear();
            stageStatusTable.getColumns().clear();
            MModel model = new MModel();
            int statusIdx = missionsComboBox.getSelectionModel().getSelectedIndex();
            String[] status = mainApp.fs.GetStageStatus(statusIdx);
            String[] types = mainApp.fs.GetMissionTypes(statusIdx);
            int i = 0;
            if (missions.isEmpty()) {
                return;
            }
            for (String mission: this.missions.get(statusIdx)) {
                if (i < status.length) {
                    model.getItems().add(new Mission(types[i], mission, status[i]));
                } else {
                    model.getItems().add(new Mission(types[i], mission, "Out of range"));
                }
                i++;
            }
            stageStatusTable.setItems(FXCollections.observableList(model.getItems()));
            stageStatusTable.getColumns().add(column("Type", Mission::getType));
            stageStatusTable.getColumns().add(column("Mission", Mission::getName));
            stageStatusTable.getColumns().add(column("Status", Mission::getStatus));
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(0)).setCellFactory(ComboBoxTableCell.forTableColumn(new String[]{"Red", "Yellow"}));
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(0)).setOnEditCommit(event -> {
                Mission m = event.getTableView().getItems().get(event.getTablePosition().getRow());
                m.setType(event.getNewValue());
            });
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(1)).setCellFactory(ComboBoxTableCell.forTableColumn(mainApp.fs.GetStrings()));
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(1)).setOnEditCommit(event -> {
                Mission m = event.getTableView().getItems().get(event.getTablePosition().getRow());
                m.setName(event.getNewValue());
            });
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(2)).setCellFactory(ComboBoxTableCell.forTableColumn(new String[] {"Not completed", "Started", "Completed"}));
            ((TableColumn<Mission, String>)stageStatusTable.getColumns().get(2)).setOnEditCommit(event -> {
                Mission m = event.getTableView().getItems().get(event.getTablePosition().getRow());
                m.setStatus(event.getNewValue());
            });
        } else {
            stageStatusTable.getItems().clear();
            stageStatusTable.getColumns().clear();
        }
    }

    @FXML
    private void onGameModeChanged() {
        boolean[] checks;
        if (freeRad.isSelected()) {
            // free play
            checks = mainApp.fs.getUnlocks(true);
        } else {
            // original game
            checks = mainApp.fs.getUnlocks(false);
        }
        bioACheck.setSelected(checks[0]);
        evoACheck.setSelected(checks[1]);
        metACheck.setSelected(checks[2]);
        evoBCheck.setSelected(checks[3]);
        optACheck.setSelected(checks[4]);
        evoCCheck.setSelected(checks[5]);
        bioBCheck.setSelected(checks[6]);
        metBCheck.setSelected(checks[7]);
        optBCheck.setSelected(checks[8]);
        geoACheck.setSelected(checks[9]);
        evoDCheck.setSelected(checks[10]);
        creditsCheck.setSelected(checks[11]);
    }

    @FXML
    private void onChangeMode() {
        int modeIdx = (gameModeSelector.getSelectionModel().getSelectedIndex()) * 5;
        rankingTable.getItems().clear();
        rankingTable.getColumns().clear();
        Model model = new Model();
        for (int i = modeIdx; i < modeIdx + 5; i++) {
            String[] scoreRow = mainApp.fs.GetScore(i);
            if (scoreRow.length > 0) {
                model.getItems().add(new ScoreRow(scoreRow[0], scoreRow[1], scoreRow[2], scoreRow[3], scoreRow[4], scoreRow[5]));
            }
        }
        rankingTable.setItems(FXCollections.observableList(model.getItems()));
        rankingTable.getColumns().add(column("Rank", ScoreRow::getRank));
        rankingTable.getColumns().add(column("Initials", ScoreRow::getInitials));
        rankingTable.getColumns().add(column("Score", ScoreRow::getScore));
        rankingTable.getColumns().add(column("Combos", ScoreRow::getCombos));
        rankingTable.getColumns().add(column("Difficulty", ScoreRow::getDifficulty));
        rankingTable.getColumns().add(column("Offset", ScoreRow::getOffset));
        int i = 0;
        for (TableColumn tc : rankingTable.getColumns()) {
            if (i == rankingTable.getColumns().size() - 1) {
                break;
            }
            if (i == 4) {
                tc.setCellFactory(ComboBoxTableCell.forTableColumn(new String[] {"Easy", "Normal", "Hard"}));
            }
            else if (i != 0) {
                tc.setCellFactory(TextFieldTableCell.forTableColumn());
            }
            tc.setOnEditCommit(event -> {
                ScoreRow sr = ((TableColumn.CellEditEvent<ScoreRow, ?>)event).getTableView().getItems().get(((TableColumn.CellEditEvent<ScoreRow, ?>)event).getTablePosition().getRow());
                sr.setColumn(((TableColumn.CellEditEvent<ScoreRow, ?>) event).getTableColumn().getText(), ((TableColumn.CellEditEvent<ScoreRow, ?>) event).getNewValue().toString());
            });
            i++;
        }
        rankingTable.editableProperty().set(true);
    }

    @FXML
    private void resetButton() {
        mainApp.fs.ResetGame(freeRad.isSelected());
        update(mainApp.fs);
        onGameModeChanged();
    }

    @FXML
    private void updateChecksums() {
        mainApp.fs.UpdateChecksum();
        update(mainApp.fs);
        onGameModeChanged();
    }

    @FXML
    private void applyFixes() {
        String[] fixes = mainApp.fs.FixStructure();
        if (fixes.length == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fix file structure");
            alert.setHeaderText("No fixes were applied");
            alert.setContentText("Your save file appears to have the correct structure");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fix file structure");
        alert.setHeaderText("Success");
        alert.setContentText("Corrections were made to the save file:\n\n" + String.join("\n", fixes));
        alert.showAndWait();
    }

    @FXML
    private void updateChecks() {
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 0, bioACheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 1, evoACheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 2, metACheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 3, evoBCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 4, optACheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 5, evoCCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 6, bioBCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 7, metBCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 8, optBCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 9, geoACheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 10, evoDCheck.isSelected());
        mainApp.fs.WriteUnlock(freeRad.isSelected(), 11, creditsCheck.isSelected());
        update(mainApp.fs);
    }

    @FXML
    private void updateSettings() {
        if (locked) return; // prevent weird race conditions
        mainApp.fs.SetOption(FlipnicSave.Options.SOUND_MODE, (byte) ((soundModeLabel.getSelectionModel().getSelectedIndex() == 0) ? 0 : 1));
        mainApp.fs.SetOption(FlipnicSave.Options.SFX_VOLUME, (byte) (sfxVolumeLabel.getSelectionModel().getSelectedIndex()));
        mainApp.fs.SetOption(FlipnicSave.Options.BGM_VOLUME, (byte) (bgmVolumeLabel.getSelectionModel().getSelectedIndex()));
        mainApp.fs.SetOption(FlipnicSave.Options.VIBRATION, (byte) (vibrationLabel.isSelected() ? 0 : 1));
        mainApp.fs.SetControl(FlipnicSave.Control.LEFT_NUDGE, (byte) (leftNudgeLabel.getSelectionModel().getSelectedIndex()));
        mainApp.fs.SetControl(FlipnicSave.Control.RIGHT_NUDGE, (byte) (rightNudgeLabel.getSelectionModel().getSelectedIndex()));
        mainApp.fs.SetControl(FlipnicSave.Control.LEFT_FLIPPER, (byte) (leftFlipperLabel.getSelectionModel().getSelectedIndex()));
        mainApp.fs.SetControl(FlipnicSave.Control.RIGHT_FLIPPER, (byte) (rightFlipperLabel.getSelectionModel().getSelectedIndex()));
        update(mainApp.fs);
    }

    private static <S,T> TableColumn<S,T> column(String title, Function<S,T> property) {
        TableColumn<S,T> col = new TableColumn<>(title);
        col.editableProperty().set(true);
        col.setCellValueFactory(cellData -> new ObservableValueBase<T>() {
            @Override
            public T getValue() {
                return property.apply(cellData.getValue());
            };
        });
        return col ;
    }
    public static class Model {
        private List<ScoreRow> items ;

        public Model() {
            this.items = new ArrayList<>();
        }

        public List<ScoreRow> getItems() {
            return items ;
        }
    }
    public static class MModel {
        private List<Mission> items ;

        public MModel() {
            this.items = new ArrayList<>();
        }

        public List<Mission> getItems() {
            return items ;
        }
    }

    private class Mission {
        private String name;
        private String status;
        private String type;

        private Mission(String type, String name, String status) {
            this.type = type;
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public void setName(String value) {
            this.name = value;
            if (locked) return;
            mainApp.fs.SetMission(missionsComboBox.getSelectionModel().getSelectedIndex(), stageStatusTable.getSelectionModel().getSelectedIndex(), value);
            update(mainApp.fs);
        }

        public void setStatus(String value) {
            switch (value) {
                case "Not completed":
                case "Started":
                case "Completed":
                    this.status = value;
                default:
                    break;
            }
            if (locked) return;
            mainApp.fs.SetStageStatus(missionsComboBox.getSelectionModel().getSelectedIndex(), stageStatusTable.getSelectionModel().getSelectedIndex(), value);
            update(mainApp.fs);
        }

        public void setType(String value) {
            this.type = value;
            if (locked) return;
            mainApp.fs.SetMissionType(missionsComboBox.getSelectionModel().getSelectedIndex(), stageStatusTable.getSelectionModel().getSelectedIndex(), value.equals("Red"));
            update(mainApp.fs);
        }
    }

    private class ScoreRow {
        private final String rank;
        private String initials;
        private String score;
        private String combos;

        private final String offset;
        private String difficulty;


        private ScoreRow(String rank, String initials, String score, String combos, String offset, String difficulty) {
            this.rank = rank;
            this.initials = initials;
            this.score = score;
            this.combos = combos;
            this.offset = offset;
            this.difficulty = difficulty;
        }

        public String getRank() {
            return rank;
        }


        public String getInitials() {
            return initials;
        }


        public String getScore() {
            return score;
        }

        public String getCombos() {
            return combos;
        }

        public String getOffset() {
            return offset;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setInitials(String value) {
            this.initials = value;
        }

        public void setScore(String value) {
            this.score = value;
        }

        public void setCombos(String value) {
            this.combos = value;
        }

        public void setDifficulty(String value) {
            switch (value) {
                case "Easy":
                case "Normal":
                case "Hard":
                    this.difficulty = value;
                default:
                    break;
            }
        }

        public void setColumn(String column, String value) {
            switch (column) {
                case "Initials":
                    setInitials(value);
                    break;
                case "Score":
                    setScore(value);
                    break;
                case "Combos":
                    setCombos(value);
                    break;
                case "Difficulty":
                    setDifficulty(value);
                    break;
                default:
                    return;
            }
            FlipnicSave.Difficulty diff = FlipnicSave.Difficulty.EASY;
            diff = switch (getDifficulty()) {
                case "Normal" -> FlipnicSave.Difficulty.NORMAL;
                case "Hard" -> FlipnicSave.Difficulty.HARD;
                default -> diff;
            };
            if (locked) return;
            mainApp.fs.SetScore(gameModeSelector.getSelectionModel().getSelectedIndex(), rankingTable.getSelectionModel().getSelectedIndex(), Integer.parseInt(getScore()), getInitials(), Integer.parseInt(getCombos()), diff);
            update(mainApp.fs);
        }
    }
}
