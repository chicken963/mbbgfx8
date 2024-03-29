package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.control.TextField;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.NewGameController;

@Service
public class GameValidator extends FieldsValidator {

    public void validateGameScene(NewGameController.GameScene gameScene) {

        TextField gameNameInput = gameScene.getGameNameInput();
        RoundsTabPane roundsTabPane = gameScene.getRoundsTabPane();

        this.validateTextField(gameNameInput, "New game name");
        this.validateTabPane(roundsTabPane);

    }
}
