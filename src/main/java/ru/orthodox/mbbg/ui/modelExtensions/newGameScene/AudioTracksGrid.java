package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.EventsHandlingService;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.ui.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.GridRowUtils;
import ru.orthodox.mbbg.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.orthodox.mbbg.services.PlayFromWorkBenchService.*;
import static ru.orthodox.mbbg.ui.hierarchy.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.ui.modelExtensions.newGameScene.AudioTrackButtonsService.prepareButtonView;
import static ru.orthodox.mbbg.ui.modelExtensions.newGameScene.RangeSliderService.updateRangeSlider;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toStringFormat;

public class AudioTracksGrid implements AudioTracksView {

    private static final int NUMBER_OF_COLUMNS = 10;
    private static final int ARTIST_COLUMN_INDEX = 0;
    private static final int TITLE_COLUMN_INDEX = 1;
    private static final int START_TIME_COLUMN_INDEX = 2;
    private static final int TIMELINE_COLUMN_INDEX = 3;
    private static final int END_TIME_COLUMN_INDEX = 4;
    private static final int PROGRESS_COLUMN_INDEX = 5;
    private static final int PLAY_BUTTON_COLUMN_INDEX = 6;
    private static final int PAUSE_BUTTON_COLUMN_INDEX = 7;
    private static final int STOP_BUTTON_COLUMN_INDEX = 8;
    private static final int DELETE_BUTTON_COLUMN_INDEX = 9;


    private final EventsHandlingService eventsHandlingService;
    private final GridPane audioTracksGridPane;
    private List<AudioTrack> audioTracks;
    private PlayService playService;
    private final List<AudioTrackGridRow> gridRows = new ArrayList<>();
    private final RowConstraints templateRowConstraints;
    private final TextField artistTemplate;
    private final TextField songTitleTemplate;
    private final Label startTimeTemplate;
    private final Label endTimeTemplate;
    private final Label progressTemplate;
    private final HBox timelineContainerTemplate;
    private final RangeSlider rangeSliderTemplate;
    private final HBox playButtonContainerTemplate;
    private final HBox pauseButtonContainerTemplate;
    private final HBox stopButtonContainerTemplate;
    private final HBox deleteButtonContainerTemplate;

    public AudioTracksGrid(GridPane audioTracksGridPane, EventsHandlingService eventsHandlingService) {
        this.eventsHandlingService = eventsHandlingService;
        this.audioTracksGridPane = audioTracksGridPane;
        this.templateRowConstraints = audioTracksGridPane.getRowConstraints().get(0);
        this.artistTemplate = (TextField) findGridPaneNodeByIndexes(1, ARTIST_COLUMN_INDEX);
        this.songTitleTemplate = (TextField) findGridPaneNodeByIndexes(1, TITLE_COLUMN_INDEX);
        this.startTimeTemplate = (Label) findGridPaneNodeByIndexes(1, START_TIME_COLUMN_INDEX);
        this.timelineContainerTemplate = (HBox) findGridPaneNodeByIndexes(1, TIMELINE_COLUMN_INDEX);
        this.rangeSliderTemplate = (RangeSlider) timelineContainerTemplate.getChildren().get(0);
        this.endTimeTemplate = (Label) findGridPaneNodeByIndexes(1, END_TIME_COLUMN_INDEX);
        this.progressTemplate = (Label) findGridPaneNodeByIndexes(1, PROGRESS_COLUMN_INDEX);
        this.playButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(1, PLAY_BUTTON_COLUMN_INDEX);
        this.pauseButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(1, PAUSE_BUTTON_COLUMN_INDEX);
        this.stopButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(1, STOP_BUTTON_COLUMN_INDEX);
        this.deleteButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(1, DELETE_BUTTON_COLUMN_INDEX);

        removeTemplateElements(1);

        eventsHandlingService.setGridRows(gridRows);
        ThreadUtils.runTaskInSeparateThread(() -> updateRangeSlider(playService, gridRows), "newGamePlayInfo" + this.hashCode());
    }

    private void removeTemplateElements(int rowIndex) {
        audioTracksGridPane.getRowConstraints().remove(1);
        audioTracksGridPane.getChildren().remove(NUMBER_OF_COLUMNS * rowIndex, NUMBER_OF_COLUMNS * (rowIndex + 1));
    }

    private Node findGridPaneNodeByIndexes(int rowIndex, int columnIndex) {
        return ElementFinder.findGridPaneNodeByIndexes(audioTracksGridPane, rowIndex, columnIndex);
    }

    @Override
    public List<AudioTrack> getAudioTracks() {
        return audioTracks;
    }

    @Override
    public Label getPlaceholder() {
        return gridRows.get(1).getProgressLabel();
    }

    @Override
    public boolean isEmpty() {
        return audioTracks == null || audioTracks.size() == 0;
    }

    @Override
    public boolean isFilled() {
        return gridRows.stream().allMatch(gridRow ->
                !gridRow.getArtistLabel().getText().isEmpty()
             && !gridRow.getSongTitleLabel().getText().isEmpty()
        );
    }

    @Override
    public void addAudioTracks(List<AudioTrack> audioTracks) {
        audioTracks.forEach(this::addTrack);
    }

    private void addTrack(AudioTrack audioTrack) {
        addNewRowConstraint();
        AudioTrackGridRow row = addNewRowToGridPane(audioTrack);
        gridRows.add(row);
    }

    private AudioTrackGridRow addNewRowToGridPane(AudioTrack audioTrack) {
        int rowIndex = audioTracksGridPane.getRowConstraints().size();
        audioTracksGridPane.addRow(rowIndex);

        AudioTrackGridRow row = createUIRowElementsFromTemplates(audioTrack, rowIndex);

        row.setAudioTrack(audioTrack);
        row.defineLabelsLogic();
        return row;
    }

