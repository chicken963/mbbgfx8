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
import ru.orthodox.mbbg.events.create.ActiveRowChangedEvent;
import ru.orthodox.mbbg.events.create.TextFieldChangeEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.GameAudioTracksListChangedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.AudioTrackUIViewService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.services.create.AudioTrackCreateModeButtonsService.prepareButtonView;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

public class EditAudioTracksTable implements AudioTracksTable {

    private final PlayMediaService playMediaService;
    private final EventPublisherService eventPublisherService;
    @Getter
    @Setter
    private Round round;

    private boolean isPlayed;

    private final AudioTrackUIViewService audioTrackUIViewService;

    private final VBox audioTracksTable;
    private final List<AudioTrackEditUIView> gridRows = new ArrayList<>();
    @Getter
    private final Label placeholder;

    public EditAudioTracksTable(VBox audioTracksTable,
                                HBox audioTracksTableRowTemplate,
                                PlayMediaService playMediaService,
                                EventPublisherService eventPublisherService,
                                Round round) {
        this.playMediaService = playMediaService;
        this.eventPublisherService = eventPublisherService;
        this.round = round;
        this.audioTrackUIViewService = new AudioTrackUIViewService();
        this.audioTracksTable = audioTracksTable;
        this.placeholder = findElementByTypeAndStyleclass(audioTracksTable, "tracks-grid-placeholder");
        this.placeholder.managedProperty().bind(this.placeholder.visibleProperty());

        AudioTrackGridRow.setRowContainerTemplate(audioTracksTableRowTemplate);
        AudioTrackGridRow.setEventPublisherService(eventPublisherService);


    }

    @Override
    public List<AudioTrackEditUIView> getRows() {
        return gridRows;
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
        AudioTrackEditUIView row = AudioTrackGridRow.of(audioTrack, playMediaService);
        populateRowWithButtons(row);
        audioTracksTable.getChildren().add(row.getRowContainer());
        gridRows.add(row);
        eventPublisherService.publishEvent(new GameAudioTracksListChangedEvent(round, row, EntityUpdateMode.ADD));
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
                    AudioTrack trackBeforeClickingPlay = playMediaService.getCurrentTrack();
                    playMediaService.play(audioTrack);
                    if (trackBeforeClickingPlay != playMediaService.getCurrentTrack()) {
                        eventPublisherService.publishEvent(
                            new ActiveRowChangedEvent(this, findRowByPlayButton((Button) event.getSource()))
                        );
                    }
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
        round.getAudioTracks().remove(audioTrack);
        audioTracksTable.getChildren().remove(rowIndex + 1);
        if (audioTracksTable.getChildren().size() == 1) {
            placeholder.setVisible(true);
        }
        eventPublisherService.publishEvent(new GameAudioTracksListChangedEvent(round, rowToDelete, EntityUpdateMode.DELETE));
    }

    private AudioTrackEditUIView findRowByPlayButton(Button playButton) {
        return gridRows.stream()
                .filter(row -> row.getPlayButtonContainer().getChildren().get(0) == playButton)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No row found for event source"));
    }
}
