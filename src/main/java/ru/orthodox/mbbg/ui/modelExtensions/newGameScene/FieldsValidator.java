package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.ui.PopupAlerter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FieldsValidator {

    @Autowired
    private PopupAlerter popupAlerter;

    public void validateTabPane(RoundsTabPane roundsTabPane) {
        boolean tabPaneIsValid = true;

        for (RoundTab roundTab: roundsTabPane.getRoundTabs()) {
            try {
                validateRoundTab(roundTab);
            } catch (GameInputsValidationException ex) {
                tabPaneIsValid = false;
            }
        }

        if (!tabPaneIsValid) {
            throw new GameInputsValidationException();
        }
    }

    private void validateRoundTab(RoundTab tab) {
        boolean tabIsValid = true;

        try {
            TextField roundName = tab.getRoundNameTextField();
            validateTextField(roundName, "Round name field");

            TextField numberOfBlanks = tab.getNumberOfBlanks();
            validateIntegerTextField(numberOfBlanks, "Number of blanks");
        } catch (GameInputsValidationException ex) {
            tabIsValid = false;
        }

        try {
            Map<ChoiceBox, Label> fieldsAndWarningLabels = findCheckBoxesAndWarningLabels(tab);
            validateCheckBoxes(fieldsAndWarningLabels);
        } catch (GameInputsValidationException ex) {
            tabIsValid = false;
        }


        try {
            AudioTracksView table = tab.getAudioTracksTable();
            validateAudiotracksTable(table);
        } catch (GameInputsValidationException ex) {
            tabIsValid = false;
        }

        if (!tabIsValid) {
            throw new GameInputsValidationException();
        }
    }

    private Map<ChoiceBox, Label> findCheckBoxesAndWarningLabels(RoundTab tab) {
        ChoiceBox<WinCondition> firstWinCondition = tab.getFirstPrizeCondition();
        ChoiceBox<WinCondition> secondWinCondition = tab.getSecondPrizeCondition();
        ChoiceBox<WinCondition> thirdWinCondition = tab.getThirdPrizeCondition();

        Label firstWinConditionWarning = tab.getFirstPrizeConditionWarning();
        Label secondWinConditionWarning = tab.getSecondPrizeConditionWarning();
        Label thirdWinConditionWarning = tab.getThirdPrizeConditionWarning();

        ChoiceBox<String> blankDimensions = tab.getBlankDimensions();


        Label dimensionsWarning = tab.getDimensionsWarning();

        return new HashMap<ChoiceBox, Label>(){{
            put(firstWinCondition, firstWinConditionWarning);
            put(secondWinCondition, secondWinConditionWarning);
            put(thirdWinCondition, thirdWinConditionWarning);
            put(blankDimensions, dimensionsWarning);
        }};
    }

    public void validateTextField(TextField textField, String message){
        if (!(inputIsFilled(textField, message))) {
            throw new GameInputsValidationException();
        }
    }

    public void validateIntegerTextField(TextField textField, String message){
        if (!(inputIsPositiveInteger(textField, message))) {
            throw new GameInputsValidationException();
        }
    }

    private boolean inputIsFilled(TextField textField, String message){
        if (textField.getText().trim().isEmpty()) {
            showMessageUntilInput(textField, message, "should not be empty!");
            return false;
        }
        return true;
    }

    private boolean inputIsPositiveInteger(TextField textField, String message){
        int value;
        try {
            value = Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessageUntilInput(textField, message, "should be numeric!");
            return false;
        }
        if (value == 0) {
            showMessageUntilInput(textField, message, "should be greater than zero!");
            return false;
        }
        return true;
    }

    private void showMessageUntilInput(TextField textField, String message, String details) {
        textField.getStyleClass().add("warning");
        textField.setPromptText(message + " " + details);
        textField.setOnKeyPressed(event -> ((Node) event.getSource()).getStyleClass().remove("warning"));
    }

    private void validateCheckBoxes(Map<ChoiceBox, Label> fieldsAndWarningLabels) {
        for (ChoiceBox choiceBox: fieldsAndWarningLabels.keySet()) {
            if (choiceBox.getValue() == null) {
                EventHandler<ActionEvent> bufferedOnAction = choiceBox.getOnAction();
                fieldsAndWarningLabels.get(choiceBox).setVisible(true);
                choiceBox.setOnAction(event -> {
                    fieldsAndWarningLabels.get(choiceBox).setVisible(false);
                    choiceBox.setOnAction(bufferedOnAction);
                });
                throw new GameInputsValidationException();
            }
        }
    }

    private void validateAudiotracksTable(AudioTracksView table){
        Label tablePlaceHolder = table.getPlaceholder();
        if (table.isEmpty()) {
            tablePlaceHolder.setVisible(true);
            throw new GameInputsValidationException();
        }
        List<AudioTrack> tracksWithEmptyArtist = table.getAudioTracks().stream()
                .filter(track -> track.getArtist().isEmpty())
                .collect(Collectors.toList());
        if (!tracksWithEmptyArtist.isEmpty()) {
            String detailedMessage = tracksWithEmptyArtist.stream()
                    .map(AudioTrack::getTitle)
                    .map(trackTitle -> "\u2023 " + trackTitle)
                    .collect(Collectors.joining(",\n"));

            popupAlerter.invoke(tablePlaceHolder.getScene().getWindow(),
                    "Some of the tracks have empty artist info, here they are:",
                    detailedMessage, tracksWithEmptyArtist.size());

            throw new GameInputsValidationException();
        }
    }
}
