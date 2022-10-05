package ru.orthodox.mbbg.utils;


import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.create.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.events.create.BrokenAudiotrackEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.utils.common.NormalizedPathString;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AudioTrackAsyncDataUpdater {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Setter
    private Round round;
    @Setter
    private Button sourceButton;
    @Autowired
    private PopupAlerter popupAlerter;

    public void setAudioTrackLength(AudioTrack audioTrack, ExecutorService threadPoolToLoadMediaInfo) {
        Media media;
        try {
            media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        } catch (MediaException e) {
            eventPublisher.publishEvent(new BrokenAudiotrackEvent(audioTrack));
            popupAlerter.invoke(sourceButton.getScene().getWindow(), "Corrupted audiofile",
                    "Audiotrack with local path " + audioTrack.getLocalPath() + " cannot be decoded and will be skipped.");
            return;
        }

        threadPoolToLoadMediaInfo.execute(() -> {
            int counter = 0;
            do {
                counter++;
                try {
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        audioTrack.setFinishInSeconds(media.getDuration().toSeconds());
                        audioTrack.setLengthInSeconds(media.getDuration().toSeconds());
                        eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(audioTrack, round));
                    });
                } catch (NullPointerException | ConcurrentModificationException e) {
                    log.warn(e.getMessage());
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    log.warn(e.getMessage());
                }
                if (counter > 1) {
                    log.info(audioTrack + " " + counter);
                }
            } while (audioTrack.getLengthInSeconds() == 0);

        });

    }
}
