package ru.orthodox.mbbg.ui.modelExtensions.playGameScene;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.ui.hierarchy.NodeDeepCopyProvider.createDeepCopy;

public class AudioTracksStackManager {

    private Label artistTemplate;
    private Label songTitleTemplate;
    private List<GridPaneRow> rows;
    private HBox rowTemplate;

    private final VBox audioTracksTable;

    public AudioTracksStackManager(VBox audioTracksTable) {
        this.audioTracksTable = audioTracksTable;
        HBox rowTemplateFromView = (HBox) audioTracksTable.getChildren().get(1);
        this.rowTemplate = createDeepCopy(rowTemplateFromView);
        this.artistTemplate = createDeepCopy((Label) (rowTemplateFromView.getChildren().get(0)));
        this.songTitleTemplate = createDeepCopy((Label) (rowTemplateFromView.getChildren().get(1)));
        audioTracksTable.getChildren().remove(1);
        this.rows = new ArrayList<>();
    }

    public void populateWithAudioTracks(List<AudioTrack> audioTracks) {
        for (AudioTrack audioTrack: audioTracks) {
            addRow(audioTrack);
        }
    }

    private void addRow(AudioTrack audioTrack) {
        int currentRowsCount = audioTracksTable.getChildren().size();

        Label artistLabel = createDeepCopy(artistTemplate);
        artistLabel.setText(audioTrack.getArtist());

        Label titleLabel = createDeepCopy(songTitleTemplate);
        titleLabel.setText(audioTrack.getTitle());

        HBox rowContainer = createDeepCopy(rowTemplate);

        rowContainer.getChildren().setAll(artistLabel, titleLabel);

        audioTracksTable.getChildren().add(rowContainer);
        rows.add(new GridPaneRow(audioTrack, rowContainer, artistLabel, titleLabel));
    }

    private GridPaneRow findRowByAudioTrack(AudioTrack audioTrack) {
        return rows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no row for audio track " + audioTrack.getTitle()));
    }

    public void updateActiveRow(AudioTrack currentTrack) {
        rows.forEach(row -> row.setActive(false));
        findRowByAudioTrack(currentTrack).setActive(true);
    }

    @Getter
    @AllArgsConstructor
    private static class GridPaneRow {
        private static final String ACTIVE_STYLECLASS = "active";

        private AudioTrack audioTrack;
        private HBox rowContainer;
        private Label artist;
        private Label title;

        public void setActive(boolean active) {
            if (active) {
                rowContainer.getStyleClass().add(ACTIVE_STYLECLASS);
                rowContainer.getChildren().forEach(label -> label.getStyleClass().add(ACTIVE_STYLECLASS));
            } else {
                rowContainer.getStyleClass().remove(ACTIVE_STYLECLASS);
                rowContainer.getChildren().forEach(label -> label.getStyleClass().remove(ACTIVE_STYLECLASS));
            }
        }
    }
}
