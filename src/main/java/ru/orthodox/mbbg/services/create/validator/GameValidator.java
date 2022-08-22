package ru.orthodox.mbbg.services.create.validator;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.NewGameController;
import ru.orthodox.mbbg.events.ChoiceBoxChangeEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.model.proxy.play.RoundsTabPane;

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
        saveButton.setDisable(allTextFieldsAreFilled() && allCheckBoxesAreFilled());
    }

    private boolean allCheckBoxesAreFilled() {
        return tabPane.getRoundTabs().stream().allMatch(RoundTab::isFilled);
    }

    private boolean allAudioTrackTextFieldsAreFilled() {
        return tabPane.getRoundTabs().stream()
                .map(RoundTab::getEditAudioTracksTable)
                .allMatch(EditAudioTracksTable::isFilled);
    }

    private boolean allTextFieldsAreFilled() {
        return !gameName.getText().isEmpty()
                && allAudioTrackTextFieldsAreFilled();
    }

    @EventListener
    public void onChoiceBoxValueChanged(ChoiceBoxChangeEvent choiceBoxChangeEvent) {
        saveButton.setDisable(allTextFieldsAreFilled() && allCheckBoxesAreFilled());
    }

    public void validateGameScene(NewGameController.GameScene gameScene) {

        TextField gameNameInput = gameScene.getGameNameInput();
        RoundsTabPane roundsTabPane = gameScene.getRoundsTabPane();

        this.validateTextField(gameNameInput, "New game name");
        this.validateTabPane(roundsTabPane);

    }
}
