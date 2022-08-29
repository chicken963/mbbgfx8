package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.utils.common.ThreadUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.services.create.AudioTrackCreateModeButtonsService.prepareButtonView;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Component
public class AudioTracksLibraryTable implements AudioTracksTable {

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

    private VBox audioTracksGrid;
    @Setter
    private HBox rowTemplate;

    private final List<AudioTrackEditUIView> gridRows = new ArrayList<>();
    @Getter
    private Button addSelectedTracksButton;

    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private RangeSliderService rangeSliderService;

    @PostConstruct
    public void setup(){
        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderService.updateRangeSlider(gridRows), "library");
    }

    public void setRoot(AnchorPane root) {
        this.audioTracksGrid = findElementByTypeAndStyleclass(root, "tracks-grid");
        this.addSelectedTracksButton = findElementByTypeAndStyleclass(root, "add-selected-tracks");
    }

    public void addAudioTracks(List<AudioTrack> audioTracks) {
        audioTracks.forEach(this::addTrack);
    }

    private void addTrack(AudioTrack audioTrack) {
        AudioTrackLibraryGridRow row = createUIRowElementsFromTemplates(audioTrack);
        audioTracksGrid.getChildren().add(row.getRowContainer());
        gridRows.add(row);
    }

    private AudioTrackLibraryGridRow createUIRowElementsFromTemplates(AudioTrack audioTrack) {
        HBox newRowUI = (HBox) createDeepCopy(rowTemplate);

        AudioTrackLibraryGridRow newLibraryRow = AudioTrackLibraryGridRow.builder()
                .audioTrack(audioTrack)
                .rowContainer(newRowUI)
                .build();

        newLibraryRow.getArtistLabel().setText(audioTrack.getArtist());
        newLibraryRow.getSongTitleLabel().setText(audioTrack.getTitle());
        newLibraryRow.getStartTimeLabel().setText(toStringFormat(audioTrack.getStartInSeconds()));
        newLibraryRow.getEndTimeLabel().setText(toStringFormat(audioTrack.getFinishInSeconds()));
        newLibraryRow.getProgressLabel().setText(getSongProgressAsString(0, audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));

        RangeSlider rangeSlider = (RangeSlider) newLibraryRow.getRangeSliderContainer().getChildren().get(0);
        rangeSlider.setMin(0);
        rangeSlider.setMax(audioTrack.getLengthInSeconds());
        rangeSlider.setHighValue(audioTrack.getFinishInSeconds());
        rangeSlider.setLowValue(audioTrack.getStartInSeconds());

        newLibraryRow.getPlayButtonContainer().getChildren().add(
                prepareEditPlayerButton("/mediaplayerIcons/play-small2.png", ButtonType.PLAY, audioTrack));
        newLibraryRow.getPauseButtonContainer().getChildren().add(
                prepareEditPlayerButton("/mediaplayerIcons/pause-small3.png", ButtonType.PAUSE, audioTrack));
        newLibraryRow.getStopButtonContainer().getChildren().add(
                prepareEditPlayerButton("/mediaplayerIcons/stop-small2.png", ButtonType.STOP, audioTrack));

        return newLibraryRow;
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
                default:
                    playMediaService.stop(audioTrack);
                    break;
            }
        });
        return buttonView;
    }

    public List<AudioTrack> getSelectedAudiotracks() {
        return gridRows.stream()
                .map(row -> (AudioTrackLibraryGridRow) row)
                .filter(AudioTrackLibraryGridRow::isSelected)
                .map(AudioTrackEditUIView::getAudioTrack)
                .collect(Collectors.toList());
    }

    public void clear() {
        this.gridRows.clear();
    }

    @Override
    public List<AudioTrackEditUIView> getRows() {
        return gridRows;
    }

    @Override
    public List<AudioTrack> getAudioTracks() {
        return gridRows.stream()
                .map(AudioTrackEditUIView::getAudioTrack)
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    private static class AudioTrackLibraryGridRow implements AudioTrackEditUIView {
        private AudioTrack audioTrack;
        private HBox rowContainer;

        public RangeSlider getRangeSlider() {
            return (RangeSlider) getRangeSliderContainer().getChildren().get(0);
        }

        @Override
        public HBox getDeleteButtonContainer() {
            return null;
        }

        public boolean isSelected(){
            return getCheckBox().isSelected();
        }

        private CheckBox getCheckBox() {
            return (CheckBox) this.getCheckBoxContainer().getChildren().get(0);
        }

        public Label getArtistLabel() {
            return (Label) rowContainer.getChildren().get(ARTIST_COLUMN_INDEX);
        }

        public Label getSongTitleLabel() {
            return (Label) rowContainer.getChildren().get(TITLE_COLUMN_INDEX);
        }

        public Label getStartTimeLabel() {
            return (Label) rowContainer.getChildren().get(START_TIME_COLUMN_INDEX);
        }

        public Label getEndTimeLabel() {
            return (Label) rowContainer.getChildren().get(END_TIME_COLUMN_INDEX);
        }

        public Label getProgressLabel() {
            return (Label) rowContainer.getChildren().get(PROGRESS_COLUMN_INDEX);
        }

        public HBox getRangeSliderContainer() {
            return (HBox) rowContainer.getChildren().get(TIMELINE_COLUMN_INDEX);
        }

        public HBox getPlayButtonContainer() {
            return (HBox) rowContainer.getChildren().get(PLAY_BUTTON_COLUMN_INDEX);
        }

        public HBox getPauseButtonContainer() {
            return (HBox) rowContainer.getChildren().get(PAUSE_BUTTON_COLUMN_INDEX);
        }

        public HBox getStopButtonContainer() {
            return (HBox) rowContainer.getChildren().get(STOP_BUTTON_COLUMN_INDEX);
        }

        public HBox getCheckBoxContainer() {
            return (HBox) rowContainer.getChildren().get(CHECKBOX_COLUMN_INDEX);
        }
    }
}
