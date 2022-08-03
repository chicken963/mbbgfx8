package ru.orthodox.mbbg.services;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Blank;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.ui.modelExtensions.viewBlanksScene.BlankPreviewAnchorPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewRoundBlanksInGameService {
    private final AnchorPane blankPreview;
    private final Label blankItem;
    private final BlanksProgressService blanksProgressService;

    public ViewRoundBlanksInGameService(AnchorPane blankPreview, Label blankItem, BlanksProgressService blanksProgressService) {
        this.blankPreview = blankPreview;
        this.blankItem = blankItem;
        this.blanksProgressService = blanksProgressService;
    }
    Map<AudioTrack, Label> tracksAndCells = new HashMap<>();

    public void renderBlankByMiniature(Button eventSourceButton, Round round) {
        String blankName = eventSourceButton.getText();

        Blank blankToRender = round.getBlanks().stream()
                .filter(blank -> blank.getNumber().equals(blankName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No blank with name %s was found", blankName)));

        BlankPreviewAnchorPane blankPreviewAnchorPane = new BlankPreviewAnchorPane(blankPreview, blankToRender, "", blankItem);
        blankPreview.setVisible(true);
        List<Label> blankItems = blankPreviewAnchorPane.addItemsToPreview();
        tracksAndCells = blankItems.stream()
                .collect(Collectors.toMap(cell -> round.getAudioTracks().stream()
                        .filter(audioTrack -> audioTrack.getArtist().equals(cell.getText()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No cell found in preview with artist " + cell.getText())),
                        Function.identity()));
        Set<AudioTrack> playedAudiotracksForTheBlank = round.getPlayedAudiotracks().stream()
                .distinct()
                .filter(tracksAndCells.keySet()::contains)
                .collect(Collectors.toSet());
        for (AudioTrack audioTrack: playedAudiotracksForTheBlank) {
            tracksAndCells.get(audioTrack).getStyleClass().add("passed");
        }
    }
}
