package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.events.GameAudioTracksListChangedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.AudioTrackAsyncLengthLoadService;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.AudioTrackUIViewService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.utils.common.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.services.create.AudioTrackCreateModeButtonsService.prepareButtonView;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

public class EditAudioTracksTable {

    @Setter
    private static HBox audioTracksTableRowTemplate;
    @Setter
    private static PlayMediaService playMediaService;
    @Setter
    private static EventPublisherService eventPublisherService;

    private final RangeSliderService rangeSliderService;

    private final AudioTrackUIViewService audioTrackUIViewService;

    private final VBox audioTracksTable;
    private final List<AudioTrackEditUIView> gridRows = new ArrayList<>();
    @Getter
    private final Label placeholder;

    public EditAudioTracksTable(VBox audioTracksTable) {
        this.audioTrackUIViewService = new AudioTrackUIViewService();
        this.rangeSliderService = new RangeSliderService(gridRows, audioTrackUIViewService);
        this.audioTracksTable = audioTracksTable;
        this.placeholder = findElementByTypeAndStyleclass(audioTracksTable, "tracks-grid-placeholder");
        this.placeholder.managedProperty().bind(this.placeholder.visibleProperty());

        AudioTrackGridRow.setRowContainerTemplate(audioTracksTableRowTemplate);
        AudioTrackGridRow.setEventPublisherService(eventPublisherService);

        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderService.updateRangeSlider(playMediaService), "editTable-" + this.hashCode());
    }



    public List<AudioTrack> getAudioTracks() {
        return gridRows.stream()
                .map(AudioTrackEditUIView::getAudioTrack)
                .collect(Collectors.toList());
    }

    private boolean isEmpty() {
        return gridRows.isEmpty();
    }

    public boolean isFilled() {
        return !isEmpty() && gridRows.stream().allMatch(gridRow ->
                !((TextField) gridRow.getArtistLabel()).getText().isEmpty()
             && !((TextField) gridRow.getSongTitleLabel()).getText().isEmpty()
        );
    }

    public void addAudioTracks(List<AudioTrack> audioTracks) {
        if (!audioTracks.isEmpty()) {
            placeholder.setVisible(false);
        }
        audioTracks.forEach(this::addTrack);
        eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
    }

    private void addTrack(AudioTrack audioTrack) {
        AudioTrackEditUIView row = AudioTrackGridRow.of(audioTrack);
        populateRowWithButtons(row);

        audioTracksTable.getChildren().add(row.getRowContainer());
        gridRows.add(row);
        eventPublisherService.publishEvent(new GameAudioTracksListChangedEvent(this, row, EntityUpdateMode.ADD));
    }

    private void populateRowWithButtons(AudioTrackEditUIView row) {
        row.getPlayButtonContainer()
                .getChildren()
                .add(prepareEditPlayerButton("/mediaplayerIcons/play-small2.png", ButtonType.PLAY, row.getAudioTrack()));

        row.getPauseButtonContainer()
                .getChildren()
                .add(prepareEditPlayerButton("/mediaplayerIcons/pause-small3.png", ButtonType.PAUSE, row.getAudioTrack()));

        row.getStopButtonContainer()
                .getChildren()
                .add(prepareEditPlayerButton("/mediaplayerIcons/stop-small2.png", ButtonType.STOP, row.getAudioTrack()));

        row.getDeleteButtonContainer()
                .getChildren()
                .add(prepareEditPlayerButton("/mediaplayerIcons/delete2.png", ButtonType.DELETE, row.getAudioTrack()));
    }

    private Button prepareEditPlayerButton(String imageUrl, ButtonType type, AudioTrack audioTrack) {
        Button buttonView = prepareButtonView(imageUrl);
        buttonView.setOnAction(event -> {
            switch (type) {
                case PLAY:
                    playMediaService.play(audioTrack);
                    break;
                case PAUSE:
                    playMediaService.pause(audioTrack);
                    break;
                case STOP:
                    playMediaService.stop(audioTrack);
                    break;
                default:
                    deleteRow(audioTrack);
                    break;
            }
        });
        return buttonView;
    }

    private void deleteRow(AudioTrack audioTrack) {
        playMediaService.stop(audioTrack);
        AudioTrackEditUIView rowToDelete = audioTrackUIViewService.findByAudioTrack(gridRows, audioTrack)
                .orElseThrow(() -> new IllegalArgumentException("There is no row for audiotrack " + audioTrack.getTitle()));
        int rowIndex = gridRows.indexOf(rowToDelete);
        gridRows.remove(rowToDelete);
        audioTracksTable.getChildren().remove(rowIndex + 1);
        if (audioTracksTable.getChildren().size() == 1) {
            placeholder.setVisible(true);
        }
        eventPublisherService.publishEvent(new GameAudioTracksListChangedEvent(this, rowToDelete, EntityUpdateMode.DELETE));
        eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
    }


}
