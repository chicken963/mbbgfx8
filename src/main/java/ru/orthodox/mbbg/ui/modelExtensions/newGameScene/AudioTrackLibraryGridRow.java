package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.control.CheckBox;
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

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class AudioTrackLibraryGridRow implements AudioTrackUIMapper {
    @Setter
    private AudioTrack audioTrack;
    private RowConstraints rowConstraints;
    private Label artistLabel;
    private Label songTitleLabel;
    private Label startTimeLabel;
    private Label endTimeLabel;
    private Label progressLabel;
    private HBox rangeSliderContainer;
    private HBox playButtonContainer;
    private HBox pauseButtonContainer;
    private HBox stopButtonContainer;
    private HBox checkBoxContainer;

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
        result.add(checkBoxContainer);
        return result;
    }

    public RangeSlider getRangeSlider() {
        return (RangeSlider) getRangeSliderContainer().getChildren().get(0);
    }

    public void definSubmitLogic(){

    }

/*    public void defineHoverLogic() {
        List<Region> children = this.getUIElements();
        children.forEach(element -> {
            element.setOnMouseEntered(event -> children.forEach(child -> child.getStyleClass().add("grid-row-hovered")));
            element.setOnMouseExited(event -> children.forEach(child -> child.getStyleClass().remove("grid-row-hovered")));
        });
    }*/

    public boolean isSelected(){
        return getCheckBox().isSelected();
    }

    private CheckBox getCheckBox() {
        return (CheckBox) this.getCheckBoxContainer().getChildren().get(0);
    }
}
