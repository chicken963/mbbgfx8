package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.util.Collection;
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
    private static EventPublisherService eventPublisherService;

    private HBox rowContainer;

    public RangeSlider getRangeSlider() {
        return (RangeSlider) getRangeSliderContainer().getChildren().get(0);
    }

    public void defineLabelsLogic(){
        getArtistLabel().setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                VBox tracksTable = (VBox) getArtistLabel().getParent().getParent();
                List<TextField> allEditableFields = findAllEditableFields(tracksTable);

                if (mouseEvent.getClickCount() == 2) {
                    String oldValue = getArtistLabel().getText();

                    allEditableFields.forEach(textField -> textField.setEditable(false));
                    getArtistLabel().setEditable(true);

                    getArtistLabel().setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER)) {

                            String newValue = getArtistLabel().getText();
                            this.audioTrack.setArtist(newValue);
                            getArtistLabel().setEditable(false);
                            eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
                        } else if (ke.getCode().equals(KeyCode.ESCAPE)) {

                            getArtistLabel().setText(oldValue);
                            getArtistLabel().setEditable(false);

                        }
                    });
                }
 /*               else if (mouseEvent.getClickCount() == 1) {
                    Optional<TextField> editedField = allEditableFields.stream()
                            .filter(TextInputControl::isEditable)
                            .findFirst();
                    if (editedField.isPresent()) {

                    }
                }*/
            }
        });

        getSongTitleLabel().setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                VBox tracksTable = (VBox) getArtistLabel().getParent().getParent();
                List<TextField> allEditableFields = findAllEditableFields(tracksTable);
                if (mouseEvent.getClickCount() == 2) {
                    String oldValue = getSongTitleLabel().getText();
                    allEditableFields.forEach(textField -> textField.setEditable(false));
                    getSongTitleLabel().setEditable(true);

                    getSongTitleLabel().setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER)) {

                            String newValue = getSongTitleLabel().getText();
                            this.audioTrack.setTitle(newValue);
                            getSongTitleLabel().setEditable(false);
                            eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
                        } else if (ke.getCode().equals(KeyCode.ESCAPE)) {

                            getSongTitleLabel().setText(oldValue);
                            getSongTitleLabel().setEditable(false);

                        }
                    });
                }
            }
        });
    }

    public void defineRangeSliderBoundListeners(PlayMediaService playMediaService) {
        getRangeSlider().highValueProperty().addListener((observable, oldValue, newValue) -> {
            playMediaService.pause(audioTrack);
            String currentTrackFinish = toStringFormat(newValue.doubleValue());
            this.getEndTimeLabel().setText(currentTrackFinish);
            audioTrack.setFinishInSeconds(newValue.doubleValue());
            this.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
        });
        getRangeSlider().lowValueProperty().addListener((observable, oldValue, newValue) -> {
            playMediaService.pause(audioTrack);
            String currentTrackStart = toStringFormat(newValue.doubleValue());
            this.getStartTimeLabel().setText(currentTrackStart);
            audioTrack.setStartInSeconds(newValue.doubleValue());
            this.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
        });
    }

    public static AudioTrackGridRow of(AudioTrack audioTrack, PlayMediaService playMediaService) {
        HBox rowContainer = (HBox) createDeepCopy(rowContainerTemplate);

        AudioTrackGridRow row =  AudioTrackGridRow.builder()
                .audioTrack(audioTrack)
                .rowContainer(rowContainer)
                .build();

        row.getArtistLabel().setText(audioTrack.getArtist());
        row.getSongTitleLabel().setText(audioTrack.getTitle());
        row.getStartTimeLabel().setText(toStringFormat(audioTrack.getStartInSeconds()));
        row.getEndTimeLabel().setText(toStringFormat(audioTrack.getFinishInSeconds()));
        row.getProgressLabel().setText(getSongProgressAsString(0, audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
        row.getRangeSlider().setMin(0);
        row.getRangeSlider().setMax(audioTrack.getLengthInSeconds());
        row.getRangeSlider().setHighValue(audioTrack.getFinishInSeconds());
        row.getRangeSlider().setLowValue(audioTrack.getStartInSeconds());
        row.defineLabelsLogic();
        row.defineRangeSliderBoundListeners(playMediaService);
        return row;
    }

    @Override
    public CheckBox getCheckBox() {
        return null;
    }

    @Override
    public TextField getArtistLabel() {
        return (TextField) rowContainer.getChildren().get(ARTIST_COLUMN_INDEX);
    }

    @Override
    public TextField getSongTitleLabel() {
        return (TextField) rowContainer.getChildren().get(TITLE_COLUMN_INDEX);
    }

    @Override
    public Label getStartTimeLabel() {
        return (Label) rowContainer.getChildren().get(START_TIME_COLUMN_INDEX);
    }

    @Override
    public Label getEndTimeLabel() {
        return (Label) rowContainer.getChildren().get(END_TIME_COLUMN_INDEX);
    }

    @Override
    public Label getProgressLabel() {
        return (Label) rowContainer.getChildren().get(PROGRESS_COLUMN_INDEX);
    }

    @Override
    public HBox getRangeSliderContainer() {
        return (HBox) rowContainer.getChildren().get(TIMELINE_COLUMN_INDEX);
    }

    @Override
    public HBox getPlayButtonContainer() {
        return (HBox) rowContainer.getChildren().get(PLAY_BUTTON_COLUMN_INDEX);
    }

    @Override
    public HBox getPauseButtonContainer() {
        return (HBox) rowContainer.getChildren().get(PAUSE_BUTTON_COLUMN_INDEX);
    }

    @Override
    public HBox getStopButtonContainer() {
        return (HBox) rowContainer.getChildren().get(STOP_BUTTON_COLUMN_INDEX);
    }

    @Override
    public HBox getDeleteButtonContainer() {
        return (HBox) rowContainer.getChildren().get(DELETE_BUTTON_COLUMN_INDEX);
    }

    private List<TextField> findAllEditableFields(VBox tracksTable) {
        List<HBox> rows = tracksTable.getChildren().stream()
                .filter(child -> child instanceof HBox)
                .map(child -> (HBox) child)
                .collect(Collectors.toList());
        return rows.stream()
                .map(Pane::getChildren)
                .flatMap(Collection::stream)
                .filter(child -> child instanceof TextField)
                .map(child -> (TextField) child)
                .collect(Collectors.toList());
    }
}
