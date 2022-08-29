package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.basic.AudioTrack;

public interface AudioTrackEditUIView extends AudioTrackUIView {
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
