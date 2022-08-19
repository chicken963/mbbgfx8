package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.common.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.services.create.AudioTrackCreateModeButtonsService.prepareButtonView;
import static ru.orthodox.mbbg.services.create.RangeSliderService.updateRangeSlider;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

public class AudioTracksLibraryGrid {

    private static final int NUMBER_OF_COLUMNS = 10;
    private static final int CHECKBOX_COLUMN_INDEX = 0;
    private static final int ARTIST_COLUMN_INDEX = 1;
    private static final int TITLE_COLUMN_INDEX = 2;
    private static final int START_TIME_COLUMN_INDEX = 3;
    private static final int TIMELINE_COLUMN_INDEX = 4;
    private static final int END_TIME_COLUMN_INDEX = 5;
    private static final int PROGRESS_COLUMN_INDEX = 6;
    private static final int PLAY_BUTTON_COLUMN_INDEX = 7;
    private static final int PAUSE_BUTTON_COLUMN_INDEX = 8;
    private static final int STOP_BUTTON_COLUMN_INDEX = 9;
    private static final int TEMPLATE_ROW_INDEX = 0;

    private HBox checkBoxContainerTemplate;
    private final GridPane audioTracksGridPane;
    private List<AudioTrack> audioTracks;
    @Setter
    private PlayMediaService playMediaService;
    private final List<AudioTrackUIView> gridRows = new ArrayList<>();
    private final RowConstraints templateRowConstraints;
    private Label artistTemplate;
    private Label songTitleTemplate;
    private Label startTimeTemplate;
    private Label endTimeTemplate;
    private Label progressTemplate;
    private HBox timelineContainerTemplate;
    private RangeSlider rangeSliderTemplate;
    private HBox playButtonContainerTemplate;
    private HBox pauseButtonContainerTemplate;
    private HBox stopButtonContainerTemplate;
    private final CheckBox checkBoxTemplate;

    public AudioTracksLibraryGrid(GridPane audioTracksGridPane){
        this.audioTracksGridPane = audioTracksGridPane;
        this.templateRowConstraints = audioTracksGridPane.getRowConstraints().get(0);
        initiateTemplatesFromTemplateRowInUI();

        this.rangeSliderTemplate = (RangeSlider) timelineContainerTemplate.getChildren().get(0);
        this.checkBoxTemplate = (CheckBox) checkBoxContainerTemplate.getChildren().get(0);

        ThreadUtils.runTaskInSeparateThread(() -> updateRangeSlider(playMediaService, gridRows), "newGamePlayInfo" + this.hashCode());
    }

