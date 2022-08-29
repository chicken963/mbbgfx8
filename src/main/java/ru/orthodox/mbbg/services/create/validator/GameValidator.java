package ru.orthodox.mbbg.services.create.validator;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.BlankDimensionsChangedEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.events.WinConditionChangedEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;
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
    public void onChoiceBoxValueChanged(BlankDimensionsChangedEvent blankDimensionsChangedEvent) {
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
