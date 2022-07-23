package ru.orthodox.mbbg.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.model.AudioTrack;

@Service
public class AudioUtils {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public double setAudioTrackLength(AudioTrack audioTrack) {
        Media media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            audioTrack.setFinishInSeconds(media.getDuration().toSeconds());
            audioTrack.setLengthInSeconds(media.getDuration().toSeconds());
            eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(this, audioTrack));

        });
        return media.getDuration().toSeconds();
    }
}