    private void initiateTemplatesFromTemplateRowInUI() {
        this.artistTemplate = (Label) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, ARTIST_COLUMN_INDEX);
        this.songTitleTemplate = (Label) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, TITLE_COLUMN_INDEX);
        this.startTimeTemplate = (Label) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, START_TIME_COLUMN_INDEX);
        this.timelineContainerTemplate = (HBox) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, TIMELINE_COLUMN_INDEX);
        this.endTimeTemplate = (Label) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, END_TIME_COLUMN_INDEX);
        this.progressTemplate = (Label) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, PROGRESS_COLUMN_INDEX);
        this.playButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, PLAY_BUTTON_COLUMN_INDEX);
        this.pauseButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, PAUSE_BUTTON_COLUMN_INDEX);
        this.stopButtonContainerTemplate = (HBox) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, STOP_BUTTON_COLUMN_INDEX);
        this.checkBoxContainerTemplate = (HBox) findGridPaneNodeByIndexes(TEMPLATE_ROW_INDEX, CHECKBOX_COLUMN_INDEX);
    }

    public void addAudioTracks(List<AudioTrack> audioTracks) {
        audioTracks.forEach(this::addTrack);
        removeTemplateElementsFromGrid();
    }

    private void addTrack(AudioTrack audioTrack) {
        addNewRowConstraint();
        AudioTrackLibraryGridRow row = addNewRowToGridPane(audioTrack);
        gridRows.add(row);
    }

    private AudioTrackLibraryGridRow addNewRowToGridPane(AudioTrack audioTrack) {
        int rowIndex = audioTracksGridPane.getRowConstraints().size();
        audioTracksGridPane.addRow(rowIndex - 1);

        AudioTrackLibraryGridRow row = createUIRowElementsFromTemplates(audioTrack, rowIndex);

        row.setAudioTrack(audioTrack);

        return row;
    }

    private void addNewRowConstraint() {
        this.audioTracksGridPane.getRowConstraints().add(createDeepCopy(templateRowConstraints));
    }

    private AudioTrackLibraryGridRow createUIRowElementsFromTemplates(AudioTrack audioTrack, int rowIndex) {
        HBox checkBoxContainer = createDeepCopy(checkBoxContainerTemplate);
        CheckBox checkBox = createDeepCopy(checkBoxTemplate);
        checkBoxContainer.getChildren().add(checkBox);
        audioTracksGridPane.add(checkBoxContainer, CHECKBOX_COLUMN_INDEX, rowIndex);

        Label artist = createDeepCopy(artistTemplate);
        artist.setText(audioTrack.getArtist());
        audioTracksGridPane.add(artist, ARTIST_COLUMN_INDEX, rowIndex);

        Label songTitle = createDeepCopy(songTitleTemplate);
        songTitle.setText(audioTrack.getTitle());
        audioTracksGridPane.add(songTitle, TITLE_COLUMN_INDEX, rowIndex);

        Label startTime = createDeepCopy(startTimeTemplate);
        startTime.setText(toStringFormat(audioTrack.getStartInSeconds()));
        audioTracksGridPane.add(startTime, START_TIME_COLUMN_INDEX, rowIndex);

        HBox rangeSliderContainer = createDeepCopy(timelineContainerTemplate);
        RangeSlider rangeSlider = createDeepCopy(rangeSliderTemplate);
        rangeSlider.setMin(0);
        rangeSlider.setMax(audioTrack.getLengthInSeconds());
        rangeSlider.setHighValue(audioTrack.getFinishInSeconds());
        rangeSlider.setLowValue(audioTrack.getStartInSeconds());
        rangeSliderContainer.getChildren().add(rangeSlider);
        audioTracksGridPane.add(rangeSliderContainer, TIMELINE_COLUMN_INDEX, rowIndex);

        Label endTime = createDeepCopy(endTimeTemplate);
        endTime.setText(toStringFormat(audioTrack.getFinishInSeconds()));
        audioTracksGridPane.add(endTime, END_TIME_COLUMN_INDEX, rowIndex);

        Label progress = createDeepCopy(progressTemplate);
        progress.setText(getSongProgressAsString(0, audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
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

        AudioTrackLibraryGridRow row = AudioTrackLibraryGridRow.builder()
                .artistLabel(artist)
                .songTitleLabel(songTitle)
                .startTimeLabel(startTime)
                .endTimeLabel(endTime)
                .progressLabel(progress)
                .rangeSliderContainer(rangeSliderContainer)
                .playButtonContainer(playButtonContainer)
                .pauseButtonContainer(pauseButtonContainer)
                .stopButtonContainer(stopButtonContainer)
                .checkBoxContainer(checkBoxContainer)
                .build();

//        row.defineHoverLogic();
        return row;
    }

    private Button prepareEditPlayerButton(String imageUrl, ButtonType type, AudioTrack audioTrack) {
        Button buttonView = prepareButtonView(imageUrl);
        buttonView.setOnAction(event -> {
            switch (type) {
                case PLAY:
                    defineOnPlayLogic(audioTrack);
                    break;
                case PAUSE:
                    playMediaService.pause(audioTrack);
                    break;
                default:
                    playMediaService.stop(audioTrack);
                    break;
            }
        });
        return buttonView;
    }

    private void defineOnPlayLogic(AudioTrack audioTrack) {
        if (playMediaService.getCurrentTrack().equals(audioTrack) && !playMediaService.isCurrentStopInPlayableRange()) {
            playMediaService.stop(audioTrack);
        }
        playMediaService.play(audioTrack);
    }

    public List<AudioTrack> getSelectedAudiotracks() {
        return gridRows.stream()
                .map(row -> (AudioTrackLibraryGridRow) row)
                .filter(AudioTrackLibraryGridRow::isSelected)
                .map(AudioTrackUIView::getAudioTrack)
                .collect(Collectors.toList());
    }

    private Node findGridPaneNodeByIndexes(int rowIndex, int columnIndex) {
        return ElementFinder.findGridPaneNodeByIndexes(audioTracksGridPane, rowIndex, columnIndex);
    }

    private void removeTemplateElementsFromGrid() {
        audioTracksGridPane.getRowConstraints().remove(TEMPLATE_ROW_INDEX);
        audioTracksGridPane.getChildren()
                .subList(NUMBER_OF_COLUMNS * TEMPLATE_ROW_INDEX, NUMBER_OF_COLUMNS * (TEMPLATE_ROW_INDEX + 1))
                .forEach(elem -> elem.setVisible(false));
    }
}
