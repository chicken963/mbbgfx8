package ru.orthodox.mbbg.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ru.orthodox.mbbg.model.AudioTrack;

public class AudioUtils {

    public static double setAudioTrackLength(AudioTrack audioTrack) {
        Media media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            audioTrack.setFinishInSeconds(media.getDuration().toSeconds());
            audioTrack.setLengthInSeconds(media.getDuration().toSeconds());
        });
        return media.getDuration().toSeconds();
    }
}
