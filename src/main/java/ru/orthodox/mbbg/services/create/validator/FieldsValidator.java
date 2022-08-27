package ru.orthodox.mbbg.services.create.validator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.model.proxy.play.RoundsTabPane;
import ru.orthodox.mbbg.services.popup.PopupAlerter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FieldsValidator {

    @Autowired
    private PopupAlerter popupAlerter;

    private String messageAboutNotUniqueArtist;
    private String messageAboutInsufficientNumberOfAudioTracks;

    public void validateTabPane(RoundsTabPane roundsTabPane, Button saveButton) {
        messageAboutNotUniqueArtist = "";
        messageAboutInsufficientNumberOfAudioTracks = "";

        for (RoundTab roundTab: roundsTabPane.getRoundTabs()) {
            validateRoundTab(roundTab);
        }

        String messageToShow = messageAboutNotUniqueArtist.isEmpty() ? "" : messageAboutNotUniqueArtist;
        if (!messageAboutInsufficientNumberOfAudioTracks.isEmpty()) {
            messageToShow += "\n";
            messageToShow += messageAboutInsufficientNumberOfAudioTracks;
        }
        if (!messageToShow.isEmpty()) {
            popupAlerter.invoke(saveButton.getScene().getWindow(), "Not valid game data", messageToShow);
        }
    }

    private void validateRoundTab(RoundTab tab) {
        int blankCapacity = tab.getBlankDimensions().getValue().getCapacity();
        List<AudioTrack> tabAudioTracks = tab.getEditAudioTracksTable().getAudioTracks();
        if (tabAudioTracks.size() < blankCapacity) {
            messageAboutInsufficientNumberOfAudioTracks += "Number of audiotracks in round '" + tab.getTab().getText() + "' (" + tabAudioTracks.size() + ") is less than blank capacity (" + blankCapacity + "). Please add more tracks.\n";
        }
        Map<String, List<AudioTrack>> artistsOccurences = tabAudioTracks.stream()
                .collect(Collectors.groupingBy(AudioTrack::getArtist));

        String notUniqueArtistMessageRoundWise = artistsOccurences.entrySet().stream()
                .filter(artistSet -> artistSet.getValue().size() > 1)
                .map(artistSet -> "There are " + artistSet.getValue().size() + " audiotracks for artist "
                        + artistSet.getKey() + " in round '" + tab.getTab().getText() + "' ("
                        + artistSet.getValue().stream().map(AudioTrack::getTitle).collect(Collectors.joining(", "))
                        + "). Please leave only one track of this artist in the specified round.")
                .collect(Collectors.joining("\n"));
        if (!notUniqueArtistMessageRoundWise.isEmpty()) {
            messageAboutNotUniqueArtist += notUniqueArtistMessageRoundWise + "\n";
        }
    }
}
