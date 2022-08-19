package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.basic.AudioTrack;

import java.util.List;

public interface AudioTrackUIView {
    AudioTrack getAudioTrack();
    Label getProgressLabel();
    Control getArtistLabel();
    Control getSongTitleLabel();
    List<Region> getUIElements();
    HBox getRangeSliderContainer();
    Label getEndTimeLabel();
    Label getStartTimeLabel();
    RangeSlider getRangeSlider();
}
