package ru.orthodox.mbbg.utils;


import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.create.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.events.create.BrokenAudiotrackEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.utils.common.NormalizedPathString;

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

    public void setAudioTrackLength(AudioTrack audioTrack) {
        Media media;
        try {
            media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        } catch (MediaException e) {
            eventPublisher.publishEvent(new BrokenAudiotrackEvent(audioTrack));
            popupAlerter.invoke(sourceButton.getScene().getWindow(), "Corrupted audiofile",
                    "Audiotrack with local path " + audioTrack.getLocalPath() + " cannot be decoded and will be skipped.");
            return;
        }

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            audioTrack.setFinishInSeconds(media.getDuration().toSeconds());
            audioTrack.setLengthInSeconds(media.getDuration().toSeconds());
            eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(audioTrack, round));

        });
    }
}
