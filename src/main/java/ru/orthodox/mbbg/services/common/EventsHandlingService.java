package ru.orthodox.mbbg.services.common;

import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackUIView;

import java.util.List;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

@Service
public class EventsHandlingService implements ApplicationListener<AudioTrackLengthLoadedEvent> {
    @Setter
    private List<AudioTrackUIView> gridRows;

    @Override
    public void onApplicationEvent(AudioTrackLengthLoadedEvent audioTrackLengthLoadedEvent) {
        AudioTrack audioTrack = audioTrackLengthLoadedEvent.getAudioTrack();
        AudioTrackUIView rowToUpdate = gridRows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("AudioTrack not found in the grid"));
        RangeSlider rangeSlider = (RangeSlider) rowToUpdate.getRangeSliderContainer().getChildren().get(0);
        rangeSlider.setMin(0);
        rangeSlider.setMax(audioTrack.getLengthInSeconds());
        rangeSlider.setHighValue(audioTrack.getFinishInSeconds());
        rangeSlider.setLowValue(audioTrack.getStartInSeconds());
        rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            String currentTrackFinish = toStringFormat(newValue.doubleValue());
            rowToUpdate.getEndTimeLabel().setText(currentTrackFinish);
            audioTrack.setFinishInSeconds(newValue.doubleValue());
            rowToUpdate.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
        });
        rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            String currentTrackStart = toStringFormat(newValue.doubleValue());
            rowToUpdate.getStartTimeLabel().setText(currentTrackStart);
            audioTrack.setStartInSeconds(newValue.doubleValue());
            rowToUpdate.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
        });
        rowToUpdate.getEndTimeLabel().setText(toStringFormat(audioTrack.getFinishInSeconds()));
        rowToUpdate.getProgressLabel().setText(getSongProgressAsString(0, audioTrack.getLengthInSeconds()));
    }
}
