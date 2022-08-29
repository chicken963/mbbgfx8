package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;


@Slf4j
@Configurable
public class PopupController {
    @FXML
    private Button okButton;

    @FXML
    private void close(ActionEvent event){
        Scene currentScene = ((Button) event.getSource()).getScene();
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.close();
    }
}
