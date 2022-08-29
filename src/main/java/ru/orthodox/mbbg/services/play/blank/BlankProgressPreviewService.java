package ru.orthodox.mbbg.services.play.blank;

import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.NextTrackChangedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.BlankItem;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.BlinkingColorPair;
import ru.orthodox.mbbg.model.proxy.viewblanks.BlankPreviewAnchorPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BlankProgressPreviewService implements ApplicationListener<NextTrackChangedEvent>  {
    private AnchorPane blankPreviewContainer;
    private Label blankItemTemplate;
    private Label blankPreviewPlaceholder;
    private Round activeRound;

    private Button activeMiniature;

    @Autowired
    private AnimationBackgroundService animationService;
    @Autowired
    private ProgressService progressService;


    Map<AudioTrack, Label> tracksAndCells = new HashMap<>();

    public void configureUIElements(AnchorPane blankPreview, Label blankPreviewPlaceholder, Label blankItemTemplate) {
        this.blankPreviewContainer = blankPreview;
        this.blankPreviewPlaceholder = blankPreviewPlaceholder;
        this.blankItemTemplate = blankItemTemplate;
    }

    public void setActiveRound(Round round) {
        this.activeRound = round;
    }

    public void renderBlankByMiniature(Button eventSourceButton) {
        activeMiniature = eventSourceButton;
        String blankName = eventSourceButton.getText();

        Blank blankToRender = activeRound.getBlanks().stream()
                .filter(blank -> blank.getNumber().equals(blankName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No blank with name %s was found", blankName)));

        BlankPreviewAnchorPane blankPreviewAnchorPane = new BlankPreviewAnchorPane(blankPreviewContainer, blankToRender, activeRound.getName(), blankItemTemplate);
        blankPreviewContainer.setVisible(true);
        List<Label> blankItems = blankPreviewAnchorPane.addItemsToPreview();
        tracksAndCells = blankItems.stream()
                .collect(Collectors.toMap(cell -> activeRound.getAudioTracks().stream()
                                .filter(audioTrack -> audioTrack.getArtist().equals(cell.getText()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("No cell found in preview with artist " + cell.getText())),
                        Function.identity()));
        Set<AudioTrack> playedAudiotracksForTheBlank = activeRound.getPlayedAudiotracks().stream()
                .distinct()
                .filter(tracksAndCells.keySet()::contains)
                .collect(Collectors.toSet());

        Set<String> winningSetArtists = blankToRender.getWinningSet().stream()
                .map(BlankItem::getArtist)
                .collect(Collectors.toSet());

        Set<Label> winningSetCells = blankItems.stream()
                .filter(cell -> winningSetArtists.contains(cell.getText()))
                .collect(Collectors.toSet());

        winningSetCells
                .forEach(label -> {
                    final Animation animation = animationService.provide(
                            label,
                            BlinkingColorPair.forNextProgress(1.0),
                            5.0);
                    animation.play();
                });

        tracksAndCells.entrySet().stream()
                .filter(entry -> playedAudiotracksForTheBlank.contains(entry.getKey()))
                .filter(entry -> !winningSetCells.contains(entry.getValue()))
                .map(Map.Entry::getValue)
                .forEach(label -> label.getStyleClass().add("passed"));

        double nextProgress = activeRound.getBlanks().stream()
                .filter(blank -> blank.equals(blankToRender))
                .map(Blank::getNextProgress)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No progress found for blank " + blankToRender.getNumber()));

        if (activeRound.getNextTrack() != null) {
            blankItems.stream()
                    .filter(item -> item.getText().equals(activeRound.getNextTrack().getArtist()))
                    .limit(1)
                    .forEach(label -> {
                        final Animation animation = animationService.provide(
                                label,
                                BlinkingColorPair.forNextProgress(winningSetCells.isEmpty() ? nextProgress : 0.99),
                                5.0);
                        animation.play();
                    });
        }
    }

    public void emptyBlankPreview() {
        activeMiniature = null;
        blankPreviewContainer.setVisible(false);
        blankPreviewPlaceholder.setVisible(true);
    }

    public void showBlankWithProgress(Button miniature) {
        blankPreviewPlaceholder.setVisible(false);
        renderBlankByMiniature(miniature);
        blankPreviewContainer.setVisible(true);
    }

    @Override
    public void onApplicationEvent(NextTrackChangedEvent nextTrackChangedEvent) {
        if (activeMiniature != null) {
            renderBlankByMiniature(activeMiniature);
        }
    }
}
