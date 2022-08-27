package ru.orthodox.mbbg.services.create.validator;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.NewGameController;
import ru.orthodox.mbbg.events.ChoiceBoxChangeEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.events.WinConditionChangedEvent;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.model.proxy.play.RoundsTabPane;
import ru.orthodox.mbbg.services.create.NewGameService;

@Service
public class GameValidator extends FieldsValidator {

    @Setter
    private Button saveButton;
    @Setter
    private RoundsTabPane tabPane;
    @Setter
    private TextField gameName;

    @EventListener
    public void onTextFieldValueChanged(TextFieldChangeEvent textFieldChangeEvent) {
        saveButton.setDisable(!allFieldsAreFilled());
    }

    @EventListener
    public void onChoiceBoxValueChanged(ChoiceBoxChangeEvent choiceBoxChangeEvent) {
        saveButton.setDisable(!allFieldsAreFilled());
    }

    @Order(2)
    @EventListener
    public void onWinConditionChangedEvent(WinConditionChangedEvent winConditionChangedEvent) {
        saveButton.setDisable(!allFieldsAreFilled());
    }

    @Order(2)
    @EventListener
    public void onTabAddedEvent(TabAddedEvent tabAddedEvent) {
        saveButton.setDisable(!allFieldsAreFilled());
    }

    private boolean allTabFieldsAreFilled() {
        return tabPane.getRoundTabs().stream().allMatch(RoundTab::isFilled);
    }

    private boolean allFieldsAreFilled() {
        return !gameName.getText().isEmpty() && allTabFieldsAreFilled();
    }

    public void validateGameScene(NewGameService.GameScene gameScene, Button saveButton) {
        validateTabPane(gameScene.getRoundsTabPane(), saveButton);
    }
}
