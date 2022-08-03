package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class AudioTrackGridRow implements AudioTrackUIMapper {
    @Setter
    private AudioTrack audioTrack;
    private RowConstraints rowConstraints;
    private TextField artistLabel;
    private TextField songTitleLabel;
    private Label startTimeLabel;
    private Label endTimeLabel;
    private Label progressLabel;
    private HBox rangeSliderContainer;
    private HBox playButtonContainer;
    private HBox pauseButtonContainer;
    private HBox stopButtonContainer;
    private HBox deleteButtonContainer;

    public List<Region> getUIElements() {
        List<Region> result = new ArrayList<>();
        result.add(artistLabel);
        result.add(songTitleLabel);
        result.add(startTimeLabel);
        result.add(endTimeLabel);
        result.add(progressLabel);
        result.add(rangeSliderContainer);
        result.add(playButtonContainer);
        result.add(pauseButtonContainer);
        result.add(stopButtonContainer);
        result.add(deleteButtonContainer);
        return result;
    }

    public RangeSlider getRangeSlider() {
        return (RangeSlider) getRangeSliderContainer().getChildren().get(0);
    }

    public void defineLabelsLogic(){
        artistLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if (mouseEvent.getClickCount() == 2) {
                    ((GridPane) artistLabel.getParent()).getChildren().stream()
                            .filter(child -> child instanceof TextField)
                            .map(child -> (TextField) child)
                            .filter(child -> !child.equals(artistLabel))
                            .forEach(child -> child.setEditable(false));
                    artistLabel.setEditable(true);
                    artistLabel.setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER)) {
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
                    ((GridPane) songTitleLabel.getParent()).getChildren().stream()
                            .filter(child -> child instanceof TextField)
                            .map(child -> (TextField) child)
                            .filter(child -> !child.equals(songTitleLabel))
                            .forEach(child -> child.setEditable(false));
                    songTitleLabel.setEditable(true);
                    songTitleLabel.setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER)) {
                            String newValue = songTitleLabel.getText();
                            this.audioTrack.setTitle(newValue);
                            songTitleLabel.setEditable(false);
                        }
                    });
                }
            }
        });
    }

/*    public void defineHoverLogic() {
        List<Region> children = this.getUIElements();
        children.forEach(element -> {
            element.setOnMouseEntered(event -> children.forEach(child -> child.getStyleClass().add("grid-row-hovered")));
            element.setOnMouseExited(event -> children.forEach(child -> child.getStyleClass().remove("grid-row-hovered")));
        });
    }*/
}
