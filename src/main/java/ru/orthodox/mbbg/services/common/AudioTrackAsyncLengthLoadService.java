package ru.orthodox.mbbg.services.common;

import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.services.create.AudioTrackUIViewService;

import java.util.List;
import java.util.Optional;

import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

@Service
public class AudioTrackAsyncLengthLoadService implements ApplicationListener<AudioTrackLengthLoadedEvent> {
    @Getter
    @Setter
    private List<AudioTrackEditUIView> gridRows;
    @Autowired
    private AudioTrackUIViewService audioTrackUIViewService;
    @Autowired
    private PlayMediaService playMediaService;

    @Override
    public void onApplicationEvent(AudioTrackLengthLoadedEvent audioTrackLengthLoadedEvent) {
        AudioTrack audioTrack = audioTrackLengthLoadedEvent.getAudioTrack();
        Optional<AudioTrackEditUIView> rowToUpdateOpt = audioTrackUIViewService.findByAudioTrack(gridRows, audioTrack);
        if(rowToUpdateOpt.isPresent()) {
            AudioTrackEditUIView rowToUpdate = rowToUpdateOpt.get();
            RangeSlider rangeSlider = (RangeSlider) rowToUpdate.getRangeSliderContainer().getChildren().get(0);
            rangeSlider.setMin(0);
            rangeSlider.setMax(audioTrack.getLengthInSeconds());
            rangeSlider.setHighValue(audioTrack.getFinishInSeconds());
            rangeSlider.setLowValue(audioTrack.getStartInSeconds());
            rangeSlider.highValueProperty().addListener((observable, oldValue, newValue) -> {
                playMediaService.pause(audioTrack);
                String currentTrackFinish = toStringFormat(newValue.doubleValue());
                rowToUpdate.getEndTimeLabel().setText(currentTrackFinish);
                audioTrack.setFinishInSeconds(newValue.doubleValue());
                rowToUpdate.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
            });
            rangeSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
                playMediaService.pause(audioTrack);
                String currentTrackStart = toStringFormat(newValue.doubleValue());
                rowToUpdate.getStartTimeLabel().setText(currentTrackStart);
                audioTrack.setStartInSeconds(newValue.doubleValue());
                rowToUpdate.getProgressLabel().setText("00:00/" + toStringFormat(audioTrack.getFinishInSeconds() - audioTrack.getStartInSeconds()));
            });
            rowToUpdate.getEndTimeLabel().setText(toStringFormat(audioTrack.getFinishInSeconds()));
            rowToUpdate.getProgressLabel().setText(getSongProgressAsString(0, audioTrack.getLengthInSeconds()));
        }

    }
}
