package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.utils.ui.PopupAlerter;
import ru.orthodox.mbbg.utils.ui.PopupConfigurator;

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
            AudioTracksTable table = tab.getAudioTracksTable();
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

        ChoiceBox<Integer> rowsNumber = tab.getRowsNumber();
        ChoiceBox<Integer> columnsNumber = tab.getColumnsNumber();

        Label rowsWarning = tab.getRowsWarning();
        Label columnsWarning = tab.getColumnsWarning();

        return new HashMap<ChoiceBox, Label>(){{
            put(firstWinCondition, firstWinConditionWarning);
            put(secondWinCondition, secondWinConditionWarning);
            put(thirdWinCondition, thirdWinConditionWarning);
            put(rowsNumber, rowsWarning);
            put(columnsNumber, columnsWarning);
        }};
    }

    public void validateTextField(TextField textField, String message){
        if (!(inputIsFilled(textField, message))) {
            throw new GameInputsValidationException();
        }
    }

    private boolean inputIsFilled(TextField textField, String message){
        if (textField.getText().trim().isEmpty()) {
            textField.getStyleClass().add("warning");
            textField.setPromptText(message + " should not be empty!");
            textField.setOnKeyPressed(event -> ((Node) event.getSource()).getStyleClass().remove("warning"));
            return false;
        }
        return true;
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

    private void validateAudiotracksTable(AudioTracksTable table){
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
