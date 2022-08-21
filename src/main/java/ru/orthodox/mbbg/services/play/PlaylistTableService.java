package ru.orthodox.mbbg.services.play;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.CurrentTrackChangedEvent;
import ru.orthodox.mbbg.events.NextTrackChangeRequestedByUserEvent;
import ru.orthodox.mbbg.events.NextTrackChangedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackUIView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Service
public class PlaylistTableService  {

    @EventListener
    public void onNextTrackChanged(NextTrackChangedEvent nextTrackChangedEvent) {
        highlightNextRow(activeRound.getNextTrack());
    }

    @EventListener
    public void onCurrentTrackChanged(CurrentTrackChangedEvent currentTrackChangedEvent) {
        highlightActiveRow(activeRound.getCurrentTrack());
        disableTextContainers(activeRound.getPreviousTrack());
        disablePlayedRows();
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private Button nextIconTemplate;
    private Label artistTemplate;
    private Label songTitleTemplate;
    private List<PlaylistTableRow> rows;
    private HBox rowTemplate;
    private Round activeRound;

    private VBox audioTracksTable;

    public void configureUIElements(VBox audioTracksTable, HBox audioTracksTableRowTemplate) {
        this.audioTracksTable = audioTracksTable;
        this.rowTemplate = createDeepCopy(audioTracksTableRowTemplate);
        this.nextIconTemplate = (Button) createDeepCopy((Parent) audioTracksTableRowTemplate.getChildren().get(0));
        this.artistTemplate = createDeepCopy((Label) (audioTracksTableRowTemplate.getChildren().get(1)));
        this.songTitleTemplate = createDeepCopy((Label) (audioTracksTableRowTemplate.getChildren().get(2)));
    }

    public void setActiveRound(Round round) {
        this.activeRound = round;

        this.rows = new ArrayList<>();
        audioTracksTable.getChildren().clear();

        populateWithAudioTracks(round.getAudioTracks());

    }

    public void populateWithAudioTracks(List<AudioTrack> audioTracks) {
        for (AudioTrack audioTrack: audioTracks) {
            addRow(audioTrack);
        }
    }

    private void addRow(AudioTrack audioTrack) {

        Button nextIcon = (Button) createDeepCopy(nextIconTemplate);
        nextIcon.getGraphic().setVisible(false);

        Label artistLabel = createDeepCopy(artistTemplate);
        artistLabel.setText(audioTrack.getArtist());

        Label titleLabel = createDeepCopy(songTitleTemplate);
        titleLabel.setText(audioTrack.getTitle());

        HBox rowContainer = createDeepCopy(rowTemplate);

        rowContainer.getChildren().setAll(nextIcon, artistLabel, titleLabel);

        audioTracksTable.getChildren().add(rowContainer);

        rows.add(PlaylistTableRow.builder()
                .audioTrack(audioTrack)
                .rowContainer(rowContainer)
                .nextIcon(nextIcon)
                .artist(artistLabel)
                .title(titleLabel)
                .disabled(false)
                .build());
    }

    private PlaylistTableRow findRowByAudioTrack(AudioTrack audioTrack) {
        return rows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no row for audio track " + audioTrack.getTitle()));
    }

    private void highlightActiveRow(AudioTrack currentTrack) {
        rows.forEach(row -> row.setActive(false));
        findRowByAudioTrack(currentTrack).setActive(true);
    }

    private void highlightNextRow(AudioTrack audioTrack) {
        rows.forEach(row -> row.setNext(false));
        if (audioTrack != null) {
            findRowByAudioTrack(audioTrack).setNext(true);
        }
    }

    public PlaylistTableRow findRowByButton(Button button) {
        return rows.stream()
                .filter(row -> row.getNextIcon().equals(button))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no row for the button"));
    }

    private void disablePlayedRows() {
        Set<AudioTrack> playedAudioTracks = activeRound.getPlayedAudiotracks();
        rows.forEach(row -> {
            boolean trackIsPlayed = playedAudioTracks.contains(row.getAudioTrack());
            row.setDisabled(trackIsPlayed);
            row.getNextIcon().setDisable(trackIsPlayed);
        });
    }


    private void disableTextContainers(AudioTrack audioTrack) {
        if (audioTrack != null) {
            PlaylistTableRow row = findRowByAudioTrack(audioTrack);
            row.getRowContainer().setDisable(true);
        }
    }

    public void setNextTrack(Button sourceButton) {
        AudioTrack targetAudioTrack = findRowByButton(sourceButton).getAudioTrack();
        eventPublisher.publishEvent(new NextTrackChangeRequestedByUserEvent(this, targetAudioTrack));
    }

    public Optional<AudioTrack> findFirstActiveTrack() {
        return rows.stream()
                .filter(row -> !row.isDisabled())
                .map(PlaylistTableRow::getAudioTrack)
                .findFirst();
    }

    @Getter
    @Builder
    public static class PlaylistTableRow implements AudioTrackUIView {
        private static final String DISABLED_STYLECLASS = "disabled";
        private static final String ACTIVE_STYLECLASS = "active";
        private static final String NEXT_STYLECLASS = "next";

        private AudioTrack audioTrack;
        private HBox rowContainer;
        private Button nextIcon;
        private Label artist;
        private Label title;
        @Setter
        private boolean disabled;

        public void setActive(boolean active) {
            if (active) {
                rowContainer.getStyleClass().add(ACTIVE_STYLECLASS);
                rowContainer.getChildren().forEach(label -> label.getStyleClass().add(ACTIVE_STYLECLASS));
            } else {
                rowContainer.getStyleClass().remove(ACTIVE_STYLECLASS);
                rowContainer.getChildren().forEach(label -> label.getStyleClass().remove(ACTIVE_STYLECLASS));
            }
        }

        public void setNext(boolean next) {
            nextIcon.getGraphic().setVisible(next);
        }
    }
}