    private void addNewRowConstraint() {
        this.audioTracksGridPane.getRowConstraints().add(createDeepCopy(templateRowConstraints));
    }

    private AudioTrackGridRow createUIRowElementsFromTemplates(AudioTrack audioTrack, int rowIndex) {
        TextField artist = createDeepCopy(artistTemplate);
        artist.setText(audioTrack.getArtist());
        audioTracksGridPane.add(artist, ARTIST_COLUMN_INDEX, rowIndex);

        TextField songTitle = createDeepCopy(songTitleTemplate);
        songTitle.setText(audioTrack.getTitle());
        audioTracksGridPane.add(songTitle, TITLE_COLUMN_INDEX, rowIndex);

        Label startTime = createDeepCopy(startTimeTemplate);
        startTime.setText(toStringFormat(audioTrack.getStartInSeconds()));
        audioTracksGridPane.add(startTime, START_TIME_COLUMN_INDEX, rowIndex);

        HBox rangeSliderContainer = createDeepCopy(timelineContainerTemplate);
        RangeSlider rangeSlider = createDeepCopy(rangeSliderTemplate);
        rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) -> { if (playService != null) playService.pause(); });
        rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> { if (playService != null) playService.pause(); });
        rangeSliderContainer.getChildren().add(rangeSlider);
        audioTracksGridPane.add(rangeSliderContainer, TIMELINE_COLUMN_INDEX, rowIndex);

        Label endTime = createDeepCopy(endTimeTemplate);
        endTime.setText(toStringFormat(audioTrack.getFinishInSeconds()));
        audioTracksGridPane.add(endTime, END_TIME_COLUMN_INDEX, rowIndex);

        Label progress = createDeepCopy(progressTemplate);
        progress.setText(getSongProgressAsString(0, audioTrack.getLengthInSeconds()));
        audioTracksGridPane.add(progress, PROGRESS_COLUMN_INDEX, rowIndex);

        HBox playButtonContainer = createDeepCopy(playButtonContainerTemplate);
        playButtonContainer.getChildren().add(prepareEditPlayerButton("/mediaplayerIcons/play-small2.png", ButtonType.PLAY, audioTrack));
        audioTracksGridPane.add(playButtonContainer, PLAY_BUTTON_COLUMN_INDEX, rowIndex);

        HBox pauseButtonContainer = createDeepCopy(pauseButtonContainerTemplate);
        pauseButtonContainer.getChildren().add(prepareEditPlayerButton("/mediaplayerIcons/pause-small3.png", ButtonType.PAUSE, audioTrack));
        audioTracksGridPane.add(pauseButtonContainer, PAUSE_BUTTON_COLUMN_INDEX, rowIndex);

        HBox stopButtonContainer = createDeepCopy(stopButtonContainerTemplate);
        stopButtonContainer.getChildren().add(prepareEditPlayerButton("/mediaplayerIcons/stop-small2.png", ButtonType.STOP, audioTrack));
        audioTracksGridPane.add(stopButtonContainer, STOP_BUTTON_COLUMN_INDEX, rowIndex);

        HBox deleteButtonContainer = createDeepCopy(deleteButtonContainerTemplate);
        deleteButtonContainer.getChildren().add(prepareEditPlayerButton("/mediaplayerIcons/delete2.png", ButtonType.DELETE, audioTrack));
        audioTracksGridPane.add(deleteButtonContainer, DELETE_BUTTON_COLUMN_INDEX, rowIndex);

        AudioTrackGridRow row =AudioTrackGridRow.builder()
                .artistLabel(artist)
                .songTitleLabel(songTitle)
                .startTimeLabel(startTime)
                .endTimeLabel(endTime)
                .progressLabel(progress)
                .rangeSliderContainer(rangeSliderContainer)
                .playButtonContainer(playButtonContainer)
                .pauseButtonContainer(pauseButtonContainer)
                .stopButtonContainer(stopButtonContainer)
                .deleteButtonContainer(deleteButtonContainer)
                .build();

        row.defineHoverLogic();
        return row;
    }

    private Button prepareEditPlayerButton(String imageUrl, ButtonType type, AudioTrack audioTrack) {
        Button buttonView = prepareButtonView(imageUrl);
        buttonView.setOnAction(event -> {
            switch (type) {
                case PLAY:
                    playService = defineOnPlayLogic(playService, audioTrack);
                    break;
                case PAUSE:
                    defineOnPauseLogic(playService, audioTrack);
                    break;
                case STOP:
                    defineOnStopLogic(playService, audioTrack);
                    break;
                default:
                    deleteRow(audioTrack);
                    break;
            }
        });
        return buttonView;
    }

    private void deleteRow(AudioTrack audioTrack) {
        if (playService != null && playService.getCurrentTrack().equals(audioTrack)) {
            playService.stop();
        }
        AudioTrackGridRow rowToDelete = GridRowUtils.findByAudioTrack(gridRows, audioTrack);
        int rowIndex = gridRows.indexOf(rowToDelete);
        gridRows.remove(rowToDelete);
        gridRows.subList(rowIndex, gridRows.size())
                .forEach(gridRow ->
                        gridRow.getUIElements()
                                .forEach(uiElem ->
                                        GridPane.setRowIndex(uiElem, GridPane.getRowIndex(uiElem) - 1)));
        audioTracksGridPane.getRowConstraints().remove(rowIndex);
        audioTracksGridPane.getChildren().remove(NUMBER_OF_COLUMNS * (rowIndex + 1), NUMBER_OF_COLUMNS * (rowIndex + 2));
    }
}
