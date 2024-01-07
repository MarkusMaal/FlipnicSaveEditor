package com.example.flipnic_save_edit;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FirstForm {
    @FXML
    private Label checkSumLabel;

    @FXML
    private ComboBox<String> gameModeSelector;

    @FXML
    private TableView<ScoreRow> rankingTable;
    @FXML
    private Label currentScoreLabel;

    @FXML
    private Label currentStageLabel;

    @FXML
    private TableColumn<ScoreRow, String> rankColumn;

    @FXML
    private TableColumn<ScoreRow, String> scoreColumn;

    @FXML
    private TableColumn<ScoreRow, String> initialsColumn;

    @FXML
    private TableColumn<ScoreRow, String> combosColumn;


    private FlipnicSave fs;

    private String[] gameModes = {"Original game", "Biology A", "Biology B", "Metallurgy A", "Metallurgy B", "Optics A", "Optics B", "Geometry A",
            "Biology A (Time Attack)", "Biology B (Time Attack)", "Metallurgy A (Time Attack)", "Metallurgy B (Time Attack)", "Optics A (Time Attack)", "Optics B (Time Attack)", "Geometry A (Time Attack)"};


    @FXML
    private void initialize() {
        List<String> strings = new ArrayList<>(Arrays.asList(this.gameModes));
        gameModeSelector.setItems(FXCollections.observableList(strings));
    }

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.fs = mainApp.fs;
        update(this.fs);
    }

    public void update(FlipnicSave fs) {
        if (fs.isLoaded()) {
            checkSumLabel.setText(fs.GetChecksum());
            currentScoreLabel.setText(String.valueOf(fs.GetCurrentScore()));
            currentStageLabel.setText(fs.GetCurrentStage());
        } else {
            checkSumLabel.setText("Not loaded");
            currentScoreLabel.setText("0");
            currentStageLabel.setText("Biology A");
        }
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
    }
    private static <S,T> TableColumn<S,T> column(String title, Function<S,T> property) {
        TableColumn<S,T> col = new TableColumn<>(title);
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
    private static class ScoreRow {
        private final String rank;
        private final String initials;
        private final String score;
        private final String combos;

        private final String offset;
        private final String difficulty;


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
    }
}
