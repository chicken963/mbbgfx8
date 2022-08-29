package ru.orthodox.mbbg.services.create.validator;

import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;
import ru.orthodox.mbbg.services.popup.PopupAlerter;

import java.util.List;
import java.util.Map;
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
            messageToShow += "\n\n";
            messageToShow += messageAboutInsufficientNumberOfAudioTracks;
        }
        if (!messageToShow.isEmpty()) {
            popupAlerter.invoke(saveButton.getScene().getWindow(), "Not valid game data", messageToShow);
            throw new GameInputsValidationException();
        }
    }

    private void validateRoundTab(RoundTab tab) {
        int blankCapacity = tab.getBlankDimensions().getValue().getCapacity();
        List<AudioTrack> tabAudioTracks = tab.getEditAudioTracksTable().getAudioTracks();
        if (tabAudioTracks.size() < blankCapacity) {
            messageAboutInsufficientNumberOfAudioTracks += "\u2022 Number of audiotracks in round '" + tab.getTab().getText() + "' (" + tabAudioTracks.size() + ") is less than blank capacity (" + blankCapacity + "). Please add more tracks.\n";
        }
        Map<String, List<AudioTrack>> artistsOccurences = tabAudioTracks.stream()
                .collect(Collectors.groupingBy(AudioTrack::getArtist));

        messageAboutNotUniqueArtist = artistsOccurences.entrySet().stream()
                .filter(artistSet -> artistSet.getValue().size() > 1)
                .map(artistSet -> "\u2022 There are " + artistSet.getValue().size() + " audiotracks for artist "
                        + artistSet.getKey() + " in round '" + tab.getTab().getText() + "' ("
                        + artistSet.getValue().stream().map(AudioTrack::getTitle).collect(Collectors.joining(", "))
                        + "). Please leave only one track of this artist in the specified round.")
                .collect(Collectors.joining("\n\n"));
    }
}
