package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
        Scene currentScene = okButton.getScene();
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.close();
        try {
            currentScene.setRoot(null);
        } catch (NullPointerException e) {
            log.warn("аяяй, опять перетираешь Scene");
        }
        try {
            currentStage.setScene(null);
        } catch (NullPointerException e) {
            log.warn("аяяй, опять перетираешь Stage");
        }
        currentStage.close();
    }
}
