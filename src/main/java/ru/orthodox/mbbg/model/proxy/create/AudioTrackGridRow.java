package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
@Builder
public class AudioTrackGridRow implements AudioTrackEditUIView {

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

    @Setter
    private AudioTrack audioTrack;

    @Setter
    private static HBox rowContainerTemplate;


    @Setter
    private static PlayMediaService playMediaService;

    private HBox rowContainer;
    private Label progressLabel;
    private Label endTimeLabel;
    private Label startTimeLabel;
    private TextField artistLabel;
    private TextField songTitleLabel;
    private HBox rangeSliderContainer;
    private HBox playButtonContainer;
    private HBox pauseButtonContainer;
    private HBox stopButtonContainer;
    private HBox deleteButtonContainer;

    public RangeSlider getRangeSlider() {
        return (RangeSlider) getRangeSliderContainer().getChildren().get(0);
    }

    public void defineLabelsLogic(){
        artistLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if (mouseEvent.getClickCount() == 2) {
                    VBox tracksTable = (VBox) artistLabel.getParent().getParent();
                    List<HBox> rows = tracksTable.getChildren().stream()
                            .filter(child -> child instanceof HBox)
                            .map(child -> (HBox) child)
                            .collect(Collectors.toList());
                    rows.forEach(row ->  row.getChildren().stream()
                            .filter(child -> child instanceof TextField)
                            .map(child -> (TextField) child)
                            .forEach(child -> child.setEditable(false)));
                    artistLabel.setEditable(true);
                    artistLabel.setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER) || ke.getCode().equals(KeyCode.ESCAPE)) {
                            String newValue = artistLabel.getText();
                            this.audioTrack.setArtist(newValue);
                            artistLabel.setEditable(false);
                        }
                    });
                }
            }
        });
        songTitleLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if (mouseEvent.getClickCount() == 2) {
                    VBox tracksTable = (VBox) songTitleLabel.getParent().getParent();
                    List<HBox> rows = tracksTable.getChildren().stream()
                            .filter(child -> child instanceof HBox)
                            .map(child -> (HBox) child)
                            .collect(Collectors.toList());
                    rows.forEach(row ->  row.getChildren().stream()
                            .filter(child -> child instanceof TextField)
                            .map(child -> (TextField) child)
                            .forEach(child -> child.setEditable(false)));
                    songTitleLabel.setEditable(true);
                    songTitleLabel.setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER) || ke.getCode().equals(KeyCode.ESCAPE)) {
                            String newValue = songTitleLabel.getText();
                            this.audioTrack.setTitle(newValue);
                            songTitleLabel.setEditable(false);
                        }
                    });
                }
            }
        });
    }

    public static AudioTrackGridRow of (AudioTrack audioTrack) {

        HBox rowContainer = (HBox) createDeepCopy(rowContainerTemplate);

        TextField artist = createDeepCopy((TextField) rowContainerTemplate.getChildren().get(ARTIST_COLUMN_INDEX));
        artist.setText(audioTrack.getArtist());
        rowContainer.getChildren().add(artist);

        TextField songTitle = createDeepCopy((TextField) rowContainerTemplate.getChildren().get(TITLE_COLUMN_INDEX));
        songTitle.setText(audioTrack.getTitle());
        rowContainer.getChildren().add(songTitle);

        Label startTime = createDeepCopy((Label) rowContainerTemplate.getChildren().get(START_TIME_COLUMN_INDEX));
        startTime.setText(toStringFormat(audioTrack.getStartInSeconds()));
        rowContainer.getChildren().add(startTime);

        HBox rangeSliderContainerTemplate = (HBox) rowContainerTemplate.getChildren().get(TIMELINE_COLUMN_INDEX);
        HBox rangeSliderContainer = createDeepCopy(rangeSliderContainerTemplate);
        RangeSlider rangeSlider = createDeepCopy((RangeSlider) rangeSliderContainerTemplate.getChildren().get(0));
        rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) -> { if (playMediaService != null) playMediaService.pause(audioTrack); });
        rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> { if (playMediaService != null) playMediaService.pause(audioTrack); });

        rangeSliderContainer.getChildren().setAll(rangeSlider);
        rowContainer.getChildren().add(rangeSliderContainer);

        Label endTime = createDeepCopy((Label) rowContainerTemplate.getChildren().get(END_TIME_COLUMN_INDEX));
        endTime.setText(toStringFormat(audioTrack.getFinishInSeconds()));
        rowContainer.getChildren().add(endTime);

        Label progress = createDeepCopy((Label) rowContainerTemplate.getChildren().get(PROGRESS_COLUMN_INDEX));
        progress.setText(getSongProgressAsString(0, audioTrack.getLengthInSeconds()));
        rowContainer.getChildren().add(progress);

        HBox playButtonContainer = createDeepCopy((HBox) rowContainerTemplate.getChildren().get(PLAY_BUTTON_COLUMN_INDEX));
        rowContainer.getChildren().add(playButtonContainer);

        HBox pauseButtonContainer = createDeepCopy((HBox) rowContainerTemplate.getChildren().get(PAUSE_BUTTON_COLUMN_INDEX));
        rowContainer.getChildren().add(pauseButtonContainer);

        HBox stopButtonContainer = createDeepCopy((HBox) rowContainerTemplate.getChildren().get(STOP_BUTTON_COLUMN_INDEX));
        rowContainer.getChildren().add(stopButtonContainer);

        HBox deleteButtonContainer = createDeepCopy((HBox) rowContainerTemplate.getChildren().get(DELETE_BUTTON_COLUMN_INDEX));
        rowContainer.getChildren().add(deleteButtonContainer);

        AudioTrackGridRow row =  AudioTrackGridRow.builder()
                .audioTrack(audioTrack)
                .rowContainer(rowContainer)
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

        row.defineLabelsLogic();
        return row;
    }
}
