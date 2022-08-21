package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.basic.AudioTrack;

import java.util.List;

public interface AudioTrackEditUIView {
    Control getArtistLabel();
    Control getSongTitleLabel();
    AudioTrack getAudioTrack();
    Label getProgressLabel();
    HBox getRangeSliderContainer();
    HBox getRowContainer();
    Label getEndTimeLabel();
    Label getStartTimeLabel();
    RangeSlider getRangeSlider();
    HBox getPlayButtonContainer();
    HBox getPauseButtonContainer();
    HBox getStopButtonContainer();
    HBox getDeleteButtonContainer();
}
