package gui;

import gamelogic.Colour;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML
    private TextField firstPlayer, secondPlayer;
    @FXML
    private ChoiceBox<String> colourChoice, modeChoice;
    @FXML
    private Button go;
    @FXML
    private Label chooseColour, enterName;
    private int mode;
    private Colour colour;
    private String whiteName, blackName;


    @FXML
    private void go() throws IOException {
        if ((!Objects.equals(colourChoice.getValue(), "Choose colour")
                && !Objects.equals(modeChoice.getValue(), "Choose mode"))
                || mode == 2) {
            whiteName = firstPlayer.getText();
            blackName = secondPlayer.getText();
            GameData.getInstance().setWhichColour(colour);
            GameData.getInstance().setMode(mode);
            GameData.getInstance().setWhiteName(whiteName);
            GameData.getInstance().setBlackName(blackName);
            Stage stage = (Stage) modeChoice.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("boardScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modeChoice.getItems().addAll("Player vs Player", "Player vs AI", "AI vs AI");
        modeChoice.setValue("Choose mode");
        colourChoice.getItems().addAll("White", "Black");
        colourChoice.setValue("Choose colour");
        colourChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String currentColour = colourChoice.getSelectionModel().getSelectedItem();
                switch (currentColour) {
                    case "White" -> {
                        colour = Colour.WHITE;
                    }
                    case "Black" -> {
                        colour = Colour.BLACK;
                    }
                }
            }
        });
        modeChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String currentMode = modeChoice.getSelectionModel().getSelectedItem();
                switch (currentMode) {
                    case "Player vs Player" -> {
                        mode = 0;
                        firstPlayer.setVisible(true);
                        secondPlayer.setVisible(true);
                        chooseColour.setVisible(false);
                        enterName.setVisible(true);
                        colourChoice.setVisible(false);
                    }
                    case "Player vs AI" -> {
                        mode = 1;
                        firstPlayer.setVisible(true);
                        secondPlayer.setVisible(false);
                        chooseColour.setVisible(true);
                        enterName.setVisible(true);
                        colourChoice.setVisible(true);
                    }
                    case "AI vs AI" -> {
                        mode = 2;
                        firstPlayer.setVisible(false);
                        secondPlayer.setVisible(false);
                        chooseColour.setVisible(false);
                        enterName.setVisible(false);
                        colourChoice.setVisible(false);
                    }
                }
            }
        });
    }
}
