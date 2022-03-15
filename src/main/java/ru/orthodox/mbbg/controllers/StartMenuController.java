package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.utils.NormalizedPathString;

import javax.annotation.PostConstruct;

@Configurable
public class StartMenuController {

    @FXML
    public Button openGameButton;
    @FXML
    public Button newGameButton;
    @FXML
    public Label greetingLabel;

    @Autowired
    private ScreenService screenService;

    @PostConstruct
    private void setUp(){
        openGameButton.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 16));
        newGameButton.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 16));
        greetingLabel.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 32));
    }

    public void openNewGameForm(ActionEvent actionEvent) {
        screenService.activate("newgame");
    }

    public void openPlayGameForm(ActionEvent actionEvent) {
        screenService.activate("main");
    }
}
